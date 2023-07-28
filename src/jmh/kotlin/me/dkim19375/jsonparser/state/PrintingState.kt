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

package me.dkim19375.jsonparser.state

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.dkim19375.jsonparser.JsonParser
import me.dkim19375.jsonparser.JsonPrinter
import me.dkim19375.jsonparser.element.JsonElement
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State

@State(Scope.Thread)
open class PrintingState {
    lateinit var jsonPrinter: JsonPrinter
    lateinit var jsonPrinterPretty: JsonPrinter
    lateinit var jsonElement: JsonElement

    lateinit var gsonInstance: Gson
    lateinit var gsonInstancePretty: Gson
    lateinit var gsonJsonElement: com.google.gson.JsonElement

    lateinit var jacksonObjectWriter: ObjectWriter
    lateinit var jacksonObjectWriterPretty: ObjectWriter
    lateinit var jacksonJsonNode: JsonNode

    @Setup(Level.Trial)
    fun setup() {
        val jsonText = javaClass.classLoader.getResource("test.json").let {
            it ?: throw IllegalStateException("Could not find test.json!")
            it.readText()
        }

        jsonPrinter = JsonPrinter()
        jsonPrinterPretty = JsonPrinter(prettyPrint = true)
        jsonElement = JsonParser.parse(jsonText)

        gsonInstance = GsonBuilder().enableComplexMapKeySerialization().create()
        gsonInstancePretty = GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create()
        gsonJsonElement = com.google.gson.JsonParser.parseString(jsonText)

        val jacksonObjectMapper = ObjectMapper()
        jacksonObjectWriter = jacksonObjectMapper.writer()
        jacksonObjectWriterPretty = jacksonObjectMapper.writerWithDefaultPrettyPrinter()
        jacksonJsonNode = jacksonObjectMapper.readTree(jsonText)
    }
}