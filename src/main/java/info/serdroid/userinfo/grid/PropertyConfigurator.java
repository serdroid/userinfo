package info.serdroid.userinfo.grid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import info.serdroid.userinfo.grid.GridConfiguration.Builder;

public class PropertyConfigurator {

	public GridConfiguration configure() {
        Properties props = new Properties();
        InputStream inputStream = null;
        String propFileName = "config.properties";

        Builder builder = new GridConfiguration.Builder();
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream == null) {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            props.load(inputStream);
            if ( props.containsKey("store.partition.count") ) {
            	builder.setPartitionCount(Integer.valueOf( props.getProperty("store.partition.count")) );
            }
            if ( props.containsKey("store.node.count") ) {
            	builder.setNodeCount(Integer.valueOf( props.getProperty("store.node.count")) );
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
		return builder.build();
	}
}
