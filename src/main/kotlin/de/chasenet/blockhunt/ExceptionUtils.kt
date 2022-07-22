package de.chasenet.blockhunt

fun throws(block: () -> Unit): Boolean {
    return try {
        block()
        false
    } catch (e: Exception) {
        true
    }
}