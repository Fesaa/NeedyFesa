package fesa.needyfesa.mixin;

import com.google.gson.JsonObject;
import fesa.needyfesa.NeedyFesa;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ReplaceMessages {

    @Inject(at = @At("HEAD"), method = "sendMessage", cancellable = true)
    public void sendMessage(String msg, boolean addToHistory, CallbackInfoReturnable callbackInfoReturnable) {
        String originalMessage = msg;
        for (int i = 0; i < NeedyFesa.staticReplaceMessages.size(); i++) {
            JsonObject replaceMessage = NeedyFesa.staticReplaceMessages.get(i).getAsJsonObject();
            if (msg.contains(replaceMessage.get("text").getAsString())) {
                msg = msg.replace(replaceMessage.get("text").getAsString(), replaceMessage.get("msg").getAsString());
            }
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity p = mc.player;
        if (p != null && !msg.equals(originalMessage)) {
            p.sendChatMessage(msg, Text.of(msg));
            mc.inGameHud.getChatHud().addToMessageHistory(msg);
            mc.setScreen(null);
            callbackInfoReturnable.cancel();
        }
    }
}
