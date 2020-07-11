package de.roamingthings.workbench.sealed.serdes

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.util.NameTransformer
import de.roamingthings.workbench.sealed.Animal
import de.roamingthings.workbench.sealed.UnknownAnimal
import kotlin.reflect.KClass

fun findBySimpleClassName(simpleName: String): KClass<out Animal>? {
    return Animal::class.sealedSubclasses.firstOrNull {
        it.simpleName == simpleName
    }
}

class AnimalDeserializer : JsonDeserializer<Animal>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Animal {
        val node = parser.codec.readTree<JsonNode>(parser)
        return AnimalTypeOf(node)?.let { type ->
            findBySimpleClassName(type)?.let { targetClass ->
                parser.codec.treeToValue(node, targetClass.java)
            }
        } ?: UnknownAnimal
    }

    private fun AnimalTypeOf(node: JsonNode) = node.get("@type")?.textValue()?.toLowerCase()?.capitalize()
}

class AnimalBeanSerializerModifier : BeanSerializerModifier() {
    override fun modifySerializer(
        config: SerializationConfig, beanDesc: BeanDescription, serializer: JsonSerializer<*>): JsonSerializer<*> {
        return if (Animal::class.java.isAssignableFrom(beanDesc.beanClass)) {
            val unwrappingSerializer = serializer.unwrappingSerializer(NameTransformer.NOP)
            AnimalSerializer(unwrappingSerializer as JsonSerializer<Any>)
        } else serializer
    }
}

class AnimalSerializer(private val defaultSerializer: JsonSerializer<Any>) : StdSerializer<Animal>(Animal::class.java) {
    override fun serialize(value: Animal, jgen: JsonGenerator, provider: SerializerProvider) {
        jgen.writeStartObject();
        jgen.writeStringField("@type", value::class.java.simpleName.toSnakeCase())
        if (value !is UnknownAnimal) {
            defaultSerializer.serialize(value, jgen, provider)
        }
        jgen.writeEndObject()
    }
}

fun String.toSnakeCase(): String {
    var text: String = ""
    this.forEachIndexed{ index, it ->
        if (it.isUpperCase() && index > 0) {
            text += "_"
        }
        text += it.toUpperCase()
    }
    return text
}
