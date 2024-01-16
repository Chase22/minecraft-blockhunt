package de.chasenet.blockhunt

import com.mojang.logging.LogUtils
import de.chasenet.Blockhunt
import net.minecraft.block.Block
import net.minecraft.entity.boss.BossBarManager
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket
import net.minecraft.network.packet.s2c.play.TitleS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object UiUtils {
    private var bossBarWrapper: BossBarWrapper? = null

    fun startHuntUi(
        commandSource: ServerCommandSource,
        block: Block
    ) {
        bossBarWrapper?.destroy()
        val players = commandSource.server.playerManager.playerList
        sendTitle(players, "New Blockhunt!", block.name)
        bossBarWrapper = BossBarWrapper(
            commandSource.server.bossBarManager,
            "hunted_block",
            block.name,
            players
        )
    }

    fun endHuntUi(
        players: List<ServerPlayerEntity>,
        winner: ServerPlayerEntity
    ) {
        sendTitle(
            players = players,
            title = "Blockhunt over!",
            subtitle = Text.literal("Winner: ").append(winner.displayName)
        )
        bossBarWrapper?.destroy()
    }

    fun stopHuntUi(players: List<ServerPlayerEntity>) {
        sendTitle(players, "The Blockhunt has stopped")
        bossBarWrapper?.destroy()
    }

    private fun sendTitle(players: List<ServerPlayerEntity>, title: String, subtitle: Text? = null) {
        val subtitleTextPacket = subtitle?.let(::SubtitleS2CPacket)
        val titleTextPacket = TitleS2CPacket(Text.literal(title))
        LogUtils.getLogger().info("Sending Title $title")
        players.forEach { serverPlayer: ServerPlayerEntity ->
            subtitleTextPacket?.let { serverPlayer.networkHandler.send(it, null) }
            serverPlayer.networkHandler.send(titleTextPacket, null)
        }
    }
}

class BossBarWrapper(
    private val bossBarManager: BossBarManager,
    id: String,
    name: Text,
    players: List<ServerPlayerEntity>
) {
    private val bossbarId = Identifier(Blockhunt.MODID, id)
    private val customBossEvent = bossBarManager.add(bossbarId, name).apply {
        addPlayers(players)
    }

    fun destroy() {
        customBossEvent.clearPlayers()
        bossBarManager.remove(customBossEvent)
    }
}