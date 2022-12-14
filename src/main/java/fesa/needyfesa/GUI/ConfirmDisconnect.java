package fesa.needyfesa.GUI;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.text.Text;

public class ConfirmDisconnect extends ConfirmScreen {
    public ConfirmDisconnect(BooleanConsumer consumer, Text title) {
        super(consumer, title, Text.empty());
    }
}
