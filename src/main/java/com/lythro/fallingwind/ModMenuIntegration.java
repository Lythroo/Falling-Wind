package com.lythro.fallingwind;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ModConfig config = ModConfig.getInstance();
            
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("Falling Wind Settings"));
            
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            
            // General Category
            ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
            
            general.addEntry(entryBuilder.startBooleanToggle(
                    Text.literal("Enable Mod"),
                    config.enableMod)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Turn the falling wind sound on or off"))
                    .setSaveConsumer(value -> config.enableMod = value)
                    .build());
            
            // Sound Settings Category
            ConfigCategory sound = builder.getOrCreateCategory(Text.literal("Sound Settings"));
            
            sound.addEntry(entryBuilder.startFloatField(
                    Text.literal("Maximum Volume"),
                    config.maxVolume)
                    .setDefaultValue(0.6f)
                    .setMin(0.0f)
                    .setMax(1.0f)
                    .setTooltip(Text.literal("Maximum volume when falling at high speed (0.0 - 1.0)"))
                    .setSaveConsumer(value -> config.maxVolume = value)
                    .build());
            
            sound.addEntry(entryBuilder.startFloatField(
                    Text.literal("Minimum Volume"),
                    config.minVolume)
                    .setDefaultValue(0.0f)
                    .setMin(0.0f)
                    .setMax(1.0f)
                    .setTooltip(Text.literal("Minimum volume when starting to fall (0.0 - 1.0)"))
                    .setSaveConsumer(value -> config.minVolume = value)
                    .build());
            
            sound.addEntry(entryBuilder.startFloatField(
                    Text.literal("Fade Speed"),
                    config.fadeSpeed)
                    .setDefaultValue(0.05f)
                    .setMin(0.01f)
                    .setMax(0.2f)
                    .setTooltip(Text.literal("How quickly the sound fades in/out (0.01 - 0.2)"))
                    .setSaveConsumer(value -> config.fadeSpeed = value)
                    .build());
            
            // Fall Detection Category
            ConfigCategory detection = builder.getOrCreateCategory(Text.literal("Fall Detection"));
            
            detection.addEntry(entryBuilder.startDoubleField(
                    Text.literal("Minimum Fall Speed"),
                    config.minFallSpeed)
                    .setDefaultValue(-0.5)
                    .setMin(-2.0)
                    .setMax(0.0)
                    .setTooltip(Text.literal("How fast you need to fall to start the sound (negative number)"))
                    .setSaveConsumer(value -> config.minFallSpeed = value)
                    .build());
            
            detection.addEntry(entryBuilder.startDoubleField(
                    Text.literal("Maximum Fall Speed"),
                    config.maxFallSpeed)
                    .setDefaultValue(-3.0)
                    .setMin(-10.0)
                    .setMax(-0.5)
                    .setTooltip(Text.literal("Fall speed for maximum volume (negative number)"))
                    .setSaveConsumer(value -> config.maxFallSpeed = value)
                    .build());
            
            detection.addEntry(entryBuilder.startIntField(
                    Text.literal("Start Delay (ticks)"),
                    config.delayBeforeStart)
                    .setDefaultValue(2)
                    .setMin(0)
                    .setMax(20)
                    .setTooltip(Text.literal("How many ticks to wait before starting the sound (20 ticks = 1 second)"))
                    .setSaveConsumer(value -> config.delayBeforeStart = value)
                    .build());
            
            builder.setSavingRunnable(() -> {
                config.save();
                FallingWindMod.reloadConfig();
            });
            
            return builder.build();
        };
    }
}
