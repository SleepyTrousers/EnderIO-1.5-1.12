package com.enderio.decoration.common.block;

import com.enderio.decoration.EIODecor;
import com.enderio.decoration.common.block.painted.*;
import com.enderio.decoration.common.item.PaintedSlabBlockItem;
import com.enderio.decoration.data.loot.LootTableUtils;
import com.enderio.decoration.data.model.block.BlockStateUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;

public class DecorBlocks {

    private static final Registrate REGISTRATE = EIODecor.registrate();

    // region Painted

    private static final List<NonNullSupplier<? extends Block>> painted = new ArrayList<>();

    public static final BlockEntry<PaintedFenceBlock> PAINTED_FENCE = paintedBlock("painted_fence", PaintedFenceBlock::new, Blocks.OAK_FENCE,
        BlockTags.WOODEN_FENCES, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedFenceGateBlock> PAINTED_FENCE_GATE = paintedBlock("painted_fence_gate", PaintedFenceGateBlock::new,
        Blocks.OAK_FENCE_GATE, BlockTags.FENCE_GATES, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedSandBlock> PAINTED_SAND = paintedBlock("painted_sand", PaintedSandBlock::new, Blocks.SAND, BlockTags.SAND,
        BlockTags.MINEABLE_WITH_SHOVEL);

    public static final BlockEntry<PaintedStairBlock> PAINTED_STAIRS = paintedBlock("painted_stairs", PaintedStairBlock::new, Blocks.OAK_STAIRS,
        BlockTags.WOODEN_STAIRS, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedCraftingTableBlock> PAINTED_CRAFTING_TABLE = paintedBlock("painted_crafting_table", PaintedCraftingTableBlock::new,
        Blocks.CRAFTING_TABLE, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedRedstoneBlock> PAINTED_REDSTONE_BLOCK = paintedBlock("painted_redstone_block", PaintedRedstoneBlock::new,
        Blocks.REDSTONE_BLOCK, BlockTags.MINEABLE_WITH_PICKAXE);

    public static final BlockEntry<PaintedTrapDoorBlock> PAINTED_TRAPDOOR = paintedBlock("painted_trapdoor", PaintedTrapDoorBlock::new, Blocks.OAK_TRAPDOOR,
        BlockTags.WOODEN_TRAPDOORS, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedWoodenPressurePlateBlock> PAINTED_WOODEN_PRESSURE_PLATE = paintedBlock("painted_wooden_pressure_plate",
        PaintedWoodenPressurePlateBlock::new, Blocks.OAK_PRESSURE_PLATE, BlockTags.WOODEN_PRESSURE_PLATES, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedSlabBlock> PAINTED_SLAB = REGISTRATE
        .block("painted_slab", PaintedSlabBlock::new)
        .blockstate((ctx, cons) -> BlockStateUtils.paintedBlock(ctx, cons, Blocks.OAK_SLAB))
        .addLayer(() -> RenderType::translucent)
        .initialProperties(() -> Blocks.OAK_SLAB)
        .loot(LootTableUtils::paintedSlab)
        .tag(BlockTags.WOODEN_SLABS, BlockTags.MINEABLE_WITH_AXE)
        .item(PaintedSlabBlockItem::new)
        .build()
        .register();

    public static final BlockEntry<SinglePaintedBlock> PAINTED_GLOWSTONE = paintedBlock("painted_glowstone", SinglePaintedBlock::new, Blocks.GLOWSTONE);

    public static List<? extends Block> getPainted() {
        return painted.stream().map(NonNullSupplier::get).toList();
    }

    public static List<NonNullSupplier<? extends Block>> getPaintedSupplier() {
        return painted;
    }

    // endregion

    @SafeVarargs
    private static <T extends Block> BlockEntry<T> paintedBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> blockFactory, Block copyFrom,
        Tag.Named<Block>... tags) {
        BlockEntry<T> paintedBlockEntry = REGISTRATE
            .block(name, blockFactory)
            .blockstate((ctx, cons) -> BlockStateUtils.paintedBlock(ctx, cons, copyFrom))
            .addLayer(() -> RenderType::translucent)
            .loot(LootTableUtils::withPaint)
            .initialProperties(() -> copyFrom)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .simpleItem()
            .tag(tags)
            .register();
        painted.add(paintedBlockEntry);
        return paintedBlockEntry;
    }

    public static void register() {}
}
