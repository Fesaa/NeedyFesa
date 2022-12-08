package fesa.needyfesa.mixin;

import fesa.needyfesa.cubeCode.VarManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(at = @At(value = "INVOKE", target= "Lnet/minecraft/client/MinecraftClientGame;onLeaveGameSession()V"), method="disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    public void onDisconnect(Screen s, CallbackInfo info) {
        VarManager.partyStatus = false;
    }
}
