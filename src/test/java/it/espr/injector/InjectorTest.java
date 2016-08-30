package it.espr.injector;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import it.espr.injector.bean.EmptyBean;

public class InjectorTest {

	Injector injector = Injector.get();

	@Test
	public void whenGetSimpleclassWithoutAnnotationThenAlwaysReturnPrototype() {
		EmptyBean simpleBean1 = injector.get(EmptyBean.class);
		EmptyBean simpleBean2 = injector.get(EmptyBean.class);

		assertThat(simpleBean1).isNotNull();
		assertThat(simpleBean2).isNotNull();
		assertThat(simpleBean1).isNotEqualTo(simpleBean2);
	}
}
