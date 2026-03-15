package me.dodo.disablevillagertrade.neoforge.events;

import me.dodo.disablevillagertrade.neoforge.DisableVillagerTradeNeoForge;
import me.dodo.disablevillagertrade.neoforge.config.NeoForgeConfig;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Handles villager interaction events for NeoForge.
 */
public class VillagerTradeHandler {
    
    @SubscribeEvent
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
        boolean shouldBlock = DisableVillagerTradeNeoForge.getTradeBlocker().shouldBlockTrade(
            true,
            professionName,
            !villager.isNoAi(),      // hasAI
            !villager.isNoGravity(), // hasGravity
            dimensionName,
            NeoForgeConfig.DISABLED_DIMENSIONS.get().stream()
                .map(Object::toString)
                .toList(),
            hasBypass
        );
        
        if (shouldBlock) {
            // Cancel the interaction
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
            
            // Send message to player
            if (NeoForgeConfig.MESSAGE_ENABLED.get()) {
                player.sendSystemMessage(Component.literal(NeoForgeConfig.MESSAGE_TEXT.get()));
            }
        }
    }
}
