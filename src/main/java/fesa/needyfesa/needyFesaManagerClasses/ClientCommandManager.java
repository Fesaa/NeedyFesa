package fesa.needyfesa.needyFesaManagerClasses;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fesa.needyfesa.NeedyFesa;
import fesa.needyfesa.cubeCode.VarManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static com.mojang.brigadier.arguments.IntegerArgumentType.*;

public class ClientCommandManager {

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandManager::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("NeedyFesa")
                .then(literal("logParty")
                        .executes(ctx -> switchLogParty()))
                .then(literal("partyStatus")
                        .executes(ctx -> switchPartyStatus()))
        );
        if (NeedyFesa.configManager.needyFesaConfig.get("development-mode").getAsBoolean()) {
            dispatcher.register(literal("NeedyFesa")
                    .then(literal("debug")
                            .executes(ctx -> debugFeedback()))
                    .then(literal("reload")
                            .executes(ctx -> reloadFunc()))
                    .then(literal("update-server-ip")
                            .executes(ctx -> updateServerIp(ctx)))
            );

            for (Field field: VarManager.class.getFields()) {
                if (!(field.getName().equals("LOGGER") || field.getName().equals("configManager"))) {
                    if (field.getType().equals(String.class)) {
                        dispatcher.register(literal("NeedyFesa")
                                .then(literal("set")
                                .then(literal(field.getName())
                                .then(argument("value", greedyString())
                                        .executes(ctx -> changeStringField(field, ctx)))))
                        );
                    } else if (field.getType().equals(int.class)) {
                        dispatcher.register(literal("NeedyFesa")
                                .then(literal("set")
                                .then(literal(field.getName())
                                .then(argument("value", integer())
                                 .executes(ctx -> changeIntField(field, ctx)))))
                        );
                    }
                }
            }
        }
    }

    private static int updateServerIp(CommandContext<FabricClientCommandSource> ctx) {




        ServerInfo server = MinecraftClient.getInstance().getCurrentServerEntry();
        if (server != null) {
            VarManager.serverIP = server.address.split(":")[0];
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bUpdated to server ip to " + VarManager.serverIP));
        } else {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§4Server was null?"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int changeIntField(Field field, CommandContext<FabricClientCommandSource> ctx) {
        try {
            Object v = field.get(VarManager.class);
            field.set(v, getInteger(ctx, "value"));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bUpdated" + field.getName() + "to" + getInteger(ctx, "value") + "."));
        return Command.SINGLE_SUCCESS;
    }

    private static int changeStringField(Field field, CommandContext<FabricClientCommandSource> ctx) {
        try {
            Object v = field.get(VarManager.class);
            field.set(v, getString(ctx, "value"));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bUpdated" + field.getName() + "to" + getString(ctx, "value") + "."));
        return Command.SINGLE_SUCCESS;
    }

    private static int debugFeedback() {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(VarManager.debugString()));
        return Command.SINGLE_SUCCESS;
    }

    private static int switchPartyStatus() {
        VarManager.partyStatus = !VarManager.partyStatus;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bUpdated partyStatus to " + VarManager.partyStatus + "."));
        return Command.SINGLE_SUCCESS;
    }

    private static int switchLogParty() {
        VarManager.logParty = !VarManager.logParty;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bUpdated logParty to " + VarManager.logParty + "."));
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadFunc() {
        NeedyFesa.configManager.loadConfig();
        NeedyFesa.configManager.processConfig();
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bReloaded all json files. Can't confirm if any of them were successful."));
        return Command.SINGLE_SUCCESS;
    }

}
