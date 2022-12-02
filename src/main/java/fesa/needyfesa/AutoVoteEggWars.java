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

public class AutoVoteEggWars {
    public static void run(int minWaitTime){
        assert MinecraftClient.getInstance().player != null;
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        ClientPlayerInteractionManager clientPlayerInteractionManager =  MinecraftClient.getInstance().interactionManager;
        assert clientPlayerInteractionManager != null;

        PlayerInventory inv =  p.getInventory();
        inv.selectedSlot = 2;
        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
            Hand[] hands = Hand.values();
            clientPlayerInteractionManager.interactItem(MinecraftClient.getInstance().player, hands[0]);
        }, 300, TimeUnit.MILLISECONDS);
        waitForOpeningVoteMenu(clientPlayerInteractionManager, p, minWaitTime, NeedyFesa.needyFesaConfig.get("maxWaitTime").getAsInt());
    }

    private static void waitForOpeningVoteMenu(ClientPlayerInteractionManager clientPlayerInteractionManager, ClientPlayerEntity player, int minWaitTime, int maxWaitTime) {
        if (maxWaitTime == 0) {
            abortedAutoVote("\u00A7bAborted after 1000ms before trying to vote for items.");
            return;
        }
        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
            if (MinecraftClient.getInstance().currentScreen == null) {
                waitForOpeningVoteMenu(clientPlayerInteractionManager, player, 1, maxWaitTime-1);
            } else {
                int syncId = ((HandledScreen<?>) MinecraftClient.getInstance().currentScreen).getScreenHandler().syncId;
                clientPlayerInteractionManager.clickSlot(syncId, 11, 0, SlotActionType.PICKUP, player);
                waitForItemVoteMenu(clientPlayerInteractionManager, player, minWaitTime, NeedyFesa.needyFesaConfig.get("maxWaitTime").getAsInt());
            }
        }, minWaitTime, TimeUnit.MILLISECONDS);
    }

    private static void waitForItemVoteMenu(ClientPlayerInteractionManager clientPlayerInteractionManager, ClientPlayerEntity player, int minWaitTime, int maxWaitTime) {
        if (maxWaitTime == 0) {
            abortedAutoVote("\u00A7bAborted after 1000ms before voting for items.");
            return;
        }
        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
            if (MinecraftClient.getInstance().currentScreen == null) {
                waitForItemVoteMenu(clientPlayerInteractionManager, player, 1, maxWaitTime-1);
            } else if (!MinecraftClient.getInstance().currentScreen.getTitle().getString().equals("Items Voting")){
                waitForItemVoteMenu(clientPlayerInteractionManager, player, 1, maxWaitTime-1);
            } else {
                int syncId = ((HandledScreen<?>) MinecraftClient.getInstance().currentScreen).getScreenHandler().syncId;
                clientPlayerInteractionManager.clickSlot(syncId, NeedyFesa.needyFesaConfig.get("itemVote").getAsInt(), 0, SlotActionType.PICKUP, player); // Item Vote
                clientPlayerInteractionManager.clickSlot(syncId, 22, 0, SlotActionType.PICKUP, player);
                waitForVoteMenu(clientPlayerInteractionManager, player, minWaitTime, NeedyFesa.needyFesaConfig.get("maxWaitTime").getAsInt());
            }
        }, minWaitTime, TimeUnit.MILLISECONDS);
    }

    private static void waitForVoteMenu(ClientPlayerInteractionManager clientPlayerInteractionManager, ClientPlayerEntity player, int minWaitTime, int maxWaitTime) {
        if (maxWaitTime == 0) {
            abortedAutoVote("\u00A7bAborted after 1000ms before trying to vote for health.");
            return;
        }
        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
            if (MinecraftClient.getInstance().currentScreen == null) {
                waitForVoteMenu(clientPlayerInteractionManager, player, 1, maxWaitTime-1);
            } else if (!MinecraftClient.getInstance().currentScreen.getTitle().getString().equals("Voting")){
                waitForVoteMenu(clientPlayerInteractionManager, player, 1, maxWaitTime-1);
            } else {
                int syncId = ((HandledScreen<?>) MinecraftClient.getInstance().currentScreen).getScreenHandler().syncId;
                clientPlayerInteractionManager.clickSlot(syncId, 15, 0, SlotActionType.PICKUP, player);
                NeedyFesa.LOGGER.info("Found voting menu, switching to health voting");
                waitForHealthVoteMenu(clientPlayerInteractionManager, player, minWaitTime, NeedyFesa.needyFesaConfig.get("maxWaitTime").getAsInt());
            }
        }, minWaitTime, TimeUnit.MILLISECONDS);
    }

    private static void waitForHealthVoteMenu(ClientPlayerInteractionManager clientPlayerInteractionManager, ClientPlayerEntity player, int minWaitTime, int maxWaitTime) {
        if (maxWaitTime == 0) {
            abortedAutoVote("\u00A7bAborted after 1000ms before voting for health.");
            return;
        }
        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
            if (MinecraftClient.getInstance().currentScreen == null) {
                waitForHealthVoteMenu(clientPlayerInteractionManager, player, 1, maxWaitTime-1);
            } else if (!MinecraftClient.getInstance().currentScreen.getTitle().getString().equals("Health Voting")){
                waitForHealthVoteMenu(clientPlayerInteractionManager, player, 1, maxWaitTime-1);
            } else {
                int syncId = ((HandledScreen<?>) MinecraftClient.getInstance().currentScreen).getScreenHandler().syncId;
                clientPlayerInteractionManager.clickSlot(syncId, NeedyFesa.needyFesaConfig.get("healthVote").getAsInt(), 0, SlotActionType.PICKUP, player); // Health Vote
                MinecraftClient.getInstance().currentScreen.close();
            }
        }, minWaitTime, TimeUnit.MILLISECONDS);
    }

    private static void abortedAutoVote(String s) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(s));
    }

}