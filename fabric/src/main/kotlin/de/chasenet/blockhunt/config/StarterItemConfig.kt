package de.chasenet.blockhunt.config

import de.chasenet.blockhunt.getRegistryKey
import de.chasenet.blockhunt.getRegistryKeyForEnchantment
import kotlinx.serialization.Serializable
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

@Serializable
data class StarterItemConfig(
    val id: String,
    val amount: Int = 1,
    val enchantments: Map<String, Int> = emptyMap()
) {
    constructor(itemStack: ItemStack): this(
        id = getRegistryKey(itemStack.item).toString(),
        amount = itemStack.count,
        enchantments = EnchantmentHelper.get(itemStack).map { getRegistryKeyForEnchantment(it.key) to it.value }.toMap()
    )

    fun toItemStack(): ItemStack = ItemStack(Registries.ITEM.get(Identifier(id)))
        .apply {
            count = amount
            this@StarterItemConfig.enchantments.forEach {
                addEnchantment(Registries.ENCHANTMENT.get(Identifier(it.key)), it.value)
            }
        }
}