package fesa.needyfesa.mixin;

import fesa.needyfesa.NeedyFesa;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ChatScreen.class)
public class CustomCommands {

    @Inject(at = @At("HEAD"), method = "sendMessage", cancellable = true)
    public void sendMessage(String msg, boolean addToHistory, CallbackInfoReturnable callbackInfoReturnable) {

        if (msg.equals("/needyfesa reload")) {
            NeedyFesa.JsonReload();
            cancelMsg(callbackInfoReturnable, msg);
        }

        if (msg.equalsIgnoreCase("/needyfesa logparty")) {
            NeedyFesa.logParty = !NeedyFesa.logParty;
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("\u00A7bUpdated logParty to " + NeedyFesa.logParty));
            cancelMsg(callbackInfoReturnable, msg);
        }

        if (msg.equals("/needyfesa debug")) {
            String s = "";
            s += "Debug info for Needyfesa";
            s += "\nlogParty: " + NeedyFesa.logParty;
            s += "\neggWarsMap: " + NeedyFesa.eggWarsMap;
            s += "\npartyStatus: " + NeedyFesa.partyStatus;
            s += "\nchestPartyAnnounce: " + NeedyFesa.chestPartyAnnounce;
            if (NeedyFesa.currentChestCoords != null) {
                s += "\ncurrentChestCoords: " + NeedyFesa.currentChestCoords.toShortString();
            }
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(s + "\n"));
            cancelMsg(callbackInfoReturnable, msg);
        }

    }

    private void cancelMsg(CallbackInfoReturnable callbackInfoReturnable, String msg) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(msg);
        MinecraftClient.getInstance().setScreen(null);
        callbackInfoReturnable.cancel();
    }

}
