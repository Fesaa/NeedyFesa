package fesa.needyfesa.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fesa.needyfesa.NeedyFesa;
import fesa.needyfesa.cubeCode.VarManager;
import fesa.needyfesa.needyFesaManagerClasses.AntiSpamMessageClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

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
        if (NeedyFesa.configManager.needyFesaConfig.get("spam-prevention").getAsBoolean()
            && checkServerForAntiSpam()
            && !msg.startsWith("/")) {
            LocalDateTime now = LocalDateTime.now();

            if (VarManager.antiSpamHashMap.containsKey(msg.toLowerCase())) {
                VarManager.antiSpamHashMap.get(msg.toLowerCase()).update(now);

                if (!VarManager.antiSpamHashMap.get(msg.toLowerCase()).safe()) {
                    String warningString =
                            "§3You have already send your message " +
                            (NeedyFesa.configManager.needyFesaConfig.get("spam-count").getAsInt() -1)
                            + " times in the past "
                            + NeedyFesa.configManager.needyFesaConfig.get("spam-time").getAsInt()
                            + " minutes. Are you sure you want to send it?";

                    String finalMsg = msg;
                    Text warningText = Text.literal(warningString);
                    Text confirmSend = Text.literal("§a[YES]").styled(currentStyle -> currentStyle.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "") {
                        private int count = 0;
                        @Override
                        public Action getAction() {
                            if (count == 0) {
                                mc.player.sendChatMessage(finalMsg, Text.of(finalMsg));
                                count++;
                            }
                            return super.getAction();
                        }
                    }));

                    mc.inGameHud.getChatHud().addMessage(warningText);
                    mc.inGameHud.getChatHud().addMessage(confirmSend);
                    mc.inGameHud.getChatHud().addToMessageHistory(msg);
                    mc.setScreen(null);
                    callbackInfoReturnable.cancel();
                }
            } else {
                VarManager.antiSpamHashMap.put(msg.toLowerCase(), (new AntiSpamMessageClass()).create(now));
            }

            // Cleaning up old unused messages
            Set<String> keySet = ((HashMap<String, AntiSpamMessageClass>) VarManager.antiSpamHashMap.clone()).keySet();
            for (String key : keySet) {
                if (VarManager.antiSpamHashMap.get(key).checkToDelete()) {
                    VarManager.antiSpamHashMap.remove(key);
                }
            }
        }
    }

    private boolean checkServerForAntiSpam() {
        for (JsonElement serverIp : NeedyFesa.configManager.needyFesaConfig.get("spam-prevention-servers").getAsJsonArray()) {
            if (serverIp.getAsString().equals(VarManager.serverIP)) {
                return true;
            }
        }
        return false;
    }
}
