package org.tbe.jug.demo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReflectionUtils {

	public static <KEY, VALUE> Map<KEY, VALUE> createGenericMap(Class<KEY> keyClass, Class<VALUE> valueClass, Collection<VALUE> elements, String keyFieldName) {
		VALUE value = getFirstElement(elements);
		if (value != null) {
			MethodChain keyMethod = getKeyMethodByFieldName(value, keyFieldName);
			return createMapInternal(keyClass, valueClass, elements, keyMethod);
		}
		return new HashMap<KEY, VALUE>();
	}

	public static <KEY, VALUE> Map<KEY, List<VALUE>> createAccumulatedMap(Class<KEY> keyClass, Class<VALUE> valueClass, Collection<VALUE> elements,
			String keyFieldName) {
		VALUE value = getFirstElement(elements);
		if (value != null) {
			MethodChain keyMethod = getKeyMethodByFieldName(value, keyFieldName);
			return createAccumulatedMapInternal(keyClass, valueClass, elements, keyMethod);
		}
		return new HashMap<KEY, List<VALUE>>();
	}

	private static <VALUE> VALUE getFirstElement(Collection<VALUE> elements) {
		if (elements != null && elements.size() > 0) {
			return elements.iterator().next();
		}
		return null;
	}

	private static <VALUE> MethodChain getKeyMethodByFieldName(VALUE value, String fieldName) {
		String[] names = fieldName.split("\\.");
		if (names.length > 1) {
			return getKeyMethodInternal(value, names);
		} else {
			String casedName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			return getKeyMethodInternal(value, casedName);
		}
	}

	private static <VALUE> MethodChain getKeyMethodInternal(VALUE value, String casedName) {
		try {
			Method method = value.getClass().getMethod("get" + casedName) != null ? value.getClass().getMethod("get" + casedName) : value.getClass().getMethod(
					"is" + casedName) != null ? value.getClass().getMethod("is" + casedName) : null;
			if (method == null) {
				throw new IllegalArgumentException("No get method defined for field :" + casedName);
			}
			return new MethodChain(method);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Cannot auto-map - get method for request field not properly defined", e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Cannot auto-map - get method for request field not properly defined", e);
		}
	}

	private static <VALUE> MethodChain getKeyMethodInternal(VALUE value, String[] names) {
		try {
			MethodChain chain = new MethodChain();
			Object current = value;
			for (String name : names) {
				String casedName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
				Method method = current.getClass().getMethod("get" + casedName) != null ? current.getClass().getMethod("get" + casedName) : current.getClass()
						.getMethod("is" + casedName) != null ? current.getClass().getMethod("is" + casedName) : null;
				if (method == null) {
					throw new IllegalArgumentException("No get method defined for field :" + name);
				}
				chain.addMethod(method);
				current = chain.invokeChain(value);
			}
			return chain;
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Cannot auto-map - get method for request field not properly defined", e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Cannot auto-map - get method for request field not properly defined", e);
		}
	}

	private static <KEY, VALUE> Map<KEY, VALUE> createMapInternal(Class<KEY> keyClass, Class<VALUE> valueClass, Collection<VALUE> elements,
			MethodChain keyMethod) {
		Map<KEY, VALUE> result = new LinkedHashMap<KEY, VALUE>();
		if (elements.size() > 0) {
			for (VALUE value : elements) {
				@SuppressWarnings("unchecked")
				KEY key = (KEY) keyMethod.invokeChain(value);
				result.put(key, value);
			}
		}
		return result;
	}

	private static <KEY, VALUE> Map<KEY, List<VALUE>> createAccumulatedMapInternal(Class<KEY> keyClass, Class<VALUE> valueClass, Collection<VALUE> elements,
			MethodChain keyMethod) {
		Map<KEY, List<VALUE>> result = new LinkedHashMap<KEY, List<VALUE>>();
		if (elements.size() > 0) {
			for (VALUE value : elements) {
				@SuppressWarnings("unchecked")
				KEY key = (KEY) keyMethod.invokeChain(value);
				List<VALUE> accumulator = result.get(key);
				if (accumulator == null) {
					accumulator = new ArrayList<VALUE>();
					result.put(key, accumulator);
				}
				accumulator.add(value);
			}
		}
		return result;
	}

	private static String joinClauses(List<String> whereClauses) {
		if (whereClauses.size() == 0) {
			return " 1 = 1 ";// should never happen
		} else {
			final StringBuffer join = new StringBuffer();
			for (int i = 0; i < whereClauses.size(); i++) {
				if (i != 0) {
					join.append(" AND ");
				}
				join.append(whereClauses.get(i));
			}
			return join.toString();
		}
	}

}
