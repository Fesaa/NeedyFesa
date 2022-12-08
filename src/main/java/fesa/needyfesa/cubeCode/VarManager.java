package fesa.needyfesa.cubeCode;

import fesa.needyfesa.needyFesaManagerClasses.AntiSpamMessageClass;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class VarManager {
    public static String map = "";
    public static String teamColour = "";
    public static String name = "";
    public static String serverIP = "";

    public static boolean partyStatus = false;
    public static boolean logParty = false;

    public static int chestPartyAnnounce = 0;
    public static BlockPos currentChestCoords = null;

    public static HashMap<String, AntiSpamMessageClass> antiSpamHashMap = new HashMap<>();

    public static String debugString() {
        return "\nDebug info for Needyfesa" +
                "\nMap: " + map +
                "\nteamColour: " + teamColour +
                "\nname: " + name +
                "\nserverIP: " + serverIP +
                "\npartyStatus: " + partyStatus +
                "\nlogParty: " + logParty +
                "\nchestPartyAnnounce: " + chestPartyAnnounce +
                "\nantiSpamHashMap:" +
                "\n    Size: " + antiSpamHashMap.size() +
                "\n    Keys: " + antiSpamHashMap.keySet() +
                "\n";
    }

}
