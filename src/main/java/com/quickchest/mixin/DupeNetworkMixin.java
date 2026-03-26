package com.example.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@Mixin(ClientPlayNetworkHandler.class)
public class DupeNetworkMixin {

    // Har game tick par check karne ke liye
    @Inject(method = "onTick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        // Agar player kisi container (Hopper/Chest) ko dekh raha hai
        if (client.player != null && client.currentScreen != null) {
            // Yahan hum "SyncId" nikalte hain jo current open window ki hoti hai
            int syncId = client.player.currentScreenHandler.syncId;
            
            // Logic: Agar dupe mode on hai (aap yahan toggle key check kar sakte hain)
            // Hum slot 0 (Hopper ka pehla slot) se item 'Quick Move' (Shift+Click) karne ka packet bhejte hain
            if (isDupeActive()) { 
                client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                    syncId, 
                    0, // Slot ID (Hopper ka pehla slot)
                    0, // Button (Left Click)
                    SlotActionType.QUICK_MOVE, 
                    ItemStack.EMPTY, 
                    Collections.emptyMap()
                ));
            }
        }
    }

    private boolean isDupeActive() {
        // Aap yahan apni Keybinding ya toggle logic daal sakte hain
        return true; 
    }
}

