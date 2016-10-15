package it.espr.injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Injector {

	private static final Logger log = LoggerFactory.getLogger(Injector.class);

	private static Injector injector;

	private BeanFactory beanFactory;

	private Configuration configuration;

	private ClassInspector classInspector;

	private void configure(Configuration configuration) {
		if (configuration == null) {
			this.configuration = new Configuration() {
				// empty configuration
			};
		} else {
			this.configuration = configuration;
		}
		this.configuration.initialise();
		this.classInspector = new ClassInspector(this.configuration.bindings);
		this.beanFactory = new BeanFactory();
	}

	public static Injector injector() {
		return injector((Configuration) null);
	}

	public static Injector injector(Configuration configuration) {
		if (injector == null || configuration != null) {
			// create and configure new Injector
			injector = new Injector();
			injector.configure(configuration);
		}
		return injector;
	}

	public <Type> Type get(Class<Type> type) {
		return this.get(type, null);
	}

	public <Type> Type get(Class<Type> type, String name) {
		Type instance = null;
		try {
			Bean<Type> bean = classInspector.inspect(type, name);
			instance = beanFactory.create(bean);
		} catch (Exception e) {
			log.error("Problem when getting instance of {}", type, e);
			throw new RuntimeException("Can't inject bean of type '" + type + "'", e);
		}

		return instance;
	}

}
