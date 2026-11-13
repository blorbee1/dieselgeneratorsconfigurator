package com.blorbee.dieselgeneratorsconfig.mixin;

import com.blorbee.dieselgeneratorsconfig.config.FuelConfig;
import com.jesz.createdieselgenerators.content.diesel_engine.EngineTypes;
import com.jesz.createdieselgenerators.content.diesel_engine.huge.HugeDieselEngineBlockEntity;
import com.jesz.createdieselgenerators.content.diesel_engine.modular.ModularDieselEngineBlockEntity;
import com.jesz.createdieselgenerators.fuel_type.FuelType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FuelType.class)
public abstract class FuelTypeMixin {
    @Inject(
        method = "getGenerated(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lcom/jesz/createdieselgenerators/fuel_type/FuelType$PerEngineProperties;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void dgc$overrideBlockEntityProperties(BlockEntity blockEntity, CallbackInfoReturnable<FuelType.PerEngineProperties> cir) {
        FuelType fuelType = (FuelType) (Object) this;
        String engine;

        if (blockEntity instanceof HugeDieselEngineBlockEntity) {
            engine = "huge";
        } else if (blockEntity instanceof ModularDieselEngineBlockEntity) {
            engine = "modular";
        } else {
            engine = "normal";
        }

        cir.setReturnValue(FuelConfig.getProperties(fuelType, engine, cir.getReturnValue()));
    }

    @Inject(
        method = "getGenerated(Lcom/jesz/createdieselgenerators/content/diesel_engine/EngineTypes;)Lcom/jesz/createdieselgenerators/fuel_type/FuelType$PerEngineProperties;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void dgc$overrideEngineProperties(EngineTypes engineType, CallbackInfoReturnable<FuelType.PerEngineProperties> cir) {
        FuelType fuelType = (FuelType) (Object) this;
        String engine;

        if (engineType == EngineTypes.HUGE) {
            engine = "huge";
        } else if (engineType == EngineTypes.MODULAR) {
            engine = "modular";
        } else {
            engine = "normal";
        }

        cir.setReturnValue(FuelConfig.getProperties(fuelType, engine, cir.getReturnValue()));
    }

    @Inject(method = "burnerStrength()F", at = @At("RETURN"), cancellable = true)
    private void dgc$overrideBurnerStrength(CallbackInfoReturnable<Float> cir) {
        FuelType fuelType = (FuelType) (Object) this;
        cir.setReturnValue(FuelConfig.getBurnerMultiplier(fuelType, cir.getReturnValue()));
    }
}
