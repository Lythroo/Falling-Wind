package com.lythro.fallingwind;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FallingWindMod implements ClientModInitializer {
    public static final String MOD_ID = "fallingwind";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ModConfig config;
    private FallingWindSound windSound = null;
    private int ticksSinceFalling = 0;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Falling Wind Mod initializing...");

        // Load configuration
        config = ModConfig.getInstance();
        LOGGER.info("Configuration loaded");

        // Register the wind sound
        ModSounds.register();
        LOGGER.info("Wind sound registered: {}", ModSounds.WIND_ID);

        // Register tick event to check falling state
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        LOGGER.info("Falling Wind Mod initialized successfully!");
    }

    private void onClientTick(MinecraftClient client) {
        // Check if mod is enabled
        if (!config.enableMod) {
            if (windSound != null) {
                stopWindSound(client);
            }
            return;
        }

        PlayerEntity player = client.player;

        // Safety checks - also check if game is paused
        if (player == null || client.world == null || client.isPaused()) {
            stopWindSound(client);
            ticksSinceFalling = 0;
            return;
        }

        // Get player's vertical velocity
        double verticalVelocity = player.getVelocity().y;

        // Check if player is wearing elytra and is gliding
        ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
        boolean hasElytra = chestplate.isOf(Items.ELYTRA);

        // Player is gliding if they have elytra and are moving forward while falling
        double horizontalSpeed = Math.sqrt(player.getVelocity().x * player.getVelocity().x +
                player.getVelocity().z * player.getVelocity().z);
        boolean isGlidingWithElytra = hasElytra && !player.isOnGround() && horizontalSpeed > 0.5;

        // Check if player is falling (and not on ground, not in water, not flying, not using elytra)
        boolean isFalling = verticalVelocity < config.minFallSpeed
                && !player.isOnGround()
                && !player.isTouchingWater()
                && !player.getAbilities().flying
                && !isGlidingWithElytra
                && !player.hasVehicle() // Don't play when riding entities
                && !player.isClimbing(); // Don't play when climbing ladders/vines

        if (isFalling) {
            ticksSinceFalling++;

            // Calculate target volume based on fall speed
            float targetVolume = calculateVolume(verticalVelocity);

            // Start sound if not playing and delay has passed
            if (windSound == null && ticksSinceFalling > config.delayBeforeStart) {
                LOGGER.info("Starting wind sound - Fall velocity: {}", verticalVelocity);
                windSound = new FallingWindSound(player, targetVolume);
                client.getSoundManager().play(windSound);
            } else if (windSound != null) {
                // Update target volume dynamically - the sound will fade to this volume
                windSound.setTargetVolume(targetVolume);
            }
        } else {
            // Fade out the sound when not falling
            if (windSound != null) {
                windSound.setTargetVolume(0.0f);
                // The sound will stop itself when volume reaches 0
                if (windSound.getTargetVolume() <= 0.01f) {
                    windSound = null;
                }
            }
            ticksSinceFalling = 0;
        }
    }

    private float calculateVolume(double velocity) {
        // Map velocity to volume (linear interpolation)
        float normalizedSpeed = (float) Math.min(1.0,
                (config.minFallSpeed - velocity) / (config.minFallSpeed - config.maxFallSpeed));

        return config.minVolume + (config.maxVolume - config.minVolume) * normalizedSpeed;
    }

    private void stopWindSound(MinecraftClient client) {
        if (windSound != null) {
            windSound.setTargetVolume(0.0f);
            windSound = null;
        }
    }

    public static void reloadConfig() {
        config = ModConfig.getInstance();
        LOGGER.info("Configuration reloaded");
    }

    public static ModConfig getConfig() {
        return config;
    }
}