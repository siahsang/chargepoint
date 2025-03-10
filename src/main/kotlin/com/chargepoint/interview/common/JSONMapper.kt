package com.chargepoint.interview.common

import com.chargepoint.interview.common.JSONMapper.Companion.JACKSON_MAPPER
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule



/**
 * Utility class for JSON mapping using Jackson.
 */
class JSONMapper {
    companion object {

        /**
         * The Jackson ObjectMapper configured for the application.
         */
        val JACKSON_MAPPER: ObjectMapper = ObjectMapper().let {
            it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            it.registerModules(KotlinModule.Builder().build())
        }

    }

}

/**
 * Extension function to convert any object to its JSON string representation.
 *
 * @return The JSON string representation of the object.
 */
fun Any.toJson(): String {
    return JACKSON_MAPPER.writeValueAsString(this)
}

/**
 * Extension function to parse a JSON string into an object of the specified type.
 *
 * @return The object parsed from the JSON string.
 */
inline fun <reified T> String.parseJson() = JACKSON_MAPPER.readValue(this, T::class.java)