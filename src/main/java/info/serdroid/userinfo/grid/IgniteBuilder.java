package info.serdroid.userinfo.grid;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

public class IgniteBuilder {

    public Ignite buildIgnite(boolean clientMode) {
    	Ignite ignite;
    	Ignition.setClientMode(clientMode);
    	String cacheConfigFile = "uinfo-cache.xml";
        ignite = Ignition.start(cacheConfigFile);
        return ignite;
    }

}
