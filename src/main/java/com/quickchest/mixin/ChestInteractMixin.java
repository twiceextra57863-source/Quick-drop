package com.quickchest.mixin;

import com.quickchest.QuickChestMod;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class ChestInteractMixin {

    @Inject(
        method = "onUseWithItem",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onChestUseWithItem(
        ItemStack stack,
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        BlockHitResult hit,
        CallbackInfoReturnable<ItemActionResult> cir
    ) {
        if (!world.isClient()) return;
        if (!QuickChestMod.isEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (!player.getUuid().equals(client.player.getUuid())) return;

        boolean handled = QuickChestMod.handleChestClick(pos);
        if (handled) {
            cir.setReturnValue(ItemActionResult.SUCCESS);
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
        if (!world.isClient()) return;
        if (!QuickChestMod.isEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (!player.getUuid().equals(client.player.getUuid())) return;

        boolean handled = QuickChestMod.handleChestClick(pos);
        if (handled) {
            cir.setReturnValue(ActionResult.SUCCESS);
            cir.cancel();
        }
    }
}
