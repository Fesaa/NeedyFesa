package fesa.needyfesa.mixin;

import fesa.needyfesa.NeedyFesa;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Shadow @Final private ClientWorld.Properties clientWorldProperties;

    @Inject(at = @At("TAIL"), method = "setTimeOfDay", cancellable = true)
    @Environment(EnvType.CLIENT)
    public void setTimeOfDay(long time, CallbackInfo ci) {
        if (NeedyFesa.configManager.needyFesaConfig.get("customTime").getAsBoolean()) {
            this.clientWorldProperties.setTimeOfDay(NeedyFesa.configManager.needyFesaConfig.get("timeOfDay").getAsInt());
        } else {
            ci.cancel();
        }
    }
}
