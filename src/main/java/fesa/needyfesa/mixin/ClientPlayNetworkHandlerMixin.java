package fesa.needyfesa.mixin;

import fesa.needyfesa.NeedyFesa;
import fesa.needyfesa.cubeCode.AutoVote;
import fesa.needyfesa.cubeCode.VarManager;
import fesa.needyfesa.cubeCode.EggWarsMapInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow
    @Final
    private ClientConnection connection;

    @ModifyArg(method = "onTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setTitle(Lnet/minecraft/text/Text;)V"))
    public Text onTitle(Text title) {
        if (VarManager.name.equals("Team EggWars") && title.toString().contains("8")) {
            VarManager.teamColour = getTeamColour();
            EggWarsMapInfo.handleRequest(VarManager.map, VarManager.teamColour, VarManager.partyStatus && VarManager.logParty);
        }
        return title;
    }

    @Inject(at = @At("TAIL"), method="onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld world = mc.world;
        if (world == null) {
            VarManager.partyStatus = false;
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

        if (connection.getAddress().toString().contains("play.cubecraft.net")
                || connection.getAddress().toString().contains("ccgn.co")) {
            VarManager.map = map;
            VarManager.name = currentScoreboard.getDisplayName().getString();
            VarManager.teamColour = getTeamColour();
            VarManager.serverIP = "play.cubecraft.net";

            if (NeedyFesa.configManager.needyFesaConfig.get("autoVote").getAsBoolean()
                    && NeedyFesa.configManager.needyFesaConfig.has(VarManager.name)) {
                AutoVote.vote();
            }


        } else {
            VarManager.serverIP = connection.getAddress().toString();
            VarManager.partyStatus = false;
        }
    }

    private String getTeamColour() {
        assert MinecraftClient.getInstance().player != null;
        String unformatted = Objects.requireNonNull(MinecraftClient.getInstance().player.getDisplayName().getStyle().getColor()).getName();

        StringBuilder Colour = new StringBuilder();
        for (String s: unformatted.replace("_", " ").split(" ")) {
            Colour.append(s.substring(0, 1).toUpperCase());
            Colour.append(s.substring(1));
            Colour.append(" ");
        }
        return String.valueOf(Colour).stripTrailing();
    }
}

