package de.roamingthings.workbench.sealed.serdes

import com.fasterxml.jackson.databind.BeanDescription
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

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

fun BeanDescription.isSealedClass(): Boolean = this.beanClass?.kotlin?.isSealed == true
