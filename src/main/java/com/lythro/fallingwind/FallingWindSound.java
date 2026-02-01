package com.lythro.fallingwind;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;

public class FallingWindSound extends MovingSoundInstance {
    private final PlayerEntity player;
    private float targetVolume;

    public FallingWindSound(PlayerEntity player, float initialVolume) {
        super(ModSounds.WIND_SOUND, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f; // Start silent
        this.targetVolume = initialVolume;
        this.relative = false;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    @Override
    public void tick() {
        // Check if player still exists
        if (player.isRemoved()) {
            this.setDone();
            return;
        }

        // Update position to follow player
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();

        // Get fade speed from config
        float fadeSpeed = FallingWindMod.getConfig().fadeSpeed;

        // Smoothly fade volume towards target
        if (this.volume < targetVolume) {
            this.volume = Math.min(targetVolume, this.volume + fadeSpeed);
        } else if (this.volume > targetVolume) {
            this.volume = Math.max(targetVolume, this.volume - fadeSpeed);
        }

        // Stop the sound if volume reaches zero
        if (this.volume <= 0.01f && targetVolume <= 0.01f) {
            this.setDone();
        }
    }

    public void setTargetVolume(float volume) {
        this.targetVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public float getTargetVolume() {
        return this.targetVolume;
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public boolean canPlay() {
        return !player.isRemoved();
    }
}