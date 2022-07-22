package de.chasenet.blockhunt

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.ForgeConfigSpec
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
            ResourceLocation.isValidResourceLocation(it.toString())
        }

    val idBlacklistPatterns: ConfigValue<List<String>> =
        builder.comment("The blacklisted block ids regex patterns").defineList(
            "block_id_blacklist_pattern", listOf("minecraft:infested_.+", "minecraft:potted_.+", "minecraft:.+_ore")
        ) { !throws { Regex(it.toString()) } }
}