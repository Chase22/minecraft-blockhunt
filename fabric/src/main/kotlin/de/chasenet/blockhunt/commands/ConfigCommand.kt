package de.chasenet.blockhunt.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import de.chasenet.blockhunt.config.BlockHuntConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

object ConfigCommand {
    private val json = Json { encodeDefaults = true }

    fun build(): LiteralArgumentBuilder<ServerCommandSource> =
        literal<ServerCommandSource>("config")
            .requires { it.player!!.hasPermissionLevel(2) }
            .executes {
                it.source.sendFeedback( {
                    Text.literal(json.encodeToString(BlockHuntConfig.instance.toConfigFile()))
                }, false)
                1
            }
}