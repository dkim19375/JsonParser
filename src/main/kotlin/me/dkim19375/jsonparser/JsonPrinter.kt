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

package me.dkim19375.jsonparser

import me.dkim19375.jsonparser.element.JsonElement
import me.dkim19375.jsonparser.element.JsonList
import me.dkim19375.jsonparser.element.JsonObject
import me.dkim19375.jsonparser.element.JsonPrimitive
import me.dkim19375.jsonparser.error.JsonPrintException

class JsonPrinter(private val prettyPrint: Boolean = false, indentSpaceAmount: Int = 2) {

    private val baseIndent = " ".repeat(indentSpaceAmount)

    fun print(jsonElement: JsonElement): String = printElement(jsonElement)

    private fun indent(indentLevel: Int): String = baseIndent.repeat(indentLevel)

    private fun printElement(element: JsonElement, indentLevel: Int = 0): String = when (element) {
        is JsonObject -> printObject(element, indentLevel)
        is JsonList -> printList(element, indentLevel)
        is JsonPrimitive -> printPrimitive(element)
        else -> throw JsonPrintException(
            "Unknown JSON element type: ${
                element::class.qualifiedName ?: element::class.java.name
            }"
        )
    }

    private fun printObject(element: JsonObject, indentLevel: Int = 0): String {
        element.elements.ifEmpty { return@printObject "{}" }
        val iterator = element.elements.iterator()
        val builder = StringBuilder("{")
        fun addToBuilder(key: String, value: JsonElement) {
            if (prettyPrint) {
                builder.append('\n').append(indent(indentLevel + 1))
            }
            builder.append('"').append(key).append('"').append(":")
            if (prettyPrint) {
                builder.append(' ')
            }
            builder.append(printElement(value, indentLevel + 1))
        }
        iterator.next().let { (key, value) ->
            addToBuilder(key, value)
        }
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            builder.append(",")
            addToBuilder(key, value)
        }
        if (prettyPrint) {
            builder.append('\n').append(indent(indentLevel))
        }
        return builder.append("}").toString()
    }

    private fun printList(element: JsonList, indentLevel: Int = 0): String {
        element.elements.ifEmpty { return@printList "[]" }
        val iterator = element.elements.iterator()
        val builder = StringBuilder("[")
        if (prettyPrint) {
            builder.append('\n').append(indent(indentLevel + 1))
        }
        builder.append(printElement(iterator.next(), indentLevel + 1))
        while (iterator.hasNext()) {
            builder.append(",")
            if (prettyPrint) {
                builder.append('\n').append(indent(indentLevel + 1))
            }
            builder.append(printElement(iterator.next(), indentLevel + 1))
        }
        return if (prettyPrint) {
            builder.append('\n').append(indent(indentLevel))
        } else {
            builder
        }.append("]").toString()
    }

    private fun printPrimitive(element: JsonPrimitive): String {
        if (element.isString()) {
            return "\"${printString(element.asString())}\""
        }
        if (element.isNumber()) {
            val number = element.asDouble()
            if (number % 1.0 == 0.0) {
                return element.asLong().toString()
            }
            return element.getValue().toString()
        }
        return element.getValue().toString()
    }

    private fun printString(text: String): String {
        val newString = StringBuilder()
        for (char in text) {
            when (char) {
                '"' -> newString.append("\\\"")
                '\\' -> newString.append("\\\\")
                '/' -> newString.append("\\/")
                '\b' -> newString.append("\\b")
                '\u000C' -> newString.append("\\f")
                '\n' -> newString.append("\\n")
                '\r' -> newString.append("\\r")
                '\t' -> newString.append("\\t")
                else -> newString.append(char)
            }
        }
        return newString.toString()
    }
}