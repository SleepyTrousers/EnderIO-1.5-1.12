package com.enderio.base.common.block.glass;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.config.base.BaseConfig;
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
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable BlockGetter pLevel, @Nonnull List<Component> pTooltip, @Nonnull TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);

        if (explosionResistant)
            pTooltip.add(EIOLang.BLOCK_BLAST_RESISTANT);
        if (emitsLight)
            pTooltip.add(EIOLang.FUSED_QUARTZ_EMITS_LIGHT);
        if (blocksLight)
            pTooltip.add(EIOLang.FUSED_QUARTZ_BLOCKS_LIGHT);
        collisionPredicate.getDescription().ifPresent(pTooltip::add);
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
        return explosionResistant ? BaseConfig.COMMON.BLOCKS.EXPLOSION_RESISTANCE.get() : super.getExplosionResistance();
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
