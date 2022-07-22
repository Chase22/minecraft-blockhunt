package de.chasenet.blockhunt

import com.mojang.logging.LogUtils
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraftforge.registries.ForgeRegistries

object BlockHuntGame {
    private val BLACKLISTED_IDS by lazy {
        listOf(
            ForgeRegistries.BLOCKS.keys.filter { it.path.startsWith("infested_") },
            ForgeRegistries.BLOCKS.keys.filter { it.path.endsWith("_ore") },
            ForgeRegistries.BLOCKS.keys.filter { it.path.startsWith("potted_") }
        ).flatten()
    }

    private val additionalKeys: MutableList<ResourceKey<Block>> = ArrayList()

    private val LOG = LogUtils.getLogger()

    var block: Block? = null
        private set

    val isActive: Boolean
        get() = block != null

    fun startGame(sourceStack: CommandSourceStack, block: Block? = null) {
        try {
            val blocksList = ForgeRegistries.ITEMS.entries.filter { it.value is BlockItem }
                .map { it.key to (it.value as BlockItem).block }
                .filter { !(BLACKLISTED_IDS+additionalKeys).contains(it.first.location()) }

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

    fun stopGame(sourceStack: CommandSourceStack) {
        block = null
        UiUtils.stopHuntUi(sourceStack.server.playerList.players)
    }

    @JvmStatic
    fun onBlockObtained(player: Player, stack: ItemStack) {
        if (block == null) return

        if (stack.item == block!!.asItem()) {
            UiUtils.endHuntUi(player.server!!.playerList.players, player)
            block = null
        }
    }

    fun skipGame(sourceStack: CommandSourceStack) {
        if (block == null) return
        additionalKeys.add(ForgeRegistries.BLOCKS.getResourceKey(block).get())
        LOG.info(additionalKeys.toString())
        startGame(sourceStack)
    }
}