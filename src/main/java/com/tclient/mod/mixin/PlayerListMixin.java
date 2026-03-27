package com.tclient.mod.mixin;

import com.tclient.mod.data.PlayerProfile;
import com.tclient.mod.data.ProfileManager;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerListHud.class)
public class PlayerListMixin {
    
    @ModifyVariable(method = "getPlayerName", at = @At("STORE"), ordinal = 0)
    private Text modifyPlayerName(Text text, PlayerListEntry entry) {
        if (entry != null && entry.getProfile() != null) {
            PlayerProfile profile = ProfileManager.getInstance()
                .getProfile(entry.getProfile().getId());
            if (profile != null) {
                return Text.literal(profile.getFormattedName());
            }
        }
        return text;
    }
}
