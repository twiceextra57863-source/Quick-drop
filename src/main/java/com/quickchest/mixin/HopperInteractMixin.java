package com.quickchest.mixin;

import com.quickchest.QuickChestMod;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlock.class)
public class HopperInteractMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onHopperUse(
        BlockState state, World world, BlockPos pos,
        PlayerEntity player, BlockHitResult hit,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (!world.isClient()) return;
        if (!QuickChestMod.isEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (!player.getUuid().equals(client.player.getUuid())) return;
        if (client.player.getMainHandStack().isEmpty()) return;

        if (QuickChestMod.handleContainerClick(pos, ContainerType.HOPPER)) {
            cir.setReturnValue(ActionResult.SUCCESS);
            cir.cancel();
        }
    }
}
