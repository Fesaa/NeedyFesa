package fesa.needyfesa.mixin;

import com.google.gson.JsonObject;
import fesa.needyfesa.NeedyFesa;
import fesa.needyfesa.needyFesaManagerClasses.AntiSpamMessageClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.LocalDateTime;
import java.util.HashMap;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    private final HashMap<String, AntiSpamMessageClass> antiSpamHashMap = new HashMap<>();

    @Inject(at = @At("HEAD"), method = "sendMessage", cancellable = true)
    public void sendMessage(String msg, boolean addToHistory, CallbackInfoReturnable callbackInfoReturnable) {
        String originalMessage = msg;
        for (int i = 0; i < NeedyFesa.configManager.staticReplaceMessages.size(); i++) {
            JsonObject replaceMessage = NeedyFesa.configManager.staticReplaceMessages.get(i).getAsJsonObject();
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

        // Anti Spam logic
        if (NeedyFesa.configManager.needyFesaConfig.get("spam-prevention").getAsBoolean()) {
            LocalDateTime now = LocalDateTime.now();

            if (antiSpamHashMap.containsKey(msg)) {
                antiSpamHashMap.get(msg).update(now);

                if (!antiSpamHashMap.get(msg).safe()) {
                    String warningString = "You have already send your message 2 times in the past 2 minutes. Are you sure you want to send it?";
                    Text warningText = Text.literal(warningString);
                }
            } else {
                antiSpamHashMap.put(msg, (new AntiSpamMessageClass()).create(now));
            }

            // Cleaning up old unused messages
            for (String key : antiSpamHashMap.keySet()) {
                if (antiSpamHashMap.get(key).checkToDelete()) {
                    antiSpamHashMap.remove(key);
                }
            }
        }
    }
}
