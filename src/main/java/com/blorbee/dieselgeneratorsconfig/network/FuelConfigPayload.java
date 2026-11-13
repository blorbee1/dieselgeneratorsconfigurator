package com.blorbee.dieselgeneratorsconfig.network;

import com.blorbee.dieselgeneratorsconfig.DieselGeneratorsConfig;
import com.blorbee.dieselgeneratorsconfig.config.FuelConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FuelConfigPayload(String json) implements CustomPacketPayload {
    public static final Type<FuelConfigPayload> TYPE = new Type<>(DieselGeneratorsConfig.path("fuel_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FuelConfigPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            FuelConfigPayload::json,
            FuelConfigPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FuelConfigPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> FuelConfig.setClientConfig(payload.json()));
    }
}
