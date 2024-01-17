package de.chasenet.blockhunt

import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

fun getRegistryKeyForBlock(block: Block): Identifier = Registries.BLOCK.getKey(block).get().value
