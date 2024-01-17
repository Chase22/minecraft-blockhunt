package de.chasenet.blockhunt

import de.chasenet.blockhunt.config.BlockHuntConfig
import net.minecraft.entity.player.PlayerInventory

fun clearAndAddKit(inventory: PlayerInventory) {
    inventory.clear()
    BlockHuntConfig.instance.starterKit.forEach(inventory::offerOrDrop)
}