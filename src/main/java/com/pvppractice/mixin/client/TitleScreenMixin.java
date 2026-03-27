package com.pvppractice.mixin.client;

import com.pvppractice.client.gui.PVPDashboardScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    
    protected TitleScreenMixin(Text title) {
        super(title);
    }
    
    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        // Add custom button to title screen
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§c❤ PVP Practice"),
            button -> {
                if (this.client != null) {
                    this.client.setScreen(new PVPDashboardScreen());
                }
            }
        ).dimensions(this.width / 2 + 104, this.height / 4 + 48 + 24, 98, 20).build());
    }
    
    @Inject(method = "render", at = @At(value = "HEAD"))
    private void onRender(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Custom title screen modifications
    }
    
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void onTick(CallbackInfo ci) {
        // Title screen tick logic
    }
}
