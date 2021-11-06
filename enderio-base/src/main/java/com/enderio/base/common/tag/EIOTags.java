package com.enderio.base.common.tag;

import com.enderio.base.EnderIO;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class EIOTags {

    public static void init() {
        Items.init();
        Blocks.init();
    }

    public static class Items {

        private static void init() {}
    
        public static final IOptionalNamedTag<Item> WRENCH = ItemTags.createOptional(new ResourceLocation("forge", "tools/wrench"));

        public static final IOptionalNamedTag<Item> DUSTS_LAPIS = ItemTags.createOptional(new ResourceLocation("forge", "dusts/lapis"));
        public static final IOptionalNamedTag<Item> DUSTS_COAL = ItemTags.createOptional(new ResourceLocation("forge", "dusts/coal"));
        public static final IOptionalNamedTag<Item> DUSTS_IRON = ItemTags.createOptional(new ResourceLocation("forge", "dusts/iron"));
        public static final IOptionalNamedTag<Item> DUSTS_GOLD = ItemTags.createOptional(new ResourceLocation("forge", "dusts/gold"));
        public static final IOptionalNamedTag<Item> DUSTS_COPPER = ItemTags.createOptional(new ResourceLocation("forge", "dusts/copper"));
        public static final IOptionalNamedTag<Item> DUSTS_TIN = ItemTags.createOptional(new ResourceLocation("forge", "dusts/tin"));
        public static final IOptionalNamedTag<Item> DUSTS_ENDER = ItemTags.createOptional(new ResourceLocation("forge", "dusts/ender"));
        public static final IOptionalNamedTag<Item> DUSTS_OBSIDIAN = ItemTags.createOptional(new ResourceLocation("forge", "dusts/obsidian"));
        public static final IOptionalNamedTag<Item> DUSTS_ARDITE = ItemTags.createOptional(new ResourceLocation("forge", "dusts/ardite"));
        public static final IOptionalNamedTag<Item> DUSTS_COBALT = ItemTags.createOptional(new ResourceLocation("forge", "dusts/cobalt"));
        public static final IOptionalNamedTag<Item> DUSTS_QUARTZ = ItemTags.createOptional(new ResourceLocation("forge", "dusts/quartz"));
        public static final IOptionalNamedTag<Item> SILICON = ItemTags.createOptional(new ResourceLocation("forge", "silicon"));
        public static final IOptionalNamedTag<Item> GEARS = ItemTags.createOptional(new ResourceLocation("forge", "gears"));
        public static final IOptionalNamedTag<Item> GEARS_WOOD = ItemTags.createOptional(new ResourceLocation("forge", "gears/wood"));
        public static final IOptionalNamedTag<Item> GEARS_STONE = ItemTags.createOptional(new ResourceLocation("forge", "gears/stone"));
        public static final IOptionalNamedTag<Item> GEARS_IRON = ItemTags.createOptional(new ResourceLocation("forge", "gears/stone"));
        public static final IOptionalNamedTag<Item> GEARS_ENERGIZED = ItemTags.createOptional(new ResourceLocation("forge", "gears/energized"));
        public static final IOptionalNamedTag<Item> GEARS_VIBRANT = ItemTags.createOptional(new ResourceLocation("forge", "gears/vibrant"));
        public static final IOptionalNamedTag<Item> GEARS_DARK_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "gears/dark_steel"));

        public static final IOptionalNamedTag<Item> FUSED_QUARTZ = ItemTags.createOptional(new ResourceLocation(EnderIO.MODID, "fused_quartz"));
        public static final IOptionalNamedTag<Item> CLEAR_GLASS = ItemTags.createOptional(new ResourceLocation(EnderIO.MODID, "clear_glass"));

    }

    public static class Blocks {

        private static void init() {}

        public static final IOptionalNamedTag<Block> FUSED_QUARTZ = BlockTags.createOptional(new ResourceLocation(EnderIO.MODID, "fused_quartz"));
        public static final IOptionalNamedTag<Block> CLEAR_GLASS = BlockTags.createOptional(new ResourceLocation(EnderIO.MODID, "clear_glass"));

    }
}
