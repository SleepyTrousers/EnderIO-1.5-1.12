package com.enderio.base.common.block.glass;

import com.enderio.base.EnderIO;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Container helper for the fused glass/quartz blocks as theres a lot, and this will tidy stuff up.
 */
public class GlassBlocks {
    public BlockEntry<FusedQuartzBlock> CLEAR, WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK;

    private final GlassCollisionPredicate collisionPredicate;

    private final boolean emitsLight, blocksLight, explosionResistant;

    /**
     * Create the entire color family for this configuration of fused glass.
     *
     * @param registrate
     * @param name
     * @param english
     * @param collisionPredicate
     * @param emitsLight
     * @param blocksLight
     * @param explosionResistant
     */
    public GlassBlocks(Registrate registrate, String name, String english, GlassCollisionPredicate collisionPredicate, boolean emitsLight, boolean blocksLight,
        boolean explosionResistant) {
        this.collisionPredicate = collisionPredicate;
        this.emitsLight = emitsLight;
        this.blocksLight = blocksLight;
        this.explosionResistant = explosionResistant;

        CLEAR = register(registrate, name, english);
        WHITE = register(registrate, name.concat("_white"), "White ".concat(english), DyeColor.WHITE);
        ORANGE = register(registrate, name.concat("_orange"), "Orange ".concat(english), DyeColor.ORANGE);
        MAGENTA = register(registrate, name.concat("_magenta"), "Magenta ".concat(english), DyeColor.MAGENTA);
        LIGHT_BLUE = register(registrate, name.concat("_light_blue"), "Light Blue ".concat(english), DyeColor.LIGHT_BLUE);
        YELLOW = register(registrate, name.concat("_yellow"), "Yellow ".concat(english), DyeColor.YELLOW);
        LIME = register(registrate, name.concat("_lime"), "Lime ".concat(english), DyeColor.LIME);
        PINK = register(registrate, name.concat("_pink"), "Pink ".concat(english), DyeColor.PINK);
        GRAY = register(registrate, name.concat("_gray"), "Gray ".concat(english), DyeColor.GRAY);
        LIGHT_GRAY = register(registrate, name.concat("_light_gray"), "Light Gray ".concat(english), DyeColor.LIGHT_GRAY);
        CYAN = register(registrate, name.concat("_cyan"), "Cyan ".concat(english), DyeColor.CYAN);
        PURPLE = register(registrate, name.concat("_purple"), "Purple ".concat(english), DyeColor.PURPLE);
        BLUE = register(registrate, name.concat("_blue"), "Blue ".concat(english), DyeColor.BLUE);
        BROWN = register(registrate, name.concat("_brown"), "Brown ".concat(english), DyeColor.BROWN);
        GREEN = register(registrate, name.concat("_green"), "Green ".concat(english), DyeColor.GREEN);
        RED = register(registrate, name.concat("_red"), "Red ".concat(english), DyeColor.RED);
        BLACK = register(registrate, name.concat("_black"), "Black ".concat(english), DyeColor.BLACK);
    }

    private static ResourceLocation getModelFile(String name) {
        return name.contains("clear_glass") ?
            EnderIO.loc("block/clear_glass") :
            EnderIO.loc("block/fused_quartz");
    }

    // Dirty dirty. TODO: Just access transforms for these in Blocks??
    private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
        return false;
    }

    private static boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {
        return false;
    }

    /**
     * Register a non-colored glass
     *
     * @param registrate
     * @param name
     * @param english
     * @return
     */
    private BlockEntry<FusedQuartzBlock> register(Registrate registrate, String name, String english) {
        return registrate
            .block(name, props -> new FusedQuartzBlock(props, collisionPredicate, emitsLight, blocksLight, explosionResistant))
            .lang(english)
            .blockstate((con, prov) -> prov.simpleBlock(con.get(), prov.models().getExistingFile(getModelFile(name))))
            .addLayer(() -> RenderType::cutout)
            .properties(props -> props
                .noOcclusion()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(GlassBlocks::never)
                .isRedstoneConductor(GlassBlocks::never)
                .isSuffocating(GlassBlocks::never)
                .isViewBlocking(GlassBlocks::never))
            .item(FusedQuartzItem::new)
            .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
            .build()
            .register();
    }

    // MAJOR TODO: Trim the number of models down significantly; should only need two.

    /**
     * Register a colored glass.
     *
     * @param registrate
     * @param name
     * @param english
     * @param color
     * @return
     */
    private BlockEntry<FusedQuartzBlock> register(Registrate registrate, String name, String english, DyeColor color) {
        return registrate
            .block(name, props -> new FusedQuartzBlock(props, collisionPredicate, emitsLight, blocksLight, explosionResistant))
            .lang(english)
            .blockstate((con, prov) -> prov.simpleBlock(con.get(), prov.models().getExistingFile(getModelFile(name))))
            .addLayer(() -> RenderType::cutout)
            .color(new NonNullLazyValue<>(() -> () -> (p_92567_, p_92568_, p_92569_, p_92570_) -> color.getMaterialColor().col))
            .properties(props -> props
                .noOcclusion()
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(GlassBlocks::never)
                .isRedstoneConductor(GlassBlocks::never)
                .isSuffocating(GlassBlocks::never)
                .isViewBlocking(GlassBlocks::never)
                .color(color.getMaterialColor()))
            .item(FusedQuartzItem::new)
            .group(new NonNullLazyValue<>(() -> EIOCreativeTabs.BLOCKS))
            .color(new NonNullLazyValue<>(() -> () -> (ItemColor) (p_92672_, p_92673_) -> color.getMaterialColor().col))
            .build()
            .register();
    }
}
