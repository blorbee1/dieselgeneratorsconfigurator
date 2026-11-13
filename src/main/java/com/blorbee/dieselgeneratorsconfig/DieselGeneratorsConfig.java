package com.blorbee.dieselgeneratorsconfig;

import com.blorbee.dieselgeneratorsconfig.config.FuelConfig;
import com.blorbee.dieselgeneratorsconfig.network.FuelConfigPayload;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

@Mod(DieselGeneratorsConfig.MOD_ID)
public class DieselGeneratorsConfig {
    public static final String MOD_ID = "dieselgeneratorsconfig";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DieselGeneratorsConfig(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLogin);

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

    public void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(FuelConfigPayload.TYPE, FuelConfigPayload.STREAM_CODEC, FuelConfigPayload::handle);
    }

    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;

        String json = FuelConfig.getConfigJson(player.server);
        if (json == null) {
            LOGGER.error("[{}] Failed to initialize fuel configuration before sending it to {}", MOD_ID, player.getName().getString());
            return;
        }

        PacketDistributor.sendToPlayer(player, new FuelConfigPayload(json));
    }
}
