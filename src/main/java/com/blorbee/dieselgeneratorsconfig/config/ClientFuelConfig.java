package com.blorbee.dieselgeneratorsconfig.config;

import com.google.gson.JsonObject;
import com.jesz.createdieselgenerators.fuel_type.FuelType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ClientFuelConfig {
    private static final ResourceKey<Registry<FuelType>> FUEL_TYPE_REGISTRY = FuelConfig.FUEL_TYPE_REGISTRY;

    @Nullable
    public static JsonObject getFuelEntry(FuelType fuelType) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null)
            return null;

        Registry<FuelType> registry = minecraft.level.registryAccess()
            .registryOrThrow(FUEL_TYPE_REGISTRY);

        ResourceLocation id = registry.getResourceKey(fuelType)
            .map(ResourceKey::location)
            .orElse(null);

        if (id == null)
            return null;

        return FuelConfig.getClientEntry(id);
    }
}
