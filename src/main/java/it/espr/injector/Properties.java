package it.espr.injector;

import static it.espr.injector.Utils.isEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Properties {

	private static final Logger log = LoggerFactory.getLogger(Properties.class);

	public Map<String, String> load() {
		return this.load(null);
	}

	public Map<String, String> load(String filename) {
		Map<String, String> properties = new HashMap<>();
		java.util.Properties file = this.loadPropertyFile(this.getFileClasspath(filename));
		if (file != null && !file.isEmpty()) {
			for (Entry<Object, Object> entry : file.entrySet()) {
				if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
					String name = (String) entry.getKey();
					String value = (String) entry.getValue();
					if (!isEmpty(name) && !isEmpty(value)) {
						properties.put(name, value);
					}
				}
			}
		}
		return properties;
	}

	private String getFileClasspath(String filename) {
		String fileClasspath = "";
		if (Utils.isEmpty(filename)) {
			fileClasspath = "configuration.properties";
		} else {
			fileClasspath += filename;
		}
		if (!fileClasspath.startsWith("/")) {
			fileClasspath = "/" + fileClasspath;
		}
		return fileClasspath;
	}

	private java.util.Properties loadPropertyFile(String filename) {
		java.util.Properties prop = new java.util.Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(new File(this.getClass().getResource(filename).toURI()));
			prop.load(input);
		} catch (Exception e) {
			log.debug("Couldn't load configuration properties file {}", filename, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error("Problem when closing input when loading property configuration file", e);
				}
			}
		}
		return prop;
	}
}
