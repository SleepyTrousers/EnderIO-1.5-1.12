package com.tterrag.registrate.builders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.entry.TileEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmllegacy.RegistryObject;

/**
 * A builder for tile entities, allows for customization of the valid blocks.
 * 
 * @param <T>
 *            The type of tile entity being built
 * @param <P>
 *            Parent object type
 */
public class TileEntityBuilder<T extends BlockEntity, P> extends AbstractBuilder<BlockEntityType<?>, BlockEntityType<T>, P, TileEntityBuilder<T, P>> {

    public interface BlockEntityFactory<T extends BlockEntity> {

        public T create(BlockPos pos, BlockState state, BlockEntityType<T> type);

    }

    /**
     * Create a new {@link TileEntityBuilder} and configure data. Used in lieu of adding side-effects to constructor, so that alternate initialization strategies can be done in subclasses.
     * <p>
     * The tile entity will be assigned the following data:
     * 
     * @param <T>
     *            The type of the builder
     * @param <P>
     *            Parent object type
     * @param owner
     *            The owning {@link AbstractRegistrate} object
     * @param parent
     *            The parent object
     * @param name
     *            Name of the entry being built
     * @param callback
     *            A callback used to actually register the built entry
     * @param factory
     *            Factory to create the tile entity
     * @return A new {@link TileEntityBuilder} with reasonable default data generators.
     */
    public static <T extends BlockEntity, P> TileEntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        return new TileEntityBuilder<>(owner, parent, name, callback, factory);
    }

    private final BlockEntityFactory<T> factory;
    private final Set<NonNullSupplier<? extends Block>> validBlocks = new HashSet<>();
    @Nullable
    private NonNullSupplier<NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<? super T>>> renderer;

    protected TileEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        super(owner, parent, name, callback, BlockEntityType.class);
        this.factory = factory;
    }
    
    /**
     * Add a valid block for this tile entity.
     * 
     * @param block
     *            A supplier for the block to add at registration time
     * @return this {@link TileEntityBuilder}
     */
    public TileEntityBuilder<T, P> validBlock(NonNullSupplier<? extends Block> block) {
        validBlocks.add(block);
        return this;
    }
    
    /**
     * Add valid blocks for this tile entity.
     * 
     * @param blocks
     *            An array of suppliers for the block to add at registration time
     * @return this {@link TileEntityBuilder}
     */
    @SafeVarargs
    public final TileEntityBuilder<T, P> validBlocks(NonNullSupplier<? extends Block>... blocks) {
        Arrays.stream(blocks).forEach(this::validBlock);
        return this;
    }
    
    /**
     * Register an {@link TileEntityRenderer} for this tile entity.
     * <p>
     * 
     * @apiNote This requires the {@link Class} of the tile entity object, which can only be gotten by inspecting an instance of it. Thus, the entity will be constructed to register the renderer.
     * 
     * @param renderer
     *            A (server safe) supplier to an {@link Function} that will provide this tile entity's renderer given the renderer dispatcher
     * @return this {@link TileEntityBuilder}
     */
    public TileEntityBuilder<T, P> renderer(NonNullSupplier<NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<? super T>>> renderer) {
        if (this.renderer == null) { // First call only
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::registerRenderer);
        }
        this.renderer = renderer;
        return this;
    }
    
    protected void registerRenderer() {
        OneTimeEventReceiver.addModListener(FMLClientSetupEvent.class, $ -> {
            var renderer = this.renderer;
            if (renderer != null) {
                BlockEntityRenderers.register(getEntry(), renderer.get()::apply);
            }
        });
    }

    @Override
    protected BlockEntityType<T> createEntry() {
        BlockEntityFactory<T> factory = this.factory;
        Supplier<BlockEntityType<T>> supplier = asSupplier();
        return BlockEntityType.Builder.<T>of((pos, state) -> factory.create(pos, state, (BlockEntityType<T>) supplier.get()), validBlocks.stream().map(NonNullSupplier::get).toArray(Block[]::new))
                .build(null);
    }
    
    @Override
    protected RegistryEntry<BlockEntityType<T>> createEntryWrapper(RegistryObject<BlockEntityType<T>> delegate) {
        return new TileEntityEntry<>(getOwner(), delegate);
    }
    
    @Override
    public TileEntityEntry<T> register() {
        return (TileEntityEntry<T>) super.register();
    }
}
