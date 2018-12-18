package info.serdroid.userinfo.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cluster.ClusterNode;

import info.serdroid.userinfo.grid.model.SubjectKey;
import info.serdroid.userinfo.grid.model.UserInfo;

public class GridLoader implements Serializable {
	private static final long serialVersionUID = 3164382224117633827L;
	
	private transient Ignite ignite;

	void setupIgniteConfiguration() {
		ignite = new IgniteBuilder().buildIgnite(true);
	}

	void loadAllUsers(Ignite igniteArg) {
		IgniteCache<String, UserInfo> userCache = igniteArg.cache(UserInfo.class.getName());
		HashMap<String, UserInfo> userMap = new HashMap<>();
		long start = System.currentTimeMillis();
		IgniteCompute compute = igniteArg.compute(igniteArg.cluster().forDataNodes(UserInfo.class.getName()));
		Collection<List<UserInfo>> users = compute.broadcast(() -> {
			ClusterNode localNode = igniteArg.cluster().localNode();
			ArrayList<UserInfo> allUsers = new ArrayList<>();
			int[] parts = igniteArg.affinity(UserInfo.class.getName()).primaryPartitions(localNode);
			for (int part : parts) {
				ScanQuery<String, UserInfo> sq = new ScanQuery<String, UserInfo>().setFilter((k, v) -> true)
						.setLocal(true).setPartition(part);
				userCache.query(sq).getAll().stream().forEach(entry -> {
					allUsers.add(entry.getValue());
				});
			}
			return allUsers;
		});
		for (List<UserInfo> userPart : users) {
			userPart.stream().forEach(user -> {
				userMap.put(user.getUserid(), user);
			});
		}
		long end = System.currentTimeMillis();
		System.out.println(">>> Loaded " + userMap.size() + " users in " + (end - start) + "ms.");
	}

	void getUserKeys(Ignite igniteArg) {
		IgniteCache<String, UserInfo> userCache = igniteArg.cache(UserInfo.class.getName());
		HashSet<String> userSet = new HashSet<>();
		long start = System.currentTimeMillis();
		IgniteCompute compute = igniteArg.compute(igniteArg.cluster().forDataNodes(UserInfo.class.getName()));
		Collection<List<String>> users = compute.broadcast(() -> {
			ClusterNode localNode = igniteArg.cluster().localNode();
			List<String> allUsers = new ArrayList<>();
			int[] parts = igniteArg.affinity(UserInfo.class.getName()).primaryPartitions(localNode);
			for (int part : parts) {
				ScanQuery<String, UserInfo> sq = new ScanQuery<String, UserInfo>().setFilter((k, v) -> true)
						.setLocal(true).setPartition(part);
				userCache.query(sq).getAll().stream().forEach(entry -> {
					allUsers.add(entry.getKey());
				});
			}
			return allUsers;
		});
		for (List<String> userPart : users) {
			userPart.stream().forEach(user -> {
				userSet.add(user);
			});
		}
		long end = System.currentTimeMillis();
		System.out.println(">>> Get " + userSet.size() + " keys in " + (end - start) + "ms.");
	}

	void examinePartitions(Ignite igniteArg) {
		IgniteCompute compute = igniteArg.compute(igniteArg.cluster().forDataNodes(UserInfo.class.getName()));
		Collection<String> users = compute.broadcast(() -> {
			ClusterNode localNode = igniteArg.cluster().localNode();
			int[] parts = igniteArg.affinity(UserInfo.class.getName()).primaryPartitions(localNode);
			Stream.of(parts);
			String partString = Arrays.stream(parts).boxed().map(String::valueOf)
					.collect(Collectors.joining(", ", "[", "]"));
			return localNode.id().toString() + "=>" + partString;
		});
		users.stream().forEach(System.out::println);
	}

	void examineBackupPartitions(Ignite igniteArg) {
		IgniteCompute compute = igniteArg.compute(igniteArg.cluster().forDataNodes(UserInfo.class.getName()));
		Collection<String> users = compute.broadcast(() -> {
			ClusterNode localNode = igniteArg.cluster().localNode();
			int[] parts = igniteArg.affinity(UserInfo.class.getName()).backupPartitions(localNode);
			Stream.of(parts);
			String partString = Arrays.stream(parts).boxed().map(String::valueOf)
					.collect(Collectors.joining(", ", "[", "]"));
			return localNode.id().toString() + "=>" + partString;
		});
		users.stream().forEach(System.out::println);
	}

	private void loadCache() {
		IgniteCache<String, UserInfo> userCache = ignite.cache(UserInfo.class.getName());
		loadCache(userCache);
		IgniteCache<String, SubjectKey> subjectCache = ignite.cache(SubjectKey.class.getName());
		loadCache(subjectCache);
	}

	/**
	 * Makes initial cache loading.
	 *
	 * @param cache
	 *            Cache to load.
	 */
	private void loadCache(IgniteCache<?, ?> cache) {
		long start = System.currentTimeMillis();

		// Start loading cache from persistent store on all caching nodes.
		System.out.println("Start loading cache from persistent store on all caching nodes.");
		cache.loadCache(null);

		long end = System.currentTimeMillis();

		System.out.println(">>> Loaded " + cache.size() + " keys with backups in " + (end - start) + "ms.");
	}

	void run() {
		setupIgniteConfiguration();
		examinePartitions(ignite);
		examineBackupPartitions(ignite);
		if(load) {
			loadCache();
		}
		if(keys) {
			getUserKeys(ignite);
		}
	}

	boolean load = false;
	boolean keys = false;
	boolean examineParts = true;

	void parseArgs(String args[]) {
		for (int idx = 0; idx < args.length; ++idx) {
			if ("-l".equals(args[idx]) || "--load".equals(args[idx])) {
				load = true;
			}
			if ("-k".equals(args[idx]) || "--keys".equals(args[idx])) {
				keys = true;
			}
		}
	}

	public static void main(String[] args) {
		GridLoader loader = new GridLoader();
		loader.parseArgs(args);
		loader.run();
	}

}
