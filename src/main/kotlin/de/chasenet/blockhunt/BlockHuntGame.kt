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
import net.minecraft.world.scores.Scoreboard
import net.minecraft.world.scores.criteria.ObjectiveCriteria
import net.minecraftforge.registries.ForgeRegistries

object BlockHuntGame {
    private val LOG = LogUtils.getLogger()
    const val OBJECTIVE_ID = "Blockhunt"

    var block: Block? = null
        private set

    val isActive: Boolean
        get() = block != null

    fun startGame(sourceStack: CommandSourceStack, block: Block? = null) {
        with(sourceStack.scoreboard) {
            if (!hasObjective(OBJECTIVE_ID)) {
                val objective = addObjective(
                    OBJECTIVE_ID,
                    ObjectiveCriteria.DUMMY,
                    Component.literal("Blockhunt"),
                    ObjectiveCriteria.RenderType.INTEGER
                )
                setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, objective)
            }
        }


        try {
            val blackList = BlockHuntMod.blockHuntConfig.idBlacklistPatterns.get().map { pattern ->
                val regex = pattern.toRegex()
                ForgeRegistries.BLOCKS.keys.filter { it.toString().matches(regex) }
            }.flatten() + BlockHuntMod.blockHuntConfig.idBlacklist.get().map(::ResourceLocation)

            val blocksList = ForgeRegistries.ITEMS.entries.filter { it.value is BlockItem }
                .map { it.key to (it.value as BlockItem).block }
                .filter { !blackList.contains(it.first.location()) }

            val selectedBlock = block ?: blocksList.random().second

            val key = ForgeRegistries.BLOCKS.getResourceKey(selectedBlock).get()

            UiUtils.startHuntUi(sourceStack, selectedBlock, key)

            this.block = selectedBlock
            sourceStack.sendSuccess(
                Component.literal("Started hunt for block: ").append(
                    key.location().toString()
                ), true
            )
        } catch (e: Exception) {
            sourceStack.sendFailure(Component.literal("An error occurred while starting the hunt. Please check the logs"))
            LOG.error("Error executing command", e)
        }
    }

    fun stopGame(server: MinecraftServer) {
        block = null
        UiUtils.stopHuntUi(server.playerList.players)
    }

    @JvmStatic
    fun onBlockObtained(player: Player, stack: ItemStack) {
        if (block == null) return

        if (stack.item == block!!.asItem()) {
            UiUtils.endHuntUi(player.server!!.playerList.players, player)
            player.scoreboard.getObjective(OBJECTIVE_ID)
                ?.let { player.scoreboard.getOrCreatePlayerScore(player.scoreboardName, it).increment() }
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