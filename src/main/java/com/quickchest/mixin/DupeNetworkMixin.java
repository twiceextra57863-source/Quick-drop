package com.quickchest.mixin;

import com.quickchest.DupeSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

import java.util.Collections;

@Mixin(ClientPlayNetworkHandler.class)
public class DupeNetworkMixin {

    @Unique
    private int autoTickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Mod ON hai aur koi Screen (Chest/Hopper) khuli hai
        if (DupeSettings.isEnabled && client.player != null && client.currentScreen != null) {
            
            autoTickCounter++;

            // Hopper 8 ticks (0.4s) mein transfer karta hai. Hum 7th tick par "Snipe" karenge.
            if (autoTickCounter >= 7) {
                int syncId = client.player.currentScreenHandler.syncId;
                int revision = client.player.currentScreenHandler.getRevision();

                // 1. STEP: ITEM SNATCHING (SPAM)
                // Ek hi tick mein 10 baar shift-click packet bhej rahe hain
                for (int i = 0; i < 10; i++) {
                    client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                        syncId,
                        revision,
                        0, // Pehla slot (Slot 0)
                        0, 
                        SlotActionType.QUICK_MOVE, 
                        ItemStack.EMPTY,
                        Int2ObjectMaps.emptyMap()
                    ));
                }

                // 2. STEP: AUTO-CLOSE (DESYNC)
                // Item nikalne ke theek baad screen band karne ka packet bhej rahe hain
                // Isse server ko "Final Inventory State" update karne ka time nahi milta
                client.getNetworkHandler().sendPacket(new CloseScreenC2SPacket(syncId));
                
                // Client-side screen ko bhi band kar dena taaki loop crash na ho
                client.execute(() -> client.player.closeHandledScreen());

                autoTickCounter = 0; 
            }
        } else {
            autoTickCounter = 0; 
        }
    }
}
