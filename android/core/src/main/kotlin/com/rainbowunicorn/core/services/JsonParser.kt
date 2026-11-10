package com.rainbowunicorn.core.services

sealed class JsonElement {
    data class JsonObject(val fields: Map<String, JsonElement>) : JsonElement() {
        fun getString(key: String): String? = (fields[key] as? JsonString)?.value
        fun getInt(key: String): Int? = (fields[key] as? JsonNumber)?.value?.toInt()
        fun getDouble(key: String): Double? = (fields[key] as? JsonNumber)?.value
        fun getBoolean(key: String): Boolean? = (fields[key] as? JsonBoolean)?.value
        fun getArray(key: String): List<JsonElement>? = (fields[key] as? JsonArray)?.elements
        fun getObject(key: String): JsonObject? = fields[key] as? JsonObject
    }

    data class JsonArray(val elements: List<JsonElement>) : JsonElement()
    data class JsonString(val value: String) : JsonElement()
    data class JsonNumber(val value: Double) : JsonElement()
    data class JsonBoolean(val value: Boolean) : JsonElement()
    data object JsonNull : JsonElement()
}

class JsonParser(private val src: String) {
    private var pos = 0

    fun parse(): JsonElement {
        skipWhitespace()
        val result = parseValue()
        skipWhitespace()
        return result
    }

    private fun parseValue(): JsonElement {
        skipWhitespace()
        if (pos >= src.length) return JsonElement.JsonNull

        return when (val ch = src[pos]) {
            '{' -> parseObject()
            '[' -> parseArray()
            '"' -> parseString()
            't', 'f' -> parseBoolean()
            'n' -> parseNull()
            '-', in '0'..'9' -> parseNumber()
            else -> error("Unexpected character '$ch' at position $pos")
        }
    }

    private fun parseObject(): JsonElement.JsonObject {
        expect('{')
        val fields = mutableMapOf<String, JsonElement>()
        skipWhitespace()
        if (pos < src.length && src[pos] == '}') {
            pos++
            return JsonElement.JsonObject(fields)
        }

        while (pos < src.length) {
            skipWhitespace()
            val keyElement = parseString()
            val key = keyElement.value
            skipWhitespace()
            expect(':')
            skipWhitespace()
            val value = parseValue()
            fields[key] = value
            skipWhitespace()
            if (pos < src.length && src[pos] == ',') {
                pos++
            } else if (pos < src.length && src[pos] == '}') {
                pos++
                break
            } else {
                error("Expected ',' or '}' at position $pos")
            }
        }
        return JsonElement.JsonObject(fields)
    }

    private fun parseArray(): JsonElement.JsonArray {
        expect('[')
        val elements = mutableListOf<JsonElement>()
        skipWhitespace()
        if (pos < src.length && src[pos] == ']') {
            pos++
            return JsonElement.JsonArray(elements)
        }

        while (pos < src.length) {
            val item = parseValue()
            elements.add(item)
            skipWhitespace()
            if (pos < src.length && src[pos] == ',') {
                pos++
            } else if (pos < src.length && src[pos] == ']') {
                pos++
                break
            } else {
                error("Expected ',' or ']' at position $pos")
            }
        }
        return JsonElement.JsonArray(elements)
    }

    private fun parseString(): JsonElement.JsonString {
        expect('"')
        val sb = StringBuilder()
        while (pos < src.length) {
            val ch = src[pos++]
            if (ch == '"') {
                return JsonElement.JsonString(sb.toString())
            }
            if (ch == '\\') {
                if (pos >= src.length) break
                val esc = src[pos++]
                when (esc) {
                    '"' -> sb.append('"')
                    '\\' -> sb.append('\\')
                    '/' -> sb.append('/')
                    'b' -> sb.append('\b')
                    'f' -> sb.append('\u000c')
                    'n' -> sb.append('\n')
                    'r' -> sb.append('\r')
                    't' -> sb.append('\t')
                    'u' -> {
                        val hex = src.substring(pos, pos + 4)
                        pos += 4
                        sb.append(hex.toInt(16).toChar())
                    }
                    else -> sb.append(esc)
                }
            } else {
                sb.append(ch)
            }
        }
        return JsonElement.JsonString(sb.toString())
    }

    private fun parseNumber(): JsonElement.JsonNumber {
        val start = pos
        if (pos < src.length && src[pos] == '-') pos++
        while (pos < src.length && src[pos] in '0'..'9') pos++
        if (pos < src.length && src[pos] == '.') {
            pos++
            while (pos < src.length && src[pos] in '0'..'9') pos++
        }
        if (pos < src.length && (src[pos] == 'e' || src[pos] == 'E')) {
            pos++
            if (pos < src.length && (src[pos] == '+' || src[pos] == '-')) pos++
            while (pos < src.length && src[pos] in '0'..'9') pos++
        }
        val numStr = src.substring(start, pos)
        return JsonElement.JsonNumber(numStr.toDouble())
    }

    private fun parseBoolean(): JsonElement.JsonBoolean {
        return if (src.startsWith("true", pos)) {
            pos += 4
            JsonElement.JsonBoolean(true)
        } else if (src.startsWith("false", pos)) {
            pos += 5
            JsonElement.JsonBoolean(false)
        } else {
            error("Expected boolean at position $pos")
        }
    }

    private fun parseNull(): JsonElement.JsonNull {
        if (src.startsWith("null", pos)) {
            pos += 4
            return JsonElement.JsonNull
        }
        error("Expected null at position $pos")
    }

    private fun skipWhitespace() {
        while (pos < src.length && src[pos].isWhitespace()) {
            pos++
        }
    }

    private fun expect(ch: Char) {
        if (pos >= src.length || src[pos] != ch) {
            error("Expected '$ch' but found '${if (pos < src.length) src[pos] else "EOF"}' at position $pos")
        }
        pos++
    }

    companion object {
        fun parse(json: String): JsonElement = JsonParser(json).parse()
    }
}
