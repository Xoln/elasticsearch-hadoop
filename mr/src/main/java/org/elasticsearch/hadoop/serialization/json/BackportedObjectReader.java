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
import org.elasticsearch.hadoop.thirdparty.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;

public class BackportedObjectReader implements ObjectReader {

    private final org.elasticsearch.hadoop.thirdparty.jackson.databind.ObjectReader reader;

    public static BackportedObjectReader create(ObjectMapper mapper, Class<?> type) {
        return new BackportedObjectReader(mapper.readerFor(type));
    }

    protected BackportedObjectReader(org.elasticsearch.hadoop.thirdparty.jackson.databind.ObjectReader reader) {
        this.reader = reader;
    }

    @Override
    public <T> Iterator<T> readValues(JsonParser jp) throws IOException {
        return reader.readValues(jp);
    }
}