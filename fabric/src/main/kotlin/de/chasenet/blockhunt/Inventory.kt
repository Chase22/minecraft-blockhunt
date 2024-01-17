package de.chasenet.blockhunt

import de.chasenet.blockhunt.config.BlockHuntConfig
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack

fun clearAndAddKit(inventory: PlayerInventory) {
    inventory.clear()
    BlockHuntConfig.instance.starterKit.map(ItemStack::copy).forEach(inventory::offerOrDrop)
}