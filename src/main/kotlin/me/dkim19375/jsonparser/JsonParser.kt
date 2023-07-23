package me.dkim19375.jsonparser

import me.dkim19375.jsonparser.element.JsonElement
import me.dkim19375.jsonparser.element.JsonList
import me.dkim19375.jsonparser.element.JsonObject
import me.dkim19375.jsonparser.element.JsonPrimitive
import me.dkim19375.jsonparser.error.JsonParseException
import me.dkim19375.jsonparser.util.StringCharIterator

object JsonParser {
    fun parse(text: String): JsonElement = parseValue(StringCharIterator(text))

    private fun parseValue(text: StringCharIterator): JsonElement {
        text.skipWhitespaces()
        val char = text.peek()
        if (char == '{') {
            return parseObject(text)
        }
        if (char == '[') {
            return parseList(text)
        }
        return parsePrimitive(text)
    }

    private fun parseObject(text: StringCharIterator): JsonObject {

    }

    private fun parseList(text: StringCharIterator): JsonList {

    }

    private fun parsePrimitive(text: StringCharIterator): JsonPrimitive {
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
        throw JsonParseException("Invalid json (attempted primitive)")
    }

    private fun parseNumber(text: StringCharIterator): Double {
        val builder = StringBuilder()
        var hasDecimal = false
        var first = true
        var leadingZero = false
        var isExponential = false
        test@ while (true) {
            val char = text.nextCharOrNull() ?: if (first) {
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
                    continue
                }
                if (char == '+') {
                    throw JsonParseException("A + can only be used in a number after E/e")
                }
                if (char == '-') {
                    if (wasFirst && !hasDecimal) {
                        builder.append('-')
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
                    continue
                }
                if (char !in '0'..'9') {
                    throw JsonParseException("Digits in a number must be between 0 and 9")
                }
                if (char == '0' && wasFirst && !hasDecimal) {
                    leadingZero = true
                    builder.append(char)
                    continue
                }
                if (wasLeadingZero) {
                    throw JsonParseException("Number cannot have leading zeros")
                }
                builder.append(char)
                continue
            }
            if (wasFirst) {
                if (char == '+' || char == '-') {
                    builder.append(char)
                    continue
                }
            }
            if (char !in '0'..'9') {
                throw JsonParseException("Digits in a number after E/e must be between 0 and 9")
            }
            builder.append(char)
        }
        return builder.toString().toDouble()
    }

    private fun parseString(text: StringCharIterator): String {
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