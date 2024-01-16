package de.chasenet.blockhunt

import com.mojang.logging.LogUtils
import de.chasenet.blockhunt.commands.SkipHuntCommand
import de.chasenet.blockhunt.commands.StartHuntCommand
import de.chasenet.blockhunt.commands.StopHuntCommand
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import thedarkcolour.kotlinforforge.forge.DIST
import thedarkcolour.kotlinforforge.forge.FORGE_BUS

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

    val LOG = LogUtils.getLogger()

    private val configPair = ForgeConfigSpec.Builder().configure(::BlockHuntConfig)
    val blockHuntConfig: BlockHuntConfig = configPair.left

    private val blockHuntConfigSpec: ForgeConfigSpec = configPair.right

    init {
        if (DIST.isDedicatedServer) {
            FORGE_BUS.addListener(BlockHuntMod::registerCommands)
            FORGE_BUS.addListener(BlockHuntMod::onItemPickup)
            FORGE_BUS.addListener(BlockHuntMod::onItemCrafted)
            FORGE_BUS.addListener(BlockHuntMod::onItemSmelted)
            FORGE_BUS.addListener(BlockHuntMod::onServerStopping)
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, blockHuntConfigSpec)
    }

    private fun onServerStopping(it: ServerStoppingEvent) {
        BlockHuntGame.stopGame(it.server)
    }

    private fun onItemPickup(event: PlayerEvent.ItemPickupEvent) {
        BlockHuntGame.onBlockObtained(event.entity, event.stack)
    }

    private fun onItemCrafted(event: PlayerEvent.ItemCraftedEvent) {
        BlockHuntGame.onBlockObtained(event.entity, event.crafting)
    }

    private fun onItemSmelted(event: PlayerEvent.ItemSmeltedEvent) {
        BlockHuntGame.onBlockObtained(event.entity, event.smelting)
    }

    private fun registerCommands(event: RegisterCommandsEvent) {
        StartHuntCommand.register(event.dispatcher, event.buildContext)
        StopHuntCommand.register(event.dispatcher)
        SkipHuntCommand.register(event.dispatcher)
    }
}