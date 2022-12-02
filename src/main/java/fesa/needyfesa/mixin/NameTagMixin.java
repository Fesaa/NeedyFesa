package fesa.needyfesa.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntityRenderer.class})
public class NameTagMixin {
    @Inject(at = {@At("HEAD")}, method = {"Lnet/minecraft/client/render/entity/LivingEntityRenderer;hasLabel(Lnet/minecraft/entity/LivingEntity;)Z"}, cancellable = true)
    private void showName(LivingEntity entity, CallbackInfoReturnable<Boolean> booleanCallbackInfoReturnable) {
        if (entity == MinecraftClient.getInstance().cameraEntity) {
            booleanCallbackInfoReturnable.setReturnValue(MinecraftClient.isHudEnabled());
        }
    }
}
