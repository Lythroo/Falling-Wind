package com.lythro.fallingwind;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    // Using the elytra flying sound
    public static final Identifier WIND_ID = Identifier.of("minecraft", "item.elytra.flying");
    public static final SoundEvent WIND_SOUND = SoundEvent.of(WIND_ID);

    public static void register() {
        // No need to register since we're using a vanilla Minecraft sound
        // But keeping this method for future custom sounds
    }
}