package ru.command.mephi12.utils

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.slf4j.LoggerFactory
import java.math.BigInteger

abstract class JsonArrayConverter<T> : AttributeConverter<List<T>, String> {

    companion object {
        val objectMapper = ObjectMapper()
        val log = LoggerFactory.getLogger(JsonArrayConverter::class.java)
    }

    override fun convertToDatabaseColumn(attribute: List<T>?): String =
        try {
            objectMapper.writeValueAsString(attribute)
        } catch (e: Exception) {
            log.warn("Exception while converting $attribute into JSON")
            "[]"
        }

    override fun convertToEntityAttribute(dbData: String?): List<T> =
        try {
            objectMapper.readValue(dbData, List::class.java) as List<T>
        } catch (e: Exception) {
            log.warn("Exception while converting $dbData into entity param")
            listOf<T>()
        }
}

@Converter
class BigIntegerListConverter : JsonArrayConverter<BigInteger>()

@Converter
class BooleanListConverter : JsonArrayConverter<Boolean>()