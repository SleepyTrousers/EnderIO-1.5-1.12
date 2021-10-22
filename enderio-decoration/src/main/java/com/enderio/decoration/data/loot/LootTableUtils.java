package com.enderio.decoration.data.loot;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;

// TODO: Name
public class LootTableUtils {

    public static <T extends Block> void withPaint(RegistrateBlockLootTables loot, T block) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("paint", "BlockEntityTag.paint")))));
    }

    public static <T extends Block> void paintedSlab(RegistrateBlockLootTables loot, T block) {
        loot.add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(LootItem
                .lootTableItem(block)
                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("paint", "BlockEntityTag.paint"))
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(block).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.TOP))))))
            .withPool(new LootPool.Builder().add(LootItem
                .lootTableItem(block)
                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("paint2", "BlockEntityTag.paint"))
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(block).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.BOTTOM)))))));
    }
}
