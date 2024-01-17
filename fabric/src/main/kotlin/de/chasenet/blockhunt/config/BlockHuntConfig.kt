package de.chasenet.blockhunt.config

import de.chasenet.blockhunt.getRegistryKey
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

data class BlockHuntConfig(
    val idBlacklist: List<Identifier>,
    val idBlacklistPatterns: List<String>,
    val clearInventory: Boolean,
    val starterKit: List<ItemStack>
) {
    internal constructor(configFile: BlockHuntConfigFile): this(
        configFile.idBlacklist.map(::Identifier),
        configFile.idBlacklistPatterns,
        configFile.clearInventory,
        configFile.starterKit.map(StarterItemConfig::toItemStack)
    )

    internal fun toConfigFile() = BlockHuntConfigFile(
        idBlacklist.map(Identifier::toString).sorted(),
        idBlacklistPatterns,
        clearInventory,
        starterKit.map(::StarterItemConfig)
    )

    companion object {
        lateinit var instance: BlockHuntConfig
            private set

        fun init() {
            instance = BlockHuntConfig(BlockHuntConfigFile.readFromFile())
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