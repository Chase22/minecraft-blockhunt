package de.chasenet.blockhunt.commands

import com.mojang.brigadier.CommandDispatcher
import de.chasenet.blockhunt.BlockHuntGame
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.blocks.BlockStateArgument

object StartHuntCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, commandBuildContext: CommandBuildContext) {
        dispatcher.register(Commands.literal("startHunt").requires {
            it.player!!.hasPermissions(2)
        }.executes {
            BlockHuntGame.startGame(it.source)
            return@executes 1
        }.then(Commands.argument("block", BlockStateArgument.block(commandBuildContext)).executes {
            BlockHuntGame.startGame(it.source, BlockStateArgument.getBlock(it, "block").state.block)
            return@executes 1
        }))
    }
}