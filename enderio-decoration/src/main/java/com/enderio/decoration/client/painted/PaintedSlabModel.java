package com.enderio.decoration.client.painted;

import com.enderio.decoration.common.blockentity.DoublePaintedBlockEntity;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PaintedSlabModel extends PaintedModel implements IDynamicBakedModel {

    private final Block referenceModel;

    public PaintedSlabModel(Block referenceModel, ItemTransforms transforms) {
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
        List<BakedQuad> quads = new ArrayList<>();
        if (state != null && state.hasProperty(SlabBlock.TYPE)) {
            SlabType slabType = state.getValue(SlabBlock.TYPE);
            if (slabType == SlabType.BOTTOM || slabType == SlabType.DOUBLE) {
                Block paint = extraData.getData(DoublePaintedBlockEntity.PAINT);
                // @formatter:off
                List<BakedQuad> shape = getModel(referenceModel.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM))
                    .getQuads(state, side, rand, EmptyModelData.INSTANCE);
                // @formatter:on
                quads.addAll(getQuadsUsingShape(paint, shape, side, rand, null));
            }
            if (slabType == SlabType.TOP || slabType == SlabType.DOUBLE) {
                Block paint = extraData.getData(DoublePaintedBlockEntity.PAINT2);
                // @formatter:off
                List<BakedQuad> shape = getModel(referenceModel.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP))
                    .getQuads(state, side, rand, EmptyModelData.INSTANCE);
                // @formatter:on
                quads.addAll(getQuadsUsingShape(paint, shape, side, rand, null));
            }
        }
        return quads;
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@Nonnull IModelData data) {
        TextureAtlasSprite sprite = super.getParticleIcon(data);
        if (!sprite.getName().getPath().equals("missingno"))
            return sprite;
        Block paint = data.getData(DoublePaintedBlockEntity.PAINT2);
        if (paint != null) {
            BakedModel model = getModel(paint.defaultBlockState());
            return model.getParticleIcon(EmptyModelData.INSTANCE);
        }
        return getMissingTexture();
    }
}
