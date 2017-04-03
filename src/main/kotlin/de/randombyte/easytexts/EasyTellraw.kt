package de.randombyte.easytexts

import com.google.inject.Inject
import de.randombyte.kosp.bstats.BStats
import de.randombyte.kosp.config.serializers.texttemplate.SimpleTextTemplateTypeSerializer
import de.randombyte.kosp.extensions.toText
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandCallable
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.GenericArguments.player
import org.spongepowered.api.command.args.GenericArguments.remainingRawJoinedStrings
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Plugin

@Plugin(id = EasyTellraw.ID, name = EasyTellraw.NAME, version = EasyTellraw.VERSION,
        authors = arrayOf(EasyTellraw.AUTHOR))
class EasyTellraw @Inject constructor(
        val logger: Logger,
        val bStats: BStats
) {
    internal companion object {
        const val ID = "easy-texts"
        const val NAME = "EasyTexts"
        const val VERSION = "0.1"
        const val AUTHOR = "RandomByte"

        const val ROOT_PERMISSION = "easytexts"

        const val PLAYER_ARG = "player"
        const val MESSAGE_ARG = "message"
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        fun registerCommand(commandCallable: CommandCallable, vararg alias: String) =
                Sponge.getCommandManager().register(this, commandCallable, *alias)

        registerCommand(CommandSpec.builder()
                .permission("$ROOT_PERMISSION.tellraw")
                .arguments(player(PLAYER_ARG.toText()), remainingRawJoinedStrings(MESSAGE_ARG.toText()))
                .executor { _, args ->
                    val player = args.getOne<Player>(PLAYER_ARG).get()
                    val messageString = args.getOne<String>(MESSAGE_ARG).get()

                    val message = SimpleTextTemplateTypeSerializer.deserialize(messageString)
                    player.sendMessage(message)

                    return@executor CommandResult.success()
                }
                .build(), "easytellraw", "eztellraw", "eztr")

        logger.info("$NAME loaded: $VERSION")
    }
}