package de.chasenet.blockhunt

import com.mojang.logging.LogUtils
import de.chasenet.Blockhunt
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.scoreboard.ScoreboardCriterion
import net.minecraft.scoreboard.ScoreboardDisplaySlot
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

object BlockHuntGame {
    private val LOG = LogUtils.getLogger()
    const val OBJECTIVE_ID = "Blockhunt"

    var block: Block? = null
        private set

    val isActive: Boolean
        get() = block != null

    fun startGame(sourceStack: ServerCommandSource, block: Block? = null) {
        with(sourceStack.server.scoreboard) {

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


        try {
            val blackList = BlockHuntConfig.instance.idBlacklistPatterns.map { pattern ->
                val regex = pattern.toRegex()
                Registries.BLOCK.keys.filter { it.toString().matches(regex) }
            }.flatten() + BlockHuntConfig.instance.idBlacklist.map { Registries.BLOCK.get(Identifier.tryParse(it)) }

            val blocksList = Registries.ITEM.entrySet.filter { it.value is BlockItem }
                .map { it.key to (it.value as BlockItem).block }
                .filter { !blackList.contains(it.first.value.toTranslationKey()) }

            val selectedBlock = block ?: blocksList.random().second

            UiUtils.startHuntUi(sourceStack, selectedBlock)

            this.block = selectedBlock

            if (BlockHuntConfig.instance.clearInventory) {
                sourceStack.server.playerManager.playerList.forEach {
                    clearAndAddKit(it.inventory)
                }
            }

            sourceStack.sendFeedback(
                { Text.literal("Started hunt for block: ").append(
                    Text.translatable(selectedBlock.translationKey)
                )}, true
            )
        } catch (e: Exception) {
            sourceStack.sendError(Text.literal("An error occurred while starting the hunt. Please check the logs"))
            LOG.error("Error executing command", e)
        }
    }

    fun stopGame(server: MinecraftServer) {
        block = null
        UiUtils.stopHuntUi(server.playerManager.playerList)
    }

    @JvmStatic
    fun onBlockObtained(player: ServerPlayerEntity, stack: ItemStack) {
        if (block == null) return

        if (stack.item == block!!.asItem()) {
            win(player)
        }
    }

    fun win(player: ServerPlayerEntity) {
        UiUtils.endHuntUi(player.server!!.playerManager.playerList, player)
        player.scoreboard.getNullableObjective(OBJECTIVE_ID)
            ?.let { player.scoreboard.getOrCreateScore(player, it).incrementScore() }
        block = null
    }

    fun skipGame(sourceStack: ServerCommandSource, retain: Boolean = false) {
        if (block == null) return
        if (!retain) {
            val key = Registries.BLOCK.getKey(block).get()
            //TODO FIX ME
            //BlockHuntMod.blockHuntConfig.idBlacklist.apply {
            //    set(get().plus(key.location().toString()))
            //    save()
            //}
        }
        startGame(sourceStack)
    }
}