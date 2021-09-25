package com.snapnoob.notes.network

import android.text.TextUtils
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.gson.Gson
import java.io.IOException
import java.lang.reflect.Type

object JsonUtil {
    var gson = Gson()

    private val objectMapper: ObjectMapper = ObjectMapper()

    init {
        // any null field in any class serialized through this mapper is going to be ignored:
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        // allow serialization of "empty" POJOs (no properties to serialize)
        // (without this setting, an exception is thrown in those cases)
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

        // prevent exception when encountering unknown property:
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        // allow coercion of JSON empty String ("") to null Object value:
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
    }

    fun toJson(value: Any?): String? {
        return try {
            if (value == null) {
                null
            } else objectMapper.writeValueAsString(value)
        } catch (iox: IOException) {
            throw RuntimeException("Failed convert object to json: $iox")
        }
    }

    fun <T> fromJson(content: String?, valueType: Class<T>?): T? {
        return try {
            if (TextUtils.isEmpty(content)) {
                null
            } else objectMapper.readValue(content, valueType)
        } catch (iox: IOException) {
            throw RuntimeException("Failed convert json to object: $iox")
        }
    }

    fun <T> gsonFromJson(content: String?, type: Class<T>?): T {
        return gson.fromJson(content, type)
    }

    fun <T> gsonFromJson(content: String?, type: Type?): T {
        return gson.fromJson(content, type)
    }

    fun gsonToJson(type: Any?): String? {
        return gson.toJson(type)
    }

    fun <T> fromJson(content: String?, typeReferenceValue: TypeReference<T>?): T? {
        return try {
            if (TextUtils.isEmpty(content)) {
                null
            } else objectMapper.readValue(content, typeReferenceValue)
        } catch (iox: IOException) {
            throw RuntimeException("Failed convert json to object: $iox")
        }
    }
}
