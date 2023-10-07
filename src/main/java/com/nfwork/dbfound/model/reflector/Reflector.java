package com.nfwork.dbfound.model.reflector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.util.DataUtil;

/**
 * This class represents a cached set of class definition information that
 * allows for easy mapping between property names and getter/setter methods.
 */
public class Reflector {

	private final Class<?> type;
	private final String[] readablePropertyNames;
	private final String[] writeablePropertyNames;
	private final Map<String, Invoker> methods = new HashMap<>();
	private final Map<String, Invoker> setMethods = new HashMap<>();
	private final Map<String, Invoker> getMethods = new HashMap<>();
	private final Map<String, Class<?>> setTypes = new HashMap<>();
	private final Map<String, Class<?>> getTypes = new HashMap<>();
	private final Map<String, String> alias_name = new HashMap<>(); // 字段的别名-字段名

	private Reflector(Class<?> clazz) {
		type = clazz;
		Method[] methods = getClassMethods(clazz);
		for(Method method : methods){
			this.methods.put(method.getName(),new MethodInvoker(method));
		}
		addGetMethods(methods);
		addSetMethods(methods);
		addFields(clazz);
		int rSize = getMethods.size();
		readablePropertyNames = getMethods.keySet().toArray(new String[rSize]);
		int wSize = setMethods.size();
		writeablePropertyNames = setMethods.keySet().toArray(new String[wSize]);
	}

	private void addGetMethods(Method[] methods) {
		Map<String, List<Method>> conflictingGetters = new HashMap<>();

		for (Method method : methods) {
			String name = method.getName();
			if (name.startsWith("get") && name.length() > 3) {
				if (method.getParameterTypes().length == 0) {
					name = PropertyNamer.methodToProperty(name);
					addMethodConflict(conflictingGetters, name, method);
				}
			} else if (name.startsWith("is") && name.length() > 2) {
				if (method.getParameterTypes().length == 0) {
					name = PropertyNamer.methodToProperty(name);
					addMethodConflict(conflictingGetters, name, method);
				}
			}
		}
		resolveGetterConflicts(conflictingGetters);
	}

	private void resolveGetterConflicts(Map<String, List<Method>> conflictingGetters) {
		for (String propName : conflictingGetters.keySet()) {
			List<Method> getters = conflictingGetters.get(propName);
			Iterator<Method> iterator = getters.iterator();
			Method firstMethod = iterator.next();
			if (getters.size() == 1) {
				addGetMethod(propName, firstMethod);
			} else {
				Method getter = firstMethod;
				Class<?> getterType = firstMethod.getReturnType();
				while (iterator.hasNext()) {
					Method method = iterator.next();
					Class<?> methodType = method.getReturnType();
					if (methodType.equals(getterType)) {
						throw new ReflectionException(
								"Illegal overloaded getter method with ambiguous type for property " + propName
										+ " in class " + firstMethod.getDeclaringClass()
										+ ".  This breaks the JavaBeans "
										+ "specification and can cause unpredicatble results.");
					} else if (methodType.isAssignableFrom(getterType)) {
						// OK getter type is descendant
					} else if (getterType.isAssignableFrom(methodType)) {
						getter = method;
						getterType = methodType;
					} else {
						throw new ReflectionException(
								"Illegal overloaded getter method with ambiguous type for property " + propName
										+ " in class " + firstMethod.getDeclaringClass()
										+ ".  This breaks the JavaBeans "
										+ "specification and can cause unpredicatble results.");
					}
				}
				addGetMethod(propName, getter);
			}
		}
	}

	private void addGetMethod(String name, Method method) {
		if (isValidPropertyName(name)) {
			getMethods.put(name, new MethodInvoker(method));
			getTypes.put(name, method.getReturnType());
		}
	}

	private void addSetMethods(Method[] methods) {
		Map<String, List<Method>> conflictingSetters = new HashMap<>();
		for (Method method : methods) {
			String name = method.getName();
			if (name.startsWith("set") && name.length() > 3) {
				if (method.getParameterTypes().length == 1) {
					name = PropertyNamer.methodToProperty(name);
					addMethodConflict(conflictingSetters, name, method);
				}
			}
		}
		resolveSetterConflicts(conflictingSetters);
	}

	private void addMethodConflict(Map<String, List<Method>> conflictingMethods, String name, Method method) {
		List<Method> list = conflictingMethods.get(name);
		if (list == null) {
			list = new ArrayList<>();
			conflictingMethods.put(name, list);
		}
		list.add(method);
	}

	private void resolveSetterConflicts(Map<String, List<Method>> conflictingSetters) {
		for (String propName : conflictingSetters.keySet()) {
			List<Method> setters = conflictingSetters.get(propName);
			Method firstMethod = setters.get(0);
			if (setters.size() == 1) {
				addSetMethod(propName, firstMethod);
			} else {
				Class<?> expectedType = getTypes.get(propName);
				if (expectedType == null) {
					throw new ReflectionException("Illegal overloaded setter method with ambiguous type for property "
							+ propName + " in class " + firstMethod.getDeclaringClass()
							+ ".  This breaks the JavaBeans " + "specification and can cause unpredicatble results.");
				} else {
					Iterator<Method> methods = setters.iterator();
					Method setter = null;
					while (methods.hasNext()) {
						Method method = methods.next();
						if (method.getParameterTypes().length == 1
								&& expectedType.equals(method.getParameterTypes()[0])) {
							setter = method;
							break;
						}
					}
					if (setter == null) {
						throw new ReflectionException(
								"Illegal overloaded setter method with ambiguous type for property " + propName
										+ " in class " + firstMethod.getDeclaringClass()
										+ ".  This breaks the JavaBeans "
										+ "specification and can cause unpredicatble results.");
					}
					addSetMethod(propName, setter);
				}
			}
		}
	}

	private void addSetMethod(String name, Method method) {
		if (isValidPropertyName(name)) {
			setMethods.put(name, new MethodInvoker(method));
			setTypes.put(name, method.getParameterTypes()[0]);
		}
	}

	private void addFields(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isPublic(field.getModifiers())) {
				if (!setMethods.containsKey(field.getName())) {
					// issue 379 - removed the check for final because JDK 1.5
					// allows
					// modification of final fields through reflection
					// (JSR-133). (JGB)
					addSetField(field);
				}
				if (!getMethods.containsKey(field.getName())) {
					addGetField(field);
				}
			}
			// 处理g3db_alias注解
			Column alias = field.getAnnotation(Column.class);
			if (alias != null) {
				alias_name.put(alias.name(), field.getName());
			}
		}
		if (clazz.getSuperclass() != null) {
			addFields(clazz.getSuperclass());
		}
	}

	private void addSetField(Field field) {
		if (isValidPropertyName(field.getName())) {
			setMethods.put(field.getName(), new SetFieldInvoker(field));
			setTypes.put(field.getName(), field.getType());
		}
	}

	private void addGetField(Field field) {
		if (isValidPropertyName(field.getName())) {
			getMethods.put(field.getName(), new GetFieldInvoker(field));
			getTypes.put(field.getName(), field.getType());
		}
	}

	private boolean isValidPropertyName(String name) {
		return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
	}

	/**
	 * This method returns an array containing all methods declared in this
	 * class and any superclass. We use this method, instead of the simpler
	 * Class.getMethods(), because we want to look for private methods as well.
	 * 
	 * @param cls The class
	 * 
	 * @return An array containing all methods in this class
	 */
	private Method[] getClassMethods(Class<?> cls) {
		HashMap<String, Method> uniqueMethods = new HashMap<>();
		Class<?> currentClass = cls;
		while (currentClass != null && currentClass != Object.class) {
			addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());

			// we also need to look for interface methods -
			// because the class may be abstract
			Class<?>[] interfaces = currentClass.getInterfaces();
			for (Class<?> anInterface : interfaces) {
				addUniqueMethods(uniqueMethods, anInterface.getMethods());
			}

			currentClass = currentClass.getSuperclass();
		}

		Collection<Method> methods = uniqueMethods.values();

		int size = methods.size();
		return methods.toArray(new Method[size]);
	}

	private void addUniqueMethods(HashMap<String, Method> uniqueMethods, Method[] methods) {
		for (Method currentMethod : methods) {
			if (!currentMethod.isBridge()) {
				String signature = getSignature(currentMethod);
				if (!uniqueMethods.containsKey(signature)) {
					if (Modifier.isPublic(currentMethod.getModifiers())) {
						uniqueMethods.put(signature, currentMethod);
					}
				}
			}
		}
	}

	private String getSignature(Method method) {
		StringBuilder sb = new StringBuilder();
		Class<?> returnType = method.getReturnType();
		sb.append(returnType.getName()).append('#');
		sb.append(method.getName());
		Class<?>[] parameters = method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			if (i == 0) {
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(parameters[i].getName());
		}
		return sb.toString();
	}

	/**
	 * Gets the name of the class the instance provides information for
	 * 
	 * @return The class name
	 */
	public Class<?> getType() {
		return type;
	}

	public Invoker getSetInvoker(String propertyName) {
		Invoker method = setMethods.get(propertyName);
		if (method == null) {
			throw new ReflectionException("There is no setter or setter is not public for property named '" + propertyName + "' in '" + type
					+ "'");
		}
		return method;
	}

	public Invoker getGetInvoker(String propertyName) {
		Invoker method = getMethods.get(propertyName);
		if (method == null) {
			throw new ReflectionException("There is no getter or getter is not public for property named '" + propertyName + "' in '" + type
					+ "'");
		}
		return method;
	}

	/**
	 * Gets the type for a property setter
	 * 
	 * @param propertyName - the name of the property
	 * 
	 * @return The Class of the propery setter
	 */
	public Class<?> getSetterType(String propertyName) {
		Class<?> clazz = setTypes.get(propertyName);
		if (clazz == null) {
			throw new ReflectionException("There is no setter or setter is not public for property named '" + propertyName + "' in '" + type
					+ "'");
		}
		return clazz;
	}

	/**
	 * Gets the type for a property getter
	 * 
	 * @param propertyName - the name of the property
	 * 
	 * @return The Class of the propery getter
	 */
	public Class<?> getGetterType(String propertyName) {
		Class<?> clazz = getTypes.get(propertyName);
		if (clazz == null) {
			throw new ReflectionException("There is no getter or getter is not public for property named '" + propertyName + "' in '" + type
					+ "'");
		}
		return clazz;
	}

	/**
	 * Gets an array of the readable properties for an object
	 *
	 * @return The array
	 */
	public String[] getGetablePropertyNames() {
		return readablePropertyNames;
	}
	/**
	 * Gets an array of the writeable properties for an object
	 *
	 * @return The array
	 */
	public String[] getSetablePropertyNames() {
		return writeablePropertyNames;
	}

	/**
	 * Check to see if a class has a writeable property by name
	 * 
	 * @param propertyName - the name of the property to check
	 * 
	 * @return True if the object has a writeable property by the name
	 */
	public boolean hasSetter(String propertyName) {
		return setMethods.containsKey(propertyName);
	}

	/**
	 * Check to see if a class has a readable property by name
	 * 
	 * @param propertyName - the name of the property to check
	 * 
	 * @return True if the object has a readable property by the name
	 */
	public boolean hasGetter(String propertyName) {
		return getMethods.containsKey(propertyName);
	}

	private static final ConcurrentMap<Class<?>, Future<Reflector>> REFLECTOR_MAP = new ConcurrentHashMap<>();

	/**
	 * Gets an instance of ClassInfo for the specified class.
	 *
	 * @param clazz The class for which to lookup the method cache.
	 *
	 * @return The method cache for the class
	 */
	public static Reflector forClass(final Class<?> clazz) {

		Future<Reflector> future = REFLECTOR_MAP.get(clazz);
		if (future == null) {
			Callable<Reflector> callable = () -> new Reflector(clazz);
			FutureTask<Reflector> task = new FutureTask<>(callable);

			future = REFLECTOR_MAP.putIfAbsent(clazz, task);
			if (future == null) {
				future = task;
				task.run();
			}
		}

		try {
			return future.get();
		} catch (Exception e) {
			REFLECTOR_MAP.remove(clazz);
			throw new DBFoundPackageException(e);
		}
	}

	public String getFieldName(String column) {
		String name = alias_name.get(column);
		if (name != null) {
			return name;
		} else {
			return column;
		}
	}

	public Invoker getMethodInvoker(String methodName){
		Invoker method = methods.get(methodName);
		if (method == null) {
			throw new ReflectionException("There is no method is not public named '" + methodName + "' in '" + type
					+ "'");
		}
		return method;
	}

	public void setProperty(Object target, String name, Object value) throws InvocationTargetException, IllegalAccessException {
		Invoker invoker = this.getSetInvoker(name);
		Class<?> clazz = invoker.getType();

		if(value != null) {
			if (clazz == String.class) {
				value = value.toString();
			} if (clazz == int.class || clazz == Integer.class) {
				if("".equals(value)){
					value = null;
				}else {
					value = DataUtil.intValue(value);
				}
			} else if (clazz == long.class || clazz == Long.class) {
				if("".equals(value)){
					value = null;
				}else {
					value = DataUtil.longValue(value);
				}
			} else if (clazz == double.class || clazz == Double.class) {
				if("".equals(value)){
					value = null;
				}else {
					value = DataUtil.doubleValue(value);
				}
			} else if (clazz == float.class || clazz == Float.class) {
				if("".equals(value)){
					value = null;
				}else {
					value = DataUtil.floatValue(value);
				}
			} else if (clazz == boolean.class || clazz == Boolean.class) {
				if("".equals(value)){
					value = null;
				}else {
					value = Boolean.valueOf(value.toString());
				}
			}
		}

		invoker.invoke(target, new Object[] {value});
	}
}
