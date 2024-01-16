package de.chasenet.blockhunt

import de.chasenet.blockhunt.BlockHuntMod.LOG
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries


fun clearAndAddKit(inventory: Inventory) {
    inventory.clearContent()

    BlockHuntMod.blockHuntConfig.starterKit.get().forEach {
        val parts = it.split("|")
        if (parts.size != 2) {
            LOG.warn("Malformed kit entry $it")
            return@forEach
        }
        val (id, amountString) = parts
        val amount = amountString.toIntOrNull()

        val item = ResourceLocation.tryParse(id)?.let(ForgeRegistries.ITEMS::getValue)
        if (item == null) {
            LOG.warn("No item for id $id in kit $it")
            return@forEach
        }
        if (amount == null) {
            LOG.warn("Amount $amountString in kit $it not an integer")
            return@forEach
        }
        inventory.add(ItemStack(item, amount))
    }
}