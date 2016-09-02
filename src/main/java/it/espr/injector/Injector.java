package it.espr.injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Injector {

	private static final Logger log = LoggerFactory.getLogger(Injector.class);

	private static Injector injector;

	private BeanFactory beanFactory;

	private Configuration configuration = new Configuration();

	private ClassInspector classInspector;

	public void configure(Configuration... configurations) {
		this.configuration = new Configuration();
		for (Configuration configuration : configurations) {
			this.configuration.bindings.putAll(configuration.bindings);
			this.configuration.instances.putAll(configuration.instances);
		}
		this.beanFactory = new BeanFactory(this.configuration);
		this.classInspector = new ClassInspector(this.configuration);
	}

	public static Injector get(Configuration... configurations) {
		if (injector == null || (configurations != null && configurations.length > 0)) {
			// create and configure new Injector
			injector = new Injector();
			injector.configure(configurations);
		}
		return injector;
	}

	public <Type> Type get(Class<Type> type) {
		Type instance = null;
		try {
			Bean<Type> bean = classInspector.inspect(type);
			instance = beanFactory.create(bean);
		} catch (Exception e) {
			log.error("Problem when getting instance of {}", type, e);
			throw new RuntimeException("Can't inject bean of type '" + type + "'", e);
		}

		return instance;
	}
}
