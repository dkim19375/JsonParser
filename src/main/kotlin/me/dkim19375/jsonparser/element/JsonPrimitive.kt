package me.dkim19375.jsonparser.element

class JsonPrimitive(private val primitive: Any?) : JsonElement() {
    fun asString(): String = primitive as String

    fun asInt(): Int = primitive as? Int ?: (primitive as Number).toInt()

    fun asLong(): Long = primitive as? Long ?: (primitive as Number).toLong()

    fun asFloat(): Float = primitive as? Float ?: (primitive as Number).toFloat()

    fun asDouble(): Double = primitive as? Double ?: (primitive as Number).toDouble()

    fun asBoolean(): Boolean = primitive as? Boolean ?: primitive.toString().toBoolean()

    fun asNull(): Nothing? = null
}