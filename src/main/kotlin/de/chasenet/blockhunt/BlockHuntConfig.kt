package de.chasenet.blockhunt

import de.chasenet.blockhunt.BlockHuntMod.LOG
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue

class BlockHuntConfig(builder: ForgeConfigSpec.Builder) {
    val idBlacklist: ConfigValue<List<String>> = builder.comment("The blacklisted block ids")
        .defineList(
            "block_id_blacklist", listOf(
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
            )
        ) {
            val valid = ResourceLocation.isValidResourceLocation(it.toString())
            LOG.debug("Checking validity of $it: $valid")
            valid
        }

    val idBlacklistPatterns: ConfigValue<List<String>> =
        builder.comment("The blacklisted block ids regex patterns").defineList(
            "block_id_blacklist_pattern", listOf("minecraft:infested_.+", "minecraft:potted_.+", "minecraft:.+_ore")
        ) {
            val valid = !throws { Regex(it.toString()) }
            LOG.debug("Checking validity of $it: $valid")
            valid
        }

    val starterKit: ConfigValue<List<String>> =
        builder.comment("This starter kit is being given to every player at start. Will be ignored if clear_inventory is false!\n Format: [id]|[amount] (e.g minecraft:diamond_pickaxe|1)")
            .defineList(
                "starter_kit", listOf(
                    "minecraft:diamond_pickaxe|1",
                    "minecraft:diamond_axe|1",
                    "minecraft:diamond_sword|1",
                    "minecraft:diamond_shovel|1",
                    "minecraft:bucket|1",
                )
            ) {
                val (id, amount) = it.toString().split("|")
                val resourceLocationValid = ResourceLocation.isValidResourceLocation(id)
                val amountValid = amount.toIntOrNull() != null
                LOG.debug("Checking validity of $id: $resourceLocationValid and amount $amount: $amountValid")

                resourceLocationValid && amountValid
            }

    val clearInventory: BooleanValue = builder
        .comment("If the inventory should be cleared at the start of a new round. Should be used in conjunction with a start kit to level the playfield")
        .define("clear_inventory", false)
}