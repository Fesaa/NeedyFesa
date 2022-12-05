package fesa.needyfesa.mixin;

import fesa.needyfesa.NeedyFesa;
import fesa.needyfesa.cubeCode.AutoVote;
import fesa.needyfesa.cubeCode.CubeVarManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mixin(ClientPlayNetworkHandler.class)
public class GameManagerMixin {

    @Shadow
    @Final
    private ClientConnection connection;

    @Inject(at = @At("TAIL"), method="onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world == null) {
            CubeVarManager.serverIP = "";
            CubeVarManager.partyStatus = false;
            return;
        }
        waitForScoreBoard(world);
    }

    private void waitForScoreBoard(@NotNull ClientWorld world) {
        if (world.getScoreboard().getObjectiveForSlot(1) == null) {
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
                        waitForScoreBoard(world);
            }
            , 100, TimeUnit.MILLISECONDS);
        } else {
            toRun(Objects.requireNonNull(world.getScoreboard().getObjectiveForSlot(1)));
        }
    }

    private void toRun(@NotNull ScoreboardObjective currentScoreboard) {
        String map = "";

        ScoreboardPlayerScore lastEntry = null;
        for (ScoreboardPlayerScore scoreboardPlayerScore : currentScoreboard.getScoreboard().getAllPlayerScores(currentScoreboard)) {
            if (scoreboardPlayerScore.getPlayerName().contains("Map:")) {
                assert lastEntry != null;
                map = lastEntry.getPlayerName().substring(2);
                break;
            }
            lastEntry = scoreboardPlayerScore;
        }

        assert MinecraftClient.getInstance().player != null;
        String unformatted = Objects.requireNonNull(MinecraftClient.getInstance().player.getDisplayName().getStyle().getColor()).getName();

        StringBuilder Colour = new StringBuilder();
        for (String s: unformatted.replace("_", " ").split(" ")) {
            Colour.append(s.substring(0, 1).toUpperCase());
            Colour.append(s.substring(1));
            Colour.append(" ");
        }

        if (connection.getAddress().toString().equals("play.cubecraft.net")
                || connection.getAddress().toString().contains("ccgn.co")) {
            CubeVarManager.map = map;
            CubeVarManager.name = currentScoreboard.getDisplayName().getString();
            CubeVarManager.teamColour = String.valueOf(Colour).stripTrailing();
            CubeVarManager.serverIP = "play.cubecraft.net";

            if (NeedyFesa.configManager.needyFesaConfig.get("autoVote").getAsBoolean()
                    && NeedyFesa.configManager.needyFesaConfig.has(CubeVarManager.name)) {
                AutoVote.vote();
            }


        } else {
            CubeVarManager.serverIP = connection.getAddress().toString();
            CubeVarManager.partyStatus = false;
        }
    }
}

