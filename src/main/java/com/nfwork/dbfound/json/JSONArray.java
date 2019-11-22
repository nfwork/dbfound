package com.nfwork.dbfound.json;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSONArray {

	public static JSONArray fromArray(Object[] array) {
		return new JSONArray(Arrays.asList(array));
	}

	public static JSONArray fromCollection(Collection collection) {
		return new JSONArray(collection);
	}

	public static JSONArray fromJSONTokener(JSONTokener tokener) {
		return new JSONArray(tokener);
	}

	public static JSONArray fromObject(Object object) {
		if (object instanceof Collection) {
			return fromCollection((Collection) object);
		} else if (object instanceof JSONTokener) {
			return fromJSONTokener((JSONTokener) object);
		} else if (object instanceof String) {
			return fromString((String) object);
		} else if (object != null && object.getClass().isArray()) {
			Class type = object.getClass().getComponentType();
			if (!type.isPrimitive()) {
				return fromArray((Object[]) object);
			} else {
				if (type == Boolean.TYPE) {
					return new JSONArray((boolean[]) object);
				} else if (type == Byte.TYPE) {
					return new JSONArray((byte[]) object);
				} else if (type == Short.TYPE) {
					return new JSONArray((short[]) object);
				} else if (type == Integer.TYPE) {
					return new JSONArray((int[]) object);
				} else if (type == Long.TYPE) {
					return new JSONArray((long[]) object);
				} else if (type == Float.TYPE) {
					return new JSONArray((float[]) object);
				} else if (type == Double.TYPE) {
					return new JSONArray((double[]) object);
				} else if (type == Character.TYPE) {
					return new JSONArray((char[]) object);
				} else {
					throw new IllegalArgumentException("Unsupported type");
				}
			}
		} else {
			throw new IllegalArgumentException("Unsupported type");
		}
	}

	public static JSONArray fromString(String string) {
		return new JSONArray(string);
	}

	public static Object[] toArray(JSONArray jsonArray) {
		return toArray(jsonArray, null);
	}

	public static Object[] toArray(JSONArray jsonArray, Class objectClass) {
		Object[] array = new Object[jsonArray.length()];
		int size = jsonArray.length();
		for (int i = 0; i < size; i++) {
			Object value = jsonArray.get(i);
			if (JSONUtils.isNull(value)) {
				array[i] = null;
			} else {
				Class type = value.getClass();
				if (JSONArray.class.isAssignableFrom(type)) {
					array[i] = toArray((JSONArray) value, objectClass);
				} else if (String.class.isAssignableFrom(type)
						|| Boolean.class.isAssignableFrom(type)
						|| Byte.class.isAssignableFrom(type)
						|| Short.class.isAssignableFrom(type)
						|| Integer.class.isAssignableFrom(type)
						|| Long.class.isAssignableFrom(type)
						|| Float.class.isAssignableFrom(type)
						|| Double.class.isAssignableFrom(type)
						|| Character.class.isAssignableFrom(type)) {
					array[i] = value;
				} else {
					if (objectClass != null) {
						array[i] = JSONObject.toBean((JSONObject) value,
								objectClass);
					} else {
						array[i] = JSONObject.toBean((JSONObject) value);
					}
				}
			}
		}
		return array;
	}

	/**
	 * Creates a List from a JSONArray.
	 */
	public static List toList(JSONArray jsonArray) {
		return toList(jsonArray, null);
	}

	/**
	 * Creates a List from a JSONArray.
	 */
	public static List toList(JSONArray jsonArray, Class objectClass) {
		List list = new ArrayList();
		int size = jsonArray.length();
		for (int i = 0; i < size; i++) {
			Object value = jsonArray.get(i);
			if (JSONUtils.isNull(value)) {
				list.add(null);
			} else {
				Class type = value.getClass();
				if (JSONArray.class.isAssignableFrom(type)) {
					list.add(toList((JSONArray) value, objectClass));
				} else if (String.class.isAssignableFrom(type)
						|| Boolean.class.isAssignableFrom(type)
						|| Byte.class.isAssignableFrom(type)
						|| Short.class.isAssignableFrom(type)
						|| Integer.class.isAssignableFrom(type)
						|| Long.class.isAssignableFrom(type)
						|| Float.class.isAssignableFrom(type)
						|| Double.class.isAssignableFrom(type)
						|| Character.class.isAssignableFrom(type)) {
					list.add(value);
				} else {
					if (objectClass != null) {
						list.add(JSONObject.toBean((JSONObject) value,
								objectClass));
					} else {
						list.add(JSONObject.toBean((JSONObject) value));
					}
				}
			}
		}
		return list;
	}

	// ------------------------------------------------------

	/**
	 * The arrayList where the JSONArray's properties are kept.
	 */
	private ArrayList myArrayList;

	/**
	 * Construct an empty JSONArray.
	 */
	public JSONArray() {
		this.myArrayList = new ArrayList();
	}

	/**
	 * Construct a JSONArray from an boolean[].<br>
	 * 
	 * @param array
	 *            An boolean[] array.
	 */
	public JSONArray(boolean[] array) {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(array));
	}

	/**
	 * Construct a JSONArray from an byte[].<br>
	 * 
	 * @param array
	 *            An byte[] array.
	 */
	public JSONArray(byte[] array) {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(array));
	}

	/**
	 * Construct a JSONArray from an char[].<br>
	 * 
	 * @param array
	 *            An char[] array.
	 */
	public JSONArray(char[] array) {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(JSONUtils.toObject(array)));
	}

	/**
	 * Construct a JSONArray from a Collection.
	 * 
	 * @param collection
	 *            A Collection.
	 */
	public JSONArray(Collection collection) {
		this.myArrayList = new ArrayList();
		if (collection != null) {
			for (Iterator elements = collection.iterator(); elements.hasNext();) {
				Object element = elements.next();
				if (JSONUtils.isArray(element)) {
					this.myArrayList.add(fromObject(element));
				} else if (JSONUtils.isObject(element)) {
					this.myArrayList.add(JSONObject.fromObject(element));
				} else {
					this.myArrayList.add(element);
				}
			}
		}
	}

	/**
	 * Construct a JSONArray from an double[].<br>
	 * 
	 * @param array
	 *            An double[] array.
	 */
	public JSONArray(double[] array) {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(array));
	}

	/**
	 * Construct a JSONArray from an float[].<br>
	 * 
	 * @param array
	 *            An float[] array.
	 */
	public JSONArray(float[] array) {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(array));
	}

	/**
	 * Construct a JSONArray from an int[].<br>
	 * 
	 * @param array
	 *            An int[] array.
	 */
	public JSONArray(int[] array) {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(array));
	}

	/**
	 * Construct a JSONArray from a JSONTokener.
	 * 
	 * @param x
	 *            A JSONTokener
	 * @throws JSONException
	 *             If there is a syntax error.
	 */
	public JSONArray(JSONTokener x) {
		this();
		if (x.nextClean() != '[') {
			throw x.syntaxError("A JSONArray text must start with '['");
		}
		if (x.nextClean() == ']') {
			return;
		}
		x.back();
		for (;;) {
			if (x.nextClean() == ',') {
				x.back();
				this.myArrayList.add(null);
			} else {
				x.back();
				Object v = x.nextValue();
				this.myArrayList.add(v);
			}
			switch (x.nextClean()) {
			case ';':
			case ',':
				if (x.nextClean() == ']') {
					return;
				}
				x.back();
				break;
			case ']':
				return;
			default:
				throw x.syntaxError("Expected a ',' or ']'");
			}
		}
	}

	/**
	 * Construct a JSONArray from an long[].<br>
	 * 
	 * @param array
	 *            An long[] array.
	 */
	public JSONArray(long[] array) {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(array));
	}

	/**
	 * Construct a JSONArray from an Object[].<br>
	 * Assumes the object hierarchy is acyclical.
	 * 
	 * @param array
	 *            An Object[] array.
	 */
	public JSONArray(Object[] array) {
		this.myArrayList = new ArrayList();
		for (int i = 0; i < array.length; i++) {
			Object element = array[i];
			if (JSONUtils.isArray(element)) {
				this.myArrayList.add(fromObject(element));
			} else if (JSONUtils.isObject(element)) {
				this.myArrayList.add(JSONObject.fromObject(element));
			} else {
				this.myArrayList.add(element);
			}
		}
	}

	/**
	 * Construct a JSONArray from an short[].<br>
	 * 
	 * @param array
	 *            An short[] array.
	 */
	public JSONArray(short[] array) {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(array));
	}

	/**
	 * Construct a JSONArray from a source JSON text.
	 * 
	 * @param string
	 *            A string that begins with <code>[</code>&nbsp;<small>(left
	 *            bracket)</small> and ends with <code>]</code>
	 *            &nbsp;<small>(right bracket)</small>.
	 * @throws JSONException
	 *             If there is a syntax error.
	 */
	public JSONArray(String string) {
		this(new JSONTokener(string));
	}

	/**
	 * Get the object value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return An object value.
	 * @throws JSONException
	 *             If there is no value for the index.
	 */
	public Object get(int index) {
		Object o = opt(index);
		if (o == null) {
			throw new JSONException("JSONArray[" + index + "] not found.");
		}
		return o;
	}

	/**
	 * Get the boolean value associated with an index. The string values "true"
	 * and "false" are converted to boolean.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The truth.
	 * @throws JSONException
	 *             If there is no value for the index or if the value is not
	 *             convertable to boolean.
	 */
	public boolean getBoolean(int index) {
		Object o = get(index);
		if (o.equals(Boolean.FALSE)
				|| (o instanceof String && ((String) o)
						.equalsIgnoreCase("false"))) {
			return false;
		} else if (o.equals(Boolean.TRUE)
				|| (o instanceof String && ((String) o)
						.equalsIgnoreCase("true"))) {
			return true;
		}
		throw new JSONException("JSONArray[" + index + "] is not a Boolean.");
	}

	/**
	 * Get the double value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 * @throws JSONException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number.
	 */
	public double getDouble(int index) {
		Object o = get(index);
		try {
			return o instanceof Number ? ((Number) o).doubleValue() : Double
					.parseDouble((String) o);
		} catch (Exception e) {
			throw new JSONException("JSONArray[" + index + "] is not a number.");
		}
	}

	/**
	 * Get the int value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 * @throws JSONException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number. if the value cannot be converted to a number.
	 */
	public int getInt(int index) {
		Object o = get(index);
		return o instanceof Number ? ((Number) o).intValue()
				: (int) getDouble(index);
	}

	/**
	 * Get the JSONArray associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return A JSONArray value.
	 * @throws JSONException
	 *             If there is no value for the index. or if the value is not a
	 *             JSONArray
	 */
	public JSONArray getJSONArray(int index) {
		Object o = get(index);
		if (o instanceof JSONArray) {
			return (JSONArray) o;
		}
		throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
	}

	/**
	 * Get the JSONObject associated with an index.
	 * 
	 * @param index
	 *            subscript
	 * @return A JSONObject value.
	 * @throws JSONException
	 *             If there is no value for the index or if the value is not a
	 *             JSONObject
	 */
	public JSONObject getJSONObject(int index) {
		Object o = get(index);
		if (o instanceof JSONObject) {
			return (JSONObject) o;
		}
		throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
	}

	/**
	 * Get the long value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 * @throws JSONException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number.
	 */
	public long getLong(int index) {
		Object o = get(index);
		return o instanceof Number ? ((Number) o).longValue()
				: (long) getDouble(index);
	}

	/**
	 * Get the string associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return A string value.
	 * @throws JSONException
	 *             If there is no value for the index.
	 */
	public String getString(int index) {
		return get(index).toString();
	}

	/**
	 * Make a string from the contents of this JSONArray. The
	 * <code>separator</code> string is inserted between each element. Warning:
	 * This method assumes that the data structure is acyclical.
	 * 
	 * @param separator
	 *            A string that will be inserted between the elements.
	 * @return a string.
	 * @throws JSONException
	 *             If the array contains an invalid number.
	 */
	public String join(String separator) {
		int len = length();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < len; i += 1) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(JSONUtils.valueToString(this.myArrayList.get(i)));
		}
		return sb.toString();
	}

	/**
	 * Get the number of elements in the JSONArray, included nulls.
	 * 
	 * @return The length (or size).
	 */
	public int length() {
		return this.myArrayList.size();
	}

	/**
	 * Get the optional object value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return An object value, or null if there is no object at that index.
	 */
	public Object opt(int index) {
		return (index < 0 || index >= length()) ? null : this.myArrayList
				.get(index);
	}

	/**
	 * Get the optional boolean value associated with an index. It returns false
	 * if there is no value at that index, or if the value is not Boolean.TRUE
	 * or the String "true".
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The truth.
	 */
	public boolean optBoolean(int index) {
		return optBoolean(index, false);
	}

	/**
	 * Get the optional boolean value associated with an index. It returns the
	 * defaultValue if there is no value at that index or if it is not a Boolean
	 * or the String "true" or "false" (case insensitive).
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @param defaultValue
	 *            A boolean default.
	 * @return The truth.
	 */
	public boolean optBoolean(int index, boolean defaultValue) {
		try {
			return getBoolean(index);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Get the optional double value associated with an index. NaN is returned
	 * if there is no value for the index, or if the value is not a number and
	 * cannot be converted to a number.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 */
	public double optDouble(int index) {
		return optDouble(index, Double.NaN);
	}

	/**
	 * Get the optional double value associated with an index. The defaultValue
	 * is returned if there is no value for the index, or if the value is not a
	 * number and cannot be converted to a number.
	 * 
	 * @param index
	 *            subscript
	 * @param defaultValue
	 *            The default value.
	 * @return The value.
	 */
	public double optDouble(int index, double defaultValue) {
		try {
			return getDouble(index);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Get the optional int value associated with an index. Zero is returned if
	 * there is no value for the index, or if the value is not a number and
	 * cannot be converted to a number.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 */
	public int optInt(int index) {
		return optInt(index, 0);
	}

	/**
	 * Get the optional int value associated with an index. The defaultValue is
	 * returned if there is no value for the index, or if the value is not a
	 * number and cannot be converted to a number.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @param defaultValue
	 *            The default value.
	 * @return The value.
	 */
	public int optInt(int index, int defaultValue) {
		try {
			return getInt(index);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Get the optional JSONArray associated with an index.
	 * 
	 * @param index
	 *            subscript
	 * @return A JSONArray value, or null if the index has no value, or if the
	 *         value is not a JSONArray.
	 */
	public JSONArray optJSONArray(int index) {
		Object o = opt(index);
		return o instanceof JSONArray ? (JSONArray) o : null;
	}

	/**
	 * Get the optional JSONObject associated with an index. Null is returned if
	 * the key is not found, or null if the index has no value, or if the value
	 * is not a JSONObject.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return A JSONObject value.
	 */
	public JSONObject optJSONObject(int index) {
		Object o = opt(index);
		return o instanceof JSONObject ? (JSONObject) o : null;
	}

	/**
	 * Get the optional long value associated with an index. Zero is returned if
	 * there is no value for the index, or if the value is not a number and
	 * cannot be converted to a number.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 */
	public long optLong(int index) {
		return optLong(index, 0);
	}

	/**
	 * Get the optional long value associated with an index. The defaultValue is
	 * returned if there is no value for the index, or if the value is not a
	 * number and cannot be converted to a number.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @param defaultValue
	 *            The default value.
	 * @return The value.
	 */
	public long optLong(int index, long defaultValue) {
		try {
			return getLong(index);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Get the optional string value associated with an index. It returns an
	 * empty string if there is no value at that index. If the value is not a
	 * string and is not null, then it is coverted to a string.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return A String value.
	 */
	public String optString(int index) {
		return optString(index, "");
	}

	/**
	 * Get the optional string associated with an index. The defaultValue is
	 * returned if the key is not found.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @param defaultValue
	 *            The default value.
	 * @return A String value.
	 */
	public String optString(int index, String defaultValue) {
		Object o = opt(index);
		return o != null ? o.toString() : defaultValue;
	}

	/**
	 * Append a boolean value. This increases the array's length by one.
	 * 
	 * @param value
	 *            A boolean value.
	 * @return this.
	 */
	public JSONArray put(boolean value) {
		put(value ? Boolean.TRUE : Boolean.FALSE);
		return this;
	}

	/**
	 * Put a value in the JSONArray, where the value will be a JSONArray which
	 * is produced from a Collection.
	 * 
	 * @param value
	 *            A Collection value.
	 * @return this.
	 */
	public JSONArray put(Collection value) {
		put(fromCollection(value));
		return this;
	}

	/**
	 * Append a double value. This increases the array's length by one.
	 * 
	 * @param value
	 *            A double value.
	 * @throws JSONException
	 *             if the value is not finite.
	 * @return this.
	 */
	public JSONArray put(double value) {
		Double d = new Double(value);
		JSONUtils.testValidity(d);
		put(d);
		return this;
	}

	/**
	 * Append an int value. This increases the array's length by one.
	 * 
	 * @param value
	 *            An int value.
	 * @return this.
	 */
	public JSONArray put(int value) {
		put(new Integer(value));
		return this;
	}

	/**
	 * Put or replace a boolean value in the JSONArray. If the index is greater
	 * than the length of the JSONArray, then null elements will be added as
	 * necessary to pad it out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            A boolean value.
	 * @return this.
	 * @throws JSONException
	 *             If the index is negative.
	 */
	public JSONArray put(int index, boolean value) {
		put(index, value ? Boolean.TRUE : Boolean.FALSE);
		return this;
	}

	/**
	 * Put a value in the JSONArray, where the value will be a JSONArray which
	 * is produced from a Collection.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            A Collection value.
	 * @return this.
	 * @throws JSONException
	 *             If the index is negative or if the value is not finite.
	 */
	public JSONArray put(int index, Collection value) {
		put(index, new JSONArray(value));
		return this;
	}

	/**
	 * Put or replace a double value. If the index is greater than the length of
	 * the JSONArray, then null elements will be added as necessary to pad it
	 * out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            A double value.
	 * @return this.
	 * @throws JSONException
	 *             If the index is negative or if the value is not finite.
	 */
	public JSONArray put(int index, double value) {
		put(index, new Double(value));
		return this;
	}

	/**
	 * Put or replace an int value. If the index is greater than the length of
	 * the JSONArray, then null elements will be added as necessary to pad it
	 * out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            An int value.
	 * @return this.
	 * @throws JSONException
	 *             If the index is negative.
	 */
	public JSONArray put(int index, int value) {
		put(index, new Integer(value));
		return this;
	}

	/**
	 * Put or replace a long value. If the index is greater than the length of
	 * the JSONArray, then null elements will be added as necessary to pad it
	 * out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            A long value.
	 * @return this.
	 * @throws JSONException
	 *             If the index is negative.
	 */
	public JSONArray put(int index, long value) {
		put(index, new Long(value));
		return this;
	}

	/**
	 * Put a value in the JSONArray, where the value will be a JSONObject which
	 * is produced from a Map.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            The Map value.
	 * @return this.
	 * @throws JSONException
	 *             If the index is negative or if the the value is an invalid
	 *             number.
	 */
	public JSONArray put(int index, Map value) {
		put(index, new JSONObject(value));
		return this;
	}

	/**
	 * Put or replace an object value in the JSONArray. If the index is greater
	 * than the length of the JSONArray, then null elements will be added as
	 * necessary to pad it out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            The value to put into the array. The value should be a
	 *            Boolean, Double, Integer, JSONArray, JSONObject, Long, or
	 *            String, or the JSONObject.NULL object.
	 * @return this.
	 * @throws JSONException
	 *             If the index is negative or if the the value is an invalid
	 *             number.
	 */
	public JSONArray put(int index, Object value) {
		JSONUtils.testValidity(value);
		if (index < 0) {
			throw new JSONException("JSONArray[" + index + "] not found.");
		}
		if (index < length()) {
			this.myArrayList.set(index, value);
		} else {
			while (index != length()) {
				put(JSONNull.getInstance());
			}
			put(value);
		}
		return this;
	}

	/**
	 * Append an long value. This increases the array's length by one.
	 * 
	 * @param value
	 *            A long value.
	 * @return this.
	 */
	public JSONArray put(long value) {
		put(new Long(value));
		return this;
	}

	/**
	 * Put a value in the JSONArray, where the value will be a JSONObject which
	 * is produced from a Map.
	 * 
	 * @param value
	 *            A Map value.
	 * @return this.
	 */
	public JSONArray put(Map value) {
		put(new JSONObject(value));
		return this;
	}

	/**
	 * Append an object value. This increases the array's length by one.
	 * 
	 * @param value
	 *            An object value. The value should be a Boolean, Double,
	 *            Integer, JSONArray, JSONObject, JSONFunction, Long, or String,
	 *            or the JSONObject.NULL object.
	 * @return this.
	 */
	public JSONArray put(Object value) {
		this.myArrayList.add(value);
		return this;
	}

	/**
	 * Produce an Object[] with the contents of this JSONArray.
	 */
	public Object[] toArray() {
		return this.myArrayList.toArray();
	}

	/**
	 * Produce a JSONObject by combining a JSONArray of names with the values of
	 * this JSONArray.
	 * 
	 * @param names
	 *            A JSONArray containing a list of key strings. These will be
	 *            paired with the values.
	 * @return A JSONObject, or null if there are no names or if this JSONArray
	 *         has no values.
	 * @throws JSONException
	 *             If any of the names are null.
	 */
	public JSONObject toJSONObject(JSONArray names) {
		if (names == null || names.length() == 0 || length() == 0) {
			return null;
		}
		JSONObject jo = new JSONObject();
		for (int i = 0; i < names.length(); i += 1) {
			jo.put(names.getString(i), this.opt(i));
		}
		return jo;
	}

	/**
	 * Make a JSON text of this JSONArray. For compactness, no unnecessary
	 * whitespace is added. If it is not possible to produce a syntactically
	 * correct JSON text then null will be returned instead. This could occur if
	 * the array contains an invalid number.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @return a printable, displayable, transmittable representation of the
	 *         array.
	 */
	public String toString() {
		try {
			return '[' + join(",") + ']';
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Make a prettyprinted JSON text of this JSONArray. Warning: This method
	 * assumes that the data structure is acyclical.
	 * 
	 * @param indentFactor
	 *            The number of spaces to add to each level of indentation.
	 * @return a printable, displayable, transmittable representation of the
	 *         object, beginning with <code>[</code>&nbsp;<small>(left
	 *         bracket)</small> and ending with <code>]</code>
	 *         &nbsp;<small>(right bracket)</small>.
	 * @throws JSONException
	 */
	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}

	/**
	 * Write the contents of the JSONArray as JSON text to a writer. For
	 * compactness, no whitespace is added.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 * 
	 * @return The writer.
	 * @throws JSONException
	 */
	public Writer write(Writer writer) {
		try {
			boolean b = false;
			int len = length();

			writer.write('[');

			for (int i = 0; i < len; i += 1) {
				if (b) {
					writer.write(',');
				}
				Object v = this.myArrayList.get(i);
				if (v instanceof JSONObject) {
					((JSONObject) v).write(writer);
				} else if (v instanceof JSONArray) {
					((JSONArray) v).write(writer);
				} else {
					writer.write(JSONUtils.valueToString(v));
				}
				b = true;
			}
			writer.write(']');
			return writer;
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	/**
	 * Make a prettyprinted JSON text of this JSONArray. Warning: This method
	 * assumes that the data structure is acyclical.
	 * 
	 * @param indentFactor
	 *            The number of spaces to add to each level of indentation.
	 * @param indent
	 *            The indention of the top level.
	 * @return a printable, displayable, transmittable representation of the
	 *         array.
	 * @throws JSONException
	 */
	String toString(int indentFactor, int indent) {
		int len = length();
		if (len == 0) {
			return "[]";
		}
		int i;
		StringBuffer sb = new StringBuffer("[");
		if (len == 1) {
			sb.append(JSONUtils.valueToString(this.myArrayList.get(0),
					indentFactor, indent));
		} else {
			int newindent = indent + indentFactor;
			sb.append('\n');
			for (i = 0; i < len; i += 1) {
				if (i > 0) {
					sb.append(",\n");
				}
				for (int j = 0; j < newindent; j += 1) {
					sb.append(' ');
				}
				sb.append(JSONUtils.valueToString(this.myArrayList.get(i),
						indentFactor, newindent));
			}
			sb.append('\n');
			for (i = 0; i < indent; i += 1) {
				sb.append(' ');
			}
		}
		sb.append(']');
		return sb.toString();
	}
}