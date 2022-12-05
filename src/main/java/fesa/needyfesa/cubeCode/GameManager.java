package fesa.needyfesa.cubeCode;

import net.minecraft.util.math.BlockPos;
import java.lang.reflect.Field;

public class GameManager {
    public static String map = "";
    public static String teamColour = "";
    public static String name = "";
    public static String serverIP = "";

    public static boolean partyStatus = false;
    public static boolean logParty = false;

    public static int chestPartyAnnounce = 0;
    public static BlockPos currentChestCoords = null;

    public static String debugString() {
        StringBuilder s = new StringBuilder();
        s.append("\nDebug info for Needyfesa");
        for (Field field : GameManager.class.getFields()) {
            try {
                s.append("\n").append(field.getName()).append(": ").append(field.get(GameManager.class).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        s.append("\n");
        return s.toString();
    }

}
