package de.chasenet.blockhunt.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import de.chasenet.blockhunt.BlockHuntGame
import net.minecraft.item.ItemStack
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object GiveHuntCommand {
    fun build(): LiteralArgumentBuilder<ServerCommandSource> =
        literal<ServerCommandSource>("give")
            .requires { it.player!!.hasPermissionLevel(2) }
            .executes {
                if (BlockHuntGame.isActive) {
                    it.source.player!!.inventory.offerOrDrop(
                        ItemStack(BlockHuntGame.block!!.asItem(), 1)
                    )
                } else {
                    it.source.sendError(Text.literal("No hunt is active"))
                }
                1
            }
}