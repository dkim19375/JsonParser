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

package me.dkim19375.jsonparser.util

class StringCharIterator(private val string: String) : CharIterator() {

    private var index = 0

    override fun hasNext(): Boolean = index < string.length

    override fun nextChar(): Char = string[index++]

    fun skipWhitespaces() {
        while (peekOrNull()?.isWhitespace() == true) {
            index++
        }
    }

    fun nextCharOrNull(): Char? = if (hasNext()) nextChar() else null

    fun peek(): Char = string[index]

    fun peekOrNull(): Char? = if (hasNext()) peek() else null

    fun attemptGetText(text: String, fail: () -> Unit): Boolean {
        for (char in text) {
            if (nextCharOrNull() != char) {
                fail()
                return false
            }
        }
        return true
    }

    fun getRemainingText(): String = string.substring(index)
}