package com.quickchest.mixin;

import com.quickchest.QuickChestMod;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin {
    
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onChestUse(PlayerEntity player, World world, BlockPos pos, Hand hand, 
                            BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        
        if (world.isClient && QuickChestMod.isEnabled() && QuickChestMod.canPerformAction()) {
            MinecraftClient client = MinecraftClient.getInstance();
            
            if (client.player != null && !client.player.getMainHandStack().isEmpty()) {
                // Drop the item
                client.player.dropSelectedItem(false);
                QuickChestMod.LOGGER.info("Dropped item from hand");
            }
        }
    }
}
