package de.roamingthings.workbench.sealed.serdes

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

fun createObjectMapper(): ObjectMapper {
    val mapper = ObjectMapper()
    mapper.registerModule(KotlinModule())
    mapper.registerModule(JavaTimeModule())
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    mapper.registerModule(modellSerDesModule())
//    mapper.setSerializationInclusion(NON_NULL);
    return mapper
}

fun modellSerDesModule(): SimpleModule {
    val modellSerDesModule = SimpleModule()
    modellSerDesModule.setSerializerModifier(SealedClassBeanSerializerModifier())
    modellSerDesModule.setDeserializerModifier(SealedClassBeanDeserializerModifier())
    return modellSerDesModule
}
