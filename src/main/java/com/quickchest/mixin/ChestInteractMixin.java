package com.quickchest.mixin;

import com.quickchest.QuickChestMod;
import net.minecraft.block.ChestBlock;
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

@Mixin(ChestBlock.class)
public class ChestInteractMixin {

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
        // Server side pe kuch mat karo
        if (!world.isClient()) return;
        if (!QuickChestMod.isEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Sirf local player ke liye
        if (!player.getUuid().equals(client.player.getUuid())) return;

        // Haath khali hai toh normal chest open hone do
        if (client.player.getMainHandStack().isEmpty()) return;

        // Quick drop+store karo
        boolean handled = QuickChestMod.handleChestClick(pos);

        if (handled) {
            // Chest GUI open hone se roko
            cir.setReturnValue(ActionResult.SUCCESS);
            cir.cancel();
        }
    }
}
