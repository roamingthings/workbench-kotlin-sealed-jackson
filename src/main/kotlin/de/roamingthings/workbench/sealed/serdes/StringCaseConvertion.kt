package de.roamingthings.workbench.sealed.serdes

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
