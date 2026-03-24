package com.quickchest.mixin;

import com.quickchest.QuickChestMod;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class ChestInteractMixin {

    /**
     * 1.21.4: onUseWithItem replaced onUse in many blocks
     * We inject into both to be safe
     */
    @Inject(
        method = "onUseWithItem",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onChestUseWithItem(
        net.minecraft.item.ItemStack stack,
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        BlockHitResult hit,
        CallbackInfoReturnable<net.minecraft.item.ItemActionResult> cir
    ) {
        if (!world.isClient) return;
        if (!QuickChestMod.isEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !player.equals(client.player)) return;

        boolean handled = QuickChestMod.handleChestClick(pos);
        if (handled) {
            cir.setReturnValue(net.minecraft.item.ItemActionResult.SUCCESS);
            cir.cancel();
        }
    }

    @Inject(
        method = "onUse",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onChestUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        BlockHitResult hit,
        CallbackInfoReturnable<ActionResult> cir
    ) {
        if (!world.isClient) return;
        if (!QuickChestMod.isEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !player.equals(client.player)) return;

        boolean handled = QuickChestMod.handleChestClick(pos);
        if (handled) {
            cir.setReturnValue(ActionResult.SUCCESS);
            cir.cancel();
        }
    }
}
