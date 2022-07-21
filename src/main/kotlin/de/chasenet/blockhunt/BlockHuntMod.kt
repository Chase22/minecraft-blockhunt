package de.chasenet.blockhunt

import com.mojang.logging.LogUtils
import de.chasenet.blockhunt.commands.StartHuntCommand
import net.minecraft.client.Minecraft
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Mod(BlockHuntMod.MODID)
object BlockHuntMod {
    const val MODID = "blockhunt"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(MODID)

    init {
        LOGGER.log(Level.INFO, "Hello world!")

        runForDist(
            clientTarget = {
                MOD_BUS.addListener(BlockHuntMod::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(BlockHuntMod::onServerSetup)
            })

        FORGE_BUS.addListener(BlockHuntMod::registerCommands)
        FORGE_BUS.addListener(BlockHuntMod::onItemPickup)
    }

    private fun onItemPickup(event: PlayerEvent.ItemPickupEvent) {
        BlockHuntGame.onBlockObtained(event.entity, event.stack)
        LogUtils.getLogger().info(event.entity.name.string + ": " + event.stack.displayName.string)
    }

    private fun onItemCrafted(event: PlayerEvent.ItemCraftedEvent) {
        BlockHuntGame.onBlockObtained(event.entity, event.crafting)
    }

    private fun onItemSmelted(event: PlayerEvent.ItemSmeltedEvent) {
        BlockHuntGame.onBlockObtained(event.entity, event.smelting)
    }

    private fun registerCommands(event: RegisterCommandsEvent) {
        StartHuntCommand.register(event.dispatcher, event.buildContext)
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }
}