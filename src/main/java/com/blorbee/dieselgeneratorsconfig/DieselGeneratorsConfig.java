package com.blorbee.dieselgeneratorsconfig;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(DieselGeneratorsConfig.MOD_ID)
public class DieselGeneratorsConfig {
    public static final String MOD_ID = "dieselgeneratorsconfig";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DieselGeneratorsConfig(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Diesel Generators Configurator Gantry started");
    }

    public static Component lang(String path, Object... args) {
        return Component.translatable(MOD_ID + "." + path, args);
    }

    public static ResourceLocation path(final String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }

    public static ResourceLocation upperPath(final String path) {
        return ResourceLocation.fromNamespaceAndPath("createdieselgenerators", path);
    }
}
