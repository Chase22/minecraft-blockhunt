package de.chasenet.blockhunt

//FIXME Actually make this configurable
class BlockHuntConfig {
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
    )

    val idBlacklistPatterns: List<String> = listOf("minecraft:infested_.+", "minecraft:potted_.+", "minecraft:.+_ore")

    val starterKit = emptyList<String>()
    val clearInventory = false
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