package com.enderio.base.common.block.glass;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FusedQuartzBlock extends AbstractGlassBlock {
    // TODO: Connected textures

    private final GlassCollisionPredicate collisionPredicate;
    private final boolean emitsLight;
    private final boolean blocksLight;
    private final boolean explosionResistant;

    public FusedQuartzBlock(Properties pProps, GlassCollisionPredicate collisionPredicate, boolean emitsLight, boolean blocksLight,
        boolean explosionResistant) {
        super(pProps);
        this.collisionPredicate = collisionPredicate;
        this.emitsLight = emitsLight;
        this.blocksLight = blocksLight;
        this.explosionResistant = explosionResistant;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, @Nonnull List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);

        // TODO: Translations
        if (explosionResistant)
            pTooltip.add(new TextComponent("Blast resistant"));
        if (emitsLight)
            pTooltip.add(new TextComponent("Emits light"));
        if (blocksLight)
            pTooltip.add(new TextComponent("Blocks light"));
        collisionPredicate.getDescription().ifPresent(desc -> pTooltip.add(new TextComponent(desc)));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return emitsLight ? 15 : 0;
    }

    @Override
    public int getLightBlock(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return blocksLight ? 255 : 0;
    }

    @Override
    public float getExplosionResistance() {
        return explosionResistant ? 1200.0f : super.getExplosionResistance(); // TODO: Config for the level of explosion resistance
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        if (pContext instanceof EntityCollisionContext entityCollisionContext) {
            if (collisionPredicate.canPass(entityCollisionContext)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }
}
