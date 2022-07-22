package de.chasenet.blockhunt

import com.mojang.logging.LogUtils
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.bossevents.CustomBossEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Block

object UiUtils {
    private var bossBarWrapper: BossBarWrapper? = null

    fun startHuntUi(
        commandSourceStack: CommandSourceStack,
        block: Block,
        key: ResourceKey<Block>
    ) {
        bossBarWrapper?.destroy()
        val players = commandSourceStack.server.playerList.players
        sendTitle(players, "New Blockhunt!", block.name)
        bossBarWrapper = BossBarWrapper(
            commandSourceStack.server.customBossEvents,
            key.location().toString(),
            block.name,
            players
        )

    }

    fun endHuntUi(
        players: List<ServerPlayer>,
        winner: Player
    ) {
        sendTitle(
            players = players,
            title = "Blockhunt over!",
            subtitle = Component.literal("Winner: ").append(winner.displayName)
        )
        bossBarWrapper?.destroy()
    }

    fun stopHuntUi(players: List<ServerPlayer>) {
        sendTitle(players, "The Blockhunt has stopped")
        bossBarWrapper?.destroy()
    }

    private fun sendTitle(players: List<ServerPlayer>, title: String, subtitle: Component? = null) {
        val subtitleTextPacket = subtitle?.let(::ClientboundSetSubtitleTextPacket)
        val titleTextPacket = ClientboundSetTitleTextPacket(Component.literal(title))
        LogUtils.getLogger().info("Sending Title $title")
        players.forEach { serverPlayer: ServerPlayer ->
            subtitleTextPacket?.let { serverPlayer.connection.send(it) }
            serverPlayer.connection.send(titleTextPacket)
        }
    }
}

class BossBarWrapper(
    private val customBossEvents: CustomBossEvents,
    id: String,
    name: Component,
    players: List<ServerPlayer>
) {
    private val bossbarId = ResourceLocation("${BlockHuntMod.MODID}-$id")
    private val customBossEvent = customBossEvents.create(bossbarId, name).apply {
        this.players = players
    }

    fun destroy() {
        customBossEvent.removeAllPlayers()
        customBossEvents.remove(customBossEvent)
    }
}