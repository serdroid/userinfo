package info.serdroid.userinfo.grid;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.cache.store.CacheStoreSessionListener;

public class CacheJpaStoreSessionListener implements CacheStoreSessionListener {

	private EntityManagerFactory entityManagerFactory;
	
	public CacheJpaStoreSessionListener(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
//		System.out.println("CacheJpaStoreSessionListener c'tor");
	}

	@Override
	public void onSessionEnd(CacheStoreSession session, boolean commit) {
		System.out.println("CacheJpaStoreSessionListener onSessionEnd");
		EntityManager entityManager = session.attach(null);
		EntityTransaction entityTransaction = entityManager.getTransaction();
		if(commit) {
			entityTransaction.commit();
		} else {
			entityTransaction.rollback();
		}
		entityManager.close();
	}

	@Override
	public void onSessionStart(CacheStoreSession session) {
		System.out.println("CacheJpaStoreSessionListener onSessionStart");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		session.attach(entityManager);
	}

}
