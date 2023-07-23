/*
 * MIT License
 *
 * Copyright (c) 2023 dkim19375
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.dkim19375.jsonparser.element

class JsonPrimitive(private val primitive: Any?) : JsonElement() {
    fun asString(): String = primitive as String

    fun asInt(): Int = primitive as? Int ?: (primitive as Number).toInt()

    fun asLong(): Long = primitive as? Long ?: (primitive as Number).toLong()

    fun asFloat(): Float = primitive as? Float ?: (primitive as Number).toFloat()

    fun asDouble(): Double = primitive as? Double ?: (primitive as Number).toDouble()

    fun asBoolean(): Boolean = primitive as? Boolean ?: primitive.toString().toBoolean()

    fun asNull(): Nothing? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonPrimitive

        return primitive == other.primitive
    }

    override fun hashCode(): Int {
        return primitive?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "JsonPrimitive(primitive=${
            if (primitive is String) "\"$primitive\"" else primitive
        })"
    }
}