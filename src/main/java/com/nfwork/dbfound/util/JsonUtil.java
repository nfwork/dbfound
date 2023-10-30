package com.nfwork.dbfound.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.enums.EnumTypeHandler;
import com.nfwork.dbfound.model.reflector.Reflector;

public class JsonUtil{

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		SimpleModule module = new SimpleModule();
		module.addSerializer(ResponseObject.class, new ResponseObjectSerializer());
		module.addSerializer(Date.class, new DateSerializer());
		module.addSerializer(Temporal.class, new TemporalSerializer());
		module.addSerializer(Enum.class, new EnumSerializer());

		objectMapper.registerModule(module);
		objectMapper.setDateFormat(new SimpleDateFormat(DBFoundConfig.getDateTimeFormat()));
		objectMapper.setTimeZone(TimeZone.getTimeZone("UTC+8"));
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		if(DBFoundConfig.isCamelCaseToUnderscore()) {
			objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		}
	}

	public static String toJson(Object bean) {
		try {
			return objectMapper.writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			throw new DBFoundPackageException(e.getMessage(), e);
		}
	}

	public static List<?> jsonToList(String json) {
		try {
			return objectMapper.readValue(json, List.class);
		} catch (JsonProcessingException e) {
			throw new DBFoundPackageException(e.getMessage(), e);
		}
	}

	public static Map jsonToMap(String json) {
		try {
			return objectMapper.readValue(json, Map.class);
		} catch (JsonProcessingException e) {
			throw new DBFoundPackageException(e.getMessage(), e);
		}
	}

	public static ObjectMapper getObjectMapper(){
		return objectMapper;
	}


	public static class ResponseObjectSerializer extends JsonSerializer<ResponseObject> {
		@Override
		public void serialize(ResponseObject responseObject, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
			jsonGenerator.writeStartObject();
			Reflector reflector = Reflector.forClass(responseObject.getClass());
			String[] properties = reflector.getGetablePropertyNames();
			for (String property : properties){
				Object value = null;
				try {
					value = reflector.getGetInvoker(property).invoke(responseObject,null);
				} catch (Exception ignored) {
				}
				//jsonGenerator.writeObjectField(property,value);
				jsonGenerator.writeFieldName(property);
				writeObject(jsonGenerator, serializerProvider, value);
			}
			jsonGenerator.writeEndObject();
		}
	}

	public static class DateSerializer extends JsonSerializer<Date> {
		@Override
		public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
			if(date != null) {
				String value = LocalDateUtil.formatDate(date);
				jsonGenerator.writeString(value);
			}else{
				jsonGenerator.writeNull();
			}
		}
	}

	public static class TemporalSerializer extends JsonSerializer<Temporal> {
		@Override
		public void serialize(Temporal temporal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
			if(temporal != null) {
				String value = LocalDateUtil.formatTemporal(temporal);
				jsonGenerator.writeString(value);
			}else{
				jsonGenerator.writeNull();
			}
		}
	}

	public static class EnumSerializer extends JsonSerializer<Enum> {
		@Override
		public void serialize(Enum object, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
			EnumTypeHandler<Enum<?>> handler = EnumHandlerFactory.getEnumHandler(object.getClass());
			//jsonGenerator.writeObject(handler.getEnumValue(object));
			writeObject(jsonGenerator, serializerProvider, handler.getEnumValue(object));
		}
	}

	private static void writeObject(JsonGenerator jsonGenerator, SerializerProvider serializerProvider, Object value) throws IOException
	{
		if (value == null) {
			jsonGenerator.writeNull();
			return;
		}
		if (value instanceof String) {
			jsonGenerator.writeString((String) value);
			return;
		}
		if (value instanceof Number) {
			Number n = (Number) value;
			if (n instanceof Integer) {
				jsonGenerator.writeNumber(n.intValue());
				return;
			} else if (n instanceof Long) {
				jsonGenerator.writeNumber(n.longValue());
				return;
			} else if (n instanceof Double) {
				jsonGenerator.writeNumber(n.doubleValue());
				return;
			} else if (n instanceof Float) {
				jsonGenerator.writeNumber(n.floatValue());
				return;
			} else if (n instanceof Short) {
				jsonGenerator.writeNumber(n.shortValue());
				return;
			} else if (n instanceof Byte) {
				jsonGenerator.writeNumber(n.byteValue());
				return;
			} else if (n instanceof BigInteger) {
				jsonGenerator.writeNumber((BigInteger) n);
				return;
			} else if (n instanceof BigDecimal) {
				jsonGenerator.writeNumber((BigDecimal) n);
				return;
			} else if (n instanceof AtomicInteger) {
				jsonGenerator.writeNumber(((AtomicInteger) n).get());
				return;
			} else if (n instanceof AtomicLong) {
				jsonGenerator.writeNumber(((AtomicLong) n).get());
				return;
			}
		}
		if (value instanceof Boolean) {
			jsonGenerator.writeBoolean((Boolean) value);
			return;
		}

		JsonSerializer<Object> jsonSerializer = serializerProvider.findTypedValueSerializer(value.getClass(),true,null);
		jsonSerializer.serialize(value, jsonGenerator,serializerProvider);
	}

}
