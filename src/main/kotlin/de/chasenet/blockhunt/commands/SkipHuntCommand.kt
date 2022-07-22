package de.chasenet.blockhunt.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import de.chasenet.blockhunt.BlockHuntGame
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

object SkipHuntCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(Commands.literal("skipHunt")
            .requires { it.hasPermission(2) }
            .executes(::skipGame).then(Commands.literal("retain").executes { skipGame(it, true) })
        )
    }

    private fun skipGame(it: CommandContext<CommandSourceStack>, retain: Boolean = false): Int {
        if (BlockHuntGame.isActive) {
            BlockHuntGame.skipGame(it.source, retain)
        } else {
            it.source.sendFailure(Component.literal("No hunt is active"))
        }
        return 1
    }
}