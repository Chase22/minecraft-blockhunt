package de.chasenet.blockhunt

import com.mojang.logging.LogUtils
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraftforge.registries.ForgeRegistries

object BlockHuntGame {
    private val LOG = LogUtils.getLogger()

    var block: Block? = null
        private set

    val isActive: Boolean
        get() = block != null

    fun startGame(sourceStack: CommandSourceStack, block: Block? = null) {
        try {
            val blackList = BlockHuntMod.blockHuntConfig.idBlacklistPatterns.get().map { pattern ->
                val regex = pattern.toRegex()
                ForgeRegistries.BLOCKS.keys.filter { it.toString().matches(regex) }
            }.flatten() + BlockHuntMod.blockHuntConfig.idBlacklist.get().map(::ResourceLocation)

            LOG.info(blackList.toString())

            val blocksList = ForgeRegistries.ITEMS.entries.filter { it.value is BlockItem }
                .map { it.key to (it.value as BlockItem).block }
                .filter { !blackList.contains(it.first.location()) }

            val selectedBlock = block ?: blocksList.random().second

            val key = ForgeRegistries.BLOCKS.getResourceKey(selectedBlock).get()

            UiUtils.startHuntUi(sourceStack, selectedBlock, key)

            this.block = selectedBlock
            sourceStack.sendSuccess(
                Component.literal("Random Block selected: ").append(
                    key.location().toString()
                ), false
            )
        } catch (e: Exception) {
            sourceStack.sendFailure(Component.literal("An error occurred while starting the hunt. Please check the logs"))
            LOG.error("Error executing command", e)
        }
    }

    fun stopGame(server:MinecraftServer) {
        block = null
        UiUtils.stopHuntUi(server.playerList.players)
    }

    @JvmStatic
    fun onBlockObtained(player: Player, stack: ItemStack) {
        if (block == null) return

        if (stack.item == block!!.asItem()) {
            UiUtils.endHuntUi(player.server!!.playerList.players, player)
            block = null
        }
    }

    fun skipGame(sourceStack: CommandSourceStack, retain: Boolean = false) {
        if (block == null) return
        if (!retain) {
            val key = ForgeRegistries.BLOCKS.getResourceKey(block).get()
            BlockHuntMod.blockHuntConfig.idBlacklist.apply {
                set(get().plus(key.location().toString()))
                save()
            }
        }
        startGame(sourceStack)
    }
}

fun main() {
    val string = "minecraft:infested_cobblestone"
    val regex = "minecraft:infested_.*".toRegex()
    println(regex.matches(string))
}