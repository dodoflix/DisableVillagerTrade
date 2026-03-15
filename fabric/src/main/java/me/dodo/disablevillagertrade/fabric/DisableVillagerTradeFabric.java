package me.dodo.disablevillagertrade.fabric;

import me.dodo.disablevillagertrade.common.Constants;
import me.dodo.disablevillagertrade.common.TradeBlocker;
import me.dodo.disablevillagertrade.common.UpdateChecker;
import me.dodo.disablevillagertrade.fabric.config.FabricConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main mod class for DisableVillagerTrade (Fabric).
 */
public class DisableVillagerTradeFabric implements ModInitializer {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME);
    
    private static DisableVillagerTradeFabric instance;
    private static FabricConfig config;
    private static TradeBlocker tradeBlocker;
    private static UpdateChecker updateChecker;
    private static ScheduledExecutorService scheduler;
    
    @Override
    public void onInitialize() {
        instance = this;
        
        LOGGER.info("Initializing DisableVillagerTrade for Fabric...");
        
        // Load configuration
        config = new FabricConfig();
        
        // Initialize trade blocker
        tradeBlocker = new TradeBlocker();
        
        // Server lifecycle events
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        
        // Player join event for update notifications
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (config.isNotifyOnJoin()) {
                onPlayerJoin(handler.getPlayer(), server);
            }
        });
        
        LOGGER.info("DisableVillagerTrade initialized!");
    }
    
    private void onServerStarted(MinecraftServer server) {
        // Initialize update checker
        if (config.isUpdateCheckerEnabled()) {
            String version = net.fabricmc.loader.api.FabricLoader.getInstance()
                .getModContainer(Constants.MOD_ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
            
            updateChecker = new UpdateChecker(
                version,
                "DisableVillagerTrade-Fabric/" + version,
                msg -> LOGGER.info(msg)
            );
            
            // Start periodic update checks
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(
                () -> updateChecker.checkForUpdates(),
                0,
                config.getUpdateCheckInterval(),
                TimeUnit.HOURS
            );
        }
    }
    
    private void onServerStopping(MinecraftServer server) {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
    
    private void onPlayerJoin(ServerPlayer player, MinecraftServer server) {
        if (updateChecker != null && updateChecker.isUpdateAvailable()) {
            // Check if player has update permission (op level 2+)
            if (hasPermission(player, Constants.PERMISSION_UPDATE)) {
                String message = config.getUpdateMessage()
                    .replace("%current%", updateChecker.getCurrentVersion())
                    .replace("%latest%", updateChecker.getLatestVersion());
                
                // Send message with slight delay
                server.execute(() -> {
                    player.sendSystemMessage(Component.literal(message));
                });
            }
        }
    }
    
    /**
     * Checks if a player has a permission.
     * Uses op level check (level 2 = operator).
     */
    public static boolean hasPermission(ServerPlayer player, String permission) {
        return player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }
    
    public static DisableVillagerTradeFabric getInstance() {
        return instance;
    }
    
    public static FabricConfig getConfig() {
        return config;
    }
    
    public static TradeBlocker getTradeBlocker() {
        return tradeBlocker;
    }
}
