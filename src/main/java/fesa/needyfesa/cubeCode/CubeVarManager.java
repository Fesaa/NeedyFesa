package fesa.needyfesa.cubeCode;

import net.minecraft.util.math.BlockPos;
import java.lang.reflect.Field;

public class CubeVarManager {
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
        s.append("\nMap: ").append(map);
        s.append("\nteamColour: ").append(teamColour);
        s.append("\nname: ").append(name);
        s.append("\nserverIP: ").append(serverIP);
        s.append("\npartyStatus: ").append(partyStatus);
        s.append("\nlogParty: ").append(logParty);
        s.append("\nchestPartyAnnounce: ").append(chestPartyAnnounce);
        s.append("\n");
        return s.toString();
    }

}
