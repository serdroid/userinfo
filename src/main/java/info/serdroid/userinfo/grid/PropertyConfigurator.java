package info.serdroid.userinfo.grid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import info.serdroid.userinfo.grid.GridConfiguration.Builder;

public class PropertyConfigurator {

	public GridConfiguration configure() {
        Properties props = new Properties();
        String propFileName = "config.properties";

        Builder builder = new GridConfiguration.Builder();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName)) {
            props.load(inputStream);
            if ( props.containsKey("store.partition.count") ) {
            	builder.setPartitionCount(Integer.valueOf( props.getProperty("store.partition.count")) );
            }
            if ( props.containsKey("store.node.count") ) {
            	builder.setNodeCount(Integer.valueOf( props.getProperty("store.node.count")) );
            }
        } catch (IOException e) {
			e.printStackTrace();
		}

		return builder.build();
	}
}
