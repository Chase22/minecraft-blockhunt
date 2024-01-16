package de.chasenet.blockhunt.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import de.chasenet.blockhunt.BlockHuntGame
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object StopHuntCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(literal<ServerCommandSource>("stopHunt")
            .requires { it.player!!.hasPermissionLevel(2) }
            .executes {
                if (BlockHuntGame.isActive) {
                    BlockHuntGame.stopGame(it.source.server)
                } else {
                    it.source.sendError(Text.literal("No hunt is active"))
                }
                1
            })
    }
}