package fesa.needyfesa;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class EggWarsMapInfo {

    private static final String teamFiller = "||||||";

    private static String colourToUnicode(String colour) {
        switch (colour) {
            case "Black" -> {return "\u00A70";}
            case "Dark Blue" -> {return "\u00A71";}
            case "Dark Green" -> {return "\u00A72";}
            case "Dark Aqua" -> {return "\u00A73";}
            case "Dark Red" -> {return "\u00A74";}
            case "Dark Purple" -> {return "\u00A75";}
            case "Gold" -> {return "\u00A76";}
            case "Gray" -> {return "\u00A77";}
            case "Dark Gray" -> {return "\u00A78";}
            case "Blue" -> {return "\u00A79";}
            case "Green" -> {return "\u00A7a";}
            case "Aqua" -> {return "\u00A7b";}
            case "Red" -> {return "\u00A7c";}
            case "Light Purple" -> {return "\u00A7d";}
            case "Yellow" -> {return "\u00A7e";}
            case "White" -> {return "\u00A7f";}
        }
        return "";
    }

    private static String colourToCubeColour(String colour) {
        switch (colour) {
            case "Black" -> {return "&0";}
            case "Dark Blue" -> {return "&1";}
            case "Dark Green" -> {return "&2";}
            case "Dark Aqua" -> {return "&3";}
            case "Dark Red" -> {return "&4";}
            case "Dark Purple" -> {return "&5";}
            case "Gold" -> {return "&6";}
            case "Gray" -> {return "&7";}
            case "Dark Gray" -> {return "&8";}
            case "Blue" -> {return "&9";}
            case "Green" -> {return "&a";}
            case "Aqua" -> {return "&b";}
            case "Red" -> {return "&c";}
            case "Light Purple" -> {return "&d";}
            case "Yellow" -> {return "&e";}
            case "White" -> {return "&f";}
        }
        return "";
    }

    public static void handleRequest(String mapName, String teamColour, boolean party) {

        ArrayList<String> req = MakeMapLayout(mapName, teamColour);
        if (req == null) {
            return;
        }

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(req.get(0)));
        if (NeedyFesa.configManager.mapInfo.get("teamBuildLimit").getAsJsonObject().has(mapName)) {
            String buildLimitString = "\u00A76The build limit is: " + NeedyFesa.configManager.mapInfo.get("teamBuildLimit").getAsJsonObject().get(mapName);
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
        if (!NeedyFesa.configManager.mapInfo.get("teamColourOrder").getAsJsonObject().has(mapName)) {
            return null;
        }

        JsonObject MapInfo = NeedyFesa.configManager.mapInfo.get("teamColourOrder").getAsJsonObject().get(mapName).getAsJsonObject();

        if (MapInfo.get("style").getAsString().equals("cross")) {
            return MakeMapLayoutCross(teamColour, MapInfo);
        }

        if (MapInfo.get("style").getAsString().equals("square")) {
            return MakeMapLayoutSquare(teamColour, MapInfo);
        }

        if (MapInfo.get("style").getAsString().equals("double_triangle")) {
            return MakeMapLayoutDoubleTriangle(teamColour, MapInfo);
        }

        if (MapInfo.get("style").getAsString().equals("double_cross")) {
            return MakeMapLayoutDoubleCross(teamColour, MapInfo);
        }

        return null;
    }

    private static ArrayList<String> MakeMapLayoutDoubleCross(String teamColour, JsonObject mapInfo) {
        JsonArray mapLayout = mapInfo.get("layout").getAsJsonArray();

        int leftRight = -1;
        int groupIndex = -1;

        for (int groupCounter = 0; groupCounter < mapLayout.getAsJsonArray().size(); groupCounter++) {
            for (int leftRightCounter = 0; leftRightCounter < mapLayout.getAsJsonArray().get(groupCounter).getAsJsonArray().size(); leftRightCounter++) {
                if (mapLayout.getAsJsonArray().get(groupCounter).getAsJsonArray().get(leftRightCounter).getAsString().equals(teamColour)) {
                    leftRight = leftRightCounter;
                    groupIndex = groupCounter;
                }
            }
        }

        String teamSide = mapLayout.getAsJsonArray().get(groupIndex).getAsJsonArray().get((leftRight + 1) % 2).getAsString();
        String teamLeftLeft = mapLayout.getAsJsonArray().get((groupIndex - 1) % 4).getAsJsonArray().get(0).getAsString();
        String teamLeftRight = mapLayout.getAsJsonArray().get((groupIndex - 1) % 4).getAsJsonArray().get(1).getAsString();
        String teamRightLeft = mapLayout.getAsJsonArray().get((groupIndex + 1) % 4).getAsJsonArray().get(0).getAsString();
        String teamRightRight = mapLayout.getAsJsonArray().get((groupIndex + 1) % 4).getAsJsonArray().get(1).getAsString();
        String teamAcrossLeft = mapLayout.getAsJsonArray().get((groupIndex + 2) % 4).getAsJsonArray().get(0).getAsString();
        String teamAcrossRight = mapLayout.getAsJsonArray().get((groupIndex + 2) % 4).getAsJsonArray().get(1).getAsString();

        if (leftRight == 1) {
            String temp = teamColour;
            teamColour = teamSide;
            teamSide = temp;
        }

        String mapLayoutString = "\u00A7dMap layout:\n\n" +
                spaceMaker(4 + teamFiller.length()) + colourToUnicode(teamAcrossRight) + teamFiller +
                spaceMaker(2) + colourToUnicode(teamAcrossLeft) + teamFiller + "\n" +
                spaceMaker(2) + colourToUnicode(teamLeftLeft) + teamFiller +
                spaceMaker(6 + 2 * teamFiller.length()) + colourToUnicode(teamRightRight) + teamFiller + "\n\n" +
                spaceMaker(2) + colourToUnicode(teamLeftRight) + teamFiller +
                spaceMaker(6 + 2 * teamFiller.length()) + colourToUnicode(teamRightLeft) + teamFiller + "\n" +
                spaceMaker(4 + teamFiller.length()) + colourToUnicode(teamColour) + teamFiller +
                spaceMaker(2) + colourToUnicode(teamSide) + teamFiller;

        ArrayList<String> out = new ArrayList<>();
        out.add(mapLayoutString);
        out.add(null);

        return out;
    }

    private static ArrayList<String> MakeMapLayoutDoubleTriangle(String teamColour, JsonObject mapInfo) {
        JsonArray mapLayout = mapInfo.get("layout").getAsJsonArray();


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
                    spaceMaker(4 + teamFiller.length()) + colourToUnicode(teamUpLeft) + teamFiller +
                    spaceMaker(6) + colourToUnicode(teamUpRight) + teamFiller + "\n" +
                    spaceMaker(2) + colourToUnicode(teamLeftPoint) + teamFiller +
                    spaceMaker(10 + 2 * teamFiller.length()) + colourToUnicode(teamRightPoint) + teamFiller + "\n" +
                    spaceMaker(4 + teamFiller.length()) + colourToUnicode(teamUnderLeft) + teamFiller +
                    spaceMaker(6) + colourToUnicode(teamUnderRight) + teamFiller + "\n";
        } else {
            mapLayoutString = "\u00A7dMap layout:\n\n" +
                    spaceMaker(2 + teamFiller.length()) + colourToUnicode(teamRightPoint) + teamFiller + "\n" +
                    spaceMaker(2) + colourToUnicode(teamUpRight) + teamFiller +
                    spaceMaker(2 + teamFiller.length()) + colourToUnicode(teamUnderRight) + teamFiller + "\n\n" +
                    spaceMaker(2) + colourToUnicode(teamUpLeft) + teamFiller +
                    spaceMaker(2 + teamFiller.length()) + colourToUnicode(teamUnderLeft) + teamFiller + "\n" +
                    spaceMaker(2 + teamFiller.length()) + colourToUnicode(teamLeftPoint) + teamFiller;
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
                spaceMaker(4 + teamFiller.length()) + colourToUnicode(teamBefore) + teamFiller + "\n" +
                spaceMaker(2) + colourToUnicode(teamLeft) + teamFiller + spaceMaker(teamFiller.length() + 7) +
                colourToUnicode(teamRight) + teamFiller + "\n" +
                spaceMaker(4 + teamFiller.length()) + colourToUnicode(teamColour) + teamFiller + "\n";

        partyMapLayoutString.append("@Left: ").append(colourToCubeColour(teamLeft)).append(teamLeft)
                .append("&r. Right: ").append(colourToCubeColour(teamRight)).append(teamRight)
                .append("&r. In Front: ").append(colourToCubeColour(teamBefore)).append(teamBefore)
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
                colourToUnicode(teamAcross) + spaceMaker(2) + teamFiller + spaceMaker(7) +
                colourToUnicode(teamSideAcross) + teamFiller + "\n\n" +
                colourToUnicode(teamColour) + spaceMaker(2) + teamFiller + spaceMaker(7) +
                colourToUnicode(teamSide) + teamFiller + "\n";

        partyMapLayoutString.append("@Across: ").append(colourToCubeColour(teamAcross)).append(teamAcross)
                .append("&r. Side: ").append(colourToCubeColour(teamSide)).append(teamSide)
                .append("&r Side & Across: ").append(colourToCubeColour(teamSideAcross)).append(teamSideAcross)
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
