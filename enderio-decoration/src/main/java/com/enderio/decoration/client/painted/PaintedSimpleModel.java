package com.enderio.decoration.client.painted;

import com.enderio.decoration.common.blockentity.SinglePaintedBlockEntity;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class PaintedSimpleModel extends PaintedModel implements IDynamicBakedModel {

    private final Block referenceModel;

    public PaintedSimpleModel(Block referenceModel, ItemTransforms transforms) {
        super(transforms);
        this.referenceModel = referenceModel;
    }

    @Override
    protected Block copyModelFromBlock() {
        return referenceModel;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        List<BakedQuad> shape = getModelFromOwn(state).getQuads(copyBlockState(state), side, rand);
        Direction direction = null;
        if (state != null) {
            for (Property<?> property : state.getProperties()) {
                if (property instanceof DirectionProperty directionProperty) {
                    direction = state.getValue(directionProperty).getOpposite();
                }
            }
        }
        return getQuadsUsingShape(extraData.getData(SinglePaintedBlockEntity.PAINT), shape, side, rand, direction);
    }
}
