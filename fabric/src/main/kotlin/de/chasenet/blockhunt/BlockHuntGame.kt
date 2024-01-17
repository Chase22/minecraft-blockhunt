package de.chasenet.blockhunt

import com.mojang.logging.LogUtils
import de.chasenet.blockhunt.config.BlockHuntConfig
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.scoreboard.ScoreboardCriterion
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

object BlockHuntGame {
    private val LOG = LogUtils.getLogger()
    private const val OBJECTIVE_ID = "Blockhunt"

    var block: Block? = null
        private set

    val isActive: Boolean
        get() = block != null

    val repeat = true

    fun startGame(sourceStack: ServerCommandSource, commandBlock: Block? = null) {
        try {
            startGame(sourceStack.server, commandBlock)

            sourceStack.sendFeedback(
                {
                    Text.literal("Started hunt for block: ").append(
                        Text.translatable(this.block!!.translationKey)
                    )
                }, true
            )
        } catch (e: Exception) {
            sourceStack.sendError(Text.literal("An error occurred while starting the hunt. Please check the logs"))
            LOG.error("Error executing command", e)
        }
    }

    fun startGame(server: MinecraftServer, commandBlock: Block? = null) {
        with(server.scoreboard) {
            if (getNullableObjective(OBJECTIVE_ID) == null) {
                val objective = addObjective(
                    OBJECTIVE_ID,
                    ScoreboardCriterion.DUMMY,
                    Text.literal("Blockhunt"),
                    ScoreboardCriterion.RenderType.INTEGER,
                    true,
                    null
                )
                setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective)
            }
        }
        val selectedBlock = commandBlock ?: run {
            val blackList = (BlockHuntConfig.instance.idBlacklistPatterns.map { pattern ->
                val regex = pattern.toRegex()
                Registries.BLOCK.keys.map { it.value }.filter { it.toString().matches(regex) }
            }.flatten() + BlockHuntConfig.instance.idBlacklist).toSet()

            val blocksList = Registries.ITEM.entrySet.filter { it.value is BlockItem }
                .map { it.key to (it.value as BlockItem).block }
                .filter { !blackList.contains(it.first.value) }

            blocksList.random().second
        }

        UiUtils.startHuntUi(server, selectedBlock)

        this.block = selectedBlock

        if (BlockHuntConfig.instance.clearInventory) {
            server.playerManager.playerList.forEach {
                clearAndAddKit(it.inventory)
            }
        }
    }

    fun stopGame(server: MinecraftServer) {
        block = null
        UiUtils.stopHuntUi(server.playerManager.playerList)
    }

    fun win(player: ServerPlayerEntity) {
        UiUtils.endHuntUi(player.server.playerManager.playerList, player)
        player.scoreboard.getNullableObjective(OBJECTIVE_ID)
            ?.let { player.scoreboard.getOrCreateScore(player, it).incrementScore() }
        block = null

        if (repeat) {
            scheduleNextGame(player.server)
        }
    }

    private fun scheduleNextGame(server: MinecraftServer) {
        Thread {
            var countdown = 10
            Thread.sleep(5000)
            UiUtils.nextHuntCountdown(server.playerManager.playerList)
            Thread.sleep(2000)
            while (countdown > 0) {
                UiUtils.updateHuntCountdown(server.playerManager.playerList, countdown)
                Thread.sleep(1000)
                countdown -= 1
            }
            startGame(server)

        }.start()
    }

    fun skipGame(sourceStack: ServerCommandSource, blacklist: Boolean = false) {
        if (block == null) return
        if (blacklist) {
            BlockHuntConfig.blackListBlock(block!!)
        }
        startGame(sourceStack)
    }
}