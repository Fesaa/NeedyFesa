package fesa.needyfesa.GUI;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.CyclingListController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import fesa.needyfesa.NeedyFesa;
import fesa.needyfesa.cubeCode.HashMaps;
import fesa.needyfesa.needyFesaManagerClasses.ConfigObjectClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;


public class NeedyFesaConfigScreens {

    private static final HashMaps converters = new HashMaps();

    public static Screen baseScreen(Screen parent) {
        String ID = "needyfesa";
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable(ID + "title"))
                .category(general(ID))
                .category(autoMessages(ID))
                .category(replaceMessages(ID))
                .category(autoVote(ID))
                .save(NeedyFesa.configManager::saveConfig)
                .build()
                .generateScreen(parent);
    }

    public static Screen baseScreen(Screen parent, int topCategory) {
        String ID = "needyfesa";

        ArrayList<ConfigCategory> configCategories = new ArrayList<>();
        configCategories.add(general(ID));
        configCategories.add(autoMessages(ID));
        configCategories.add(replaceMessages(ID));
        configCategories.add(autoVote(ID));

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder();

        builder.title(Text.translatable(ID + "title"));

        for (ConfigCategory configCategory : configCategories.subList(topCategory, configCategories.size())) {
            builder.category(configCategory);
        }

        if (topCategory != 0) {
            for (ConfigCategory configCategory : configCategories.subList(0, topCategory)) {
                builder.category(configCategory);
            }
        }

        return builder.save(NeedyFesa.configManager::saveConfig)
                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory autoVote(String ID) {
        ConfigCategory.Builder configCategoryBuild = ConfigCategory.createBuilder();

        configCategoryBuild.name(Text.translatable(ID + ".AutoVote.title"));
        configCategoryBuild.tooltip(Text.translatable(ID + ".AutoVote.desc"));

        configCategoryBuild.option(intOption("AutoVote", "minWaitTime", ID, NeedyFesa.configManager.needyFesaConfig.getAsJsonObject()));
        configCategoryBuild.option(intOption("AutoVote", "maxWaitTime", ID, NeedyFesa.configManager.needyFesaConfig.getAsJsonObject()));

        JsonObject teamEggWarsVoting = NeedyFesa.configManager.needyFesaConfig.get("Team EggWars").getAsJsonObject();
        JsonObject soloSkyWarsVoting = NeedyFesa.configManager.needyFesaConfig.get("Solo SkyWars").getAsJsonObject();
        JsonObject luckyIslandsVoting = NeedyFesa.configManager.needyFesaConfig.get("Lucky Islands").getAsJsonObject();


        configCategoryBuild.group(OptionGroup.createBuilder()
                        .name(Text.translatable(ID + ".AutoVote.TeamEggWars"))
                        .tooltip(Text.translatable(ID + ".AutoVote.TeamEggWars.desc"))
                        .option(listStringOption("AutoVote.TeamEggWars", "leftVoteId", ID, teamEggWarsVoting,
                                                List.of("Hardcore", "Normal", "Overpowered"), converters.EggWarsItemsInv.get(teamEggWarsVoting.get("leftVoteId").getAsInt()),
                                                converters.EggWarsItems, converters.EggWarsItemsInv))
                        .option(listStringOption("AutoVote.TeamEggWars", "rightVoteId", ID, teamEggWarsVoting,
                               List.of("Half", "Normal", "Double"), converters.EggWarsHealthInv.get(teamEggWarsVoting.get("rightVoteId").getAsInt()),
                                converters.EggWarsHealth, converters.EggWarsHealthInv))
                .collapsed(true)
                .build());

        configCategoryBuild.group(OptionGroup.createBuilder()
                        .name(Text.translatable(ID + ".AutoVote.SoloSkyWars"))
                        .tooltip(Text.translatable(ID + ".AutoVote.SoloSkyWars.desc"))
                        .option(listStringOption("AutoVote.SoloSkyWars", "leftVoteId", ID, soloSkyWarsVoting,
                                List.of("Basic", "Normal", "Overpowered"), converters.SkyWarsChestsInv.get(soloSkyWarsVoting.get("leftVoteId").getAsInt()),
                                converters.SkyWarsChests, converters.SkyWarsChestsInv))
                        .option(listStringOption("AutoVote.SoloSkyWars", "middleVoteId", ID, soloSkyWarsVoting,
                                List.of("No Projectiles", "Normal Projectiles", "Soft Blocks"), converters.SkyWarsProjectilesInv.get(soloSkyWarsVoting.get("middleVoteId").getAsInt()),
                                converters.SkyWarsProjectiles, converters.SkyWarsProjectilesInv))
                        .option(listStringOption("AutoVote.SoloSkyWars", "rightVoteId", ID, soloSkyWarsVoting,
                                List.of("Day Time", "Night Time", "Sunset"), converters.TimeInv.get(soloSkyWarsVoting.get("rightVoteId").getAsInt()),
                                converters.Time, converters.TimeInv))
                .collapsed(true)
                .build());

        configCategoryBuild.group(OptionGroup.createBuilder()
                .name(Text.translatable(ID + ".AutoVote.LuckyIslands"))
                .tooltip(Text.translatable(ID + ".AutoVote.LuckyIslands.desc"))
                .option(listStringOption("AutoVote.LuckyIslands", "leftVoteId", ID, luckyIslandsVoting,
                        List.of("Blessed", "Normal", "Overpowered", "Crazy"), converters.LuckyIslandsBlocksInv.get(luckyIslandsVoting.get("leftVoteId").getAsInt()),
                        converters.LuckyIslandsBlocks, converters.LuckyIslandsBlocksInv))
                .option(listStringOption("AutoVote.LuckyIslands", "rightVoteId", ID, luckyIslandsVoting,
                        List.of("Day Time", "Night Time", "Sunset"), converters.TimeInv.get(luckyIslandsVoting.get("rightVoteId").getAsInt()),
                        converters.Time, converters.TimeInv))
                .collapsed(true)
                .build());

        return configCategoryBuild.build();
    }

    private static ConfigCategory general(String ID) {
        ConfigCategory.Builder configCategoryBuild = ConfigCategory.createBuilder();

        List<String> lst = NeedyFesa.configManager.staticLobbyChestLocations.getAsJsonObject().keySet().stream().toList();
        List<String> event_list = new ArrayList<>();
        String current_event = NeedyFesa.configManager.staticLobbyChestLocations.get("current-event").getAsString();
        for (String s : lst) {
            if (s.equals("current-event")) {continue;}
            event_list.add(s);
        }

        configCategoryBuild.name(Text.translatable(ID + ".general.title"));
        configCategoryBuild.tooltip(Text.translatable(ID + ".general.desc"));

        configCategoryBuild.option(booleanOption("general", "autoVote", ID, NeedyFesa.configManager.needyFesaConfig.getAsJsonObject()));
        configCategoryBuild.option(booleanOption("general", "autoMessages", ID, NeedyFesa.configManager.needyFesaConfig.getAsJsonObject()));
        configCategoryBuild.option(booleanOption("general", "chatReplacement", ID, NeedyFesa.configManager.needyFesaConfig.getAsJsonObject()));
        configCategoryBuild.option(booleanOption("general", "chestFinder", ID, NeedyFesa.configManager.needyFesaConfig.getAsJsonObject()));
        configCategoryBuild.option(listStringOption("general", "current-event", ID, NeedyFesa.configManager.staticLobbyChestLocations.getAsJsonObject(), event_list, current_event));
        configCategoryBuild.option(booleanOption("general", "spam-prevention", ID, NeedyFesa.configManager.needyFesaConfig.getAsJsonObject()));
        configCategoryBuild.option(booleanOption("general", "development-mode", ID, NeedyFesa.configManager.needyFesaConfig.getAsJsonObject()));

        return configCategoryBuild.build();
    }

    private static ConfigCategory replaceMessages(String ID) {
        ConfigCategory.Builder configCategoryBuilder = ConfigCategory.createBuilder();

        configCategoryBuilder.name(Text.translatable(ID + ".replaceMessages.title"));
        configCategoryBuilder.tooltip(Text.translatable(ID + ".replaceMessages.desc"));

        int i = 1;
        for (JsonElement replaceMessageElement :  NeedyFesa.configManager.staticReplaceMessages.getAsJsonArray()) {
            JsonObject replacementMessage = replaceMessageElement.getAsJsonObject();
            configCategoryBuilder.group(replaceMessageGroup(replacementMessage, i, ID)); i++;
        }

        configCategoryBuilder.option(addButton(NeedyFesa.configManager.staticReplaceMessages,2, ID));
        return configCategoryBuilder.build();
    }

    private static ConfigCategory autoMessages(String ID) {
        ConfigCategory.Builder configCategoryBuilder = ConfigCategory.createBuilder();

        configCategoryBuilder.name(Text.translatable(ID + ".autoMessages.title"));
        configCategoryBuilder.tooltip(Text.translatable(ID + ".autoMessages.desc"));

        int i = 1;
        for (JsonElement autoMessageElement : NeedyFesa.configManager.staticAutoMessages.getAsJsonArray()) {
            JsonObject autoMessage = autoMessageElement.getAsJsonObject();
            configCategoryBuilder.group(autoMessageGroup(autoMessage, i, ID)); i++;
        }


        configCategoryBuilder.option(addButton(NeedyFesa.configManager.staticAutoMessages, 1, ID));
        return configCategoryBuilder.build();
    }

    private static OptionGroup replaceMessageGroup(JsonObject replacementMessage, int i, String ID) {
        OptionGroup.Builder optionGroupBuilder = OptionGroup.createBuilder();

        optionGroupBuilder.name(Text.of("Text replacement #" + i));

        optionGroupBuilder.option(booleanOption("replaceMessages", "enabled", ID, replacementMessage));
        optionGroupBuilder.option(StringOption("replaceMessages", "text", ID, replacementMessage));
        optionGroupBuilder.option(StringOption("replaceMessages", "msg", ID, replacementMessage));
        optionGroupBuilder.option(deleteButton(NeedyFesa.configManager.staticAutoMessages.getAsJsonArray(), replacementMessage, 2, ID));

        optionGroupBuilder.collapsed(true);
        return optionGroupBuilder.build();
    }

    private static OptionGroup autoMessageGroup(JsonObject autoMessage, int i, String ID) {
        OptionGroup.Builder optionGroupBuilder = OptionGroup.createBuilder();

        optionGroupBuilder.name(Text.of("Chat Listener #" + i));

        optionGroupBuilder.option(booleanOption("autoMessages", "enabled", ID, autoMessage));
        optionGroupBuilder.option(StringOption("autoMessages", "regex", ID, autoMessage));
        optionGroupBuilder.option(booleanOption("autoMessages", "regex_matching", ID, autoMessage));
        optionGroupBuilder.option(StringOption("autoMessages", "msg", ID, autoMessage));
        optionGroupBuilder.option(booleanOption("autoMessages", "command", ID, autoMessage));
        optionGroupBuilder.option(booleanOption("autoMessages", "chat", ID, autoMessage));
        optionGroupBuilder.option(booleanOption("autoMessages", "party_message", ID, autoMessage));
        optionGroupBuilder.option(booleanOption("autoMessages", "sound", ID, autoMessage));
        optionGroupBuilder.option(StringOption("autoMessages", "sound_id", ID, autoMessage));
        optionGroupBuilder.option(deleteButton(NeedyFesa.configManager.staticAutoMessages.getAsJsonArray(), autoMessage, 1, ID));

        optionGroupBuilder.collapsed(true);
        return optionGroupBuilder.build();
    }

    private static Option<Integer> addButton(ConfigObjectClass configObjectClass, int topCategory, String ID) {
        return Option.createBuilder(int.class)
                .name(Text.translatable(ID + ".button.add"))
                .binding(
                        0,
                        () -> 0,
                        (value) -> {
                            for (int times = 0; times < value; times++ ) {
                                configObjectClass.addDefaultToArray();
                            }
                            MinecraftClient.getInstance().setScreen(NeedyFesaConfigScreens.baseScreen(null, topCategory));
                        }
                )
                .controller(opt -> new IntegerSliderController(opt, 0, 5, 1))
                .build();
    }

    private static Option<Boolean> deleteButton(JsonArray toDeleteIn, JsonObject toDelete, int topCategory, String ID) {
        return Option.createBuilder(Boolean.class)
                .name(Text.translatable(ID + ".button.delete"))
                .binding(
                        false,
                        () -> false,
                        (value) -> {
                            if (value) {
                                toDeleteIn.remove(toDelete);
                                MinecraftClient.getInstance().setScreen(NeedyFesaConfigScreens.baseScreen(null, topCategory));
                            }
                        }
                )
                .controller(TickBoxController::new)
                .build();

    }

    private static Option<String> listStringOption(String category, String s, String ID, JsonObject json, List<String> lst, String current, HashMap<String, Integer> converter, HashMap<Integer, String> converterInv) {
        return Option.createBuilder(String.class)
                .name(Text.translatable(ID + "." + category +"." + s))
                .binding(
                        current,
                        () -> converterInv.get(json.get(s).getAsInt()),
                        (value) -> json.addProperty(s, converter.get(value))
                )
                .controller(opt -> new CyclingListController<>(opt, lst))
                .build();
    }

    private static Option<String> listStringOption(String category, String s, String ID, JsonObject json, List<String> lst, String current) {
        return Option.createBuilder(String.class)
                .name(Text.translatable(ID + "." + category +"." + s))
                .tooltip(Text.translatable(ID + "." + category +"." + s +".desc"))
                .binding(
                        current,
                        () -> json.get(s).getAsString(),
                        (value) -> json.addProperty(s, value)
                )
                .controller(opt -> new CyclingListController<>(opt, lst))
                .build();
    }

    private static Option<Integer> intOption(String category, String s, String ID, JsonObject json) {
        return Option.createBuilder(int.class)
                .name(Text.translatable(ID + "." + category +"." + s))
                .tooltip(Text.translatable(ID + "." + category +"." + s +".desc"))
                .binding(
                        json.get(s).getAsInt(),
                        () -> json.get(s).getAsInt(),
                        (value) -> json.addProperty(s, value)
                )
                .controller(IntegerFieldController::new)
                .build();
    }

    private static Option<String> StringOption(String category, String s, String ID, JsonObject json) {
        return Option.createBuilder(String.class)
                .name(Text.translatable(ID + "." + category +"." + s))
                .tooltip(Text.translatable(ID + "." + category +"." + s +".desc"))
                .binding(
                        json.get(s).getAsString(),
                        () -> json.get(s).getAsString(),
                        (value) -> json.addProperty(s, value)
                )
                .controller(StringController::new)
                .build();
    }

    private static Option<Boolean> booleanOption(String category, String s, String ID, JsonObject json) {
        return Option.createBuilder(boolean.class)
                .name(Text.translatable(ID + "." + category +"." + s))
                .tooltip(Text.translatable(ID + "." + category +"." + s +".desc"))
                .binding(json.get(s).getAsBoolean(),
                        () -> json.get(s).getAsBoolean(),
                        (value) -> json.getAsJsonObject().addProperty(s, value))
                .controller(TickBoxController::new)
                .build();
    }
}
