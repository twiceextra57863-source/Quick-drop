package com.tclient.mod.mixin;

import com.tclient.mod.data.ProfileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class KillTrackerMixin {
    
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        PlayerEntity victim = (PlayerEntity)(Object)this;
        
        if (source.getAttacker() instanceof PlayerEntity) {
            PlayerEntity killer = (PlayerEntity) source.getAttacker();
            
            // Add kill to killer
            ProfileManager.getInstance().getOrCreateProfile(
                killer.getUuid(), killer.getName().getString()
            ).addKill();
            
            // Add death to victim
            ProfileManager.getInstance().getOrCreateProfile(
                victim.getUuid(), victim.getName().getString()
            ).addDeath();
        }
    }
}
