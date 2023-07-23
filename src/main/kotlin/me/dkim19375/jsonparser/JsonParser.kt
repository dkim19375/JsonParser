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
import me.dkim19375.jsonparser.error.JsonParseException
import me.dkim19375.jsonparser.util.StringCharIterator

object JsonParser {
    fun parse(text: String): JsonElement {
        val iterator = StringCharIterator(text)
        val element = parseValue(iterator)
        iterator.skipWhitespaces()
        if (iterator.hasNext()) {
            throw JsonParseException("Json text has extra text: ${iterator.getRemainingText()}")
        }
        return element
    }

    private fun parseValue(text: StringCharIterator): JsonElement {
        text.skipWhitespaces()
        val char = text.peekOrNull() ?: throw JsonParseException("Json value is empty")
        return when (char) {
            '{' -> parseObject(text)
            '[' -> parseList(text)
            else -> parsePrimitive(text)
        }
    }

    private fun parseObject(text: StringCharIterator): JsonObject {
        text.skipWhitespaces()
        if (text.nextCharOrNull() != '{') {
            throw JsonParseException("Object must start with {")
        }
        var key = ""
        val map = hashMapOf<String, JsonElement>()
        var endsWithComma = false
        var endsWithValue = false
        var endsWithKey = false
        var endsWithColon = false
        while (true) {
            text.skipWhitespaces()
            val char = text.peekOrNull() ?: throw JsonParseException("Object incomplete")

            if (char == '}') {
                if (map.isNotEmpty()) {
                    if (!endsWithValue) {
                        throw JsonParseException("An object needs to end with a value or be empty")
                    }
                }
                text.nextChar()
                break
            }

            if (char == ',') {
                if (!endsWithValue) {
                    throw JsonParseException("An object can only have a comma after a value")
                }
                endsWithComma = true
                endsWithValue = false
                text.nextChar()
                continue
            }

            if (char == ':') {
                if (!endsWithKey) {
                    throw JsonParseException("An object can only have a colon after a key")
                }
                endsWithColon = true
                endsWithKey = false
                text.nextChar()
                continue
            }

            if (endsWithColon) {
                // parsing value
                map[key] = parseValue(text)
                endsWithColon = false
                endsWithValue = true
                continue
            }

            // parsing key
            if (map.isNotEmpty()) {
                if (!endsWithComma) {
                    throw JsonParseException("An object can only have a key after a comma")
                }
                endsWithComma = false
            }
            if (endsWithValue) {
                throw JsonParseException("An object needs to have a comma after each key-value pair")
            }
            if (endsWithKey) {
                throw JsonParseException("An object needs to have a colon between each key-value pair")
            }
            key = parseString(text)
            endsWithKey = true
        }
        return JsonObject(map)
    }

    private fun parseList(text: StringCharIterator): JsonList {
        text.skipWhitespaces()
        if (text.nextCharOrNull() != '[') {
            throw JsonParseException("List must start with [")
        }
        val list = arrayListOf<JsonElement>()
        var endsWithComma = false
        var endsWithValue = false
        while (true) {
            text.skipWhitespaces()
            val char = text.peekOrNull() ?: throw JsonParseException("List incomplete")
            if (char == ']') {
                if (endsWithComma) {
                    throw JsonParseException("Cannot end a list with a comma")
                }
                text.nextChar()
                break
            }
            if (char == ',') {
                if (endsWithComma) {
                    throw JsonParseException("Cannot have multiple commas in a row in a list")
                }
                endsWithComma = true
                endsWithValue = false
                text.nextChar()
                continue
            }
            if (endsWithValue) {
                throw JsonParseException("Needs a comma between each value in a list")
            }
            list.add(parseValue(text))
            endsWithComma = false
            endsWithValue = true
        }
        return JsonList(list)
    }

    private fun parsePrimitive(text: StringCharIterator): JsonPrimitive {
        text.skipWhitespaces()
        val char = text.peek()
        if (char == '"') {
            return JsonPrimitive(parseString(text))
        }
        if (char == '-' || char in '0'..'9') {
            return JsonPrimitive(parseNumber(text))
        }
        if (char == 't') {
            text.attemptGetText("true") { throw JsonParseException("Invalid json (attempted primitive)") }
            return JsonPrimitive(true)
        }
        if (char == 'f') {
            text.attemptGetText("false") { throw JsonParseException("Invalid json (attempted primitive)") }
            return JsonPrimitive(false)
        }
        if (char == 'n') {
            text.attemptGetText("null") { throw JsonParseException("Invalid json (attempted primitive)") }
            return JsonPrimitive(null)
        }
        throw JsonParseException("Invalid json (attempted primitive) - got ${text.getRemainingText()}")
    }

    private fun parseNumber(text: StringCharIterator): Double {
        text.skipWhitespaces()
        val builder = StringBuilder()
        var hasDecimal = false
        var first = true
        var leadingZero = false
        var isExponential = false
        test@ while (true) {
            val char = text.peekOrNull() ?: if (first) {
                throw JsonParseException("Number is incomplete")
            } else break@test

            // first: first character (after negative), right after decimal, right after E/e
            val wasFirst = first
            first = false

            val wasLeadingZero = leadingZero
            leadingZero = false
            if (!isExponential) {
                if (char == 'e' || char == 'E') {
                    if (wasFirst) {
                        if (hasDecimal) {
                            throw JsonParseException("Number cannot have e right after a decimal")
                        }
                        throw JsonParseException("Number cannot start with e")
                    }
                    isExponential = true
                    first = true
                    builder.append(char)
                    text.nextChar()
                    continue
                }
                if (char == '+') {
                    throw JsonParseException("A + can only be used in a number after E/e")
                }
                if (char == '-') {
                    if (wasFirst && !hasDecimal) {
                        builder.append('-')
                        text.nextChar()
                        continue
                    }
                    throw JsonParseException("A - can only be used in a number after E/e or before the number")
                }
                if (char == '.') {
                    if (hasDecimal) {
                        throw JsonParseException("Number cannot have more than one decimal")
                    }
                    if (wasFirst) {
                        throw JsonParseException("A digit must be before a decimal point in a number")
                    }
                    hasDecimal = true
                    first = true
                    builder.append(char)
                    text.nextChar()
                    continue
                }
                if (char !in '0'..'9') {
                    if (wasFirst) {
                        throw JsonParseException("Digits in a number must be between 0 and 9 - got $char")
                    }
                    break@test
                }
                if (char == '0' && wasFirst && !hasDecimal) {
                    leadingZero = true
                    builder.append(char)
                    text.nextChar()
                    continue
                }
                if (wasLeadingZero) {
                    throw JsonParseException("Number cannot have leading zeros")
                }
                builder.append(char)
                text.nextChar()
                continue
            }
            if (wasFirst) {
                if (char == '+' || char == '-') {
                    builder.append(char)
                    text.nextChar()
                    continue
                }
            }
            if (char !in '0'..'9') {
                if (wasFirst) {
                    throw JsonParseException("Digits in a number after E/e must be between 0 and 9 - got $char")
                }
                break@test
            }
            builder.append(char)
            text.nextChar()
        }
        return builder.toString().toDouble()
    }

    private fun parseString(text: StringCharIterator): String {
        text.skipWhitespaces()
        if (text.nextChar() != '"') {
            throw JsonParseException("String must start with \"")
        }
        val builder = StringBuilder()
        while (true) {
            if (!text.hasNext()) {
                throw JsonParseException("String incomplete")
            }
            when (val originalChar = text.nextChar()) {
                '"' -> break
                '\n' -> throw JsonParseException("String cannot contain newlines")
                '\\' -> {
                    when (text.nextChar()) {
                        '"' -> builder.append('"')
                        '\\' -> builder.append('\\')
                        '/' -> builder.append('/')
                        'b' -> builder.append('\b')
                        'f' -> builder.append('\u000C')
                        'n' -> builder.append('\n')
                        'r' -> builder.append('\r')
                        't' -> builder.append('\t')
                        'u' -> {
                            val hexChars = StringBuilder()
                            for (i in 0..<4) {
                                if (!text.hasNext()) {
                                    throw JsonParseException("Hexadecimal escaped code point must be 4 digits long")
                                }
                                val hexChar = text.nextChar()
                                if (hexChar in '0'..'9' || hexChar in 'a'..'f' || hexChar in 'A'..'F') {
                                    hexChars.append(hexChar)
                                } else {
                                    throw JsonParseException("Invalid hexadecimal value")
                                }
                            }
                            builder.append(Integer.parseInt(hexChars.toString(), 16).toChar())
                        }
                    }
                }

                else -> builder.append(originalChar)
            }
        }
        return builder.toString()
    }
}