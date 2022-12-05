package fesa.needyfesa.mixin;

import fesa.needyfesa.cubeCode.EggWarsMapInfo;
import fesa.needyfesa.cubeCode.GameManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientPlayNetworkHandler.class)
public class EggWarsMapInfoMixin {

    @ModifyArg(method = "onTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setTitle(Lnet/minecraft/text/Text;)V"))
    public Text onTitle(Text title) {
        assert MinecraftClient.getInstance().world != null;
        ClientWorld world = MinecraftClient.getInstance().world;
        ScoreboardObjective currentScoreboard = world.getScoreboard().getObjectiveForSlot(1);
        if (currentScoreboard != null) {
            if (currentScoreboard.getDisplayName().getString().contains("Team EggWars") && title.toString().contains("8")) {
                EggWarsMapInfo.handleRequest(GameManager.name, GameManager.teamColour, GameManager.partyStatus && GameManager.logParty);
            }
        }
        return title;
    }


}
