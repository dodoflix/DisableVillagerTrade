package me.dodo.disablevillagertrade.neoforge.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.dodo.disablevillagertrade.common.CommandHandler;
import me.dodo.disablevillagertrade.neoforge.DisableVillagerTradeNeoForge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

import java.util.List;

/**
 * Registers the {@code /dvt} command for the NeoForge platform.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /dvt reload} — reloads the configuration file</li>
 *   <li>{@code /dvt status} — shows current mod status</li>
 *   <li>{@code /dvt help}   — lists available commands</li>
 * </ul>
 * All subcommands require operator level&nbsp;2.
 */
public final class DvtCommand {

    private DvtCommand() {
        // Utility class
    }

    /**
     * Registers the {@code /dvt} command tree with the given dispatcher.
     *
     * @param dispatcher the server command dispatcher
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("dvt")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("reload")
                    .executes(ctx -> {
                        DisableVillagerTradeNeoForge.getConfig().reload();
                        sendLines(ctx.getSource(), List.of(CommandHandler.reloadSuccessMessage()));
                        return 1;
                    }))
                .then(Commands.literal("status")
                    .executes(ctx -> {
                        sendLines(ctx.getSource(),
                            CommandHandler.buildStatusLines(DisableVillagerTradeNeoForge.getConfig(), "NeoForge"));
                        return 1;
                    }))
                .then(Commands.literal("help")
                    .executes(ctx -> {
                        sendLines(ctx.getSource(), CommandHandler.buildHelpLines());
                        return 1;
                    }))
                .executes(ctx -> {
                    sendLines(ctx.getSource(), CommandHandler.buildHelpLines());
                    return 1;
                })
        );
    }

    private static void sendLines(CommandSourceStack source, List<String> lines) {
        for (String line : lines) {
            source.sendSuccess(() -> Component.literal(line), false);
        }
    }
}
