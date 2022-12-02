package fesa.needyfesa.mixin;

import com.google.gson.JsonObject;
import fesa.needyfesa.AutoVote;
import fesa.needyfesa.ChestFinder;
import fesa.needyfesa.NeedyFesa;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
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

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mixin(ChatHud.class)
public class AutoMessages {

	@Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V")
	private void addMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh, CallbackInfo info) {
		assert MinecraftClient.getInstance().player != null;
		assert MinecraftClient.getInstance().world != null;
		ClientPlayerEntity p = MinecraftClient.getInstance().player;
		ClientWorld world = MinecraftClient.getInstance().world;


		ScoreboardObjective currentScoreboard = world.getScoreboard().getObjectiveForSlot(1);

		String joinRegex = "\\[\\+\\] .{0,3}" + p.getName().getString() + ".{0,3} joined your game \\(\\d{1,3}\\/\\d{1,3}\\)\\.";

		for (int i = 0; i < NeedyFesa.staticAutoMessages.size(); i++) {
			JsonObject autoMessage = NeedyFesa.staticAutoMessages.get(i).getAsJsonObject();
			String regexString = autoMessage.get("regex").getAsString();

			regexString = regexString.replace("&player", p.getName().getString());

			String msg = autoMessage.get("msg").getAsString();


			if (message.getString().matches(regexString)) {
				if (autoMessage.get("command").getAsBoolean()) {
					p.sendCommand(msg);
				}

				if (autoMessage.get("chat").getAsBoolean()) {
					p.sendChatMessage(msg, Text.of(msg));
				}

				if (autoMessage.get("sound").getAsBoolean()) {
					this.playSound(autoMessage.get("sound_id").getAsString());
				}
			}
		}

		// Chest Finder
		if (message.getString().contains("Friends, Parties, Extra help")) {
			Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
				ChestFinder.chestRequest(10);
			}, 1000, TimeUnit.MILLISECONDS);

		}

		// Party Status tracker
		if (message.getString().matches("You have joined [a-zA-Z0-9_]{2,16}'s party!")) {
			NeedyFesa.partyStatus = true;
		}
		if (message.getString().matches("You have left your party!")
			|| message.getString().matches("You were kicked from your party!")
			|| message.getString().matches("The party has been disbanded!")) {
			NeedyFesa.partyStatus = false;
		}

		if (message.getString().matches("[a-zA-Z0-9_]{2,16} joined the party!") && !NeedyFesa.partyStatus) {
			NeedyFesa.partyStatus = true;
		}

		// Game Tracker
		if (currentScoreboard != null) {
			if (message.getString().matches(joinRegex)) {
				NeedyFesa.game = currentScoreboard.getDisplayName().getString();

				// Update map
				ScoreboardPlayerScore lastEntry = null;

				for (ScoreboardPlayerScore scoreboardPlayerScore : currentScoreboard.getScoreboard().getAllPlayerScores(currentScoreboard)) {
					if (scoreboardPlayerScore.getPlayerName().contains("Map:")) {
						assert lastEntry != null;
						NeedyFesa.gameMap = lastEntry.getPlayerName().substring(2);
						break;
					}
					lastEntry = scoreboardPlayerScore;
				}

				// Auto Vote
				if (NeedyFesa.needyFesaConfig.get("autoVote").getAsBoolean() && NeedyFesa.needyFesaConfig.has(NeedyFesa.game)) {
					JsonObject voteInfo = NeedyFesa.needyFesaConfig.getAsJsonObject(NeedyFesa.game);

					int leftVoteId = voteInfo.get("leftVoteId").getAsInt();
					int middleVoteId = voteInfo.get("middleVoteId").getAsInt();
					int rightVoteId = voteInfo.get("rightVoteId").getAsInt();
					int leftChoiceId = voteInfo.get("leftChoiceId").getAsInt();
					int middleChoiceId = voteInfo.get("middleChoiceId").getAsInt();
					int rightChoiceId = voteInfo.get("rightChoiceId").getAsInt();
					int hotBarSlot = voteInfo.get("hotBarSlot").getAsInt();

					int minWaitTime = NeedyFesa.needyFesaConfig.get("minWaitTime").getAsInt();

					AutoVote.vote(minWaitTime, leftVoteId, middleVoteId, rightVoteId, leftChoiceId, middleChoiceId, rightChoiceId, hotBarSlot);
				}
			}
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


