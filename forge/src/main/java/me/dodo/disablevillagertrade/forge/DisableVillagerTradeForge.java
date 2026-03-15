package me.dodo.disablevillagertrade.forge;

import me.dodo.disablevillagertrade.common.Constants;
import me.dodo.disablevillagertrade.common.TradeBlocker;
import me.dodo.disablevillagertrade.common.UpdateChecker;
import me.dodo.disablevillagertrade.forge.config.ForgeConfig;
import me.dodo.disablevillagertrade.forge.events.VillagerTradeHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main mod class for DisableVillagerTrade (Forge).
 * Uses Forge 60.x API patterns based on simple-voice-chat implementation.
 */
@Mod(Constants.MOD_ID)
public class DisableVillagerTradeForge {
    
    public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);
    
    private static DisableVillagerTradeForge instance;
    private static ForgeConfig config;
    private static TradeBlocker tradeBlocker;
    private static UpdateChecker updateChecker;
    private static ScheduledExecutorService scheduler;
    private final VillagerTradeHandler tradeHandler;
    
    public DisableVillagerTradeForge(FMLJavaModLoadingContext context) {
        instance = this;
        tradeHandler = new VillagerTradeHandler();
        
        LOGGER.info("Initializing DisableVillagerTrade for Forge...");
        
        // Initialize config (using simple file-based config, not ForgeConfigSpec)
        config = new ForgeConfig();
        
        // Initialize trade blocker
        tradeBlocker = new TradeBlocker();
        
        // Register mod lifecycle event
        FMLCommonSetupEvent.getBus(context.getModBusGroup()).addListener(this::commonSetup);
        
        LOGGER.info("DisableVillagerTrade initialized!");
    }
    
    public void commonSetup(FMLCommonSetupEvent event) {
        // Register game events using the new Forge 60.x API
        ServerStartedEvent.BUS.addListener(this::onServerStarted);
        ServerStoppingEvent.BUS.addListener(this::onServerStopping);
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(this::onPlayerJoin);
        PlayerInteractEvent.EntityInteract.BUS.addListener(tradeHandler::onPlayerInteractEntity);
    }
    
    public void onServerStarted(ServerStartedEvent event) {
        // Initialize update checker
        if (config.isUpdateCheckerEnabled()) {
            String version = ModList.get()
                .getModContainerById(Constants.MOD_ID)
                .map(c -> c.getModInfo().getVersion().toString())
                .orElse("unknown");
            
            updateChecker = new UpdateChecker(
                version,
                "DisableVillagerTrade-Forge/" + version,
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
    
    public void onServerStopping(ServerStoppingEvent event) {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
    
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (config.isNotifyOnJoin() && updateChecker != null && updateChecker.isUpdateAvailable()) {
                // Check if player has update permission (op level 2+)
                if (player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
                    String message = config.getUpdateMessage()
                        .replace("%current%", updateChecker.getCurrentVersion())
                        .replace("%latest%", updateChecker.getLatestVersion());
                    
                    player.sendSystemMessage(Component.literal(message));
                }
            }
        }
    }
    
    public static DisableVillagerTradeForge getInstance() {
        return instance;
    }
    
    public static ForgeConfig getConfig() {
        return config;
    }
    
    public static TradeBlocker getTradeBlocker() {
        return tradeBlocker;
    }
}
