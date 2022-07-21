package de.chasenet.blockhunt

import com.mojang.logging.LogUtils
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.Consumer

object BlockHuntGame {
    private val BLACKLISTED_IDS by lazy {
        ForgeRegistries.BLOCKS.keys.filter { it.path.startsWith("infested_") } +
                ForgeRegistries.BLOCKS.keys.filter { it.path.endsWith("_ore") }
    }


    var block: Block? = null
        private set

    val isActive: Boolean
        get() = block != null

    @JvmStatic
    fun startGame(sourceStack: CommandSourceStack, block: Block? = null) {
        try {
            val blocksList = ForgeRegistries.BLOCKS.entries.filter { !BLACKLISTED_IDS.contains(it.key.location()) }

            val selectedBlock = block ?: blocksList.random().value

            val key = ForgeRegistries.BLOCKS.getResourceKey(selectedBlock).get()

            val subtitleTextPacket = ClientboundSetSubtitleTextPacket(selectedBlock!!.name)
            val titleTextPacket = ClientboundSetTitleTextPacket(Component.literal("New Blockhunt!"))
            sourceStack.server.playerList.players.forEach(Consumer { player: ServerPlayer ->
                player.connection.send(subtitleTextPacket)
                player.connection.send(titleTextPacket)
            })

            this.block = selectedBlock
            sourceStack.sendSuccess(
                Component.literal("Random Block selected: ").append(
                    key.location().toString()
                ), false
            )
        } catch (e: Exception) {
            LogUtils.getLogger().error("Error executing command", e)
        }
    }

    @JvmStatic
    fun onBlockObtained(player: Player, stack: ItemStack) {
        if (block == null) return

        val subtitleTextPacket =
            ClientboundSetSubtitleTextPacket(Component.literal("Winner: ").append(player.displayName))
        val titleTextPacket = ClientboundSetTitleTextPacket(Component.literal("Blockhunt over!"))
        if (stack.item == block!!.asItem()) {
            player.level.server!!.playerList.players.forEach(Consumer { serverPlayer: ServerPlayer ->
                serverPlayer.connection.send(subtitleTextPacket)
                serverPlayer.connection.send(titleTextPacket)
            })
            block = null
        }
    }
}