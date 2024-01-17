package de.chasenet.blockhunt.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import de.chasenet.blockhunt.config.BlockHuntConfig
import de.chasenet.blockhunt.config.BlockHuntConfigFile
import de.chasenet.blockhunt.getRegistryKey
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.argument.BlockStateArgument
import net.minecraft.command.argument.BlockStateArgumentType
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object BlacklistCommand {
    fun build(registryAccess: CommandRegistryAccess): LiteralArgumentBuilder<ServerCommandSource> =
        literal<ServerCommandSource?>("blacklist")
            .requires { it.player!!.hasPermissionLevel(2) }
            .then(argument<ServerCommandSource, BlockStateArgument>(
                "block",
                BlockStateArgumentType.blockState(registryAccess)
            ).executes { context ->
                val block = BlockStateArgumentType.getBlockState(context, "block").blockState.block
                if (BlockHuntConfig.instance.idBlacklist.contains(getRegistryKey(block))) {
                    context.source.sendFeedback({
                        Text.literal("Block: ")
                            .append(Text.translatable(block.translationKey))
                            .append(Text.literal(" is already blacklisted"))
                    }, false)
                    return@executes 1
                }

                BlockHuntConfig.blackListBlock(block)
                context.source.sendFeedback({
                    Text.literal("Blacklisted Block: ")
                        .append(Text.translatable(block.translationKey))
                }, true)
                return@executes 1
            })

}