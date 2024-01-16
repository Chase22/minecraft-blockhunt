package de.chasenet.blockhunt.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import de.chasenet.blockhunt.BlockHuntGame
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object SkipHuntCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(literal<ServerCommandSource>("skipHunt")
            .requires { it.player!!.hasPermissionLevel(2) }
            .executes(::skipGame).then(literal<ServerCommandSource>("retain").executes { skipGame(it, true) })
        )
    }

    private fun skipGame(it: CommandContext<ServerCommandSource>, retain: Boolean = false): Int {
        if (BlockHuntGame.isActive) {
            BlockHuntGame.skipGame(it.source, retain)
        } else {
            it.source.sendError(Text.literal("No hunt is active"))
        }
        return 1
    }
}