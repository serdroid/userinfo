package info.serdroid.userinfo.grid;

import javax.cache.configuration.Factory;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.store.CacheStoreSessionListener;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

public class IgniteBuilder {
	private EntityManagerFactory entityManagerFactory;

	public IgniteBuilder() {
		entityManagerFactory = Persistence.createEntityManagerFactory("userds");
	}
	
    public Ignite buildIgnite(boolean clientMode) {
    	Ignite ignite;
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setMulticastGroup("228.10.10.157");
        spi.setIpFinder(ipFinder);
        IgniteConfiguration cfg = new IgniteConfiguration();
        // Override default discovery SPI.
        cfg.setDiscoverySpi(spi);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setClientMode(clientMode);
        
        DataStorageConfiguration dscfg = new DataStorageConfiguration();
        DataRegionConfiguration drcfg = new DataRegionConfiguration();
        drcfg.setName("ssdefault");
        long GB = 1024 * 1024 * 1024;
        drcfg.setInitialSize(1 * GB);
        drcfg.setMaxSize(8 * GB);
        drcfg.setMetricsEnabled(true);
        dscfg.setDefaultDataRegionConfiguration(drcfg);
        cfg.setDataStorageConfiguration(dscfg);
        
        cfg.setCacheStoreSessionListenerFactories(new Factory<CacheStoreSessionListener>() {
            @Override
            public CacheStoreSessionListener create() {
                return new CacheJpaStoreSessionListener(entityManagerFactory);
            }
        });
        // Start Ignite node.
        ignite = Ignition.start(cfg);
        return ignite;
    }

}
