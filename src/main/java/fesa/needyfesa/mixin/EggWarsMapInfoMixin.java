package fesa.needyfesa.mixin;

import fesa.needyfesa.EggWarsMapInfo;
import fesa.needyfesa.NeedyFesa;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;

@Mixin(ClientPlayNetworkHandler.class)
public class EggWarsMapInfoMixin {

    @ModifyArg(method = "onTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setTitle(Lnet/minecraft/text/Text;)V"))
    public Text onTitle(Text title) {
        assert MinecraftClient.getInstance().world != null;
        ClientWorld world = MinecraftClient.getInstance().world;
        ScoreboardObjective currentScoreboard = world.getScoreboard().getObjectiveForSlot(1);
        if (currentScoreboard != null) {
            if (currentScoreboard.getDisplayName().getString().contains("Team EggWars") && title.toString().contains("8")) {
                assert MinecraftClient.getInstance().player != null;
                String unformatted = Objects.requireNonNull(MinecraftClient.getInstance().player.getDisplayName().getStyle().getColor()).getName();

                StringBuilder Colour = new StringBuilder();
                for (String s: unformatted.replace("_", " ").split(" ")) {
                    Colour.append(s.substring(0, 1).toUpperCase());
                    Colour.append(s.substring(1));
                    Colour.append(" ");
                }
                NeedyFesa.teamColour = String.valueOf(Colour).stripTrailing();
                EggWarsMapInfo.handleRequest(NeedyFesa.eggWarsMap, String.valueOf(Colour).stripTrailing(), NeedyFesa.partyStatus && NeedyFesa.logParty);;
            }
        }
        return title;
    }


}
