package me.dodo.disablevillagertrade.fabric.mixin;

import me.dodo.disablevillagertrade.common.Constants;
import me.dodo.disablevillagertrade.fabric.DisableVillagerTradeFabric;
import me.dodo.disablevillagertrade.fabric.config.FabricConfig;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to intercept villager interactions and block trading.
 */
@Mixin(Villager.class)
public abstract class VillagerMixin {
    
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        // Only process on server side
        if (player.level().isClientSide()) {
            return;
        }
        
        Villager villager = (Villager) (Object) this;
        FabricConfig config = DisableVillagerTradeFabric.getConfig();
        
        // Get profession name from VillagerData - profession() returns Holder<VillagerProfession>
        Holder<VillagerProfession> professionHolder = villager.getVillagerData().profession();
        String professionName = professionHolder.unwrapKey()
            .map(key -> key.identifier().getPath().toUpperCase())
            .orElse("NONE");
        
        // Get dimension name
        String dimensionName = player.level().dimension().identifier().toString();
        
        // Check bypass permission (only for server players)
        boolean hasBypass = false;
        if (player instanceof ServerPlayer serverPlayer) {
            hasBypass = DisableVillagerTradeFabric.hasPermission(serverPlayer, Constants.PERMISSION_BYPASS);
        }
        
        // Check if trade should be blocked
        boolean shouldBlock = DisableVillagerTradeFabric.getTradeBlocker().shouldBlockTrade(
            true,
            professionName,
            !villager.isNoAi(),       // hasAI
            !villager.isNoGravity(),  // hasGravity
            dimensionName,
            config.getDisabledWorlds(),
            hasBypass
        );
        
        if (shouldBlock) {
            // Cancel the interaction
            cir.setReturnValue(InteractionResult.FAIL);
            
            // Send message to player
            if (config.isMessageEnabled() && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.literal(config.getMessage()));
            }
        }
    }
}
