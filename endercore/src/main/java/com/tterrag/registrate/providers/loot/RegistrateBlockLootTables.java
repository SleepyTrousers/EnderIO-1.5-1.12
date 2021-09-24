package com.tterrag.registrate.providers.loot;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.tterrag.registrate.AbstractRegistrate;

import lombok.RequiredArgsConstructor;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

@RequiredArgsConstructor
public class RegistrateBlockLootTables extends BlockLoot implements RegistrateLootTables {
    
    private final AbstractRegistrate<?> parent;
    private final Consumer<RegistrateBlockLootTables> callback;
    
    @Override
    protected void addTables() {
        callback.accept(this);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return parent.getAll(Block.class).stream().map(Supplier::get).collect(Collectors.toList());
    }

    // @formatter:off
    // GENERATED START

    public static <T> T applyExplosionDecay(ItemLike p_124132_, FunctionUserBuilder<T> p_124133_) { return BlockLoot.applyExplosionDecay(p_124132_, p_124133_); }

    public static <T> T applyExplosionCondition(ItemLike p_124135_, ConditionUserBuilder<T> p_124136_) { return BlockLoot.applyExplosionCondition(p_124135_, p_124136_); }

    public static LootTable.Builder createSingleItemTable(ItemLike p_124127_) { return BlockLoot.createSingleItemTable(p_124127_); }

    public static LootTable.Builder createSelfDropDispatchTable(Block p_124172_, LootItemCondition.Builder p_124173_, LootPoolEntryContainer.Builder<?> p_124174_) { return BlockLoot.createSelfDropDispatchTable(p_124172_, p_124173_, p_124174_); }

    public static LootTable.Builder createSilkTouchDispatchTable(Block p_124169_, LootPoolEntryContainer.Builder<?> p_124170_) { return BlockLoot.createSilkTouchDispatchTable(p_124169_, p_124170_); }

    public static LootTable.Builder createShearsDispatchTable(Block p_124268_, LootPoolEntryContainer.Builder<?> p_124269_) { return BlockLoot.createShearsDispatchTable(p_124268_, p_124269_); }

    public static LootTable.Builder createSilkTouchOrShearsDispatchTable(Block p_124284_, LootPoolEntryContainer.Builder<?> p_124285_) { return BlockLoot.createSilkTouchOrShearsDispatchTable(p_124284_, p_124285_); }

    public static LootTable.Builder createSingleItemTableWithSilkTouch(Block p_124258_, ItemLike p_124259_) { return BlockLoot.createSingleItemTableWithSilkTouch(p_124258_, p_124259_); }

    public static LootTable.Builder createSingleItemTable(ItemLike p_176040_, NumberProvider p_176041_) { return BlockLoot.createSingleItemTable(p_176040_, p_176041_); }

    public static LootTable.Builder createSingleItemTableWithSilkTouch(Block p_176043_, ItemLike p_176044_, NumberProvider p_176045_) { return BlockLoot.createSingleItemTableWithSilkTouch(p_176043_, p_176044_, p_176045_); }

    public static LootTable.Builder createSilkTouchOnlyTable(ItemLike p_124251_) { return BlockLoot.createSilkTouchOnlyTable(p_124251_); }

    public static LootTable.Builder createPotFlowerItemTable(ItemLike p_124271_) { return BlockLoot.createPotFlowerItemTable(p_124271_); }

    public static LootTable.Builder createSlabItemTable(Block p_124291_) { return BlockLoot.createSlabItemTable(p_124291_); }

    public static LootTable.Builder createNameableBlockEntityTable(Block p_124293_) { return BlockLoot.createNameableBlockEntityTable(p_124293_); }

    public static LootTable.Builder createShulkerBoxDrop(Block p_124295_) { return BlockLoot.createShulkerBoxDrop(p_124295_); }

    public static LootTable.Builder createCopperOreDrops(Block p_176047_) { return BlockLoot.createCopperOreDrops(p_176047_); }

    public static LootTable.Builder createLapisOreDrops(Block p_176049_) { return BlockLoot.createLapisOreDrops(p_176049_); }

    public static LootTable.Builder createRedstoneOreDrops(Block p_176051_) { return BlockLoot.createRedstoneOreDrops(p_176051_); }

    public static LootTable.Builder createBannerDrop(Block p_124297_) { return BlockLoot.createBannerDrop(p_124297_); }

    public static LootTable.Builder createBeeNestDrop(Block p_124299_) { return BlockLoot.createBeeNestDrop(p_124299_); }

    public static LootTable.Builder createBeeHiveDrop(Block p_124301_) { return BlockLoot.createBeeHiveDrop(p_124301_); }

    public static LootTable.Builder createCaveVinesDrop(Block p_176053_) { return BlockLoot.createCaveVinesDrop(p_176053_); }

    public static LootTable.Builder createOreDrop(Block p_124140_, Item p_124141_) { return BlockLoot.createOreDrop(p_124140_, p_124141_); }

    public static LootTable.Builder createMushroomBlockDrop(Block p_124278_, ItemLike p_124279_) { return BlockLoot.createMushroomBlockDrop(p_124278_, p_124279_); }

    public static LootTable.Builder createGrassDrops(Block p_124303_) { return BlockLoot.createGrassDrops(p_124303_); }

    public static LootTable.Builder createStemDrops(Block p_124255_, Item p_124256_) { return BlockLoot.createStemDrops(p_124255_, p_124256_); }

    public static LootTable.Builder createAttachedStemDrops(Block p_124275_, Item p_124276_) { return BlockLoot.createAttachedStemDrops(p_124275_, p_124276_); }

    public static LootTable.Builder createShearsOnlyDrop(ItemLike p_124287_) { return BlockLoot.createShearsOnlyDrop(p_124287_); }

    public static LootTable.Builder createGlowLichenDrops(Block p_176055_) { return BlockLoot.createGlowLichenDrops(p_176055_); }

    public static LootTable.Builder createLeavesDrops(Block p_124158_, Block p_124159_, float... p_124160_) { return BlockLoot.createLeavesDrops(p_124158_, p_124159_, p_124160_); }

    public static LootTable.Builder createOakLeavesDrops(Block p_124264_, Block p_124265_, float... p_124266_) { return BlockLoot.createOakLeavesDrops(p_124264_, p_124265_, p_124266_); }

    public static LootTable.Builder createCropDrops(Block p_124143_, Item p_124144_, Item p_124145_, LootItemCondition.Builder p_124146_) { return BlockLoot.createCropDrops(p_124143_, p_124144_, p_124145_, p_124146_); }

    public static LootTable.Builder createDoublePlantShearsDrop(Block p_124305_) { return BlockLoot.createDoublePlantShearsDrop(p_124305_); }

    public static LootTable.Builder createDoublePlantWithSeedDrops(Block p_124261_, Block p_124262_) { return BlockLoot.createDoublePlantWithSeedDrops(p_124261_, p_124262_); }

    public static LootTable.Builder createCandleDrops(Block p_176057_) { return BlockLoot.createCandleDrops(p_176057_); }

    public static LootTable.Builder createCandleCakeDrops(Block p_176059_) { return BlockLoot.createCandleCakeDrops(p_176059_); }

    @Override
    public void add(Block p_124166_, LootTable.Builder p_124167_) { super.add(p_124166_, p_124167_); }

    // GENERATED END
}
