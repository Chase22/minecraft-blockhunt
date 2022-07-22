package de.chasenet.blockhunt.commands

import com.mojang.brigadier.CommandDispatcher
import de.chasenet.blockhunt.BlockHuntGame
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

object StopHuntCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(Commands.literal("stopHunt")
            .requires { it.hasPermission(2) }
            .executes {
                if (BlockHuntGame.isActive) {
                    BlockHuntGame.stopGame(it.source.server)
                } else {
                    it.source.sendFailure(Component.literal("No hunt is active"))
                }
                1
            })
    }
}