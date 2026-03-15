package me.dodo.disablevillagertrade.neoforge;

import me.dodo.disablevillagertrade.common.Constants;
import me.dodo.disablevillagertrade.common.TradeBlocker;
import me.dodo.disablevillagertrade.common.UpdateChecker;
import me.dodo.disablevillagertrade.neoforge.config.NeoForgeConfig;
import me.dodo.disablevillagertrade.neoforge.events.VillagerTradeHandler;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main mod class for DisableVillagerTrade (NeoForge).
 */
@Mod(Constants.MOD_ID)
public class DisableVillagerTradeNeoForge {
    
    public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_NAME);
    
    private static DisableVillagerTradeNeoForge instance;
    private static NeoForgeConfig config;
    private static TradeBlocker tradeBlocker;
    private static UpdateChecker updateChecker;
    private static ScheduledExecutorService scheduler;
    
    public DisableVillagerTradeNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;
        
        LOGGER.info("Initializing DisableVillagerTrade for NeoForge...");
        
        // Register config
        modContainer.registerConfig(ModConfig.Type.SERVER, NeoForgeConfig.SPEC, Constants.MOD_ID + "-server.toml");
        config = new NeoForgeConfig();
        
        // Initialize trade blocker
        tradeBlocker = new TradeBlocker();
        
        // Register event handlers
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new VillagerTradeHandler());
        
        LOGGER.info("DisableVillagerTrade initialized!");
    }
    
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        // Initialize update checker
        if (NeoForgeConfig.UPDATE_CHECKER_ENABLED.get()) {
            String version = ModList.get()
                .getModContainerById(Constants.MOD_ID)
                .map(c -> c.getModInfo().getVersion().toString())
                .orElse("unknown");
            
            updateChecker = new UpdateChecker(
                version,
                "DisableVillagerTrade-NeoForge/" + version,
                msg -> LOGGER.info(msg)
            );
            
            // Start periodic update checks
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(
                () -> updateChecker.checkForUpdates(),
                0,
                NeoForgeConfig.UPDATE_CHECK_INTERVAL.get(),
                TimeUnit.HOURS
            );
        }
    }
    
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
    
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (NeoForgeConfig.NOTIFY_ON_JOIN.get() && updateChecker != null && updateChecker.isUpdateAvailable()) {
                // Check if player has update permission (op level 2+)
                if (player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
                    String message = NeoForgeConfig.UPDATE_MESSAGE.get()
                        .replace("%current%", updateChecker.getCurrentVersion())
                        .replace("%latest%", updateChecker.getLatestVersion());
                    
                    player.sendSystemMessage(Component.literal(message));
                }
            }
        }
    }
    
    public static DisableVillagerTradeNeoForge getInstance() {
        return instance;
    }
    
    public static NeoForgeConfig getConfig() {
        return config;
    }
    
    public static TradeBlocker getTradeBlocker() {
        return tradeBlocker;
    }
}

