package com.blorbee.dieselgeneratorsconfig.config;

import com.blorbee.dieselgeneratorsconfig.DieselGeneratorsConfig;
import com.google.gson.*;
import com.jesz.createdieselgenerators.fuel_type.FuelType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class FuelConfig {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    private static final ResourceKey<Registry<FuelType>> FUEL_TYPE_REGISTRY =
        ResourceKey.createRegistryKey(DieselGeneratorsConfig.upperPath("fuel_type"));

    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get()
        .resolve(DieselGeneratorsConfig.MOD_ID)
        .resolve("fuel_overrides.json");

    private static JsonObject config = new JsonObject();
    private static Registry<FuelType> loadedRegistry;

    private static boolean initialized;
    private static boolean initializing;

    public static FuelType.PerEngineProperties getProperties(FuelType fuelType, String engine, FuelType.PerEngineProperties defaults) {
        JsonObject fuel = getFuelEntry(fuelType);
        if (fuel == null)
            return defaults;
        if (!fuel.has(engine) || !fuel.get(engine).isJsonObject())
            return defaults;

        JsonObject properties = fuel.getAsJsonObject(engine);
        return new FuelType.PerEngineProperties(
            getFloat(properties, "speed", defaults.speed()),
            getFloat(properties, "strength", defaults.strength()),
            getFloat(properties, "burn_rate", defaults.burn())
        );
    }

    public static float getBurnerMultiplier(FuelType fuelType, float defaults) {
        JsonObject fuel = getFuelEntry(fuelType);
        if (fuel == null)
            return defaults;
        return getFloat(fuel, "burner_multiplier", defaults);
    }

    @Nullable
    private static JsonObject getFuelEntry(FuelType fuelType) {
        if (initializing)
            return null;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return null;

        Registry<FuelType> registry;
        try {
            registry = server.registryAccess().registryOrThrow(FUEL_TYPE_REGISTRY);
        } catch (IllegalStateException exception) {
            return null;
        }

        initialize(registry);

        ResourceLocation id = registry.getResourceKey(fuelType)
            .map(ResourceKey::location)
            .orElse(null);

        if (id == null)
            return null;

        synchronized (FuelConfig.class) {
            return config.has(id.toString())
                && config.get(id.toString()).isJsonObject()
                ? config.getAsJsonObject(id.toString())
                : null;
        }
    }

    private static synchronized void initialize(Registry<FuelType> registry) {
        if (initializing || (initialized && loadedRegistry == registry))
            return;

        initializing = true;

        try {
            loadedRegistry = registry;
            load();

            boolean changed = false;
            for (FuelType fuelType : registry) {
                ResourceLocation id = registry.getResourceKey(fuelType)
                    .map(ResourceKey::location)
                    .orElse(null);

                if (id == null)
                    continue;

                String key = id.toString();
                if (!config.has(key) || !config.get(key).isJsonObject()) {
                    config.add(key, createDefaultEntry(fuelType));
                    changed = true;
                    continue;
                }

                JsonObject entry = config.getAsJsonObject(key);
                changed |= addMissingDefaults(entry, fuelType);
            }

            if (changed || !Files.exists(CONFIG_PATH))
                save();

            initialized = true;
            DieselGeneratorsConfig.LOGGER.info("[{}] Loaded {} fuel type configurations.", DieselGeneratorsConfig.MOD_ID, registry.size());
        } finally {
            initializing = false;
        }
    }

    private static JsonObject createDefaultEntry(FuelType fuelType) {
        JsonObject entry = new JsonObject();

        entry.add("normal", createProperties(fuelType.normal()));
        entry.add("modular", createProperties(fuelType.modular()));
        entry.add("huge", createProperties(fuelType.huge()));
        entry.addProperty("burner_multiplier", fuelType.burnerStrength());

        return entry;
    }

    private static boolean addMissingDefaults(JsonObject entry, FuelType fuelType) {
        boolean changed = false;

        changed |= addMissingEngine(entry, "normal", fuelType.normal());
        changed |= addMissingEngine(entry, "modular", fuelType.modular());
        changed |= addMissingEngine(entry, "huge", fuelType.huge());

        if (!entry.has("burner_multiplier")) {
            entry.addProperty("burner_multiplier", fuelType.burnerStrength());
            changed = true;
        }

        return changed;
    }

    private static boolean addMissingEngine(JsonObject entry, String engine, FuelType.PerEngineProperties defaults) {
        if (!entry.has(engine) || !entry.get(engine).isJsonObject()) {
            entry.add(engine, createProperties(defaults));
            return true;
        }

        JsonObject properties = entry.getAsJsonObject(engine);
        boolean changed = false;

        if (!properties.has("speed")) {
            properties.addProperty("speed", defaults.speed());
            changed = true;
        }

        if (!properties.has("strength")) {
            properties.addProperty("strength", defaults.strength());
            changed = true;
        }

        if (!properties.has("burn_rate")) {
            properties.addProperty("burn_rate", defaults.burn());
            changed = true;
        }

        return changed;
    }

    private static JsonObject createProperties(FuelType.PerEngineProperties properties) {
        JsonObject json = new JsonObject();

        json.addProperty("speed", properties.speed());
        json.addProperty("strength", properties.strength());
        json.addProperty("burn_rate", properties.burn());

        return json;
    }

    private static float getFloat(JsonObject object, String key, float fallback) {
        JsonElement element = object.get(key);
        if (element == null || !element.isJsonPrimitive())
            return fallback;

        try {
            return element.getAsFloat();
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private static synchronized void load() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            if (!Files.exists(CONFIG_PATH)) {
                config = new JsonObject();
                return;
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                JsonElement element = JsonParser.parseReader(reader);

                if (element.isJsonObject()) {
                    config = element.getAsJsonObject();
                } else {
                    DieselGeneratorsConfig.LOGGER.error("[{}] Invalid fuel configuration root. Using defaults.", DieselGeneratorsConfig.MOD_ID);
                    config = new JsonObject();
                }
            }
        } catch (IOException | RuntimeException exception) {
            DieselGeneratorsConfig.LOGGER.error("[{}] Failed to load fuel configuration.", DieselGeneratorsConfig.MOD_ID, exception);
            config = new JsonObject();
        }
    }

    private static synchronized void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException exception) {
            DieselGeneratorsConfig.LOGGER.error("[{}] Failed to save fuel configuration.", DieselGeneratorsConfig.MOD_ID, exception);
        }
    }
}
