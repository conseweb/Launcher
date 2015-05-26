package com.alibaba.fastjson2.parser.deserializer;

import java.lang.reflect.Type;

import com.alibaba.fastjson2.parser.DefaultJSONParser;

public interface ObjectDeserializer {
    <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName);
    
    int getFastMatchToken();
}
