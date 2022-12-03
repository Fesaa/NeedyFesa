package fesa.needyfesa;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class NeedyFesa implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("needyfesa");

	// Mod Vars
	public static boolean partyStatus = false;
	public static boolean logParty = false;

	public static String gameMap = "";
	public static String teamColour = "";
	public static String game = "";

	public static int chestPartyAnnounce = 0;
	public static BlockPos currentChestCoords = null;

	// Config Vars
	public static JsonArray staticLobbyChestLocations;
	public static JsonArray staticAutoMessages;
	public static JsonArray staticReplaceMessages;
	public static JsonObject mapInfo;
	public static JsonObject needyFesaConfig;

	@Override
	public void onInitialize() {

		// Getting, checking & correcting config
		File configDir = new File("./config/NeedyFesa");
		if (configDir.mkdirs()) {
			LOGGER.warn("Config directory was not present; I made it. Bug? Or first time using the NeedyFesa mod?");
		}

		File staticLobbyChestLocationsJson = new File("./config/NeedyFesa/staticLobbyChestLocations.json");
		File staticAutoMessagesJson = new File("./config/NeedyFesa/staticAutoMessages.json");
		File staticReplaceMessagesJson = new File("./config/NeedyFesa/staticReplaceMessages.json");
		File mapInfoJson = new File("./config/NeedyFesa/EggWarsMapInfo.json");
		File needyFesaConfigJson = new File("./config/NeedyFesa/needyFesaConfig.json");

		if (!staticLobbyChestLocationsJson.exists()) {
			initChestLocations(staticLobbyChestLocationsJson.getPath());
			LOGGER.warn("Config file, " + staticLobbyChestLocationsJson.getName() + ", was not present; I made one. Bug? Or first time using the NeedyFesa mod?");
		}
		if (!staticAutoMessagesJson.exists()) {
			initAutoMessages(staticAutoMessagesJson.getPath());
			LOGGER.warn("Config file, " + staticAutoMessagesJson.getName() + ", was not present; I made one. Bug? Or first time using the NeedyFesa mod?");
		}
		if (!staticReplaceMessagesJson.exists()) {
			initReplaceMessagesJson(staticReplaceMessagesJson.getPath());
			LOGGER.warn("Config file, " + staticReplaceMessagesJson.getName() + ", was not present; I made one. Bug? Or first time using the NeedyFesa mod?");
		}
		if (!mapInfoJson.exists()) {
			initMapInfo(mapInfoJson.getPath());
			LOGGER.warn("Config file, " + mapInfoJson.getName() + ", was not present; I made one. Bug? Or first time using the NeedyFesa mod?");
		}
		if (!needyFesaConfigJson.exists()) {
			initNeedyFesaConfigJson(needyFesaConfigJson.getPath());
			LOGGER.warn("Config file, " + needyFesaConfigJson.getName() + ", was not present; I made one. Bug? Or first time using the NeedyFesa mod?");
		}

		KeyBindManager.loadKeyBinds();
		KeyBindManager.registerEvents();

		JsonReload();
		LOGGER.info("NeedyFesa started successfully. \nHELLO CUTIES <3333");
	}

	private static void initNeedyFesaConfigJson(String pathname) {
		JsonObject needyFesaConfigJson = new JsonObject();
		needyFesaConfigJson.addProperty("autoVote", true);
		needyFesaConfigJson.addProperty("minWaitTime", 50);
		needyFesaConfigJson.addProperty("maxWaitTime", 1000);

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

		registerJson(pathname, needyFesaConfigJson);
	}

	private static void initReplaceMessagesJson(String pathname) {
		JsonArray replaceMessagesJson = new JsonArray();

		JsonObject replaceMessageExample = new JsonObject();
		replaceMessageExample.addProperty("text", "");
		replaceMessageExample.addProperty("msg", "");

		replaceMessagesJson.add(replaceMessageExample);

		registerJson(pathname, replaceMessagesJson);
	}

	private static void initMapInfo(String pathname) {
		JsonObject mapInfo = new JsonObject();
		mapInfo.add("teamColourOrder", new JsonObject());
		mapInfo.add("teamBuildLimit", new JsonObject());

		registerJson(pathname, mapInfo);
	}

	private static void initAutoMessages(String pathname) {
		JsonArray autoMessagesJson = new JsonArray();

		JsonObject autoMessagesExample = new JsonObject();
		autoMessagesExample.addProperty("regex", "");
		autoMessagesExample.addProperty("msg", "");
		autoMessagesExample.addProperty("command", false);
		autoMessagesExample.addProperty("chat", false);
		autoMessagesExample.addProperty("sound", false);
		autoMessagesExample.addProperty("sound_id", "");

		autoMessagesJson.add(autoMessagesExample);

		registerJson(pathname, autoMessagesJson);
	}

	private static void initChestLocations(String pathname){
		JsonArray chestLocations = new JsonArray();
		registerJson(pathname, chestLocations);
	}

	private static void registerJson(String pathname, JsonObject jsonObject) {
		try {
			FileWriter file = new FileWriter(pathname);
			file.write(jsonObject.toString());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void registerJson(String pathname, JsonArray jsonArray) {
		try {
			FileWriter file = new FileWriter(pathname);
			file.write(jsonArray.toString());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void JsonReload() {
		try {
			staticLobbyChestLocations = (new Gson()).fromJson(new FileReader("./config/NeedyFesa/staticLobbyChestLocations.json"), JsonArray.class);
		} catch (FileNotFoundException e) {
			LOGGER.error("staticLobbyChestLocations.json was not found! Needyfesa might not work as expected.");
			e.printStackTrace();
		}
		try {
			staticAutoMessages = (new Gson()).fromJson(new FileReader("./config/NeedyFesa/staticAutoMessages.json"), JsonArray.class);
		} catch (FileNotFoundException e) {
			LOGGER.error("staticAutoMessages.json was not found! Needyfesa might not work as expected.");
			e.printStackTrace();
		}
		try {
			staticReplaceMessages = (new Gson()).fromJson(new FileReader("./config/NeedyFesa/staticReplaceMessages.json"), JsonArray.class);
		} catch (FileNotFoundException e) {
			LOGGER.error("staticReplaceMessages.json was not found! Needyfesa might not work as expected.");
			e.printStackTrace();
		}
		try {
			mapInfo = (new Gson()).fromJson(new FileReader("./config/NeedyFesa/EggWarsMapInfo.json"), JsonObject.class);
		} catch (FileNotFoundException e) {
			LOGGER.error("EggWarsMapInfo.json was not found! Needyfesa might not work as expected.");
			e.printStackTrace();
		}
		try {
			needyFesaConfig = (new Gson()).fromJson(new FileReader("./config/NeedyFesa/needyFesaConfig.json"), JsonObject.class);
		} catch (FileNotFoundException e) {
			LOGGER.error("needyFesaConfig.json was not found! Needyfesa might not work as expected.");
			e.printStackTrace();
		}
	}
}

