package fesa.needyfesa.mixin;

import com.google.gson.JsonObject;
import fesa.needyfesa.cubeCode.ChestFinder;
import fesa.needyfesa.NeedyFesa;
import fesa.needyfesa.cubeCode.CubeVarManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mixin(ChatHud.class)
public class ChatHudMixin {

	@Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V")
	private void addMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh, CallbackInfo info) {
		assert MinecraftClient.getInstance().player != null;
		ClientPlayerEntity p = MinecraftClient.getInstance().player;

		for (int i = 0; i < NeedyFesa.configManager.staticAutoMessages.size(); i++) {
			JsonObject autoMessage = NeedyFesa.configManager.staticAutoMessages.get(i).getAsJsonObject();
			String regexString = autoMessage.get("regex").getAsString();

			regexString = regexString.replace("&player", p.getName().getString());

			String msg = autoMessage.get("msg").getAsString();
			boolean alreadyMatched = false;

			if (autoMessage.get("regex_matching").getAsBoolean()) {
				Matcher matcher = NeedyFesa.configManager.configCompiledRegex.get(msg).matcher(message.getString());
				if (matcher.matches()) {
					alreadyMatched = true;
					for (int j = 0; j < matcher.groupCount(); j++) {
						msg = msg.replace("&" + (j+1), matcher.group(j+1));
					}
				} else {
					continue;
				}
			}

			if (alreadyMatched || message.getString().matches(regexString)) {
				if (autoMessage.get("command").getAsBoolean()) {
					p.sendCommand(msg);
				}

				if (autoMessage.get("chat").getAsBoolean()) {
					p.sendChatMessage(msg, Text.of(msg));
				}

				if (autoMessage.get("party_message").getAsBoolean() && CubeVarManager.partyStatus) {
					p.sendChatMessage("@" + msg, Text.of("@" + msg));
				}

				if (autoMessage.get("sound").getAsBoolean()) {
					this.playSound(autoMessage.get("sound_id").getAsString());
				}
			}
		}

		// Chest Finder
		if (message.getString().matches("A chest has been hidden somewhere in the Lobby with some goodies inside!")) {
			Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
				if (CubeVarManager.name.equals("CubeCraft")) {
					ChestFinder.chestRequest(10);
				}
			}, 1000, TimeUnit.MILLISECONDS);

		}

		// Party Status tracker
		if (message.getString().matches("You have joined [a-zA-Z0-9_]{2,16}'s party!")) {
			CubeVarManager.partyStatus = true;
		}
		if (message.getString().matches("You have left your party!")
			|| message.getString().matches("You were kicked from your party!")
			|| message.getString().matches("The party has been disbanded!")) {
			CubeVarManager.partyStatus = false;
		}

		if (message.getString().matches("[a-zA-Z0-9_]{2,16} joined the party!") && !CubeVarManager.partyStatus) {
			CubeVarManager.partyStatus = true;
		}
	}

	private void playSound(String s) {
		SoundEvent sound = Registry.SOUND_EVENT.get(new Identifier(s));
		assert MinecraftClient.getInstance().world != null;
		assert MinecraftClient.getInstance().player != null;
		MinecraftClient.getInstance().world.playSound(MinecraftClient.getInstance().player.getX(), MinecraftClient.getInstance().player.getY(),
				MinecraftClient.getInstance().player.getZ(), sound, SoundCategory.MASTER, 1f, 1f, true);
	}

}


