package fesa.needyfesa;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeedyFesa implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("needyfesa");
	public static ConfigManager configManager = (new ConfigManager());

	// Mod Vars
	public static boolean partyStatus = false;
	public static boolean logParty = false;

	public static String gameMap = "";
	public static String teamColour = "";
	public static String game = "";

	public static int chestPartyAnnounce = 0;
	public static BlockPos currentChestCoords = null;

	@Override
	public void onInitialize() {

		configManager.initConfig();
		configManager.loadConfig();

		KeyBindManager.loadKeyBinds();
		KeyBindManager.registerEvents();

		ClientCommandManager.registerCommands();

		LOGGER.info("NeedyFesa started successfully. \nHELLO CUTIES <3333");
	}

}

