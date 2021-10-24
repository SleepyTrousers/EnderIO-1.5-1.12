package com.enderio.base.common.block;

import com.enderio.base.EnderIO;
import com.enderio.base.common.block.glass.GlassBlocks;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.base.data.model.block.BlockStateUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.NonNullLazyValue;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.model.generators.*;

import java.util.Objects;

@SuppressWarnings("unused")
public class EIOBlocks {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    // region Alloy Blocks

    public static final BlockEntry<Block> ELECTRICAL_STEEL_BLOCK = metalBlock("electrical_steel_block").register();
    public static final BlockEntry<Block> ENERGETIC_ALLOY_BLOCK = metalBlock("energetic_alloy_block").register();
    public static final BlockEntry<Block> VIBRANT_ALLOY_BLOCK = metalBlock("vibrant_alloy_block").register();
    public static final BlockEntry<Block> REDSTONE_ALLOY_BLOCK = metalBlock("redstone_alloy_block").register();
    public static final BlockEntry<Block> CONDUCTIVE_IRON_BLOCK = metalBlock("conductive_iron_block").register();
    public static final BlockEntry<Block> PULSATING_IRON_BLOCK = metalBlock("pulsating_iron_block").register();
    public static final BlockEntry<Block> DARK_STEEL_BLOCK = metalBlock("dark_steel_block").register();
    public static final BlockEntry<Block> SOULARIUM_BLOCK = metalBlock("soularium_block").register();
    public static final BlockEntry<Block> END_STEEL_BLOCK = metalBlock("end_steel_block").register();
    public static final BlockEntry<Block> CONSTRUCTION_ALLOY_BLOCK = metalBlock("construction_alloy_block").register();

    // endregion

    // region Chassis

    public static final BlockEntry<Block> SIMPLE_MACHINE_CHASSIS = chassisBlock("simple_machine_chassis").register();

    public static final BlockEntry<Block> INDUSTRIAL_MACHINE_CHASSIS = chassisBlock("industrial_machine_chassis").register();

    public static final BlockEntry<Block> END_STEEL_MACHINE_CHASSIS = chassisBlock("end_steel_machine_chassis").lang("End Steel Chassis").register();

    public static final BlockEntry<Block> SOUL_MACHINE_CHASSIS = chassisBlock("soul_machine_chassis").register();

    public static final BlockEntry<Block> ENHANCED_MACHINE_CHASSIS = chassisBlock("enhanced_machine_chassis").register();

    public static final BlockEntry<Block> SOULLESS_MACHINE_CHASSIS = chassisBlock("soulless_machine_chassis").register();

    // endregion

    // region Dark Steel Building Blocks

    // TODO: FASTER THAN REGULAR LADDERS TOOLTIP
    public static final BlockEntry<DarkSteelLadderBlock> DARK_STEEL_LADDER = REGISTRATE
        .block("dark_steel_ladder", Material.METAL, DarkSteelLadderBlock::new)
        .properties(props -> props.strength(0.4f).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion())
        .blockstate((ctx, prov) -> prov.horizontalBlock(ctx.get(), prov
            .models()
            .withExistingParent(ctx.getName(), prov.mcLoc("block/ladder"))
            .texture("particle", prov.blockTexture(ctx.get()))
            .texture("texture", prov.blockTexture(ctx.get()))))
        .addLayer(() -> RenderType::cutoutMipped)
        .tag(BlockTags.CLIMBABLE)
        .item()
        .model((ctx, prov) -> prov.generated(ctx, prov.modLoc("block/dark_steel_ladder")))
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
        .build()
        .register();

    public static final BlockEntry<IronBarsBlock> DARK_STEEL_BARS = REGISTRATE
        .block("dark_steel_bars", IronBarsBlock::new)
        .properties(props -> props.strength(5.0f, 1000.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion())
        .blockstate(BlockStateUtils::paneBlock)
        .addLayer(() -> RenderType::cutoutMipped)
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
        .model((ctx, prov) -> prov.generated(ctx, prov.modLoc("block/dark_steel_bars")))
        .build()
        .register();

    // TODO: Door drops itself in creative????
    public static final BlockEntry<DoorBlock> DARK_STEEL_DOOR = REGISTRATE
        .block("dark_steel_door", Material.METAL, DoorBlock::new)
        .properties(props -> props.strength(5.0f, 2000.0f).sound(SoundType.METAL).noOcclusion())
        .blockstate((ctx, prov) -> prov.doorBlock(ctx.get(), prov.modLoc("block/dark_steel_door_bottom"), prov.modLoc("block/dark_steel_door_top")))
        .addLayer(() -> RenderType::cutout)
        .item()
        .model((ctx, prov) -> prov.generated(ctx))
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
        .build()
        .register();

    public static final BlockEntry<TrapDoorBlock> DARK_STEEL_TRAPDOOR = REGISTRATE
        .block("dark_steel_trapdoor", Material.METAL, TrapDoorBlock::new)
        .properties(props -> props.strength(5.0f, 2000.0f).sound(SoundType.METAL).noOcclusion())
        .blockstate((ctx, prov) -> prov.trapdoorBlock(ctx.get(), prov.modLoc("block/dark_steel_trapdoor"), true))
        .addLayer(() -> RenderType::cutout)
        .item()
        .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.modLoc("block/dark_steel_trapdoor_bottom")))
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
        .build()
        .register();

    public static final BlockEntry<IronBarsBlock> END_STEEL_BARS = REGISTRATE
        .block("end_steel_bars", IronBarsBlock::new)
        .blockstate(BlockStateUtils::paneBlock)
        .addLayer(() -> RenderType::cutoutMipped)
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
        .model((ctx, prov) -> prov.generated(ctx, prov.modLoc("block/end_steel_bars")))
        .build()
        .register();

    public static final BlockEntry<ReinforcedObsidianBlock> REINFORCED_OBSIDIAN = REGISTRATE
        .block("reinforced_obsidian_block", Material.STONE, ReinforcedObsidianBlock::new)
        .properties(props -> props.sound(SoundType.STONE).strength(50, 2000).requiresCorrectToolForDrops().color(MaterialColor.COLOR_BLACK))
        .tag(BlockTags.WITHER_IMMUNE)
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
        .build()
        .register();

    // endregion

    // region Fused Quartz/Glass

    // TODO: Keep the important ones (the ones for recipes in here, maybe just keeping fused and quite clear, move the special ones to decor?)
    public static final GlassBlocks FUSED_QUARTZ = new GlassBlocks(REGISTRATE, "fused_quartz", "Fused Quartz", GlassCollisionPredicate.NONE, false, false,
        true);
    public static final GlassBlocks ENLIGHTENED_FUSED_QUARTZ = new GlassBlocks(REGISTRATE, "fused_quartz_e", "Enlightened Fused Quartz",
        GlassCollisionPredicate.NONE, true, false, true);
    public static final GlassBlocks DARK_FUSED_QUARTZ = new GlassBlocks(REGISTRATE, "fused_quartz_d", "Dark Fused Quartz", GlassCollisionPredicate.NONE, false,
        true, true);

    public static final GlassBlocks FUSED_QUARTZ_PLAYERS_PASS = new GlassBlocks(REGISTRATE, "fused_quartz_p", "Fused Quartz",
        GlassCollisionPredicate.PLAYERS_PASS, false, false, false);
    public static final GlassBlocks ENLIGHTENED_FUSED_QUARTZ_PLAYERS_PASS = new GlassBlocks(REGISTRATE, "fused_quartz_ep", "Enlightened Fused Quartz",
        GlassCollisionPredicate.PLAYERS_PASS, true, false, true);
    public static final GlassBlocks DARK_FUSED_QUARTZ_PLAYERS_PASS = new GlassBlocks(REGISTRATE, "fused_quartz_dp", "Dark Fused Quartz",
        GlassCollisionPredicate.PLAYERS_PASS, false, true, true);

    public static final GlassBlocks FUSED_QUARTZ_MOBS_PASS = new GlassBlocks(REGISTRATE, "fused_quartz_m", "Fused Quartz", GlassCollisionPredicate.MOBS_PASS,
        false, false, false);
    public static final GlassBlocks ENLIGHTENED_FUSED_QUARTZ_MOBS_PASS = new GlassBlocks(REGISTRATE, "fused_quartz_em", "Enlightened Fused Quartz",
        GlassCollisionPredicate.MOBS_PASS, true, false, true);
    public static final GlassBlocks DARK_FUSED_QUARTZ_MOBS_PASS = new GlassBlocks(REGISTRATE, "fused_quartz_dm", "Dark Fused Quartz",
        GlassCollisionPredicate.MOBS_PASS, false, true, true);

    public static final GlassBlocks FUSED_QUARTZ_ANIMAL_PASS = new GlassBlocks(REGISTRATE, "fused_quartz_a", "Fused Quartz", GlassCollisionPredicate.MOBS_PASS,
        false, false, false);
    public static final GlassBlocks ENLIGHTENED_FUSED_QUARTZ_ANIMAL_PASS = new GlassBlocks(REGISTRATE, "fused_quartz_ea", "Enlightened Fused Quartz",
        GlassCollisionPredicate.MOBS_PASS, true, false, true);
    public static final GlassBlocks DARK_FUSED_QUARTZ_ANIMAL_PASS = new GlassBlocks(REGISTRATE, "fused_quartz_da", "Dark Fused Quartz",
        GlassCollisionPredicate.MOBS_PASS, false, true, true);

    public static final GlassBlocks FUSED_QUARTZ_PLAYER_BLOCK = new GlassBlocks(REGISTRATE, "fused_quartz_np", "Fused Quartz",
        GlassCollisionPredicate.PLAYERS_BLOCK, false, false, false);
    public static final GlassBlocks ENLIGHTENED_FUSED_QUARTZ_PLAYER_BLOCK = new GlassBlocks(REGISTRATE, "fused_quartz_enp", "Enlightened Fused Quartz",
        GlassCollisionPredicate.PLAYERS_BLOCK, true, false, true);
    public static final GlassBlocks DARK_FUSED_QUARTZ_PLAYER_BLOCK = new GlassBlocks(REGISTRATE, "fused_quartz_dnp", "Dark Fused Quartz",
        GlassCollisionPredicate.PLAYERS_BLOCK, false, true, true);

    public static final GlassBlocks FUSED_QUARTZ_MONSTER_BLOCK = new GlassBlocks(REGISTRATE, "fused_quartz_nm", "Fused Quartz",
        GlassCollisionPredicate.MOBS_PASS, false, false, false);
    public static final GlassBlocks ENLIGHTENED_FUSED_QUARTZ_MONSTER_BLOCK = new GlassBlocks(REGISTRATE, "fused_quartz_enm", "Enlightened Fused Quartz",
        GlassCollisionPredicate.MOBS_PASS, true, false, true);
    public static final GlassBlocks DARK_FUSED_QUARTZ_MONSTER_BLOCK = new GlassBlocks(REGISTRATE, "fused_quartz_dnm", "Dark Fused Quartz",
        GlassCollisionPredicate.MOBS_PASS, false, true, true);

    public static final GlassBlocks FUSED_QUARTZ_ANIMAL_BLOCK = new GlassBlocks(REGISTRATE, "fused_quartz_na", "Fused Quartz",
        GlassCollisionPredicate.MOBS_PASS, false, false, false);
    public static final GlassBlocks ENLIGHTENED_FUSED_QUARTZ_ANIMAL_BLOCK = new GlassBlocks(REGISTRATE, "fused_quartz_ena", "Enlightened Fused Quartz",
        GlassCollisionPredicate.MOBS_PASS, true, false, true);
    public static final GlassBlocks DARK_FUSED_QUARTZ_ANIMAL_BLOCK = new GlassBlocks(REGISTRATE, "fused_quartz_dna", "Dark Fused Quartz",
        GlassCollisionPredicate.MOBS_PASS, false, true, true);

    public static final GlassBlocks QUITE_CLEAR_GLASS = new GlassBlocks(REGISTRATE, "clear_glass", "Quite Clear Glass", GlassCollisionPredicate.NONE, false,
        false, true);
    public static final GlassBlocks ENLIGHTENED_CLEAR_GLASS = new GlassBlocks(REGISTRATE, "enlightened_clear_glass", "Enlightened Clear Glass",
        GlassCollisionPredicate.NONE, true, false, true);
    public static final GlassBlocks DARK_CLEAR_GLASS = new GlassBlocks(REGISTRATE, "dark_clear_glass", "Dark Clear Glass", GlassCollisionPredicate.NONE, true,
        false, true);

    public static final GlassBlocks QUITE_CLEAR_GLASS_PLAYERS_PASS = new GlassBlocks(REGISTRATE, "clear_glass_p", "Quite Clear Glass",
        GlassCollisionPredicate.PLAYERS_PASS, false, false, false);
    public static final GlassBlocks ENLIGHTENED_CLEAR_GLASS_PLAYERS_PASS = new GlassBlocks(REGISTRATE, "clear_glass_ep", "Enlightened Clear Glass",
        GlassCollisionPredicate.PLAYERS_PASS, true, false, true);
    public static final GlassBlocks DARK_CLEAR_GLASS_PLAYERS_PASS = new GlassBlocks(REGISTRATE, "clear_glass_dp", "Dark Clear Glass",
        GlassCollisionPredicate.PLAYERS_PASS, true, false, true);

    public static final GlassBlocks QUITE_CLEAR_GLASS_MOBS_PASS = new GlassBlocks(REGISTRATE, "clear_glass_m", "Quite Clear Glass",
        GlassCollisionPredicate.MOBS_PASS, false, false, false);
    public static final GlassBlocks ENLIGHTENED_CLEAR_GLASS_MOBS_PASS = new GlassBlocks(REGISTRATE, "clear_glass_em", "Enlightened Clear Glass",
        GlassCollisionPredicate.MOBS_PASS, true, false, true);
    public static final GlassBlocks DARK_CLEAR_GLASS_MOBS_PASS = new GlassBlocks(REGISTRATE, "clear_glass_dm", "Dark Clear Glass",
        GlassCollisionPredicate.MOBS_PASS, true, false, true);

    public static final GlassBlocks QUITE_CLEAR_GLASS_ANIMAL_PASS = new GlassBlocks(REGISTRATE, "clear_glass_a", "Quite Clear Glass",
        GlassCollisionPredicate.ANIMALS_PASS, false, false, false);
    public static final GlassBlocks ENLIGHTENED_CLEAR_GLASS_ANIMAL_PASS = new GlassBlocks(REGISTRATE, "clear_glass_ea", "Enlightened Clear Glass",
        GlassCollisionPredicate.ANIMALS_PASS, true, false, true);
    public static final GlassBlocks DARK_CLEAR_GLASS_ANIMAL_PASS = new GlassBlocks(REGISTRATE, "clear_glass_da", "Dark Clear Glass",
        GlassCollisionPredicate.ANIMALS_PASS, true, false, true);

    public static final GlassBlocks QUITE_CLEAR_GLASS_PLAYER_BLOCK = new GlassBlocks(REGISTRATE, "clear_glass_np", "Quite Clear Glass",
        GlassCollisionPredicate.PLAYERS_BLOCK, false, false, false);
    public static final GlassBlocks ENLIGHTENED_CLEAR_GLASS_PLAYER_BLOCK = new GlassBlocks(REGISTRATE, "clear_glass_enp", "Enlightened Clear Glass",
        GlassCollisionPredicate.PLAYERS_BLOCK, true, false, true);
    public static final GlassBlocks DARK_CLEAR_GLASS_PLAYER_BLOCK = new GlassBlocks(REGISTRATE, "clear_glass_dnp", "Dark Clear Glass",
        GlassCollisionPredicate.PLAYERS_BLOCK, true, false, true);

    public static final GlassBlocks QUITE_CLEAR_GLASS_MONSTER_BLOCK = new GlassBlocks(REGISTRATE, "clear_glass_nm", "Quite Clear Glass",
        GlassCollisionPredicate.MOBS_PASS, false, false, false);
    public static final GlassBlocks ENLIGHTENED_CLEAR_GLASS_MONSTER_BLOCK = new GlassBlocks(REGISTRATE, "clear_glass_enm", "Enlightened Clear Glass",
        GlassCollisionPredicate.MOBS_PASS, true, false, true);
    public static final GlassBlocks DARK_CLEAR_GLASS_MONSTER_BLOCK = new GlassBlocks(REGISTRATE, "clear_glass_dnm", "Dark Clear Glass",
        GlassCollisionPredicate.MOBS_PASS, true, false, true);

    public static final GlassBlocks QUITE_CLEAR_GLASS_ANIMAL_BLOCK = new GlassBlocks(REGISTRATE, "clear_glass_na", "Quite Clear Glass",
        GlassCollisionPredicate.ANIMALS_BLOCK, false, false, false);
    public static final GlassBlocks ENLIGHTENED_CLEAR_GLASS_ANIMAL_BLOCK = new GlassBlocks(REGISTRATE, "clear_glass_ena", "Enlightened Clear Glass",
        GlassCollisionPredicate.ANIMALS_BLOCK, true, false, true);
    public static final GlassBlocks DARK_CLEAR_GLASS_ANIMAL_BLOCK = new GlassBlocks(REGISTRATE, "clear_glass_dna", "Dark Clear Glass",
        GlassCollisionPredicate.ANIMALS_BLOCK, true, false, true);

    // endregion

    // region Crystals

    public static final BlockEntry<AmethystBlock> INFINITY_CRYSTAL = REGISTRATE
        .block("infinity_crystal_block", Material.AMETHYST, AmethystBlock::new)
        .properties(props -> props.strength(1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops())
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
        .build()
        .register();

    public static final BlockEntry<BuddingInfinityCrystalBlock> BUDDING_INFINITY_CRYSTAL = REGISTRATE
        .block("budding_infinity_crystal", Material.AMETHYST, BuddingInfinityCrystalBlock::new)
        .properties(props -> props.strength(1.5F).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().randomTicks())
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
        .build()
        .register();

    public static final BlockEntry<AmethystClusterBlock> INFINITY_CRYSTAL_CLUSTER = REGISTRATE
        .block("infinity_crystal_cluster", Material.AMETHYST, props -> new AmethystClusterBlock(7, 3, props))
        .blockstate((ctx, prov) -> prov.directionalBlock(ctx.get(), prov.models().cross(ctx.getName(), prov.modLoc("block/" + ctx.getName()))))
        .addLayer(() -> RenderType::cutout)
        .properties(props -> props.noOcclusion().randomTicks().sound(SoundType.AMETHYST_CLUSTER).lightLevel((state) -> 5))
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
        .build()
        .register();

    public static final BlockEntry<AmethystClusterBlock> LARGE_INFINITY_BUD = REGISTRATE
        .block("large_infinity_bud", Material.AMETHYST, props -> new AmethystClusterBlock(5, 3, props))
        .blockstate((ctx, prov) -> prov.directionalBlock(ctx.get(), prov.models().cross(ctx.getName(), prov.modLoc("block/" + ctx.getName()))))
        .addLayer(() -> RenderType::cutout)
        .properties(props -> props.noOcclusion().randomTicks().sound(SoundType.AMETHYST_CLUSTER).lightLevel((state) -> 4))
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS)) // TODO: Take away...
        .build()
        .register();

    public static final BlockEntry<AmethystClusterBlock> MEDIUM_INFINITY_BUD = REGISTRATE
        .block("medium_infinity_bud", Material.AMETHYST, props -> new AmethystClusterBlock(4, 3, props))
        .blockstate((ctx, prov) -> prov.directionalBlock(ctx.get(), prov.models().cross(ctx.getName(), prov.modLoc("block/" + ctx.getName()))))
        .addLayer(() -> RenderType::cutout)
        .properties(props -> props.noOcclusion().randomTicks().sound(SoundType.AMETHYST_CLUSTER).lightLevel((state) -> 2))
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS)) // TODO: Take away...
        .build()
        .register();

    public static final BlockEntry<AmethystClusterBlock> SMALL_INFINITY_BUD = REGISTRATE
        .block("small_infinity_bud", Material.AMETHYST, props -> new AmethystClusterBlock(3, 3, props))
        .blockstate((ctx, prov) -> prov.directionalBlock(ctx.get(), prov.models().cross(ctx.getName(), prov.modLoc("block/" + ctx.getName()))))
        .addLayer(() -> RenderType::cutout)
        .properties(props -> props.noOcclusion().randomTicks().sound(SoundType.AMETHYST_CLUSTER).lightLevel((state) -> 1))
        .item()
        .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS)) // TODO: Take away...
        .build()
        .register();

    // endregion

    // region Misc

    public static final BlockEntry<GraveBlock> GRAVE = REGISTRATE
        .block("grave", Material.STONE, GraveBlock::new)
        .properties(props -> props.strength(-1.0F, 3600000.0F).noDrops().noOcclusion())
        .blockstate((con, prov) -> prov.simpleBlock(con.get(), prov.models().getExistingFile(EnderIO.loc("block/grave"))))
        .addLayer(() -> RenderType::cutout)
        .item()
        .group(() -> EIOCreativeTabs.BLOCKS)
        .build()
        .register();

    // endregion

    // region Pressure Plates

    public static final BlockEntry<EIOPressurePlateBlock> DARK_STEEL_PRESSURE_PLATE = pressurePlateBlock("dark_steel_pressure_plate",
        EnderIO.loc("block/block_dark_steel_pressure_plate"), EIOPressurePlateBlock.PLAYER, false);

    public static final BlockEntry<EIOPressurePlateBlock> SILENT_DARK_STEEL_PRESSURE_PLATE = pressurePlateBlock("silent_dark_steel_pressure_plate",
        EnderIO.loc("block/block_dark_steel_pressure_plate"), EIOPressurePlateBlock.PLAYER, true);

    public static final BlockEntry<EIOPressurePlateBlock> SOULARIUM_PRESSURE_PLATE = pressurePlateBlock("soularium_pressure_plate",
        EnderIO.loc("block/block_soularium_pressure_plate"), EIOPressurePlateBlock.HOSTILE_MOB, false);

    public static final BlockEntry<EIOPressurePlateBlock> SILENT_SOULARIUM_PRESSURE_PLATE = pressurePlateBlock("silent_soularium_pressure_plate",
        EnderIO.loc("block/block_soularium_pressure_plate"), EIOPressurePlateBlock.HOSTILE_MOB, true);

    public static final BlockEntry<SilentPressurePlateBlock> SILENT_OAK_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.OAK_PRESSURE_PLATE);

    public static final BlockEntry<SilentPressurePlateBlock> SILENT_ACACIA_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.ACACIA_PRESSURE_PLATE);

    public static final BlockEntry<SilentPressurePlateBlock> SILENT_DARK_OAK_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.DARK_OAK_PRESSURE_PLATE);

    public static final BlockEntry<SilentPressurePlateBlock> SILENT_SPRUCE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.SPRUCE_PRESSURE_PLATE);

    public static final BlockEntry<SilentPressurePlateBlock> SILENT_BIRCH_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.BIRCH_PRESSURE_PLATE);

    public static final BlockEntry<SilentPressurePlateBlock> SILENT_JUNGLE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.JUNGLE_PRESSURE_PLATE);

    public static final BlockEntry<SilentPressurePlateBlock> SILENT_CRIMSON_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.CRIMSON_PRESSURE_PLATE);

    public static final BlockEntry<SilentPressurePlateBlock> SILENT_WARPED_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.WARPED_PRESSURE_PLATE);

    public static final BlockEntry<SilentPressurePlateBlock> SILENT_STONE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.STONE_PRESSURE_PLATE);

    public static final BlockEntry<SilentPressurePlateBlock> SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);

    public static final BlockEntry<SilentWeightedPressurePlateBlock> SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE = silentWeightedPressurePlateBlock(
        (WeightedPressurePlateBlock) Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static final BlockEntry<SilentWeightedPressurePlateBlock> SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE = silentWeightedPressurePlateBlock(
        (WeightedPressurePlateBlock) Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);

    // endregion

    // region resetting levers

    public static final BlockEntry<ResettingLeverBlock> RESETTING_LEVER_FIVE = resettingLeverBlock("resetting_lever_five", 5, false);

    public static final BlockEntry<ResettingLeverBlock> RESETTING_LEVER_FIVE_INV = resettingLeverBlock("resetting_lever_five_inv", 5, true);

    public static final BlockEntry<ResettingLeverBlock> RESETTING_LEVER_TEN = resettingLeverBlock("resetting_lever_ten", 10, false);

    public static final BlockEntry<ResettingLeverBlock> RESETTING_LEVER_TEN_INV = resettingLeverBlock("resetting_lever_ten_inv", 10, true);

    public static final BlockEntry<ResettingLeverBlock> RESETTING_LEVER_THIRTY = resettingLeverBlock("resetting_lever_thirty", 30, false);

    public static final BlockEntry<ResettingLeverBlock> RESETTING_LEVER_THIRTY_INV = resettingLeverBlock("resetting_lever_thirty_inv", 30, true);

    public static final BlockEntry<ResettingLeverBlock> RESETTING_LEVER_SIXTY = resettingLeverBlock("resetting_lever_sixty", 60, false);

    public static final BlockEntry<ResettingLeverBlock> RESETTING_LEVER_SIXTY_INV = resettingLeverBlock("resetting_lever_sixty_inv", 60, true);

    public static final BlockEntry<ResettingLeverBlock> RESETTING_LEVER_THREE_HUNDRED = resettingLeverBlock("resetting_lever_three_hundred", 300, false);

    public static final BlockEntry<ResettingLeverBlock> RESETTING_LEVER_THREE_HUNDRED_INV = resettingLeverBlock("resetting_lever_three_hundred_inv", 300, true);

    // endregion

    public static <T extends Block> BlockBuilder<T, Registrate> simpleBlockBuilder(String name, T block) {
        return REGISTRATE.block(name, (p) -> block).item().group(() -> EIOCreativeTabs.BLOCKS).build();
    }

    private static BlockBuilder<Block, Registrate> metalBlock(String name) {
        return REGISTRATE
            .block(name, Material.METAL, Block::new)
            .properties(props -> props.sound(SoundType.METAL).color(MaterialColor.METAL))
            .item()
            .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
            .build();
    }

    private static BlockBuilder<Block, Registrate> chassisBlock(String name) {
        return REGISTRATE
            .block(name, Material.METAL, Block::new)
            .addLayer(() -> RenderType::cutout)
            .properties(props -> props.noOcclusion().sound(SoundType.METAL).color(MaterialColor.METAL))
            .item()
            .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
            .build();
    }

    private static BlockEntry<EIOPressurePlateBlock> pressurePlateBlock(String name, ResourceLocation texture, EIOPressurePlateBlock.Detector type,
        boolean silent) {

        BlockBuilder<EIOPressurePlateBlock, Registrate> bb = REGISTRATE.block(name, Material.METAL, (props) -> new EIOPressurePlateBlock(props, type, silent));

        bb.blockstate((ctx, prov) -> {

            BlockModelProvider modProv = prov.models();
            ModelFile dm = modProv.withExistingParent(ctx.getName() + "_down", prov.mcLoc("block/pressure_plate_down")).texture("texture", texture);
            ModelFile um = modProv.withExistingParent(ctx.getName(), prov.mcLoc("block/pressure_plate_up")).texture("texture", texture);

            VariantBlockStateBuilder vb = prov.getVariantBuilder(ctx.get());
            vb.partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(dm));
            vb.partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(um));
        });

        bb = bb.item().group(() -> EIOCreativeTabs.BLOCKS).build();
        return bb.register();
    }

    private static BlockEntry<SilentPressurePlateBlock> silentPressurePlateBlock(PressurePlateBlock block) {

        ResourceLocation upModelLoc = Objects.requireNonNull(block.getRegistryName());
        ResourceLocation downModelLoc = new ResourceLocation(upModelLoc.getNamespace(), upModelLoc.getPath() + "_down");

        BlockBuilder<SilentPressurePlateBlock, Registrate> bb = REGISTRATE.block("silent_" + upModelLoc.getPath(),
            (props) -> new SilentPressurePlateBlock(block));

        bb.blockstate((ctx, prov) -> {
            VariantBlockStateBuilder vb = prov.getVariantBuilder(ctx.get());
            vb.partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(prov.models().getExistingFile(downModelLoc)));
            vb.partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(prov.models().getExistingFile(upModelLoc)));
        });

        var itemBuilder = bb.item();
        itemBuilder.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), upModelLoc));
        itemBuilder.group(() -> EIOCreativeTabs.BLOCKS);
        bb = itemBuilder.build();

        return bb.register();
    }

    private static BlockEntry<SilentWeightedPressurePlateBlock> silentWeightedPressurePlateBlock(WeightedPressurePlateBlock block) {
        ResourceLocation upModelLoc = Objects.requireNonNull(block.getRegistryName());
        ResourceLocation downModelLoc = new ResourceLocation(upModelLoc.getNamespace(), upModelLoc.getPath() + "_down");

        BlockBuilder<SilentWeightedPressurePlateBlock, Registrate> bb = REGISTRATE.block("silent_" + upModelLoc.getPath(),
            (props) -> new SilentWeightedPressurePlateBlock(block));

        bb.blockstate((ctx, prov) -> prov.getVariantBuilder(ctx.get()).forAllStates(blockState -> {
            if (blockState.getValue(WeightedPressurePlateBlock.POWER) == 0) {
                return new ConfiguredModel[] { new ConfiguredModel(prov.models().getExistingFile(upModelLoc)) };
            }
            return new ConfiguredModel[] { new ConfiguredModel(prov.models().getExistingFile(downModelLoc)) };
        }));

        var itemBuilder = bb.item();
        itemBuilder.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), upModelLoc));
        itemBuilder.group(() -> EIOCreativeTabs.BLOCKS);
        bb = itemBuilder.build();

        return bb.register();
    }

    private static BlockEntry<ResettingLeverBlock> resettingLeverBlock(String name, int duration, boolean inverted) {

        BlockBuilder<ResettingLeverBlock, Registrate> bb = REGISTRATE.block(name, (props) -> new ResettingLeverBlock(duration, inverted));
        String durLab = "(" + (duration >= 60 ? duration / 60 : duration) + " " + (duration == 60 ? "minute" : duration > 60 ? "minutes" : "seconds") + ")";
        bb.lang("Resetting Lever " + (inverted ? "Inverted " : "") + durLab);

        bb.blockstate((ctx, prov) -> {

            BlockModelProvider modProv = prov.models();
            ModelFile.ExistingModelFile baseModel = modProv.getExistingFile(prov.mcLoc("block/lever"));
            ModelFile.ExistingModelFile onModel = modProv.getExistingFile(prov.mcLoc("block/lever_on"));

            VariantBlockStateBuilder vb = prov.getVariantBuilder(ctx.get());

            vb.forAllStates(blockState -> {
                ModelFile.ExistingModelFile model = blockState.getValue(ResettingLeverBlock.POWERED) ? onModel : baseModel;
                int rotationX =
                    blockState.getValue(LeverBlock.FACE) == AttachFace.CEILING ? 180 : blockState.getValue(LeverBlock.FACE) == AttachFace.WALL ? 90 : 0;
                Direction f = blockState.getValue(LeverBlock.FACING);
                int rotationY = f.get2DDataValue() * 90;
                if (blockState.getValue(LeverBlock.FACE) != AttachFace.CEILING) {
                    rotationY = (rotationY + 180) % 360;
                }
                return new ConfiguredModel[] { new ConfiguredModel(model, rotationX, rotationY, false) };
            });
        });

        var ib = bb.item().group(() -> EIOCreativeTabs.BLOCKS);
        ib.model((ctx, prov) -> prov.withExistingParent(ctx.getName(), prov.mcLoc("item/lever")));
        bb = ib.build();
        return bb.register();
    }

    public static void register() {}

    public static void clientInit() {

    }
}
