package com.quickchest.mixin;

import com.quickchest.DupeSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps; // <--- Mandatory Import

@Mixin(ClientPlayNetworkHandler.class)
public class DupeNetworkMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (DupeSettings.isEnabled && client.player != null && client.currentScreen != null) {
            
            int syncId = client.player.currentScreenHandler.syncId;
            int revision = client.player.currentScreenHandler.getRevision();

            client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                syncId,
                revision,
                0, 
                0, 
                SlotActionType.QUICK_MOVE, 
                ItemStack.EMPTY,
                Int2ObjectMaps.emptyMap() // Fixes the Map Incompatible Type Error
            ));
        }
    }
}
