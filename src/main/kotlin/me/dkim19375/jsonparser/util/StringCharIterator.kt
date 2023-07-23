package me.dkim19375.jsonparser.util

class StringCharIterator(string: String) : CharIterator() {
    private val chars = string.toCharArray()

    private var index = 0

    override fun hasNext(): Boolean = index < chars.size

    override fun nextChar(): Char = chars[index++]

    fun skipWhitespaces() {
        while (peek().isWhitespace()) {
            index++
        }
    }

    fun nextChar(skipWhitespace: Boolean): Char {
        if (!skipWhitespace) return nextChar()
        skipWhitespaces()
        return nextChar()
    }

    fun nextCharOrNull(): Char? = if (hasNext()) nextChar() else null

    fun peek(ignoreWhitespace: Boolean = false): Char = chars[index + 1]

    fun attemptGetText(text: String, fail: () -> Unit): Boolean {
        for (char in text) {
            if (nextCharOrNull() != char) {
                fail()
                return false
            }
        }
        return true
    }
}