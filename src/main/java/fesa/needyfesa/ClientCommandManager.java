package fesa.needyfesa;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class ClientCommandManager {

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandManager::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("NeedyFesa")
                .then(literal("reload")
                        .executes(ctx -> reloadFunc()))
                .then(literal("logparty")
                        .executes(ctx -> switchLogParty()))
                .then(literal("partyStatus")
                        .executes(ctx -> switchPartyStatus()))
                .then(literal("debug")
                        .executes(ctx -> debugFeedback()))
        );
    }

    private static int debugFeedback() {
        String s = "";
        s += "\nDebug info for Needyfesa";
        s += "\nlogParty: " + NeedyFesa.logParty;
        s += "\nGame: " + NeedyFesa.game;
        s += "\ngameMap: " + NeedyFesa.gameMap;
        s += "\nteamColour: " + NeedyFesa.teamColour;
        s += "\npartyStatus: " + NeedyFesa.partyStatus;
        s += "\nchestPartyAnnounce: " + NeedyFesa.chestPartyAnnounce;
        if (NeedyFesa.currentChestCoords != null) {
            s += "\ncurrentChestCoords: " + NeedyFesa.currentChestCoords.toShortString();
        }
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(s + "\n"));
        return Command.SINGLE_SUCCESS;
    }

    private static int switchPartyStatus() {
        NeedyFesa.partyStatus = !NeedyFesa.partyStatus;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("\u00A7bUpdated partyStatus to " + NeedyFesa.partyStatus + "."));
        return Command.SINGLE_SUCCESS;
    }

    private static int switchLogParty() {
        NeedyFesa.logParty = !NeedyFesa.logParty;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("\u00A7bUpdated logParty to " + NeedyFesa.logParty + "."));
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadFunc() {
        NeedyFesa.configManager.loadConfig();
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("\u00A7bReloaded all json files. Can't confirm if any of them were successful." + NeedyFesa.partyStatus));
        return Command.SINGLE_SUCCESS;
    }

}
