package de.chasenet.blockhunt.config

import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.InvalidIdentifierException

object BlockhuntConfigValidation {
    internal fun List<String>.parseIdentifier(message: (id: String) -> String) = map { parseIdentifier(it, message) }

    internal fun parseIdentifier(id: String, message: (id: String) -> String) =
        try {
            Identifier(id)
        } catch (e: InvalidIdentifierException) {
            throw InvalidBlockhuntConfigException(message(id), e)
        }

    internal fun <T> List<String>.validateIdentifiers(
        registry: Registry<T>,
        identifierMalformedMessage: (id: String) -> String,
        identifierMissingMessage: (id: String) -> String
    ) {
        parseIdentifier(identifierMalformedMessage).forEach {
            registry.getOrEmpty(it).orElseThrow {
                throw InvalidBlockhuntConfigException(identifierMissingMessage(it.toString()))
            }
        }
    }
}