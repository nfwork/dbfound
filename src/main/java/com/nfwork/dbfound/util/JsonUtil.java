package com.nfwork.dbfound.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.*;

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
		module.addSerializer(ResponseObject.class, new ResponseObjectDeserializer());
		module.addSerializer(Date.class, new DateDeserializer());
		module.addSerializer(Temporal.class, new TemporalDeserializer());
		module.addSerializer(Enum.class, new EnumDeserializer());

		objectMapper.registerModule(module);
		objectMapper.setDateFormat(new SimpleDateFormat(DBFoundConfig.getDateTimeFormat()));
		objectMapper.setTimeZone(TimeZone.getTimeZone("UTC+8"));
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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


	public static class ResponseObjectDeserializer extends JsonSerializer<ResponseObject> {
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
				jsonGenerator.writeObjectField(property,value);
			}
			jsonGenerator.writeEndObject();
		}
	}

	public static class DateDeserializer extends JsonSerializer<Date> {
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

	public static class TemporalDeserializer extends JsonSerializer<Temporal> {
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

	public static class EnumDeserializer extends JsonSerializer<Enum> {
		@Override
		public void serialize(Enum object, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
			EnumTypeHandler<Enum<?>> handler = EnumHandlerFactory.getEnumHandler(object.getClass());
			jsonGenerator.writeObject(handler.getEnumValue(object));
		}
	}

}
