package de.roamingthings.workbench.sealed.serdes

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.roamingthings.workbench.sealed.Animal

public fun createObjectMapper(): ObjectMapper {
    val mapper = ObjectMapper()
    mapper.registerModule(KotlinModule())
    mapper.registerModule(JavaTimeModule())
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    mapper.registerModule(modellSerDesModule())
    return mapper
}

fun modellSerDesModule(): SimpleModule {
    val modellSerDesModule = SimpleModule()
    modellSerDesModule.addDeserializer(Animal::class.java, AnimalDeserializer())
    modellSerDesModule.setSerializerModifier(AnimalBeanSerializerModifier())
    return modellSerDesModule
}
