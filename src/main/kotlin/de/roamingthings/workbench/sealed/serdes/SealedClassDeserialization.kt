package de.roamingthings.workbench.sealed.serdes

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.module.kotlin.isKotlinClass

class SealedClassBeanDeserializerModifier(
    private val typeFiledName: String,
    private val detailsFieldName: String? = null
) : BeanDeserializerModifier() {
    override fun modifyDeserializer(config: DeserializationConfig?, beanDesc: BeanDescription?, deserializer: JsonDeserializer<*>?): JsonDeserializer<*> {
        return when (beanDesc?.isSealedClass()) {
            true -> {
                SealedClassDeserializer(typeFiledName, detailsFieldName, beanDesc.beanClass)
            }
            else -> {
                super.modifyDeserializer(config, beanDesc, deserializer)
            }
        }
    }
}

class SealedClassDeserializer<T : Any>(
    private val typeFiledName: String,
    private val detailsFieldName: String?,
    private val sealedClass: Class<T>
) : JsonDeserializer<T>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): T? {
        val node = parser.codec.readTree<JsonNode>(parser)
        val subclassTypeName = node.targetTypeValue()
        val subclass = findSealedSubclassBySimpleClassName(subclassTypeName.snakeToCamelCase())
        return when {
            subclass == null -> {
                throw JsonMappingException(parser, mappingExceptionMessageFor(subclassTypeName))
            }
            (detailsFieldName.isNullOrBlank()) -> {
                parser.codec.treeToValue(node, subclass)
            }
            (node.has(detailsFieldName)) -> {
                val detailNode = node.get(detailsFieldName)
                parser.codec.treeToValue(detailNode, subclass)
            }
            else -> {
                throw JsonMappingException(parser, "Missing details")
            }
        }
    }

    private fun mappingExceptionMessageFor(simpleTypeName: String?): String {
        return if (simpleTypeName == null) {
            "Missing $typeFiledName"
        } else {
            "Unknown $typeFiledName $simpleTypeName"
        }
    }

    private fun JsonNode.targetTypeValue() = this.get(typeFiledName)?.textValue()

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
