package fesa.needyfesa.GUI;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import fesa.needyfesa.NeedyFesa;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {

            String ID = "needyfesa";
            return YetAnotherConfigLib.createBuilder()
                    .title(Text.translatable(ID + "title"))
                    .category(ConfigCategory.createBuilder()
                            .name(Text.translatable(ID + ".title"))
                            .group(OptionGroup.createBuilder()
                                    .name(Text.translatable(ID + ".general.title"))

                                    .option(Option.createBuilder(boolean.class)
                                            .name(Text.translatable(ID + ".general.AutoVote"))
                                            .binding(NeedyFesa.configManager.needyFesaConfig.get("AutoVote").getAsBoolean(),
                                                    () -> NeedyFesa.configManager.needyFesaConfig.get("AutoVote").getAsBoolean(),
                                                    (value) -> NeedyFesa.configManager.needyFesaConfig.getAsJsonObject().addProperty("AutoVote", value))

                                            .build())


                                    .build())

                            .build()
                    )
                    .save(NeedyFesa.configManager::saveConfig)
                    .build()
                    .generateScreen(parent);
        };
    }
}
