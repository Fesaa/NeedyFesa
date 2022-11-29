package fesa.needyfesa;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;


import java.util.concurrent.TimeUnit;

public class AutoVoteEggWars {

    public static void run(){
        assert MinecraftClient.getInstance().player != null;
        ClientPlayerEntity p = MinecraftClient.getInstance().player;
        ClientPlayerInteractionManager clientPlayerInteractionManager =  MinecraftClient.getInstance().interactionManager;
        assert clientPlayerInteractionManager != null;

        PlayerInventory inv =  p.getInventory();
        inv.selectedSlot = 2;
        wait(300);
        Hand[] hands = Hand.values();
        clientPlayerInteractionManager.interactItem(MinecraftClient.getInstance().player, hands[0]);
        NeedyFesa.LOGGER.info("<3333");

    }

    private static void waitFor(String s, ClientPlayerEntity p) {
        while (p.getInventory().getName().getString().contains(s)) {
            wait(1);
        }
    }

    private  static void wait(int s) {
        try {
            TimeUnit.MILLISECONDS.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
