package info.serdroid.userinfo.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.cache.Cache.Entry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.serdroid.userinfo.grid.model.SubjectKey;
import info.serdroid.userinfo.grid.model.UserInfo;

public class GridQuery {
	private static final Logger logger = LoggerFactory.getLogger(GridQuery.class);
	private transient Ignite ignite;
	private static final List<Integer> queryKeys = Arrays.asList(10001, 10002);
	private static final List<String> pids = Arrays.asList(
			"01124830090",
			"69480164950",
			"98456859930",
			"90235362340",
			"17734668770",
			"61007170840",
			"85135223600",
			"92248163810",
			"74601317270",
			"45527008630",
			"34921721100",
			"28426180010",
			"73323978790"			
	);
	

	void setupIgniteConfiguration() {
		ignite = new IgniteBuilder().buildIgnite(true);
	}

	void query() {
		queryKeys.forEach(this::getSubject);
//		getSubjectByKey("00000007");
		pids.forEach(this::getUserByPersonId);
	}
	
	private void getSubject(Integer key) {
		IgniteCache<Integer, SubjectKey> subjectCache = ignite.cache(SubjectKey.class.getName());
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
	
	void getSubjectByKey(String subKey) {
		IgniteCache<Integer, SubjectKey> subjectCache = ignite.cache(SubjectKey.class.getName());
		ArrayList<SubjectKey> allUsers = new ArrayList<>();
		try (QueryCursor<Entry<Integer, SubjectKey>> cursor = subjectCache.query(new ScanQuery<Integer, SubjectKey>(
				(k, v) -> v.getSKey().equals(subKey))) 
			) {
				cursor.forEach(item -> {
					allUsers.add( item.getValue() );
				});
			}
		allUsers.forEach(System.out::println);
	}
	
	void getUserByPersonId(String personId) {
		IgniteCache<String, UserInfo> userCache = ignite.cache(UserInfo.class.getName());
		ArrayList<UserInfo> allUsers = new ArrayList<>();
		ScanQuery<String, UserInfo> userScanQ = new ScanQuery<String, UserInfo>((k, v) -> v.getPersonid().equals(personId));
		try (QueryCursor<Entry<String, UserInfo>> cursor = userCache.query(userScanQ) ) {
				cursor.forEach(item -> {
					allUsers.add( item.getValue() );
				});
			}
		allUsers.forEach(System.out::println);
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
