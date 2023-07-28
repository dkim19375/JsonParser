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

package me.dkim19375.jsonparser.benchmark

import com.fasterxml.jackson.databind.ObjectMapper
import me.dkim19375.jsonparser.JsonParser
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole

@State(Scope.Benchmark)
open class ParsingBenchmarks {
    private lateinit var jsonText: String
    private lateinit var jacksonObjectMapper: ObjectMapper

    @Setup(Level.Trial)
    fun setup() {
        jsonText = javaClass.classLoader.getResource("test.json").let { resourceURL ->
            resourceURL ?: throw IllegalStateException("Could not find test.json!")
            resourceURL.readText()
        }
        jacksonObjectMapper = ObjectMapper()
    }

    @Benchmark
    fun jsonParser(blackHole: Blackhole) {
        blackHole.consume(JsonParser.parse(jsonText))
    }

    @Benchmark
    fun gson(blackHole: Blackhole) {
        blackHole.consume(com.google.gson.JsonParser.parseString(jsonText))
    }

    @Benchmark
    fun jackson(blackHole: Blackhole) {
        blackHole.consume(jacksonObjectMapper.readTree(jsonText))
    }
}