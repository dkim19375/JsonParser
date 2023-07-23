package me.dkim19375.jsonparser.error

class JsonIncorrectTypeException(message: String? = null, cause: Throwable? = null) : JsonParseException(
    message = message,
    cause = cause
)