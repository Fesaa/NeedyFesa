package fesa.needyfesa.cubeCode;

import net.minecraft.util.math.BlockPos;

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
        return "\nDebug info for Needyfesa" +
                "\nMap: " + map +
                "\nteamColour: " + teamColour +
                "\nname: " + name +
                "\nserverIP: " + serverIP +
                "\npartyStatus: " + partyStatus +
                "\nlogParty: " + logParty +
                "\nchestPartyAnnounce: " + chestPartyAnnounce +
                "\n";
    }

}
