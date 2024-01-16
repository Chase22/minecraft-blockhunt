package de.chasenet.blockhunt

import de.chasenet.Blockhunt
import de.chasenet.Blockhunt.logger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path
import kotlin.io.path.*

@Serializable
data class BlockHuntConfig(
    val idBlacklist: List<String> = listOf(
        "minecraft:air",
        "minecraft:beacon",
        "minecraft:bedrock",
        "minecraft:cave_air",
        "minecraft:command_block",
        "minecraft:farmland",
        "minecraft:frogspawn",
        "minecraft:player_head",
        "minecraft:reinforced_deepslate",
        "minecraft:spawner",
        "minecraft:structure_void",
        "minecraft:void_air"
    ),
    val idBlacklistPatterns: List<String> = listOf("minecraft:infested_.+", "minecraft:potted_.+", "minecraft:.+_ore"),
    val starterKit: List<String> = emptyList(),
    val clearInventory: Boolean = false
) {
    companion object {
        private val configFile: Path = FabricLoader.getInstance().configDir.resolve("${Blockhunt.MODID}.json")
        private val json = Json {
            encodeDefaults = true
            prettyPrint = true
        }

        lateinit var instance: BlockHuntConfig
            private set

        fun init() {
            instance = readFromFile()
        }

        fun updateConfig(config: BlockHuntConfig) {
            instance = config
            saveToFile(config)
        }

        @OptIn(ExperimentalSerializationApi::class)
        private fun readFromFile(): BlockHuntConfig {
            if (configFile.notExists()) {
                logger.info("No config file found, creating default one")
                return BlockHuntConfig().also(::saveToFile)
            }
            logger.debug("Loaded config")
            return json.decodeFromStream(configFile.inputStream())
        }

        @OptIn(ExperimentalSerializationApi::class)
        private fun saveToFile(config: BlockHuntConfig) {
            try {
                json.encodeToStream(config, configFile.outputStream())
            } catch (e: Exception) {
                logger.error("Could not save config", e)
            }
        }
    }

//    val starterKit: ConfigValue<List<String>> =
//        builder.comment("This starter kit is being given to every player at start. Will be ignored if clear_inventory is false!\n Format: [id]|[amount] (e.g minecraft:diamond_pickaxe|1)")
//            .defineList(
//                "starter_kit", listOf(
//                    "minecraft:diamond_pickaxe|1",
//                    "minecraft:diamond_axe|1",
//                    "minecraft:diamond_sword|1",
//                    "minecraft:diamond_shovel|1",
//                    "minecraft:bucket|1",
//                )
//            ) {
//                val (id, amount) = it.toString().split("|")
//                val resourceLocationValid = ResourceLocation.isValidResourceLocation(id)
//                val amountValid = amount.toIntOrNull() != null
//                LOG.debug("Checking validity of $id: $resourceLocationValid and amount $amount: $amountValid")
//
//                resourceLocationValid && amountValid
//            }
//
//    val clearInventory: BooleanValue = builder
//        .comment("If the inventory should be cleared at the start of a new round. Should be used in conjunction with a start kit to level the playfield")
//        .define("clear_inventory", false)
}