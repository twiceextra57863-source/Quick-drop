package com.tclient.mod.mixin;

import com.tclient.mod.data.PlayerProfile;
import com.tclient.mod.data.ProfileManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatFormatterMixin {
    
    @ModifyVariable(method = "onGameMessage", at = @At("HEAD"), argsOnly = true)
    private Text formatChatMessage(Text message) {
        String msg = message.getString();
        
        // Format player names in chat with tier
        for (PlayerProfile profile : ProfileManager.getInstance().getAllProfiles()) {
            if (msg.contains(profile.getUsername())) {
                String formattedName = profile.getTier().color + profile.getUsername() + "§r";
                msg = msg.replace(profile.getUsername(), formattedName);
            }
        }
        
        return Text.literal(msg);
    }
}
