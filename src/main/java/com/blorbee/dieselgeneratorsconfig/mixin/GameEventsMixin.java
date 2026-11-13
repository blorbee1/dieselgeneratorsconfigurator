package com.blorbee.dieselgeneratorsconfig.mixin;

import com.blorbee.dieselgeneratorsconfig.config.FuelConfig;
import com.jesz.createdieselgenerators.content.diesel_engine.EngineTypes;
import com.jesz.createdieselgenerators.events.GameEvents;
import com.jesz.createdieselgenerators.fuel_type.FuelType;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@OnlyIn(Dist.CLIENT)
@Mixin(value = GameEvents.class, remap = false)
public abstract class GameEventsMixin {
    private static Level dgc$getLevel() {
        return Minecraft.getInstance().level;
    }

    private static String dgc$getEngineName(EngineTypes engine) {
        return switch (engine) {
            case HUGE -> "huge";
            case MODULAR -> "modular";
            default -> "normal";
        };
    }

    @WrapOperation(
        method = "addToItemTooltip(Lnet/neoforged/neoforge/event/entity/player/ItemTooltipEvent;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/jesz/createdieselgenerators/fuel_type/FuelType;getGenerated(Lcom/jesz/createdieselgenerators/content/diesel_engine/EngineTypes;)Lcom/jesz/createdieselgenerators/fuel_type/FuelType$PerEngineProperties;"
        )
    )
    private static FuelType.PerEngineProperties dgc$overrideGeneratedProperties(FuelType instance, EngineTypes engine, Operation<FuelType.PerEngineProperties> original) {
        FuelType.PerEngineProperties defaults = original.call(instance, engine);
        Level level = dgc$getLevel();
        if (level == null)
            return defaults;

        return FuelConfig.getProperties(instance, dgc$getEngineName(engine), defaults, level);
    }

    @WrapOperation(
        method = "addToItemTooltip(Lnet/neoforged/neoforge/event/entity/player/ItemTooltipEvent;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/jesz/createdieselgenerators/fuel_type/FuelType;burnerStrength()F"
        )
    )
    private static float dgc$overrideBurnerStrength(FuelType instance, Operation<Float> original) {
        float defaults = original.call(instance);
        Level level = dgc$getLevel();
        if (level == null)
            return defaults;

        return FuelConfig.getBurnerMultiplier(instance, defaults, level);
    }

    @WrapOperation(
        method = "addToItemTooltip(Lnet/neoforged/neoforge/event/entity/player/ItemTooltipEvent;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/jesz/createdieselgenerators/fuel_type/FuelType;normal()Lcom/jesz/createdieselgenerators/fuel_type/FuelType$PerEngineProperties;"
        )
    )
    private static FuelType.PerEngineProperties dgc$overrideNormal(FuelType instance, Operation<FuelType.PerEngineProperties> original) {
        FuelType.PerEngineProperties defaults = original.call(instance);
        Level level = dgc$getLevel();
        if (level == null)
            return defaults;

        return FuelConfig.getProperties(instance, "normal", defaults, level);
    }

    @WrapOperation(
        method = "addToItemTooltip(Lnet/neoforged/neoforge/event/entity/player/ItemTooltipEvent;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/jesz/createdieselgenerators/fuel_type/FuelType;modular()Lcom/jesz/createdieselgenerators/fuel_type/FuelType$PerEngineProperties;"
        )
    )
    private static FuelType.PerEngineProperties dgc$overrideModular(FuelType instance, Operation<FuelType.PerEngineProperties> original) {
        FuelType.PerEngineProperties defaults = original.call(instance);
        Level level = dgc$getLevel();
        if (level == null)
            return defaults;

        return FuelConfig.getProperties(instance, "modular", defaults, level);
    }

    @WrapOperation(
        method = "addToItemTooltip(Lnet/neoforged/neoforge/event/entity/player/ItemTooltipEvent;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/jesz/createdieselgenerators/fuel_type/FuelType;huge()Lcom/jesz/createdieselgenerators/fuel_type/FuelType$PerEngineProperties;"
        )
    )
    private static FuelType.PerEngineProperties dgc$overrideHuge(FuelType instance, Operation<FuelType.PerEngineProperties> original) {
        FuelType.PerEngineProperties defaults = original.call(instance);
        Level level = dgc$getLevel();
        if (level == null)
            return defaults;

        return FuelConfig.getProperties(instance, "huge", defaults, level);
    }
}
