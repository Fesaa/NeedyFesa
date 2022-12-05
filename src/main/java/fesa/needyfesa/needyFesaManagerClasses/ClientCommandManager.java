package fesa.needyfesa.needyFesaManagerClasses;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fesa.needyfesa.NeedyFesa;
import fesa.needyfesa.cubeCode.GameManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
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
            );

            for (Field field: NeedyFesa.class.getFields()) {
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

    private static int changeIntField(Field field, CommandContext<FabricClientCommandSource> ctx) {
        try {
            Object v = field.get(NeedyFesa.class);
            field.set(v, getInteger(ctx, "value"));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bUpdated" + field.getName() + "to" + getInteger(ctx, "value") + "."));
        return Command.SINGLE_SUCCESS;
    }

    private static int changeStringField(Field field, CommandContext<FabricClientCommandSource> ctx) {
        try {
            Object v = field.get(NeedyFesa.class);
            field.set(v, getString(ctx, "value"));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bUpdated" + field.getName() + "to" + getString(ctx, "value") + "."));
        return Command.SINGLE_SUCCESS;
    }

    private static int debugFeedback() {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(GameManager.debugString()));
        return Command.SINGLE_SUCCESS;
    }

    private static int switchPartyStatus() {
        GameManager.partyStatus = !GameManager.partyStatus;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bUpdated partyStatus to " + GameManager.partyStatus + "."));
        return Command.SINGLE_SUCCESS;
    }

    private static int switchLogParty() {
        GameManager.logParty = !GameManager.logParty;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bUpdated logParty to " + GameManager.logParty + "."));
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadFunc() {
        NeedyFesa.configManager.loadConfig();
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bReloaded all json files. Can't confirm if any of them were successful." + GameManager.partyStatus));
        return Command.SINGLE_SUCCESS;
    }

}
