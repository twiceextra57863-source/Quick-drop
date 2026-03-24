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
    
    @Inject(method = "onUse", at = @At("HEAD"))
    private void onChestUse(PlayerEntity player, World world, BlockPos pos, Hand hand, 
                            BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        
        if (world != null && world.isClient && QuickChestMod.isEnabled()) {
            MinecraftClient client = MinecraftClient.getInstance();
            
            if (QuickChestMod.canPerformAction() && client.player != null) {
                if (!client.player.getMainHandStack().isEmpty()) {
                    client.player.dropSelectedItem(false);
                    QuickChestMod.LOGGER.info("Dropped item from hand");
                }
            }
        }
    }
}
