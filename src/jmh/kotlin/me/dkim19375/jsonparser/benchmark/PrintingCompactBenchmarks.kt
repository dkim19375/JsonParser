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

import me.dkim19375.jsonparser.state.PrintingState
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole

@State(Scope.Benchmark)
open class PrintingCompactBenchmarks {

    @Benchmark
    fun jsonParser(blackHole: Blackhole, printingState: PrintingState) {
        blackHole.consume(printingState.jsonPrinter.print(printingState.jsonElement))
    }

    @Benchmark
    fun gson(blackHole: Blackhole, printingState: PrintingState) {
        blackHole.consume(printingState.gsonInstance.toJson(printingState.gsonJsonElement))
    }

    @Benchmark
    fun jackson(blackHole: Blackhole, printingState: PrintingState) {
        blackHole.consume(printingState.jacksonObjectWriter.writeValueAsString(printingState.jacksonJsonNode))
    }

}