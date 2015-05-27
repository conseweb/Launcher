/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.fastjson;

import android.util.Log;

import com.JSONObjectSerialization.JSONInputStream;
import com.JSONObjectSerialization.JSONOutputStream;
import com.JSONObjectSerialization.JSONStreamException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;


/**
 * @author wenshao<szujobs@hotmail.com>
 */
public abstract class JSON implements JSONStreamAware, JSONAware {

    public static String DEFAULT_TYPE_KEY     = "@type";

    public static int    DEFAULT_PARSER_FEATURE;

    static {
//        DEFAULT_PARSER_FEATURE = (((((((0 | Feature.AutoCloseSource.getMask()) | Feature.InternFieldNames.getMask()) | Feature.UseBigDecimal.getMask()) | Feature.AllowUnQuotedFieldNames.getMask()) | Feature.AllowSingleQuotes.getMask()) | Feature.AllowArbitraryCommas.getMask()) | Feature.SortFeidFastMatch.getMask()) | Feature.IgnoreNotMatch.getMask();
//        DEFAULT_GENERATE_FEATURE = (((0 | SerializerFeature.QuoteFieldNames.getMask()) | SerializerFeature.SkipTransientField.getMask()) | SerializerFeature.WriteEnumUsingToString.getMask()) | SerializerFeature.SortField.getMask();
    }

    public static String DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static int    DEFAULT_GENERATE_FEATURE;

    // ======================

    public static final String toJSONString(Object object) {

        // Serializing it to JSON string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JSONOutputStream jos = new JSONOutputStream(baos);
        jos.writeObject(object);
        jos.close();

        // Get the string form the output stream and print it
        String jsonString = baos.toString();

        return jsonString;
    }

//    public void writeJSONString(Appendable out) throws IOException {
//        // Deserialize the concrete object from the JSON string
//        JSONInputStream jis = new JSONInputStream(jsonString);
//        SimpleClass sc2 = null;
//        try
//        {
//            sc2 = jis.readObject(out.class);
//        }
//        catch (JSONStreamException e)
//        {
//            Log.e("JSONObjectSerialization", "Failed to deserialize the object");
//            return;
//        }
//    }

}
