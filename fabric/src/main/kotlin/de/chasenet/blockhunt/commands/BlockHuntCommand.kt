package de.chasenet.blockhunt.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.ServerCommandSource

object BlockHuntCommand {
    fun register(commandDispatcher: CommandDispatcher<ServerCommandSource>, registryAccess: CommandRegistryAccess) {
        commandDispatcher.register(
            literal<ServerCommandSource?>("blockhunt")
                .then(StartHuntCommand.build(registryAccess))
                .then(StopHuntCommand.build())
                .then(SkipHuntCommand.build())
                .then(ConfigCommand.build())
                .then(GiveHuntCommand.build())
                .then(BlacklistCommand.build(registryAccess))
        )
    }
}