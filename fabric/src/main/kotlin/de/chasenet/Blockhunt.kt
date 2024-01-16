package de.chasenet

import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import de.chasenet.blockhunt.BlockHuntConfig
import de.chasenet.blockhunt.BlockHuntGame
import de.chasenet.blockhunt.commands.SkipHuntCommand
import de.chasenet.blockhunt.commands.StartHuntCommand
import de.chasenet.blockhunt.commands.StopHuntCommand
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.slf4j.LoggerFactory

object Blockhunt : ModInitializer {
    const val MODID = "blockhunt"

    val logger = LoggerFactory.getLogger("blockhunt")

    val blockHuntConfig: BlockHuntConfig = BlockHuntConfig()

    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Hello Fabric world!")

        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            StartHuntCommand.register(dispatcher, registryAccess)
            StopHuntCommand.register(dispatcher)
            SkipHuntCommand.register(dispatcher)
        }

        ServerTickEvents.END_SERVER_TICK.register { server ->
            if (!BlockHuntGame.isActive) return@register
            server.playerManager.playerList.find {
                it.inventory.contains(BlockHuntGame.block?.asItem()?.defaultStack)
            }?.let {
                BlockHuntGame.win(it)
            }
        }
        ServerLifecycleEvents.SERVER_STOPPING.register(BlockHuntGame::stopGame)

    }
}