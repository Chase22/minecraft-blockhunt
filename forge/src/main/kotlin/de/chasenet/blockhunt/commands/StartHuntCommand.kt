package de.chasenet.blockhunt.commands

import com.mojang.brigadier.CommandDispatcher
import de.chasenet.blockhunt.BlockHuntGame
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.blocks.BlockStateArgument
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Block

object StartHuntCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, commandBuildContext: CommandBuildContext) {
        dispatcher.register(Commands.literal("startHunt").requires {
            it.player!!.hasPermissions(2)
        }.executes {
            startHunt(it.source)
        }.then(Commands.argument("block", BlockStateArgument.block(commandBuildContext)).executes {
            startHunt(it.source, BlockStateArgument.getBlock(it, "block").state.block)
        }))
    }
    private fun startHunt(sourceStack: CommandSourceStack, block: Block? = null): Int {
        if (BlockHuntGame.isActive) {
            sourceStack.sendFailure(Component.literal("A hunt is already running"))
        } else {
            BlockHuntGame.startGame(sourceStack, block)
            sourceStack.sendSuccess(Component.literal("Hunt started successfully"), false)
        }
        return 1
    }
}