package com.pvppractice.mixin.client;

import com.pvppractice.client.gui.PVPDashboardScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    
    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        TitleScreen titleScreen = (TitleScreen)(Object)this;
        
        // Create the button
        ButtonWidget pvpButton = ButtonWidget.builder(
            Text.literal("§c❤ PVP Practice"),
            button -> {
                MinecraftClient.getInstance().setScreen(new PVPDashboardScreen());
            }
        ).dimensions(titleScreen.width / 2 + 104, titleScreen.height / 4 + 48 + 24, 98, 20).build();
        
        // Directly add to children list using reflection (works but less elegant)
        try {
            java.lang.reflect.Field childrenField = net.minecraft.client.gui.screen.Screen.class.getDeclaredField("children");
            childrenField.setAccessible(true);
            java.util.List<?> children = (java.util.List<?>) childrenField.get(titleScreen);
            children.add(pvpButton);
            
            java.lang.reflect.Field selectablesField = net.minecraft.client.gui.screen.Screen.class.getDeclaredField("selectables");
            selectablesField.setAccessible(true);
            java.util.List<?> selectables = (java.util.List<?>) selectablesField.get(titleScreen);
            selectables.add(pvpButton);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Also need to add to drawables
        try {
            java.lang.reflect.Field drawablesField = net.minecraft.client.gui.screen.Screen.class.getDeclaredField("drawables");
            drawablesField.setAccessible(true);
            java.util.List<?> drawables = (java.util.List<?>) drawablesField.get(titleScreen);
            drawables.add(pvpButton);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
