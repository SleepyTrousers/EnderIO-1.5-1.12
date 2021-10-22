package com.enderio.decoration.client.painted;

import com.enderio.base.EnderIO;
import com.enderio.decoration.common.block.DecorBlocks;
import com.enderio.decoration.common.blockentity.IPaintableBlockEntity;
import com.enderio.decoration.common.util.PaintUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    private ClientSetup() {}

    @SubscribeEvent
    public static void modelInit(final ModelRegistryEvent e) {
        ModelLoaderRegistry.registerLoader(EnderIO.loc("painted_model"), new WrappedModelLoader(false));
        ModelLoaderRegistry.registerLoader(EnderIO.loc("painted_slab"), new WrappedModelLoader(true));
    }

    @SubscribeEvent
    public static void colorItemInit(final ColorHandlerEvent.Item e) {
        // TODO: Move into registrate.
        PaintedBlockColor color = new PaintedBlockColor();
        e.getBlockColors().register(color, DecorBlocks.getPainted().toArray(new Block[0]));
        e.getItemColors().register(color, DecorBlocks.getPainted().toArray(new Block[0]));
        e.getBlockColors().register(color, DecorBlocks.PAINTED_SLAB.get());
        e.getItemColors().register(color, DecorBlocks.PAINTED_SLAB.get());
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            for (Block paintedBlock : DecorBlocks.getPainted()) {
                ItemBlockRenderTypes.setRenderLayer(paintedBlock, RenderType.translucent());
            }
            ItemBlockRenderTypes.setRenderLayer(DecorBlocks.PAINTED_SLAB.get(), RenderType.translucent());

        });
    }

    private static class PaintedBlockColor implements BlockColor, ItemColor {

        @Override
        public int getColor(@Nonnull BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
            if (level != null && pos != null && tintIndex != 0) {
                BlockEntity entity = level.getBlockEntity(pos);
                if (entity instanceof IPaintableBlockEntity paintedBlockEntity) {
                    Block[] paints = paintedBlockEntity.getPaints();
                    for (Block paint : paints) {
                        if (paint == null)
                            continue;
                        BlockState paintState = paint.defaultBlockState();
                        int color = Minecraft.getInstance().getBlockColors().getColor(paintState, level, pos, tintIndex);
                        if (color != -1)
                            return color;
                    }
                }
            }
            return 0xFFFFFF;
        }

        @Override
        public int getColor(ItemStack itemStack, int tintIndex) {
            if (itemStack.getTag() != null && itemStack.getTag().contains("BlockEntityTag")) {
                CompoundTag blockEntityTag = itemStack.getTag().getCompound("BlockEntityTag");
                if (blockEntityTag.contains("paint")) {
                    Block paint = PaintUtils.getBlockFromRL(blockEntityTag.getString("paint"));
                    if (paint == null)
                        return 0;
                    return Minecraft.getInstance().getItemColors().getColor(paint.asItem().getDefaultInstance(), tintIndex);
                }
            }
            return 0;
        }
    }

    private static class WrappedModelLoader implements IModelLoader<Geometry> {

        private static final ItemTransforms.Deserializer INSTANCE = new ItemTransforms.Deserializer();

        private final boolean isDouble;

        WrappedModelLoader(boolean isDouble) {
            this.isDouble = isDouble;
        }

        @Nonnull
        @Override
        public Geometry read(@Nonnull JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            return new Geometry(PaintUtils.getBlockFromRL(modelContents.get("reference").getAsString()),
                getItemTransforms(deserializationContext, modelContents), isDouble);
        }

        private static ItemTransforms getItemTransforms(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            if (modelContents.has("display"))
                return new DefaultItemTransforms(INSTANCE.deserialize(modelContents.get("display"), null, deserializationContext));
            return new DefaultItemTransforms();
        }

        @Override
        public void onResourceManagerReload(@Nonnull ResourceManager pResourceManager) {}

        @Nullable
        @Override
        public IResourceType getResourceType() {
            return VanillaResourceType.MODELS;
        }
    }

    private static class Geometry implements IModelGeometry<Geometry> {

        final Block shape;
        ItemTransforms transforms;
        final boolean isDouble;

        private Geometry(Block shape, ItemTransforms itemTransforms, boolean isDouble) {
            this.shape = shape;
            this.transforms = itemTransforms;
            this.isDouble = isDouble;
        }

        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
            ItemOverrides overrides, ResourceLocation modelLocation) {
            return isDouble ? new PaintedSlabModel(shape, transforms) : new PaintedSimpleModel(shape, transforms);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter,
            Set<Pair<String, String>> missingTextureErrors) {
            return Collections.singletonList(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("minecraft", "missingno")));
        }
    }

    @SuppressWarnings("deprecation")
    private static class DefaultItemTransforms extends ItemTransforms {

        private static final ItemTransforms DEFAULT_TRANSFORMS = new ItemTransforms(
            new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 2.5f / 16, 0), new Vector3f(0.375f, 0.375f, 0.375f)),
            new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 2.5f / 16, 0), new Vector3f(0.375f, 0.375f, 0.375f)),
            new ItemTransform(new Vector3f(0, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.4f, 0.4f, 0.4f)),
            new ItemTransform(new Vector3f(0, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.4f, 0.4f, 0.4f)),
            new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)),
            new ItemTransform(new Vector3f(30, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.625f, 0.625f, 0.625f)),
            new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 3f / 16, 0), new Vector3f(0.25f, 0.25f, 0.25f)),
            new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f)));

        public DefaultItemTransforms(ItemTransforms pTransforms) {
            super(pTransforms);
        }

        public DefaultItemTransforms() {
            this(DEFAULT_TRANSFORMS);
        }

        @Nonnull
        @Override
        public ItemTransform getTransform(@Nonnull TransformType pType) {
            ItemTransform transform = super.getTransform(pType);
            if (transform == ItemTransform.NO_TRANSFORM) {
                return DEFAULT_TRANSFORMS.getTransform(pType);
            }
            return transform;
        }
    }
}
