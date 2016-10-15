package it.espr.injector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import javax.inject.Named;

public class Utils {

	public static String key(String name, Class<?> clazz) {
		return (isEmpty(name) ? "" : name + "-") + clazz.getCanonicalName();
	}

	public static String key(String name, Object instance) {
		return key(name, instance.getClass());
	}

	public static boolean isEmpty(String value) {
		return value == null || value.trim().equals("");
	}

	public static String getAnnotationValue(Class<? extends Annotation> annotationClass, Annotation[] annotations) {
		String value = null;
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (annotation.annotationType() == annotationClass) {
					if (annotationClass == Named.class) {
						value = ((Named) annotation).value();
						break;
					}
				}
			}
		}
		return isEmpty(value) ? null : value;
	}

	public static boolean isAbstract(Class<?> klass) {
		if (Modifier.isAbstract(klass.getModifiers())) {
			return true;
		}
		return false;
	}

	public static boolean isPublic(Member member) {
		int modifiers = member.getModifiers();
		if (Modifier.isPublic(modifiers)) {
			return true;
		}
		return false;
	}
}
