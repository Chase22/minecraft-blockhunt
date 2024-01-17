package de.chasenet

import de.chasenet.blockhunt.config.BlockHuntConfig
import de.chasenet.blockhunt.BlockHuntGame
import de.chasenet.blockhunt.commands.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import org.slf4j.LoggerFactory

object Blockhunt : ModInitializer {
    const val MODID = "blockhunt"

    val logger = LoggerFactory.getLogger("blockhunt")

    override fun onInitialize() {
        BlockHuntConfig.init()
        CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
            BlockHuntCommand.register(dispatcher, registryAccess)
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