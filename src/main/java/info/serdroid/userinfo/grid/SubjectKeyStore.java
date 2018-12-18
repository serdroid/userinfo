package info.serdroid.userinfo.grid;

import java.util.List;

import javax.cache.Cache.Entry;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.ignite.Ignite;
import org.apache.ignite.cache.affinity.Affinity;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;
import org.apache.ignite.resources.IgniteInstanceResource;

import info.serdroid.userinfo.grid.model.SubjectKey;

public class SubjectKeyStore extends CacheStoreAdapter<String, SubjectKey> {

	// Will be automatically injected.
	@IgniteInstanceResource
	private Ignite ignite;
	/**
	 * Store session.
	 */
	@CacheStoreSessionResource
	private CacheStoreSession session;

	@Override
	public SubjectKey load(String key) throws CacheLoaderException {
		EntityManager entityManager = session.attachment();
		SubjectKey uInfo = entityManager.find(SubjectKey.class, key);
		return uInfo;
	}

	@Override
	public void write(Entry<? extends String, ? extends SubjectKey> entry) throws CacheWriterException {
		EntityManager entityManager = session.attachment();
		entityManager.persist(entry.getValue());
	}

	@Override
	public void delete(Object key) throws CacheWriterException {
		EntityManager entityManager = session.attachment();
		Query dQuery = entityManager.createQuery("delete from SubjectKey o where o.sId = '" + key + "'");
		dQuery.executeUpdate();
	}
	
	@Override
	public void loadCache(IgniteBiInClosure<String, SubjectKey> clo, Object... args) {
		loadPartition(clo, 0);
	}

//	@Override
	public void loadCache2(IgniteBiInClosure<String, SubjectKey> clo, Object... args) {
		Affinity<SubjectKey> affinity = ignite.affinity(SubjectKey.class.getName());
	    ClusterNode localNode = ignite.cluster().localNode();
    	System.out.println("primary partitions for " + localNode.id());
	    int[] partitions = affinity.primaryPartitions(localNode);
	    for( int part : partitions) {
	    	System.out.print( part + " ");
			int cnt = loadPartition(clo, part);
			System.out.printf(">>> Loaded %d values into cache, for primary partition %d \n", cnt, part);
	    }
    	System.out.println();

    	System.out.println("backup partitions for " + localNode.id());
	    partitions = affinity.backupPartitions(localNode);
	    for( int part : partitions) {
	    	System.out.print( part + " ");
			int cnt = loadPartition(clo, part);
			System.out.printf(">>> Loaded %d values into cache, for backup partition %d \n", cnt, part);
	    }
    	System.out.println();
	}

	private int loadPartition(IgniteBiInClosure<String, SubjectKey> clo, int part) {
		EntityManager entityManager = session.attachment();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    	
    	CriteriaQuery<SubjectKey> criteriaQuery = criteriaBuilder.createQuery(SubjectKey.class);
    	Root<SubjectKey> from = criteriaQuery.from(SubjectKey.class);
//    	criteriaQuery.where(criteriaBuilder.equal( from.get("partitionid").as(Integer.class) , part) );
    	TypedQuery<SubjectKey> query = entityManager.createQuery(criteriaQuery);
    	List<SubjectKey> subjectList = query.getResultList();
    	subjectList.forEach(item -> {
    		clo.apply(item.getSKey(), item);
//			System.out.printf(">>> Loaded %s into subject cache\n", item.toString());
    	});
		return subjectList.size();
	}
	
}
