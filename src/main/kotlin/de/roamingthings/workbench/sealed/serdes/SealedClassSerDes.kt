package de.roamingthings.workbench.sealed.serdes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.kotlin.isKotlinClass
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

const val DETAILS_FIELD_NAME = "details"
const val TYPE_FIELD_NAME = "@type"

class SealedClassDeserializer<T : Any>(private val sealedClass: Class<T>) : JsonDeserializer<T>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): T? {
        val node = parser.codec.readTree<JsonNode>(parser)
        val subclassTypeName = node.targetTypeValue()
        val subclass = findSealedSubclassBySimpleClassName(subclassTypeName.snakeToCamelCase())
        return when {
            subclass == null -> {
                throw JsonMappingException(parser, mappingExceptionMessageFor(subclassTypeName))
            }
            node.has(DETAILS_FIELD_NAME) -> {
                val detailNode = node.get(DETAILS_FIELD_NAME)
                parser.codec.treeToValue(detailNode, subclass)
            }
            else -> {
                throw JsonMappingException(parser, "Missing details")
            }
        }
    }

    private fun mappingExceptionMessageFor(simpleTypeName: String?): String {
        return if (simpleTypeName == null) {
            "Missing $TYPE_FIELD_NAME"
        } else {
            "Unknown $TYPE_FIELD_NAME $simpleTypeName"
        }
    }

    private fun JsonNode.targetTypeValue() = this.get(TYPE_FIELD_NAME)?.textValue()

    private fun findSealedSubclassBySimpleClassName(simpleName: String?): Class<out T>? {
        return if (sealedClass.isKotlinClass()) {
            sealedClass.kotlin.sealedSubclasses.firstOrNull {
                it.simpleName == simpleName
            }?.java
        } else {
            null
        }
    }
}

class SealedClassBeanSerializerModifier : BeanSerializerModifier() {
    override fun modifySerializer(
        config: SerializationConfig, beanDesc: BeanDescription, defaultSerializer: JsonSerializer<*>): JsonSerializer<*> {
        val sealedSuperclass = beanDesc.sealedBeanClass()

        return when {
            sealedSuperclass != null -> {
                SealedClassSerializer(defaultSerializer as JsonSerializer<Any>, sealedSuperclass.java)
            }
            else -> defaultSerializer
        }
    }
}

class SealedClassBeanDeserializerModifier : BeanDeserializerModifier() {
    override fun modifyDeserializer(config: DeserializationConfig?, beanDesc: BeanDescription?, deserializer: JsonDeserializer<*>?): JsonDeserializer<*> {
        return when (beanDesc?.isSealedClass()) {
            true -> {
                SealedClassDeserializer(beanDesc.beanClass)
            }
            else -> {
                super.modifyDeserializer(config, beanDesc, deserializer)
            }
        }
    }
}

class SealedClassSerializer<T : Any>(private val defaultSerializer: JsonSerializer<Any>, sealedClass: Class<T>)
    : StdSerializer<T>(sealedClass) {

    override fun serialize(value: T, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE_FIELD_NAME, value::class.java.simpleName.camelToSnakeCase())
        jgen.writeFieldName(DETAILS_FIELD_NAME)
        defaultSerializer.serialize(value, jgen, provider)
        jgen.writeEndObject()
    }
}

fun BeanDescription.sealedBeanClass(): KClass<*>? {
    val beanKClass = this.beanClass?.kotlin
    return when (beanKClass?.isSealed) {
        true -> {
            beanKClass
        }
        else -> {
            beanKClass?.superclasses?.find { clazz -> clazz.isSealed }
        }
    }
}

fun BeanDescription.isSealedClass(): Boolean = this?.beanClass?.kotlin?.isSealed == true

fun String.camelToSnakeCase(): String {
    var text = ""
    this.forEachIndexed { index, it ->
        if (it.isUpperCase() && index > 0) {
            text += "_"
        }
        text += it.toUpperCase()
    }
    return text
}

fun String?.snakeToCamelCase() = this?.toLowerCase()?.capitalize()
