package de.chasenet.blockhunt

import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

fun getRegistryKey(block: Block): Identifier = Registries.BLOCK.getKey(block).get().value
fun getRegistryKey(item: Item): Identifier = Registries.ITEM.getKey(item).get().value

fun getRegistryKeyForEnchantment(enchantment: Enchantment) = Registries.ENCHANTMENT.getKey(enchantment).get().value.toString()