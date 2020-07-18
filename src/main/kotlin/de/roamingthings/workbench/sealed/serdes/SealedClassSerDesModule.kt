package de.roamingthings.workbench.sealed.serdes

import com.fasterxml.jackson.databind.module.SimpleModule

private const val DETAILS_FIELD_NAME = "details"
private const val TYPE_FIELD_NAME = "@type"

class SealedClassSerDesModule(
    typeFiledName: String = TYPE_FIELD_NAME,
    detailsFieldName: String = DETAILS_FIELD_NAME
) : SimpleModule() {
    init {
        setSerializerModifier(SealedClassBeanSerializerModifier(typeFiledName, detailsFieldName))
        setDeserializerModifier(SealedClassBeanDeserializerModifier(typeFiledName, detailsFieldName))
    }
}
