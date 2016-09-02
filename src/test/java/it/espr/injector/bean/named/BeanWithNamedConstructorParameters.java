package it.espr.injector.bean.named;

import javax.inject.Named;

import it.espr.injector.bean.EmptyBean;

public class BeanWithNamedConstructorParameters {

	private EmptyBean emptyBean;

	private InterfaceForNamedBeans beanA;

	private InterfaceForNamedBeans beanB;

	public BeanWithNamedConstructorParameters(EmptyBean emptyBean, @Named("a") InterfaceForNamedBeans beanA, @Named("b") InterfaceForNamedBeans beanB) {
		super();
		this.emptyBean = emptyBean;
		this.beanB = beanB;
		this.beanA = beanA;
	}

	public EmptyBean getEmptyBean() {
		return emptyBean;
	}

	public InterfaceForNamedBeans getBeanA() {
		return beanA;
	}

	public InterfaceForNamedBeans getBeanB() {
		return beanB;
	}
}
