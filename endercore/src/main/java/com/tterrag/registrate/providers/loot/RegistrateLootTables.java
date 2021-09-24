package com.tterrag.registrate.providers.loot;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public interface RegistrateLootTables extends Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {

    default void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationresults) {}
    
}
