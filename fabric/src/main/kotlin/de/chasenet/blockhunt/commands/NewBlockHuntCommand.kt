package de.chasenet.blockhunt.commands

import com.mojang.brigadier.context.CommandContext
import de.chasenet.blockhunt.BlockHuntGame
import de.maxhenkel.admiral.annotations.Command
import de.maxhenkel.admiral.annotations.Commands
import de.maxhenkel.admiral.annotations.OptionalArgument
import net.minecraft.command.argument.BlockStateArgument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.apache.logging.log4j.core.tools.picocli.CommandLine.Option
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Command("blockhuntNew")
class NewBlockHuntCommand {

    @Command("start")
    fun startCommand(context: CommandContext<ServerCommandSource>, @OptionalArgument repeat: Boolean, block: Optional<BlockStateArgument>) {
        if (BlockHuntGame.isActive) {
            context.source.sendError(Text.literal("A hunt is already running"))
        } else {
            BlockHuntGame.repeat = repeat
            BlockHuntGame.startGame(context.source, block.getOrNull()?.blockState?.block)
            context.source.sendFeedback({ Text.literal("Hunt started successfully") }, false)
        }
    }

    @Command("stop")
    fun stopCommand(context: CommandContext<ServerCommandSource>) {
        if (BlockHuntGame.isActive) {
            BlockHuntGame.repeat = false
            BlockHuntGame.stopGame(context.source.server)
        } else {
            context.source.sendError(Text.literal("No hunt is active"))
        }
    }

    @Command("skip")
    fun stopCommand(context: CommandContext<ServerCommandSource>, @OptionalArgument blacklist: Boolean) {
        if (BlockHuntGame.isActive) {
            BlockHuntGame.skipGame(context.source, blacklist)
        } else {
            context.source.sendError(Text.literal("No hunt is active"))
        }
    }
}