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

import java.io.FileNotFoundException;
import java.io.FileReader;

public class NeedyFesa implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("needyfesa");
	public static boolean partyStatus  = false;
	public static boolean logParty  = false;
	public static String eggWarsMap  = "";
	public static int chestPartyAnnounce  = 0;
	public static BlockPos currentChestCoords = null;
	public static JsonArray staticLobbyChestLocations;
	public static JsonArray staticAutoMessages;
	public static  JsonObject mapInfo;
	private static final KeyBinding chestFinderKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("chestfinder", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_7, "needyfesa"));
	private static final KeyBinding autoVoteEggWarsKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("autovoteeggwars", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_8, "needyfesa"));

	@Override
	public void onInitialize() {
		JsonFarm();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (autoVoteEggWarsKeyBind.wasPressed()) {
				AutoVoteEggWars.run();
			}
			while (chestFinderKeyBind.wasPressed()) {
				ChestFinder.chestRequest(10);
			}
		});
		LOGGER.info("HELLO CUTIES <3333");
	}

	public static void JsonFarm() {
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
			mapInfo = (new Gson()).fromJson(new FileReader("./config/NeedyFesa/EggWarsMapInfo.json"), JsonObject.class);
		} catch (FileNotFoundException e) {
			LOGGER.error("EggWarsMapInfo.json was not found! Needyfesa might not work as expected.");
			e.printStackTrace();
		}
	}
}

