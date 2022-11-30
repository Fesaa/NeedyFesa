package fesa.needyfesa.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fesa.needyfesa.NeedyFesa;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Mixin(ClientPlayNetworkHandler.class)
public class EggWarsMapInfo {

    private static final String teamFiller = "||||||";
    private static HashMap<String, String> colourToUnicode;
    private static HashMap<String, String> colourToCubeColour;


    @ModifyArg(method = "onTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;setTitle(Lnet/minecraft/text/Text;)V"))
    public Text onTitle(Text title) {
        assert MinecraftClient.getInstance().world != null;
        ScoreboardObjective currentScoreboard = MinecraftClient.getInstance().world.getScoreboard().getObjectiveForSlot(1);
        if (currentScoreboard != null) {
            if (currentScoreboard.getDisplayName().getString().contains("Team EggWars") && title.toString().contains("8")) {
                assert MinecraftClient.getInstance().player != null;
                String unformatted = Objects.requireNonNull(MinecraftClient.getInstance().player.getDisplayName().getStyle().getColor()).getName();

                StringBuilder Colour = new StringBuilder("");

                for (String s: unformatted.replace("_", " ").split(" ")) {
                    Colour.append(s.substring(0, 1).toUpperCase());
                    Colour.append(s.substring(1));
                    Colour.append(" ");
                }

                HandleRequest(NeedyFesa.eggWarsMap, String.valueOf(Colour).stripTrailing(), NeedyFesa.partyStatus && NeedyFesa.logParty);
            }
            }
        return title;
    }

    private void HandleRequest(String mapName, String teamColour, boolean party) {
        if (colourToUnicode == null) {
            colourToUnicode = new HashMap<>();

            colourToUnicode.put("Green", "\u00A7a");
            colourToUnicode.put("Dark Aqua", "\u00A73");
            colourToUnicode.put("Yellow", "\u00A7e");
            colourToUnicode.put("Red", "\u00A7c");
            colourToUnicode.put("Dark Blue", "\u00A71");
            colourToUnicode.put("Dark Purple", "\u00A75");
            colourToUnicode.put("Aqua", "\u00A7b");
            colourToUnicode.put("Gold", "\u00A76");
            colourToUnicode.put("Light Purple", "\u00A7d");
        }
        if (colourToCubeColour == null) {
            colourToCubeColour = new HashMap<>();

            colourToCubeColour.put("Green", "&a");
            colourToCubeColour.put("Dark Aqua", "&3");
            colourToCubeColour.put("Yellow", "&e");
            colourToCubeColour.put("Red", "&c");
            colourToCubeColour.put("Dark Blue", "&1");
            colourToCubeColour.put("Dark Purple", "&5");
            colourToCubeColour.put("Aqua", "&b");
            colourToCubeColour.put("Gold", "&6");
            colourToCubeColour.put("Light Purple", "&d");
        }

        ArrayList<String> req = MakeMapLayout(mapName, teamColour);
        if (req == null) {
            return;
        }

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(req.get(0)));
        if (NeedyFesa.mapInfo.get("teamBuildLimit").getAsJsonObject().has(mapName)) {
            String buildLimitString = "\u00A76The build limit is: " + NeedyFesa.mapInfo.get("teamBuildLimit").getAsJsonObject().get(mapName);
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(buildLimitString));
        }

        if (party) {
            String partyMessage = req.get(1);
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.sendChatMessage(partyMessage, Text.of(partyMessage));
        }
    }

    private ArrayList<String> MakeMapLayout(String mapName, String teamColour) {
        if (!NeedyFesa.mapInfo.get("teamColourOrder").getAsJsonObject().has(mapName)) {
            return null;
        }

        JsonObject MapInfo = NeedyFesa.mapInfo.get("teamColourOrder").getAsJsonObject().get(mapName).getAsJsonObject();

        if (MapInfo.get("style").getAsString().equals("cross")) {
            return MakeMapLayoutCross(mapName, teamColour, MapInfo);
        }

        if (MapInfo.get("style").getAsString().equals("square")) {
            return MakeMapLayoutSquare(mapName, teamColour, MapInfo);
        }

        return null;
    }

    private ArrayList<String> MakeMapLayoutCross(String mapName, String teamColour, JsonObject mapInfo) {

        JsonArray mapLayout = mapInfo.get("layout").getAsJsonArray();
        ArrayList<String> formattedMapLayout = new ArrayList<String>();
        int teamIndex = -1;
        for (int i = 0; i < mapLayout.size(); i++) {
            formattedMapLayout.add(mapLayout.get(i).getAsString());
            if (mapLayout.get(i).getAsString().equals(teamColour)) {
                teamIndex = i;
            }
        }

        String teamLeft = formattedMapLayout.get((teamIndex + 1) % formattedMapLayout.size());
        String teamBefore = formattedMapLayout.get((teamIndex + 2) % formattedMapLayout.size());
        String teamRight = formattedMapLayout.get((teamIndex + 3) % formattedMapLayout.size());

        StringBuilder partyMapLayoutString = new StringBuilder();

        String mapLayoutString = "\u00A7dMap layout:\n\n" +
                spaceMaker(4 + teamFiller.length()) + colourToUnicode.get(teamBefore) + teamFiller + "\n" +
                spaceMaker(2) + colourToUnicode.get(teamLeft) + teamFiller + spaceMaker(teamFiller.length() + 7) +
                colourToUnicode.get(teamRight) + teamFiller + "\n" +
                spaceMaker(4 + teamFiller.length()) + colourToUnicode.get(teamColour) + teamFiller + "\n";

        partyMapLayoutString.append("@Left: ").append(colourToCubeColour.get(teamLeft)).append(teamLeft)
                            .append("&r. Right: ").append(colourToCubeColour.get(teamRight)).append(teamRight)
                            .append("&r. In Front: ").append(colourToCubeColour.get(teamBefore)).append(teamBefore)
                            .append("&r.");

        ArrayList<String> out = new ArrayList<String>();
        out.add(mapLayoutString);
        out.add(String.valueOf(partyMapLayoutString));

        return out;
    }

    private ArrayList<String> MakeMapLayoutSquare(String mapName, String teamColour, JsonObject mapInfo) {

        JsonArray mapLayout = mapInfo.get("layout").getAsJsonArray();
        int teamIndexSide = -1;
        int teamIndexDepth = -1;
        for (int i = 0; i < mapLayout.size(); i++) {
            for (int j = 0; j < mapLayout.get(i).getAsJsonArray().size(); j++) {
                if (mapLayout.get(i).getAsJsonArray().get(j).getAsString().equals(teamColour)) {
                    teamIndexSide = i;
                    teamIndexDepth = j;
                }

            }
        }

        String teamAcross = mapLayout.get((teamIndexSide + 1) % mapLayout.size()).getAsJsonArray().get(teamIndexDepth).getAsString();
        String teamSide = mapLayout.get(teamIndexSide).getAsJsonArray().get((teamIndexDepth + 1) % mapLayout.get(teamIndexSide).getAsJsonArray().size()).getAsString();
        String teamSideAcross =mapLayout.get((teamIndexSide + 1) % mapLayout.size()).getAsJsonArray().get((teamIndexDepth + 1) % mapLayout.get(teamIndexSide).getAsJsonArray().size()).getAsString();

        StringBuilder partyMapLayoutString = new StringBuilder();

        if (teamIndexDepth == 1) {
            String tempSide = teamColour;
            String tempAcross = teamSideAcross;
            teamColour = teamSide;
            teamSide = tempSide;
            teamSideAcross = teamAcross;
            teamAcross = tempAcross;
        }

        String mapLayoutString = "\u00A7dMap layout:\n\n" +
                colourToUnicode.get(teamAcross) + spaceMaker(2) + teamFiller + spaceMaker(7) +
                colourToUnicode.get(teamSideAcross) + teamFiller + "\n\n" +
                colourToUnicode.get(teamColour) + spaceMaker(2) + teamFiller + spaceMaker(7) +
                colourToUnicode.get(teamSide) + teamFiller + "\n";

        partyMapLayoutString.append("@Across: ").append(colourToCubeColour.get(teamAcross)).append(teamAcross)
                            .append("&r. Side: ").append(colourToCubeColour.get(teamSide)).append(teamSide)
                            .append("&r Side & Across: ").append(colourToCubeColour.get(teamSideAcross)).append(teamSideAcross)
                            .append("&r.");

        ArrayList<String> out = new ArrayList<String>();
        out.add(mapLayoutString);
        out.add(String.valueOf(partyMapLayoutString));

        return out;
    }

    private static String spaceMaker(int n) {
        return " ".repeat(Math.max(0, n));
    }
}
