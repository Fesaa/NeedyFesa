package fesa.needyfesa.needyFesaManagerClasses;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fesa.needyfesa.NeedyFesa;

import java.io.File;
import java.util.ArrayList;

public class ConfigManager {

    public ConfigObjectClass staticLobbyChestLocations;
    public ConfigObjectClass staticAutoMessages;
    public ConfigObjectClass staticReplaceMessages;
    public ConfigObjectClass mapInfo;
    public ConfigObjectClass needyFesaConfig;

    private ArrayList<ConfigObjectClass> configObjectClasses;

    public void initConfig() {

        configObjectClasses = new ArrayList<>();

        File configDir = new File("./config/NeedyFesa");
        if (configDir.mkdirs()) {
            NeedyFesa.LOGGER.warn("Config directory was not present; I made it. Bug? Or first time using the NeedyFesa mod?");
        }

        // Chest Locations
        JsonObject chestLocations = new JsonObject();
        chestLocations.addProperty("current-event", "");
        staticLobbyChestLocations = (new ConfigObjectClass()).make(new File("./config/NeedyFesa/staticLobbyChestLocations.json"), chestLocations);
        configObjectClasses.add(staticLobbyChestLocations);

        // Auto Messages
        JsonArray autoMessagesJson = new JsonArray();

        JsonObject autoMessagesExample = new JsonObject();
        autoMessagesExample.addProperty("regex", "");
        autoMessagesExample.addProperty("msg", "");
        autoMessagesExample.addProperty("command", false);
        autoMessagesExample.addProperty("chat", false);
        autoMessagesExample.addProperty("sound", false);
        autoMessagesExample.addProperty("partyMessage", false);
        autoMessagesExample.addProperty("sound_id", "");

        autoMessagesJson.add(autoMessagesExample);
        staticAutoMessages = (new ConfigObjectClass()).make(new File("./config/NeedyFesa/staticAutoMessages.json"), autoMessagesJson);
        configObjectClasses.add(staticAutoMessages);

        // Replace Messages
        JsonArray replaceMessagesJson = new JsonArray();

        JsonObject replaceMessageExample = new JsonObject();
        replaceMessageExample.addProperty("text", "");
        replaceMessageExample.addProperty("msg", "");

        replaceMessagesJson.add(replaceMessageExample);
        staticReplaceMessages = (new ConfigObjectClass()).make(new File("./config/NeedyFesa/staticReplaceMessages.json"), replaceMessagesJson);
        configObjectClasses.add(staticReplaceMessages);

        // EggWars Map Info
        JsonObject mapInfoJson = new JsonObject();
        mapInfoJson.add("teamColourOrder", new JsonObject());
        mapInfoJson.add("teamBuildLimit", new JsonObject());

        mapInfo = (new ConfigObjectClass()).make(new File("./config/NeedyFesa/EggWarsMapInfo.json"), mapInfoJson);
        configObjectClasses.add(mapInfo);

        // Config
        JsonObject needyFesaConfigJson = new JsonObject();
        needyFesaConfigJson.addProperty("autoVote", true);
        needyFesaConfigJson.addProperty("minWaitTime", 50);
        needyFesaConfigJson.addProperty("maxWaitTime", 1000);
        needyFesaConfigJson.addProperty("development-mode", false);

        JsonObject eggWarsVoting = new JsonObject();
        eggWarsVoting.addProperty("leftVoteId", 16);
        eggWarsVoting.addProperty("middleVoteId", -1);
        eggWarsVoting.addProperty("rightVoteId", 10);
        eggWarsVoting.addProperty("leftChoiceId", 11);
        eggWarsVoting.addProperty("middleChoiceId", -1);
        eggWarsVoting.addProperty("rightChoiceId", 15);
        eggWarsVoting.addProperty("hotBarSlot", 2);

        JsonObject soloSkyWarsVoting = new JsonObject();
        soloSkyWarsVoting.addProperty("leftVoteId", 16);
        soloSkyWarsVoting.addProperty("middleVoteId", 13);
        soloSkyWarsVoting.addProperty("rightVoteId", 10);
        soloSkyWarsVoting.addProperty("leftChoiceId", 10);
        soloSkyWarsVoting.addProperty("middleChoiceId", 13);
        soloSkyWarsVoting.addProperty("rightChoiceId", 16);
        soloSkyWarsVoting.addProperty("hotBarSlot", 1);

        JsonObject luckyIslandsVoting = new JsonObject();
        luckyIslandsVoting.addProperty("leftVoteId", 14);
        luckyIslandsVoting.addProperty("middleVoteId", -1);
        luckyIslandsVoting.addProperty("rightVoteId", 10);
        luckyIslandsVoting.addProperty("leftChoiceId", 11);
        luckyIslandsVoting.addProperty("middleChoiceId", -1);
        luckyIslandsVoting.addProperty("rightChoiceId", 15);
        luckyIslandsVoting.addProperty("hotBarSlot", 1);

        needyFesaConfigJson.add("Team EggWars", eggWarsVoting);
        needyFesaConfigJson.add("Solo SkyWars", soloSkyWarsVoting);
        needyFesaConfigJson.add("Lucky Islands", luckyIslandsVoting);

        needyFesaConfig = (new ConfigObjectClass()).make(new File("./config/NeedyFesa/needyFesaConfig.json"), needyFesaConfigJson);
        configObjectClasses.add(needyFesaConfig);
    }

    public void loadConfig() {
        for (ConfigObjectClass configObjectClass: configObjectClasses) {
            configObjectClass.configure();
        }
    }
}