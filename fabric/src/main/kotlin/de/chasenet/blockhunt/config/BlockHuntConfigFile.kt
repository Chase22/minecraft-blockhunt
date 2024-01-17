package de.chasenet.blockhunt.config

import de.chasenet.Blockhunt
import de.chasenet.Blockhunt.logger
import de.chasenet.blockhunt.getRegistryKeyForEnchantment
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.enchantment.Enchantments
import java.nio.file.Path
import kotlin.io.path.*


@Serializable
internal data class BlockHuntConfigFile(
    val idBlacklist: List<String> = listOf(
        "minecraft:air",
        "minecraft:beacon",
        "minecraft:bedrock",
        "minecraft:cave_air",
        "minecraft:command_block",
        "minecraft:command_block_minecraft",
        "minecraft:chain_command_block",
        "minecraft:repeating_command_block",
        "minecraft:dirt_path",
        "minecraft:farmland",
        "minecraft:frogspawn",
        "minecraft:player_head",
        "minecraft:reinforced_deepslate",
        "minecraft:spawner",
        "minecraft:structure_void",
        "minecraft:void_air"
    ),
    val idBlacklistPatterns: List<String> = listOf("minecraft:infested_.+", "minecraft:potted_.+"),
    val clearInventory: Boolean = true,
    val starterKit: List<StarterItemConfig> = listOf(
        StarterItemConfig("minecraft:diamond_pickaxe", 1, mapOf(getRegistryKeyForEnchantment(Enchantments.SILK_TOUCH) to 1)),
        StarterItemConfig("minecraft:diamond_axe", 1),
        StarterItemConfig("minecraft:diamond_sword", 1),
        StarterItemConfig("minecraft:diamond_shovel", 1),
        StarterItemConfig("minecraft:bucket", 1),
    ),
) {
    @OptIn(ExperimentalSerializationApi::class)
    fun save() {
        try {
            json.encodeToStream(this, configFile.outputStream())
        } catch (e: Exception) {
            logger.error("Could not save config", e)
        }
    }

    companion object {
        private val configFile: Path = FabricLoader.getInstance().configDir.resolve("${Blockhunt.MODID}.json")
        private val json = Json {
            encodeDefaults = true
            prettyPrint = true
        }

        @OptIn(ExperimentalSerializationApi::class)
        internal fun readFromFile(): BlockHuntConfigFile {
            if (configFile.notExists()) {
                logger.info("No config file found, creating default one")
                return BlockHuntConfigFile().also(BlockHuntConfigFile::save)
            }
            logger.debug("Loaded config")
            return json.decodeFromStream(configFile.inputStream())
        }
    }
}