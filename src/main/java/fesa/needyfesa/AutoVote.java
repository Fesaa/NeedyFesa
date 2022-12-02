package fesa.needyfesa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AutoVote {

    private static int leftVoteID = -1;
    private static int rightVoteID = -1;
    private static int middleVoteID = -1;
    private static int rightChoiceID = -1;
    private static int middleChoiceID = -1;



    public static void vote(int minWaitTime, int leftVoteId, int middleVoteId, int rightVoteId, int leftChoiceId, int middleChoiceId, int rightChoiceId, int slot){
        assert MinecraftClient.getInstance().player != null;
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        ClientPlayerInteractionManager clientPlayerInteractionManager =  MinecraftClient.getInstance().interactionManager;
        assert clientPlayerInteractionManager != null;

        PlayerInventory inv =  p.getInventory();
        inv.selectedSlot = slot;
        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
            Hand[] hands = Hand.values();
            clientPlayerInteractionManager.interactItem(MinecraftClient.getInstance().player, hands[0]);
        }, 300, TimeUnit.MILLISECONDS);
        leftVoteID = leftVoteId;
        middleVoteID = middleVoteId;
        rightVoteID = rightVoteId;
        middleChoiceID = middleChoiceId;
        rightChoiceID = rightChoiceId;
        waitForChoiceMenu(clientPlayerInteractionManager, p, minWaitTime, NeedyFesa.needyFesaConfig.get("maxWaitTime").getAsInt(), leftChoiceId, "");
    }

    private static void waitForChoiceMenu(ClientPlayerInteractionManager clientPlayerInteractionManager, ClientPlayerEntity player,
                                          int minWaitTime, int maxWaitTime, int choiceId, String currentTitle) {
        if (maxWaitTime == 0) {
            abortedAutoVote("\u00A7b Aborted after " + maxWaitTime + " while waiting for a choice");
            return;
        }
        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
            if (MinecraftClient.getInstance().currentScreen == null) {
                waitForChoiceMenu(clientPlayerInteractionManager, player, 1, maxWaitTime-1, choiceId, currentTitle);
            } else if (MinecraftClient.getInstance().currentScreen.getTitle().getString().equals(currentTitle)) {
                waitForChoiceMenu(clientPlayerInteractionManager, player, 1, maxWaitTime-1, choiceId, currentTitle);
            } else {
                int syncId = ((HandledScreen<?>) MinecraftClient.getInstance().currentScreen).getScreenHandler().syncId;

                // Seeing which to vote for
                int voteID = -1;
                if (leftVoteID != -1) {
                    voteID = leftVoteID;
                    leftVoteID = -1;
                } else if (middleVoteID != -1) {
                    voteID = middleVoteID;
                    middleVoteID = -1;
                } else if (rightVoteID != -1) {
                    voteID = rightVoteID;
                    rightVoteID = -1;
                }

                if (choiceId != -1) {
                    clientPlayerInteractionManager.clickSlot(syncId, choiceId, 0, SlotActionType.PICKUP, player);
                }
                if (voteID != -1) {
                    waitForVote(clientPlayerInteractionManager, player, minWaitTime, maxWaitTime, voteID, MinecraftClient.getInstance().currentScreen.getTitle().getString());
                } else {
                    clientPlayerInteractionManager.clickSlot(syncId, 22, 0, SlotActionType.PICKUP, player);
                }
            }
        }, minWaitTime, TimeUnit.MILLISECONDS);
    }

    private static void waitForVote(ClientPlayerInteractionManager clientPlayerInteractionManager, ClientPlayerEntity player,
                                    int minWaitTime, int maxWaitTime, int voteId, String currentTitle) {
        if (maxWaitTime == 0) {
            abortedAutoVote("\u00A7b Aborted after" + maxWaitTime + "ms while waiting for to vote");
            return;
        }
        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
            if (MinecraftClient.getInstance().currentScreen == null) {
                waitForVote(clientPlayerInteractionManager, player, 1, maxWaitTime-1, voteId, currentTitle);
            } else if (MinecraftClient.getInstance().currentScreen.getTitle().getString().equals(currentTitle)) {
                waitForVote(clientPlayerInteractionManager, player, 1, maxWaitTime-1, voteId, currentTitle);
            } else {
                int syncId = ((HandledScreen<?>) MinecraftClient.getInstance().currentScreen).getScreenHandler().syncId;

                // Seeing which to choose
                int choiceID = -1;
                if (middleChoiceID != -1) {
                    choiceID = middleChoiceID;
                    middleChoiceID = -1;
                } else if (rightChoiceID != -1) {
                    choiceID = rightChoiceID;
                    rightChoiceID = -1;
                }
                clientPlayerInteractionManager.clickSlot(syncId, voteId, 0, SlotActionType.PICKUP, player); // Vote
                clientPlayerInteractionManager.clickSlot(syncId, 22, 0, SlotActionType.PICKUP, player); // Back to ChoiceMenu

                waitForChoiceMenu(clientPlayerInteractionManager, player, minWaitTime, maxWaitTime, choiceID, MinecraftClient.getInstance().currentScreen.getTitle().getString());
            }
        }, minWaitTime, TimeUnit.MILLISECONDS);
    }

    private static void abortedAutoVote(String s) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(s));
    }

}