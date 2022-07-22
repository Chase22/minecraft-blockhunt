package de.chasenet.blockhunt.commands

import com.mojang.brigadier.CommandDispatcher
import de.chasenet.blockhunt.BlockHuntGame
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

object SkipHuntCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(Commands.literal("skipHunt")
            .requires { it.hasPermission(2) }
            .executes {
                if (BlockHuntGame.isActive) {
                    BlockHuntGame.skipGame(it.source)
                } else {
                    it.source.sendFailure(Component.literal("No hunt is active"))
                }
                1
            })
    }
}