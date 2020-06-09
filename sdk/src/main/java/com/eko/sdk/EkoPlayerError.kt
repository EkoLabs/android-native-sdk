package com.eko.sdk

class EkoPlayerError(var type: TYPE, message: String?) : Error(message) {
    enum class TYPE {
        REQUEST_ERROR,
        MALFORMED_RESPONSE,
        STATUS_CODE,
        INVALID_PROJECT
    }
}