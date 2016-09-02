package it.espr.injector.bean.named;

import javax.inject.Inject;
import javax.inject.Named;

import it.espr.injector.bean.EmptyBean;

public class BeanWithNamedFieldsAndConstructorParameters {

	private EmptyBean emptyBean;

	@Inject
	@Named("a")
	private InterfaceForNamedBeans beanA;

	private InterfaceForNamedBeans beanB;

	public BeanWithNamedFieldsAndConstructorParameters(EmptyBean emptyBean, @Named("b") InterfaceForNamedBeans beanB) {
		super();
		this.emptyBean = emptyBean;
		this.beanB = beanB;
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
