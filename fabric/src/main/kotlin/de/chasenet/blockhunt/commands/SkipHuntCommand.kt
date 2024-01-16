package de.chasenet.blockhunt.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.context.CommandContext
import de.chasenet.blockhunt.BlockHuntGame
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object SkipHuntCommand {
    fun build(): LiteralArgumentBuilder<ServerCommandSource> =
        literal<ServerCommandSource>("skip")
            .requires { it.player!!.hasPermissionLevel(2) }
            .executes(::skipGame).then(literal<ServerCommandSource>("blacklist").executes { skipGame(it, true) })

    private fun skipGame(it: CommandContext<ServerCommandSource>, blacklist: Boolean = false): Int {
        if (BlockHuntGame.isActive) {
            BlockHuntGame.skipGame(it.source, blacklist)
        } else {
            it.source.sendError(Text.literal("No hunt is active"))
        }
        return 1
    }
}