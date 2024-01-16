package de.chasenet.blockhunt

import net.minecraft.block.Block
import net.minecraft.registry.Registries

fun getRegistryKeyForBlock(block: Block) = Registries.BLOCK.getKey(block).get().value.toString()
