package com.tao.realweb.util;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class MessageDesc extends JsonDeserializer<Error>{

	@Override
	public Error deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Map<String,String> properties = (Map)ctxt.getAttribute("properties");
		for(String p : properties.keySet()){
		}
		return null;
	}

}
