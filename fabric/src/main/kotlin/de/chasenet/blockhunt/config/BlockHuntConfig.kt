package de.chasenet.blockhunt.config

import de.chasenet.blockhunt.config.BlockhuntConfigValidation.parseIdentifier
import de.chasenet.blockhunt.config.BlockhuntConfigValidation.validateIdentifiers
import de.chasenet.blockhunt.getRegistryKey
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class InvalidBlockhuntConfigException(message: String, cause: Throwable? = null) : Exception(message, cause)

data class BlockHuntConfig(
    val idBlacklist: List<Identifier>,
    val idBlacklistPatterns: List<String>,
    val clearInventory: Boolean,
    val starterKit: List<ItemStack>
) {
    internal fun toConfigFile() = BlockHuntConfigFile(
        idBlacklist.map(Identifier::toString).sorted(),
        idBlacklistPatterns,
        clearInventory,
        starterKit.map(::StarterItemConfig)
    )

    companion object {
        lateinit var instance: BlockHuntConfig
            private set

        private fun of(configFile: BlockHuntConfigFile): BlockHuntConfig {
            val idBlacklist = configFile.idBlacklist
                .parseIdentifier { id -> "Cannot parse identifier $id in blacklist" }

            idBlacklist.forEach {
                Registries.BLOCK.getOrEmpty(it).orElseThrow {
                    throw InvalidBlockhuntConfigException("Can't find block for id ${it}")
                }
            }

            configFile.idBlacklist.validateIdentifiers(
                registry = Registries.BLOCK,
                identifierMalformedMessage = { id -> "Cannot parse identifier $id in blacklist" },
                identifierMissingMessage = { id -> "Can't find block for id $id" }
            )

            configFile.starterKit.forEach { item ->
                val itemId = parseIdentifier(item.id) { ("Cannot parse identifier ${item.id} in starterkit") }

                Registries.ITEM.getOrEmpty(itemId).orElseThrow {
                    throw InvalidBlockhuntConfigException("Can't find item for id ${item.id}")
                }

                item.enchantments.keys.toList().validateIdentifiers(
                    registry = Registries.ENCHANTMENT,
                    identifierMalformedMessage = { "Cannot parse enchantment id $it in startkit item ${item.id}" },
                    identifierMissingMessage = { "Can't find enchantment $it for id ${item.id}" }
                )
            }

            return BlockHuntConfig(
                idBlacklist,
                configFile.idBlacklistPatterns,
                configFile.clearInventory,
                configFile.starterKit.map(StarterItemConfig::toItemStack)
            )
        }

        fun init() {
            instance = of(BlockHuntConfigFile.readFromFile())
        }

        fun updateConfig(config: BlockHuntConfig) {
            instance = config
            config.toConfigFile().save()
        }

        fun blackListBlock(block: Block) {
            updateConfig(
                instance.let {
                    it.copy(idBlacklist = (it.idBlacklist + getRegistryKey(block)))
                }
            )
        }
    }
}