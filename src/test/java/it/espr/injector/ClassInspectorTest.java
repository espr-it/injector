package it.espr.injector;

import static org.fest.assertions.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map.Entry;

import javax.inject.Named;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

import it.espr.injector.bean.BeanWithConstructorWithMultipleLevelDependencies;
import it.espr.injector.bean.BeanWithConstructorWithSingleLevelDependencies;
import it.espr.injector.bean.ComplexBean;
import it.espr.injector.bean.EmptyBean;
import it.espr.injector.bean.EmptyBeanWithConstructor;
import it.espr.injector.bean.SingletonBean;
import it.espr.injector.bean.circular.CircularBeanA;
import it.espr.injector.bean.named.BeanWithNamedConstructorParameters;
import it.espr.injector.bean.named.BeanWithNamedFields;
import it.espr.injector.bean.named.BeanWithNamedFieldsAndConstructorParameters;
import it.espr.injector.bean.named.InterfaceForNamedBeans;
import it.espr.injector.bean.named.NamedEmptyBeanA;
import it.espr.injector.bean.named.NamedEmptyBeanB;
import it.espr.injector.bean.named.NamedSingleton;
import it.espr.injector.exception.BeanException;
import it.espr.injector.exception.CircularDependencyExpection;

public class ClassInspectorTest {

	private Configuration configuration = new Configuration();

	private ClassInspector classInspector = new ClassInspector(configuration);

	@Test
	public void whenInspectingTheSameBeanTheInspectorShouldReturnTheSameInstance() throws BeanException {
		Bean<EmptyBean> emptyBean1 = classInspector.inspect(EmptyBean.class);
		Bean<EmptyBean> emptyBean2 = classInspector.inspect(EmptyBean.class);

		assertThat(emptyBean1).isSameAs(emptyBean2);
	}

	@Test
	public void inspectEmptyBean() throws BeanException {
		Bean<EmptyBean> emptyBean = classInspector.inspect(EmptyBean.class);

		assertThat(emptyBean).isNotNull();
		assertThat(emptyBean.type).isEqualTo(EmptyBean.class);
		assertThat(emptyBean.constructorParameters).isNull();
		assertThat(emptyBean.key).isEqualTo(EmptyBean.class.getCanonicalName());
	}

	@Test
	public void inspectEmptyBeanWithConstructor() throws BeanException {
		Bean<EmptyBeanWithConstructor> emptyBeanWithConstructor = classInspector.inspect(EmptyBeanWithConstructor.class);

		assertThat(emptyBeanWithConstructor).isNotNull();
		assertThat(emptyBeanWithConstructor.type).isEqualTo(EmptyBeanWithConstructor.class);
		assertThat(emptyBeanWithConstructor.constructorParameters).isNull();
		assertThat(emptyBeanWithConstructor.key).isEqualTo(EmptyBeanWithConstructor.class.getCanonicalName());
	}

	@Test
	public void inspectBeanWithConstructorWithSingleLevelDependencies() throws BeanException {
		Bean<BeanWithConstructorWithSingleLevelDependencies> bean = classInspector.inspect(BeanWithConstructorWithSingleLevelDependencies.class);

		assertThat(bean).isNotNull();
		assertThat(bean.type).isEqualTo(BeanWithConstructorWithSingleLevelDependencies.class);
		assertThat(bean.constructorParameters).hasSize(2);
		assertThat(bean.key).isEqualTo(BeanWithConstructorWithSingleLevelDependencies.class.getCanonicalName());
	}

	@Test
	public void inspectBeanWithConstructorWithMultipleLevelDependencies() throws BeanException {
		Bean<BeanWithConstructorWithMultipleLevelDependencies> bean = classInspector.inspect(BeanWithConstructorWithMultipleLevelDependencies.class);

		assertThat(bean).isNotNull();
		assertThat(bean.type).isEqualTo(BeanWithConstructorWithMultipleLevelDependencies.class);
		assertThat(bean.constructorParameters).hasSize(3);
		assertThat(bean.key).isEqualTo(BeanWithConstructorWithMultipleLevelDependencies.class.getCanonicalName());
	}

	@Test(expected = CircularDependencyExpection.class)
	public void whenCircularDependencyDetectedThrowException() throws BeanException {
		Bean<CircularBeanA> bean = classInspector.inspect(CircularBeanA.class);
	}

	@Test
	public void inspectSingletonBean() throws BeanException {
		Bean<SingletonBean> bean = classInspector.inspect(SingletonBean.class);

		assertThat(bean).isNotNull();
		assertThat(bean.type).isEqualTo(SingletonBean.class);
		assertThat(bean.constructorParameters).isNull();
		assertThat(bean.key).isEqualTo(SingletonBean.class.getCanonicalName());
		assertThat(bean.singleton).isTrue();
	}

	@Test
	public void inspectBeanWithNamedFields() throws BeanException {
		configuration.bind(InterfaceForNamedBeans.class, Arrays.asList(NamedEmptyBeanA.class, NamedEmptyBeanB.class));
		Bean<BeanWithNamedFields> bean = classInspector.inspect(BeanWithNamedFields.class);

		assertThat(bean).isNotNull();
		assertThat(bean.type).isEqualTo(BeanWithNamedFields.class);
		assertThat(bean.constructorParameters).isNull();
		assertThat(bean.key).isEqualTo(BeanWithNamedFields.class.getCanonicalName());
		assertThat(bean.singleton).isFalse();

		assertThat(bean.fields).hasSize(2);

		for (Entry<Field, Bean<?>> entry : bean.fields.entrySet()) {
			Field f = entry.getKey();
			Bean<?> b = entry.getValue();

			if (f.getName().equals("beanA")) {
				assertThat(f.getAnnotation(Named.class).value()).isEqualTo("a");
				assertThat(b.type.equals(NamedEmptyBeanA.class)).isTrue();
			}

			if (f.getName().equals("beanB")) {
				assertThat(f.getAnnotation(Named.class).value()).isEqualTo("b");
				assertThat(b.type.equals(NamedEmptyBeanB.class)).isTrue();
			}
		}
	}

	@Test
	public void inspectBeanWithNamedConstructorParameters() throws BeanException {
		configuration.bind(InterfaceForNamedBeans.class, Arrays.asList(NamedEmptyBeanA.class, NamedEmptyBeanB.class));
		Bean<BeanWithNamedConstructorParameters> bean = classInspector.inspect(BeanWithNamedConstructorParameters.class);

		assertThat(bean).isNotNull();
		assertThat(bean.type).isEqualTo(BeanWithNamedConstructorParameters.class);
		assertThat(bean.constructorParameters).hasSize(3);
		assertThat(bean.key).isEqualTo(BeanWithNamedConstructorParameters.class.getCanonicalName());
		assertThat(bean.singleton).isFalse();

		assertThat(bean.constructorParameters.get(0).type.equals(EmptyBean.class)).isTrue();
		assertThat(bean.constructorParameters.get(1).type.equals(NamedEmptyBeanA.class)).isTrue();
		assertThat(bean.constructorParameters.get(2).type.equals(NamedEmptyBeanB.class)).isTrue();
	}

	@Test
	public void inspectBeanWithNamedFieldsAndConstructorParameters() throws BeanException {
		configuration.bind(InterfaceForNamedBeans.class, Arrays.asList(NamedEmptyBeanA.class, NamedEmptyBeanB.class));
		Bean<BeanWithNamedFieldsAndConstructorParameters> bean = classInspector.inspect(BeanWithNamedFieldsAndConstructorParameters.class);

		assertThat(bean).isNotNull();
		assertThat(bean.type).isEqualTo(BeanWithNamedFieldsAndConstructorParameters.class);
		assertThat(bean.constructorParameters).hasSize(2);
		assertThat(bean.key).isEqualTo(BeanWithNamedFieldsAndConstructorParameters.class.getCanonicalName());
		assertThat(bean.singleton).isFalse();

		assertThat(bean.fields).hasSize(1);

		for (Entry<Field, Bean<?>> entry : bean.fields.entrySet()) {
			Field f = entry.getKey();
			Bean<?> b = entry.getValue();

			assertThat(f.getAnnotation(Named.class).value()).isEqualTo("a");
			assertThat(b.type.equals(NamedEmptyBeanA.class)).isTrue();
		}
	}

	@Test
	public void inspectComplexBean() throws BeanException {
		configuration.bind(InterfaceForNamedBeans.class, Arrays.asList(NamedEmptyBeanA.class, NamedEmptyBeanB.class, NamedSingleton.class));
		Bean<ComplexBean> bean = classInspector.inspect(ComplexBean.class);

		assertThat(bean).isNotNull();
		assertThat(bean.type).isEqualTo(ComplexBean.class);
		assertThat(bean.constructorParameters).hasSize(3);
		assertThat(bean.key).isEqualTo(ComplexBean.class.getCanonicalName());
		assertThat(bean.singleton).isFalse();

		assertThat(bean.fields).hasSize(3);

		for (Entry<Field, Bean<?>> entry : bean.fields.entrySet()) {
			Field f = entry.getKey();
			Bean<?> b = entry.getValue();

			if (f.getName().equals("beanA")) {
				assertThat(f.getAnnotation(Named.class).value()).isEqualTo("a");
				assertThat(b.type.equals(NamedEmptyBeanA.class)).isTrue();
			} else if (f.getName().equals("namedSingleton")) {
				assertThat(f.getAnnotation(Named.class).value()).isEqualTo("single");
				assertThat(b.type.equals(NamedSingleton.class)).isTrue();
			} else if (f.getName().equals("emptyBeanWithConstructor")) {
				assertThat(b.type.equals(EmptyBeanWithConstructor.class)).isTrue();
			} else {
				Assertions.fail("Unexpected field found: '" + f.getName());
			}
		}
	}

}
