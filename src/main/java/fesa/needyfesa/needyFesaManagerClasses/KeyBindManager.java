package fesa.needyfesa.needyFesaManagerClasses;

import com.google.gson.JsonObject;
import fesa.needyfesa.GUI.NeedyFesaConfigScreens;
import fesa.needyfesa.cubeCode.AutoVote;
import fesa.needyfesa.cubeCode.ChestFinder;
import fesa.needyfesa.cubeCode.EggWarsMapInfo;
import fesa.needyfesa.cubeCode.VarManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class KeyBindManager {

    private static JsonObject keyBinds;

    private static final ArrayList<KeyBinding> keyBindingArrayList = new ArrayList<>();

    public static void loadKeyBinds() {
        JsonObject keyBindsJson = new JsonObject();
        keyBindsJson.addProperty("Chest Finder", GLFW.GLFW_KEY_KP_7);
        keyBindsJson.addProperty("Auto Vote [EggWars]", GLFW.GLFW_KEY_KP_8);
        keyBindsJson.addProperty("Map Info", GLFW.GLFW_KEY_KP_9);
        keyBindsJson.addProperty("Mod Menu", GLFW.GLFW_KEY_RIGHT_SHIFT);

        keyBinds = keyBindsJson;
        registerKeyBinds();
    }

    public static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (KeyBinding keyBind : keyBindingArrayList) {
                while (keyBind.wasPressed()) {
                    switch (keyBind.getTranslationKey()) {
                        case "Chest Finder" -> ChestFinder.chestRequest(15);
                        case "Auto Vote [EggWars]" -> AutoVote.vote();
                        case "Map Info" -> EggWarsMapInfo.handleRequest(VarManager.map, VarManager.teamColour, false);
                        case "Mod Menu" -> MinecraftClient.getInstance().setScreen(NeedyFesaConfigScreens.baseScreen(MinecraftClient.getInstance().currentScreen));
                    }
                }
            }
        });
    }
    private static void registerKeyBinds() {
        if (keyBinds != null) {
            for (String key: keyBinds.keySet()) {
                KeyBinding newKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(key, InputUtil.Type.KEYSYM, keyBinds.get(key).getAsInt(), "NeedyFesa"));
                keyBindingArrayList.add(newKeyBind);
            }
        }
    }

}
