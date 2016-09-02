package it.espr.injector;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import it.espr.injector.bean.EmptyBean;
import it.espr.injector.bean.SingletonBean;

public class InjectorTest {

	Injector injector = Injector.get();

	@Test
	public void whenGetSimpleclassWithoutAnnotationThenAlwaysReturnPrototype() {
		EmptyBean emptyBean1 = injector.get(EmptyBean.class);
		EmptyBean emptyBean2 = injector.get(EmptyBean.class);

		assertThat(emptyBean1).isNotNull();
		assertThat(emptyBean2).isNotNull();
		assertThat(emptyBean1).isNotSameAs(emptyBean2);
	}

	@Test
	public void whenGetSingletonAnnotatedClassThenAlwaysReturnSameInstance() {
		SingletonBean singleton1 = injector.get(SingletonBean.class);
		SingletonBean singleton2 = injector.get(SingletonBean.class);

		assertThat(singleton1).isNotNull();
		assertThat(singleton2).isNotNull();
		assertThat(singleton1).isSameAs(singleton2);
	}

}
