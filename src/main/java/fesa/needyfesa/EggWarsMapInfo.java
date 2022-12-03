package fesa.needyfesa;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class EggWarsMapInfo {

    private static final String teamFiller = "||||||";
    private static HashMap<String, String> colourToUnicode;
    private static HashMap<String, String> colourToCubeColour;

    public static void handleRequest(String mapName, String teamColour, boolean party) {
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
            colourToUnicode.put("Dark Gray", "\u00A70");
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
            colourToCubeColour.put("Dark Gray", "&0");
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
            if (partyMessage != null) {
                MinecraftClient.getInstance().player.sendChatMessage(partyMessage, Text.of(partyMessage));
            }
        }
    }

    private static ArrayList<String> MakeMapLayout(String mapName, String teamColour) {
        if (!NeedyFesa.mapInfo.get("teamColourOrder").getAsJsonObject().has(mapName)) {
            return null;
        }

        JsonObject MapInfo = NeedyFesa.mapInfo.get("teamColourOrder").getAsJsonObject().get(mapName).getAsJsonObject();

        if (MapInfo.get("style").getAsString().equals("cross")) {
            return MakeMapLayoutCross(teamColour, MapInfo);
        }

        if (MapInfo.get("style").getAsString().equals("square")) {
            return MakeMapLayoutSquare(teamColour, MapInfo);
        }

        if (MapInfo.get("style").getAsString().equals("double_triangle")) {
            return MakeMapLayoutDoubleTriangle(teamColour, MapInfo);
        }

        return null;
    }

    private static ArrayList<String> MakeMapLayoutDoubleTriangle(String teamColour, JsonObject mapInfo) {
        JsonArray mapLayout = mapInfo.get("layout").getAsJsonArray();
        ArrayList<String> formattedMapLayout = new ArrayList<>();

        String mapLayoutString = "";

        int teamTriangleLocation = -1;
        int teamLeftRight = -1;

        for (int triangleLocation = 0; triangleLocation < mapLayout.size(); triangleLocation++) {
            for (int leftRight = 0; leftRight < mapLayout.get(triangleLocation).getAsJsonArray().size(); leftRight++) {
                if (mapLayout.get(triangleLocation).getAsJsonArray().get(leftRight).getAsString().equals(teamColour)) {
                    teamTriangleLocation = triangleLocation;
                    teamLeftRight = leftRight;
                    break;
                }
            }
        }

        String teamUnderLeft = "";
        String teamUnderRight = "";
        String teamLeftPoint = "";
        String teamRightPoint = "";
        String teamUpLeft = "";
        String teamUpRight = "";

        switch (teamTriangleLocation) {
            case 0 -> {
                teamUnderLeft = mapLayout.get(0).getAsJsonArray().get(0).getAsString();
                teamUnderRight = mapLayout.get(0).getAsJsonArray().get(1).getAsString();
                teamLeftPoint = mapLayout.get(1).getAsJsonArray().get(0).getAsString();
                teamRightPoint = mapLayout.get(1).getAsJsonArray().get(1).getAsString();
                teamUpLeft = mapLayout.get(2).getAsJsonArray().get(1).getAsString();
                teamUpRight = mapLayout.get(2).getAsJsonArray().get(0).getAsString();
            }
            case 1 -> {
                if (teamLeftRight == 0) {
                    teamUnderLeft = mapLayout.get(0).getAsJsonArray().get(0).getAsString();
                    teamUnderRight = mapLayout.get(0).getAsJsonArray().get(1).getAsString();
                    teamLeftPoint = mapLayout.get(1).getAsJsonArray().get(0).getAsString();
                    teamRightPoint = mapLayout.get(1).getAsJsonArray().get(1).getAsString();
                    teamUpLeft = mapLayout.get(2).getAsJsonArray().get(1).getAsString();
                    teamUpRight = mapLayout.get(2).getAsJsonArray().get(0).getAsString();
                } else {
                    teamUnderLeft = mapLayout.get(2).getAsJsonArray().get(0).getAsString();
                    teamUnderRight = mapLayout.get(2).getAsJsonArray().get(1).getAsString();
                    teamLeftPoint = mapLayout.get(1).getAsJsonArray().get(1).getAsString();
                    teamRightPoint = mapLayout.get(1).getAsJsonArray().get(0).getAsString();
                    teamUpLeft = mapLayout.get(0).getAsJsonArray().get(1).getAsString();
                    teamUpRight = mapLayout.get(0).getAsJsonArray().get(0).getAsString();
                }
            }
            case 2 -> {
                teamUnderLeft = mapLayout.get(2).getAsJsonArray().get(0).getAsString();
                teamUnderRight = mapLayout.get(2).getAsJsonArray().get(1).getAsString();
                teamLeftPoint = mapLayout.get(1).getAsJsonArray().get(1).getAsString();
                teamRightPoint = mapLayout.get(1).getAsJsonArray().get(0).getAsString();
                teamUpLeft = mapLayout.get(0).getAsJsonArray().get(1).getAsString();
                teamUpRight = mapLayout.get(0).getAsJsonArray().get(0).getAsString();
            }
        }

        if (teamTriangleLocation != 1) {
            mapLayoutString = "\u00A7dMap layout:\n\n" +
                    spaceMaker(4 + teamFiller.length()) + colourToUnicode.get(teamUpLeft) + teamFiller +
                    spaceMaker(6) + colourToUnicode.get(teamUpRight) + teamFiller + "\n" +
                    spaceMaker(2) + colourToUnicode.get(teamLeftPoint) + teamFiller +
                    spaceMaker(10 + 2 * teamFiller.length()) + colourToUnicode.get(teamRightPoint) + teamFiller + "\n" +
                    spaceMaker(4 + teamFiller.length()) + colourToUnicode.get(teamUnderLeft) + teamFiller +
                    spaceMaker(6) + colourToUnicode.get(teamUnderRight) + teamFiller + "\n";
        } else {
            mapLayoutString = "\u00A7dMap layout:\n\n" +
                    spaceMaker(2 + teamFiller.length()) + colourToUnicode.get(teamRightPoint) + teamFiller + "\n" +
                    spaceMaker(2) + colourToUnicode.get(teamUpRight) + teamFiller +
                    spaceMaker(2 + teamFiller.length()) + colourToUnicode.get(teamUnderRight) + teamFiller + "\n\n" +
                    spaceMaker(2) + colourToUnicode.get(teamUpLeft) + teamFiller +
                    spaceMaker(2 + teamFiller.length()) + colourToUnicode.get(teamUnderLeft) + teamFiller + "\n" +
                    spaceMaker(2 + teamFiller.length()) + colourToUnicode.get(teamLeftPoint) + teamFiller;
        }


        ArrayList<String> out = new ArrayList<>();
        out.add(mapLayoutString);
        out.add(null);

        return out;
    }

    private static ArrayList<String> MakeMapLayoutCross(String teamColour, JsonObject mapInfo) {

        JsonArray mapLayout = mapInfo.get("layout").getAsJsonArray();
        ArrayList<String> formattedMapLayout = new ArrayList<>();
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

        ArrayList<String> out = new ArrayList<>();
        out.add(mapLayoutString);
        out.add(String.valueOf(partyMapLayoutString));

        return out;
    }

    private static ArrayList<String> MakeMapLayoutSquare(String teamColour, JsonObject mapInfo) {

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
            if (teamIndexSide == 0) {
                String tempSide = teamColour;
                teamColour = teamSide;
                teamSide = tempSide;
            } else {
                String tempAcross = teamSideAcross;
                teamSideAcross = teamAcross;
                teamAcross = tempAcross;
            }
        } else {
            if (teamIndexSide == 0) {
                String tempAcross = teamSideAcross;
                teamSideAcross = teamAcross;
                teamAcross = tempAcross;
            } else {
                String tempSide = teamColour;
                teamColour = teamSide;
                teamSide = tempSide;
            }
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

        ArrayList<String> out = new ArrayList<>();
        out.add(mapLayoutString);
        out.add(String.valueOf(partyMapLayoutString));

        return out;
    }

    private static String spaceMaker(int n) {
        return " ".repeat(Math.max(0, n));
    }
}
