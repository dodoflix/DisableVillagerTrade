package me.dodo.disablevillagertrade.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds command response lines shared by all platform command implementations.
 * All methods are stateless and return plain strings without platform-specific
 * color codes so each platform can apply its own formatting.
 */
public final class CommandHandler {

    private CommandHandler() {
        // Utility class
    }

    /**
     * Builds the lines to send in response to the {@code /dvt status} command.
     *
     * @param config       the active mod configuration
     * @param platformName human-readable platform name (e.g. "Fabric", "Forge")
     * @return ordered list of plain-text response lines
     */
    public static List<String> buildStatusLines(ModConfig config, String platformName) {
        List<String> lines = new ArrayList<>();
        lines.add("=== " + Constants.MOD_NAME + " Status ===");
        lines.add("Platform: " + platformName);
        lines.add("Message enabled: " + config.isMessageEnabled());
        lines.add("Message: " + config.getMessage());

        List<String> disabledWorlds = config.getDisabledWorlds();
        if (disabledWorlds == null || disabledWorlds.isEmpty()) {
            lines.add("Disabled worlds: (none)");
        } else {
            lines.add("Disabled worlds: " + String.join(", ", disabledWorlds));
        }

        lines.add("Update checker: " + config.isUpdateCheckerEnabled());
        if (config.isUpdateCheckerEnabled()) {
            lines.add("Check interval: " + config.getUpdateCheckInterval() + "h");
            lines.add("Notify on join: " + config.isNotifyOnJoin());
        }

        return lines;
    }

    /**
     * Builds the lines to send in response to the {@code /dvt help} command.
     *
     * @return ordered list of plain-text help lines
     */
    public static List<String> buildHelpLines() {
        List<String> lines = new ArrayList<>();
        lines.add("=== " + Constants.MOD_NAME + " Commands ===");
        lines.add("/dvt reload   - Reload configuration");
        lines.add("/dvt status   - Show mod status");
        lines.add("/dvt help     - Show this help");
        return lines;
    }

    /**
     * Returns the message to send after a successful configuration reload.
     *
     * @return the reload success message
     */
    public static String reloadSuccessMessage() {
        return "Configuration reloaded successfully!";
    }
}
