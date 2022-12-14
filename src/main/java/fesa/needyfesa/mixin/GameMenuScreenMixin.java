package fesa.needyfesa.mixin;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static fesa.needyfesa.GUI.ConfirmDisconnectHelperFunctions.quit2Title;


@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Redirect(method = "initWidgets",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/ClickableWidget;I)Lnet/minecraft/client/gui/widget/ClickableWidget;",
                    ordinal = 0
            ))
    public <T extends ClickableWidget> T onCreate(GridWidget.Adder adder, T widget, int occupiedColumns) {
        final var mc = this.client;
        assert mc != null;
        final var component = mc.isInSingleplayer() ? Text.translatable("menu.returnToMenu") : Text.translatable("menu.disconnect");
        return (T) adder.add(ButtonWidget.builder(component, (button) -> {
            button.active = false;
            mc.setScreen(quit2Title.apply(mc, button));
        }).width(204).build(), 2);
    }
}

