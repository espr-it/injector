package it.espr.injector.bean;

import javax.inject.Inject;
import javax.inject.Named;

import it.espr.injector.bean.named.InterfaceForNamedBeans;

public class ComplexBean {

	@Inject
	@Named("a")
	private InterfaceForNamedBeans beanA;

	private InterfaceForNamedBeans beanB;

	@Inject
	@Named("single")
	private InterfaceForNamedBeans namedSingleton;

	private SingletonBean singletonBean;

	@Inject
	private EmptyBeanWithConstructor emptyBeanWithConstructor;

	private BeanWithConstructorWithSingleLevelDependencies beanWithConstructorWithSingleLevelDependencies;

	public ComplexBean(@Named("b") InterfaceForNamedBeans beanB, SingletonBean singletonBean,
			BeanWithConstructorWithSingleLevelDependencies beanWithConstructorWithSingleLevelDependencies) {
		super();
		this.beanB = beanB;
		this.singletonBean = singletonBean;
		this.beanWithConstructorWithSingleLevelDependencies = beanWithConstructorWithSingleLevelDependencies;
	}

	public InterfaceForNamedBeans getBeanA() {
		return beanA;
	}

	public InterfaceForNamedBeans getBeanB() {
		return beanB;
	}

	public InterfaceForNamedBeans getNamedSingleton() {
		return namedSingleton;
	}

	public SingletonBean getSingletonBean() {
		return singletonBean;
	}

	public EmptyBeanWithConstructor getEmptyBeanWithConstructor() {
		return emptyBeanWithConstructor;
	}

	public BeanWithConstructorWithSingleLevelDependencies getBeanWithConstructorWithSingleLevelDependencies() {
		return beanWithConstructorWithSingleLevelDependencies;
	}
}
