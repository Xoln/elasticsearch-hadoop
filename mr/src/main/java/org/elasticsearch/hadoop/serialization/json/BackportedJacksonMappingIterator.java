/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization.json;

import org.elasticsearch.hadoop.thirdparty.jackson.core.JsonParser;
import org.elasticsearch.hadoop.thirdparty.jackson.core.JsonToken;
import org.elasticsearch.hadoop.thirdparty.jackson.databind.DeserializationContext;
import org.elasticsearch.hadoop.thirdparty.jackson.databind.JsonDeserializer;
import org.elasticsearch.hadoop.thirdparty.jackson.databind.JsonMappingException;
import org.elasticsearch.hadoop.thirdparty.jackson.databind.JavaType;

import java.io.IOException;
import java.util.Iterator;

class BackportedJacksonMappingIterator<T> implements Iterator<T> {

    protected final JavaType _type;
    protected final DeserializationContext _context;
    protected final JsonDeserializer<T> _deserializer;
    protected final JsonParser _parser;

    @SuppressWarnings("unchecked")
    protected BackportedJacksonMappingIterator(JavaType type, JsonParser jp, DeserializationContext ctxt, JsonDeserializer<?> deser) {
        _type = type;
        _parser = jp;
        _context = ctxt;
        _deserializer = (JsonDeserializer<T>) deser;

        if (jp != null && jp.getCurrentToken() == JsonToken.START_ARRAY) {
            if (!jp.getParsingContext().inRoot()) {
                jp.clearCurrentToken();
            }
        }
    }

    @Override
    public boolean hasNext() {
        try {
            return hasNextValue();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public T next() {
        try {
            return nextValue();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public boolean hasNextValue() throws IOException {
        if (_parser == null) {
            return false;
        }
        JsonToken t = _parser.getCurrentToken();
        if (t == null) {
            t = _parser.nextToken();
            if (t == null) {
                _parser.close();
                return false;
            }
            if (t == JsonToken.END_ARRAY) {
                return false;
            }
        }
        return true;
    }

    public T nextValue() throws IOException {
        T result = _deserializer.deserialize(_parser, _context);
        _parser.clearCurrentToken();
        return result;
    }
}