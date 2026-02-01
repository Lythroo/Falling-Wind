package com.lythro.fallingwind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("fallingwind");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(
            FabricLoader.getInstance().getConfigDir().toFile(),
            "fallingwind.json"
    );

    // Configuration values with defaults
    public double minFallSpeed = -0.5;
    public double maxFallSpeed = -3.0;
    public float maxVolume = 0.6f;
    public float minVolume = 0.0f;
    public float fadeSpeed = 0.05f;
    public int delayBeforeStart = 2;
    public boolean enableMod = true;

    private static ModConfig INSTANCE = null;

    public static ModConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static ModConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                LOGGER.info("Loaded config from {}", CONFIG_FILE.getAbsolutePath());
                return config;
            } catch (IOException e) {
                LOGGER.error("Failed to load config, using defaults", e);
            }
        }
        
        ModConfig config = new ModConfig();
        config.save();
        return config;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
            LOGGER.info("Saved config to {}", CONFIG_FILE.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }
}
