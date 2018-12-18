package info.serdroid.userinfo.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cluster.ClusterNode;

import info.serdroid.userinfo.grid.model.SubjectKey;

public class GridQuery {
	private transient Ignite ignite;
	Integer[] queryKeys = new Integer[] {10001, 10002};

	void setupIgniteConfiguration() {
		ignite = new IgniteBuilder().buildIgnite(true);
	}

	void query() {
		Arrays.asList(queryKeys).stream().forEach(item -> {
			getSubject(ignite, item);
		});
//		getSubjectKeys(ignite);
//		getSubject(ignite, 10007);
	}
	
	private void getSubject(Ignite igniteArg, Integer key) {
		IgniteCache<Integer, SubjectKey> subjectCache = igniteArg.cache(SubjectKey.class.getName());
		SubjectKey subject = subjectCache.get(key);
		System.out.println("subject = " + subject);
	}

	void getSubjectKeys(Ignite igniteArg) {
		IgniteCache<Integer, SubjectKey> subjectCache = igniteArg.cache(SubjectKey.class.getName());
		HashSet<Integer> userSet = new HashSet<>();
		long start = System.currentTimeMillis();
		IgniteCompute compute = igniteArg.compute(igniteArg.cluster().forDataNodes(SubjectKey.class.getName()));
		Collection<List<Integer>> users = compute.broadcast(() -> {
			ClusterNode localNode = igniteArg.cluster().localNode();
			List<Integer> allUsers = new ArrayList<>();
			int[] parts = igniteArg.affinity(SubjectKey.class.getName()).primaryPartitions(localNode);
			for (int part : parts) {
				ScanQuery<Integer, SubjectKey> sq = new ScanQuery<Integer, SubjectKey>().setFilter((k, v) -> true)
						.setLocal(true).setPartition(part);
				subjectCache.query(sq).getAll().stream().forEach(entry -> {
					allUsers.add(entry.getKey());
				});
			}
			return allUsers;
		});
		for (List<Integer> userPart : users) {
			userPart.stream().forEach(user -> {
				userSet.add(user);
			});
		}
		long end = System.currentTimeMillis();
		System.out.println(">>> Get " + userSet.size() + " keys in " + (end - start) + "ms.");
	}
	
	void run() {
		setupIgniteConfiguration();
		query();
	}

	boolean load = false;
	boolean keys = false;

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
		GridQuery query = new GridQuery();
		query.parseArgs(args);
		query.run();
	}

}
