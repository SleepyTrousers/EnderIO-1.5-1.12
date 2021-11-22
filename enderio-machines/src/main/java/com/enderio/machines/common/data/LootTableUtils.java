package com.enderio.machines.common.data;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;

public class LootTableUtils {

    public static <T extends Block> void copyNBT(RegistrateBlockLootTables loot, T block) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("", "BlockEntityTag")))));
    }
}
