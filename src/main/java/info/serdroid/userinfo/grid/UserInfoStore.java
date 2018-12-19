package info.serdroid.userinfo.grid;

import java.util.List;

import javax.cache.Cache.Entry;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.ignite.Ignite;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.serdroid.userinfo.grid.model.UserInfo;

public class UserInfoStore extends CacheStoreAdapter<String, UserInfo> {
	private static final Logger logger = LoggerFactory.getLogger(UserInfoStore.class);

	// Will be automatically injected.
	@IgniteInstanceResource
	private Ignite ignite;
	/**
	 * Store session.
	 */
	@CacheStoreSessionResource
	private CacheStoreSession session;

	@Override
	public UserInfo load(String key) throws CacheLoaderException {
		EntityManager entityManager = session.attachment();
		UserInfo uInfo = entityManager.find(UserInfo.class, key);
		return uInfo;
	}

	@Override
	public void write(Entry<? extends String, ? extends UserInfo> entry) throws CacheWriterException {
		EntityManager entityManager = session.attachment();
		entityManager.persist(entry.getValue());
	}

	@Override
	public void delete(Object key) throws CacheWriterException {
		EntityManager entityManager = session.attachment();
		Query dQuery = entityManager.createQuery("delete from UserInfo u where u.userid = '" + key + "'");
		dQuery.executeUpdate();
	}

	@Override
	public void loadCache(IgniteBiInClosure<String, UserInfo> clo, Object... args) {
		Affinity<UserInfo> affinity = ignite.affinity(UserInfo.class.getName());
	    ClusterNode localNode = ignite.cluster().localNode();
    	logger.debug("primary partitions for {}", localNode.id());
	    int[] partitions = affinity.primaryPartitions(localNode);
	    for( int part : partitions) {
	    	System.out.print( part + " ");
			int cnt = loadPartition(clo, part);
	    	logger.debug(">>> Loaded {} values into cache, for primary partition {}", cnt, part);
	    }
    	logger.debug("\nbackup partitions for {}", localNode.id());
	    partitions = affinity.backupPartitions(localNode);
	    for( int part : partitions) {
	    	System.out.print( part + " ");
			int cnt = loadPartition(clo, part);
	    	logger.debug(">>> Loaded {} values into cache, for backup partition {}", cnt, part);
	    }
	}

	private int loadPartition(IgniteBiInClosure<String, UserInfo> clo, int part) {
		EntityManager entityManager = session.attachment();

		String jpqlStr = "select u from UserInfo u where u.partitionid = :partId";
		TypedQuery<UserInfo> query = entityManager.createQuery(jpqlStr, UserInfo.class);
		query.setParameter("partId", part);
		List<UserInfo> userList = query.getResultList();

		int cnt = 0;
		for (UserInfo uInfo : userList) {
			clo.apply(uInfo.getUserid(), uInfo);
			cnt++;
		}
		return cnt;
	}
	
}
