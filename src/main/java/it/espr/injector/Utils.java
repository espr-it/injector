package it.espr.injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

public class Utils {

	public String key(String name, Class<?> clazz) {
		return (name == null ? "" : name + "-") + clazz.getCanonicalName();
	}

	public String key(String name, Object instance) {
		return key(name, instance.getClass());
	}

	public boolean isEmpty(String value) {
		return value == null || value.trim().equals("");
	}

	public String getAnnotationValue(Class<? extends Annotation> annotationClass, Annotation[] annotations) {
		String value = null;
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == annotationClass) {
				if (annotationClass == Named.class) {
					value = ((Named) annotation).value();
					break;
				}
			}
		}
		return isEmpty(value) ? null : value;
	}

	public static class Pair<First, Second> {
		public final First p1;
		public final Second p2;

		public Pair(First p1, Second p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
	}

	public boolean isPublic(Member member) {
		List<Integer> modifiers = Arrays.asList(member.getModifiers());
		for (Integer modifier : modifiers) {
			if (Modifier.isPublic(modifier)) {
				return true;
			}
		}
		return false;
	}
}
