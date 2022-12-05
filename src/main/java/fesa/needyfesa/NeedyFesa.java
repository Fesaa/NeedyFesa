package fesa.needyfesa;

import fesa.needyfesa.needyFesaManagerClasses.ClientCommandManager;
import fesa.needyfesa.needyFesaManagerClasses.ConfigManager;
import fesa.needyfesa.needyFesaManagerClasses.KeyBindManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeedyFesa implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("needyfesa");
	public static ConfigManager configManager = (new ConfigManager());


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

