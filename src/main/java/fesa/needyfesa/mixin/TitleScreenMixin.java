package fesa.needyfesa.mixin;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static fesa.needyfesa.GUI.ConfirmDisconnectHelperFunctions.quitGame;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Redirect(method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
                    ordinal = 2
            ))
    public Element onInit(TitleScreen instance, Element guiEventListener) {
        final int l = this.height / 4 + 48;
        return this.addDrawableChild(ButtonWidget.builder(Text.translatable("needyfesa.confirmDisconnect"), (button) -> {
            final var mc = this.client;
            if (mc == null) return;
            mc.setScreen(quitGame.apply(mc));
        }).dimensions(this.width / 2 + 2, l + 72 + 12, 98, 20).build());
    }
}
