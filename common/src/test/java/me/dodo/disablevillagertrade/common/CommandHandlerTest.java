package me.dodo.disablevillagertrade.common;

import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommandHandler - Command Response Builder Tests")
class CommandHandlerTest {

    /** Minimal stub config used by most tests. */
    private ModConfig config;

    @BeforeEach
    void setUp() {
        config = new StubModConfig();
    }

    // -------------------------------------------------------------------------
    // Stub
    // -------------------------------------------------------------------------

    private static class StubModConfig implements ModConfig {
        private boolean messageEnabled = true;
        private String message = "You can't trade with villagers.";
        private List<String> disabledWorlds = Collections.emptyList();
        private boolean updateCheckerEnabled = true;
        private int updateCheckInterval = 24;
        private boolean notifyOnJoin = true;

        @Override public boolean isMessageEnabled() { return messageEnabled; }
        @Override public String getMessage() { return message; }
        @Override public List<String> getDisabledWorlds() { return disabledWorlds; }
        @Override public boolean isUpdateCheckerEnabled() { return updateCheckerEnabled; }
        @Override public int getUpdateCheckInterval() { return updateCheckInterval; }
        @Override public boolean isNotifyOnJoin() { return notifyOnJoin; }
        @Override public String getUpdateMessage() { return ""; }
        @Override public void reload() {}
    }

    // -------------------------------------------------------------------------
    // buildStatusLines
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("buildStatusLines")
    class BuildStatusLines {

        @Test
        @DisplayName("Should include platform name in status output")
        void shouldIncludePlatformName() {
            List<String> lines = CommandHandler.buildStatusLines(config, "Fabric");
            assertTrue(lines.stream().anyMatch(l -> l.contains("Fabric")),
                    "Status output must mention the platform name");
        }

        @Test
        @DisplayName("Should include message-enabled state")
        void shouldIncludeMessageEnabled() {
            List<String> lines = CommandHandler.buildStatusLines(config, "Forge");
            assertTrue(lines.stream().anyMatch(l -> l.contains("true")),
                    "Status output must include message-enabled value");
        }

        @Test
        @DisplayName("Should include configured message text")
        void shouldIncludeMessageText() {
            List<String> lines = CommandHandler.buildStatusLines(config, "NeoForge");
            assertTrue(lines.stream().anyMatch(l -> l.contains("You can't trade with villagers.")),
                    "Status output must include the configured message text");
        }

        @Test
        @DisplayName("Should indicate no disabled worlds when list is empty")
        void shouldIndicateNoDisabledWorlds_whenListEmpty() {
            // default stub already has empty list
            List<String> lines = CommandHandler.buildStatusLines(config, "Fabric");
            assertTrue(lines.stream().anyMatch(l -> l.toLowerCase().contains("none")),
                    "Status output must say 'none' when disabled worlds list is empty");
        }

        @Test
        @DisplayName("Should list disabled worlds when present")
        void shouldListDisabledWorlds_whenPresent() {
            StubModConfig stub = new StubModConfig();
            stub.disabledWorlds = List.of("world_nether", "world_end");
            List<String> lines = CommandHandler.buildStatusLines(stub, "Fabric");
            assertTrue(lines.stream().anyMatch(l -> l.contains("world_nether") && l.contains("world_end")),
                    "Status output must list all disabled worlds");
        }

        @Test
        @DisplayName("Should include update checker state")
        void shouldIncludeUpdateCheckerState() {
            List<String> lines = CommandHandler.buildStatusLines(config, "Forge");
            assertTrue(lines.stream().anyMatch(l -> l.toLowerCase().contains("update")),
                    "Status output must include update checker information");
        }

        @Test
        @DisplayName("Should include check interval when update checker enabled")
        void shouldIncludeCheckInterval_whenUpdateCheckerEnabled() {
            List<String> lines = CommandHandler.buildStatusLines(config, "NeoForge");
            assertTrue(lines.stream().anyMatch(l -> l.contains("24")),
                    "Status output must include the update check interval");
        }

        @Test
        @DisplayName("Should not include check interval when update checker disabled")
        void shouldNotIncludeCheckInterval_whenUpdateCheckerDisabled() {
            StubModConfig stub = new StubModConfig();
            stub.updateCheckerEnabled = false;
            List<String> lines = CommandHandler.buildStatusLines(stub, "Fabric");
            assertFalse(lines.stream().anyMatch(l -> l.contains("24")),
                    "Status output must not show check interval when update checker is disabled");
        }

        @Test
        @DisplayName("Should return a non-empty list")
        void shouldReturnNonEmptyList() {
            List<String> lines = CommandHandler.buildStatusLines(config, "Forge");
            assertFalse(lines.isEmpty(), "Status output must not be empty");
        }
    }

    // -------------------------------------------------------------------------
    // buildHelpLines
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("buildHelpLines")
    class BuildHelpLines {

        @Test
        @DisplayName("Should include reload subcommand")
        void shouldIncludeReloadSubcommand() {
            List<String> lines = CommandHandler.buildHelpLines();
            assertTrue(lines.stream().anyMatch(l -> l.toLowerCase().contains("reload")),
                    "Help output must mention 'reload'");
        }

        @Test
        @DisplayName("Should include status subcommand")
        void shouldIncludeStatusSubcommand() {
            List<String> lines = CommandHandler.buildHelpLines();
            assertTrue(lines.stream().anyMatch(l -> l.toLowerCase().contains("status")),
                    "Help output must mention 'status'");
        }

        @Test
        @DisplayName("Should include help subcommand")
        void shouldIncludeHelpSubcommand() {
            List<String> lines = CommandHandler.buildHelpLines();
            assertTrue(lines.stream().anyMatch(l -> l.toLowerCase().contains("help")),
                    "Help output must mention 'help'");
        }

        @Test
        @DisplayName("Should return a non-empty list")
        void shouldReturnNonEmptyList() {
            assertFalse(CommandHandler.buildHelpLines().isEmpty());
        }
    }

    // -------------------------------------------------------------------------
    // reloadSuccessMessage
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("reloadSuccessMessage")
    class ReloadSuccessMessage {

        @Test
        @DisplayName("Should return a non-blank message")
        void shouldReturnNonBlankMessage() {
            String msg = CommandHandler.reloadSuccessMessage();
            assertNotNull(msg);
            assertFalse(msg.isBlank(), "Reload success message must not be blank");
        }
    }
}
