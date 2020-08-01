package de.roamingthings.workbench.sealed.serdes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.util.NameTransformer.NOP

class SealedClassBeanSerializerModifier(
    private val typeFiledName: String,
    private val detailsFieldName: String? = null
) : BeanSerializerModifier() {
    override fun modifySerializer(
        config: SerializationConfig, beanDesc: BeanDescription, defaultSerializer: JsonSerializer<*>): JsonSerializer<*> {
        val sealedSuperclass = beanDesc.sealedBeanClass()

        return when {
            sealedSuperclass != null -> {
                SealedClassSerializer(
                    defaultSerializer as JsonSerializer<Any>,
                    typeFiledName,
                    detailsFieldName,
                    sealedSuperclass.java
                )
            }
            else -> defaultSerializer
        }
    }
}

class SealedClassSerializer<T : Any>(
    private val defaultSerializer: JsonSerializer<Any>,
    private val typeFiledName: String,
    private val detailsFieldName: String?,
    sealedClass: Class<T>
) : StdSerializer<T>(sealedClass) {

    private val unwrappingDefaultSerializer: JsonSerializer<Any> = defaultSerializer.unwrappingSerializer(NOP)

    override fun serialize(value: T, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeStartObject();
        jgen.writeStringField(typeFiledName, value::class.java.simpleName.camelToSnakeCase())
        if (detailsFieldName.isNullOrBlank()) {
            unwrappingDefaultSerializer.serialize(value, jgen, provider)
        } else {
            jgen.writeFieldName(detailsFieldName)
            defaultSerializer.serialize(value, jgen, provider)
        }
        jgen.writeEndObject()
    }
}
