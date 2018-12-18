package info.serdroid.userinfo.grid;

import static org.apache.ignite.cache.CacheAtomicityMode.TRANSACTIONAL;

import java.io.Serializable;
import java.util.Collection;

import javax.cache.configuration.FactoryBuilder;

import org.apache.ignite.DataRegionMetrics;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.configuration.CacheConfiguration;

import info.serdroid.userinfo.grid.model.SubjectKey;
import info.serdroid.userinfo.grid.model.UserInfo;

public class GridServer implements Serializable {
	private static final long serialVersionUID = 846515891166302712L;
	
	private transient Ignite ignite;
	GridConfiguration gridConfiguration;
	
	public GridServer(GridConfiguration config) {
		gridConfiguration = config;
	}

    void setupIgniteConfiguration() {
        ignite = new IgniteBuilder().buildIgnite(false);
    }

    void configureCache(CacheConfiguration cacheCfg, Class<?> storeClass) {
        cacheCfg.setAtomicityMode(TRANSACTIONAL);
        cacheCfg.setCacheStoreFactory(FactoryBuilder.factoryOf(storeClass));
        cacheCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheCfg.setBackups(1);
        RendezvousAffinityFunction affinityFunction = new RendezvousAffinityFunction(false, gridConfiguration.getPartitionCount());
        cacheCfg.setAffinity(affinityFunction);
        cacheCfg.setReadThrough(true);
        cacheCfg.setWriteThrough(true);
    }

    void setupCacheConfiguration() {
    	setupUserCache();
    	setupSubjectKeyCache();
    }

    private void setupUserCache() {
        CacheConfiguration<String, UserInfo> cacheCfg = new CacheConfiguration<>(UserInfo.class.getName());
        configureCache(cacheCfg, UserInfoStore.class);
        ignite.getOrCreateCache(cacheCfg);
    }

    private void setupSubjectKeyCache() {
    	CacheConfiguration<String, SubjectKey> cacheCfg = new CacheConfiguration<>(SubjectKey.class.getName());
        configureCache(cacheCfg, SubjectKeyStore.class);
        ignite.getOrCreateCache(cacheCfg);
    }
    
    
    void readDataMetrics() {
		// Get the metrics of all the data regions configured on a node.
		Collection<DataRegionMetrics> regionsMetrics = ignite.dataRegionMetrics();
		            
		// Print out some of the metrics.
		for (DataRegionMetrics metrics : regionsMetrics) {
		    System.out.println(">>> Memory Region Name: " + metrics.getName());
		    System.out.println(">>> Allocation Rate: " + metrics.getAllocationRate());
		    System.out.println(">>> Total pages: " + metrics.getTotalAllocatedPages());
		    System.out.println(">>> Fill Factor: " + metrics.getPagesFillFactor());
		}		
	}
    
    void run() {
        setupIgniteConfiguration();
        setupCacheConfiguration();
    }

    public static void main(String[] args) {
    	PropertyConfigurator propConfig = new PropertyConfigurator();
        GridServer server = new GridServer(propConfig.configure());
        server.run();
    }
}
