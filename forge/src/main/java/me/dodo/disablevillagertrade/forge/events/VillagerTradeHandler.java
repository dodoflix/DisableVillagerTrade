package me.dodo.disablevillagertrade.forge.events;

import me.dodo.disablevillagertrade.forge.DisableVillagerTradeForge;
import me.dodo.disablevillagertrade.forge.config.ForgeConfig;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Handles villager interaction events for Forge.
 * Uses Forge 60.x event handler pattern (no @SubscribeEvent annotation).
 */
public class VillagerTradeHandler {
    
    public void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        // Only process villager interactions
        if (!(event.getTarget() instanceof Villager villager)) {
            return;
        }
        
        // Only process on server side
        if (event.getLevel().isClientSide()) {
            return;
        }
        
        // Only process for server players
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        
        ForgeConfig config = DisableVillagerTradeForge.getConfig();
        
        // Get profession name - profession() returns Holder<VillagerProfession>
        Holder<VillagerProfession> professionHolder = villager.getVillagerData().profession();
        String professionName = professionHolder.unwrapKey()
            .map(key -> key.identifier().getPath().toUpperCase())
            .orElse("NONE");
        
        // Get dimension name
        String dimensionName = player.level().dimension().identifier().toString();
        
        // Check bypass permission (op level 2+)
        boolean hasBypass = player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
        
        // Check if trade should be blocked
        boolean shouldBlock = DisableVillagerTradeForge.getTradeBlocker().shouldBlockTrade(
            true,
            professionName,
            !villager.isNoAi(),      // hasAI is inverted
            !villager.isNoGravity(), // hasGravity is inverted
            dimensionName,
            config.getDisabledWorlds(),
            hasBypass
        );
        
        if (shouldBlock) {
            // Cancel the interaction - use setCancellationResult for Forge 60.x
            event.setCancellationResult(InteractionResult.FAIL);
            
            // Send message to player
            if (config.isMessageEnabled()) {
                player.sendSystemMessage(Component.literal(config.getMessage()));
            }
        }
    }
}
