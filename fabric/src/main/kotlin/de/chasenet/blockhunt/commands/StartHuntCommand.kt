package de.chasenet.blockhunt.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import de.chasenet.Blockhunt
import de.chasenet.blockhunt.BlockHuntGame
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.argument.BlockStateArgument
import net.minecraft.command.argument.BlockStateArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

fun runCatching(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        Blockhunt.logger.error("Error Starting hunt", e)
    }
}

object StartHuntCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>, registryAccess: CommandRegistryAccess) {
        dispatcher.register(
            literal<ServerCommandSource?>("startHunt")
                .requires { it.player!!.hasPermissionLevel(2) }
                .executes {
                    de.chasenet.blockhunt.commands.runCatching { startHunt(it.source) }
                    return@executes 1
                }.then(
                    argument<ServerCommandSource, BlockStateArgument>(
                        "block",
                        BlockStateArgumentType.blockState(registryAccess)
                    ).executes {
                        de.chasenet.blockhunt.commands.runCatching {
                            startHunt(
                                it.source,
                                BlockStateArgumentType.getBlockState(it, "block").blockState.block
                            )
                        }
                        return@executes 1
                    })
        )

    }

    private fun startHunt(source: ServerCommandSource, block: Block? = null): Int {
        if (BlockHuntGame.isActive) {
            source.sendError(Text.literal("A hunt is already running"))
        } else {
            BlockHuntGame.startGame(source, block)
            source.sendFeedback({ Text.literal("Hunt started successfully") }, false)
        }
        return 1
    }
}