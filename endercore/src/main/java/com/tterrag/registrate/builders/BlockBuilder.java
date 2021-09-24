package com.tterrag.registrate.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.TileEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider.LootType;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.generators.BlockStateProvider.ConfiguredModelList;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.RegistryObject;

/**
 * A builder for blocks, allows for customization of the {@link Block.Properties}, creation of block items, and configuration of data associated with blocks (loot tables, recipes, etc.).
 * 
 * @param <T>
 *            The type of block being built
 * @param <P>
 *            Parent object type
 */
public class BlockBuilder<T extends Block, P> extends AbstractBuilder<Block, T, P, BlockBuilder<T, P>> {

    /**
     * Create a new {@link BlockBuilder} and configure data. Used in lieu of adding side-effects to constructor, so that alternate initialization strategies can be done in subclasses.
     * <p>
     * The block will be assigned the following data:
     * <ul>
     * <li>A default blockstate file mapping all states to one model (via {@link #defaultBlockstate()})</li>
     * <li>A simple cube_all model (used in the blockstate) with one texture (via {@link #defaultBlockstate()})</li>
     * <li>A self-dropping loot table (via {@link #defaultLoot()})</li>
     * <li>The default translation (via {@link #defaultLang()})</li>
     * </ul>
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
     *            Factory to create the block
     * @param material
     *            The {@link Material} to use for the initial {@link Block.Properties} object
     * @return A new {@link BlockBuilder} with reasonable default data generators.
     */
    public static <T extends Block, P> BlockBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<BlockBehaviour.Properties, T> factory, Material material) {
        return new BlockBuilder<>(owner, parent, name, callback, factory, () -> BlockBehaviour.Properties.of(material))
                .defaultBlockstate().defaultLoot().defaultLang();
    }

    private final NonNullFunction<BlockBehaviour.Properties, T> factory;
    
    private NonNullSupplier<BlockBehaviour.Properties> initialProperties;
    private NonNullFunction<BlockBehaviour.Properties, BlockBehaviour.Properties> propertiesCallback = NonNullUnaryOperator.identity();
    private List<Supplier<Supplier<RenderType>>> renderLayers = new ArrayList<>(1);
    
    @Nullable
    private NonNullSupplier<Supplier<BlockColor>> colorHandler;

    protected BlockBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<BlockBehaviour.Properties, T> factory, NonNullSupplier<BlockBehaviour.Properties> initialProperties) {
        super(owner, parent, name, callback, Block.class);
        this.factory = factory;
        this.initialProperties = initialProperties;
    }

    /**
     * Modify the properties of the block. Modifications are done lazily, but the passed function is composed with the current one, and as such this method can be called multiple times to perform
     * different operations.
     * <p>
     * If a different properties instance is returned, it will replace the existing one entirely.
     * 
     * @param func
     *            The action to perform on the properties
     * @return this {@link BlockBuilder}
     */
    public BlockBuilder<T, P> properties(NonNullUnaryOperator<BlockBehaviour.Properties> func) {
        propertiesCallback = propertiesCallback.andThen(func);
        return this;
    }

    /**
     * Replace the initial state of the block properties, without replacing or removing any modifications done via {@link #properties(NonNullUnaryOperator)}.
     *
     * @param material
     *            The material of the initial properties
     * @return this {@link BlockBuilder}
     */
    public BlockBuilder<T, P> initialProperties(Material material) {
        initialProperties = () -> BlockBehaviour.Properties.of(material);
        return this;
    }

    /**
     * Replace the initial state of the block properties, without replacing or removing any modifications done via {@link #properties(NonNullUnaryOperator)}.
     * 
     * @param material
     *            The material of the initial properties
     * @param color
     *            The color of the intial properties
     * @return this {@link BlockBuilder}
     */
    public BlockBuilder<T, P> initialProperties(Material material, DyeColor color) {
        initialProperties = () -> BlockBehaviour.Properties.of(material, color);
        return this;
    }

    /**
     * Replace the initial state of the block properties, without replacing or removing any modifications done via {@link #properties(NonNullUnaryOperator)}.
     * 
     * @param material
     *            The material of the initial properties
     * @param color
     *            The color of the intial properties
     * @return this {@link BlockBuilder}
     */
    public BlockBuilder<T, P> initialProperties(Material material, MaterialColor color) {
        initialProperties = () -> BlockBehaviour.Properties.of(material, color);
        return this;
    }

    /**
     * Replace the initial state of the block properties, without replacing or removing any modifications done via {@link #properties(NonNullUnaryOperator)}.
     * 
     * @param block
     *            The block to create the initial properties from (via {@link Block.Properties#from(BlockBehaviour)})
     * @return this {@link BlockBuilder}
     */
    public BlockBuilder<T, P> initialProperties(NonNullSupplier<? extends Block> block) {
        initialProperties = () -> BlockBehaviour.Properties.copy(block.get());
        return this;
    }

    public BlockBuilder<T, P> addLayer(Supplier<Supplier<RenderType>> layer) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            Preconditions.checkArgument(RenderType.chunkBufferLayers().contains(layer.get().get()), "Invalid block layer: " + layer);
        });
        if (this.renderLayers.isEmpty()) {
            onRegister(this::registerLayers);
        }
        this.renderLayers.add(layer);
        return this;
    }

    protected void registerLayers(T entry) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            if (renderLayers.size() == 1) {
                final RenderType layer = renderLayers.get(0).get().get();
                ItemBlockRenderTypes.setRenderLayer(entry, layer);
            } else if (renderLayers.size() > 1) {
                final Set<RenderType> layers = renderLayers.stream()
                        .map(s -> s.get().get())
                        .collect(Collectors.toSet());
                ItemBlockRenderTypes.setRenderLayer(entry, layers::contains);
            }
        });
    }

    /**
     * Create a standard {@link BlockItem} for this block, building it immediately, and not allowing for further configuration.
     * <p>
     * The item will have no lang entry (since it would duplicate the block's) and a simple block item model (via {@link RegistrateItemModelProvider#blockItem(NonNullSupplier)}).
     *
     * @return this {@link BlockBuilder}
     * @see #item()
     */
    public BlockBuilder<T, P> simpleItem() {
        return item().build();
    }

    /**
     * Create a standard {@link BlockItem} for this block, and return the builder for it so that further customization can be done.
     * <p>
     * The item will have no lang entry (since it would duplicate the block's) and a simple block item model (via {@link RegistrateItemModelProvider#blockItem(NonNullSupplier)}).
     * 
     * @return the {@link ItemBuilder} for the {@link BlockItem}
     */
    public ItemBuilder<BlockItem, BlockBuilder<T, P>> item() {
        return item(BlockItem::new);
    }

    /**
     * Create a {@link BlockItem} for this block, which is created by the given factory, and return the builder for it so that further customization can be done.
     * <p>
     * By default, the item will have no lang entry (since it would duplicate the block's) and a simple block item model (via {@link RegistrateItemModelProvider#blockItem(NonNullSupplier)}).
     * 
     * @param <I>
     *            The type of the item
     * @param factory
     *            A factory for the item, which accepts the block object and properties and returns a new item
     * @return the {@link ItemBuilder} for the {@link BlockItem}
     */
    public <I extends BlockItem> ItemBuilder<I, BlockBuilder<T, P>> item(NonNullBiFunction<? super T, Item.Properties, ? extends I> factory) {
        return getOwner().<I, BlockBuilder<T, P>> item(this, getName(), p -> factory.apply(getEntry(), p))
                .setData(ProviderType.LANG, NonNullBiConsumer.noop()) // FIXME Need a beetter API for "unsetting" providers
                .model((ctx, prov) -> {
                    Optional<String> model = getOwner().getDataProvider(ProviderType.BLOCKSTATE)
                            .flatMap(p -> p.getExistingVariantBuilder(getEntry()))
                            .map(b -> b.getModels().get(b.partialState()))
                            .map(ConfiguredModelList::toJSON)
                            .filter(JsonElement::isJsonObject)
                            .map(j -> j.getAsJsonObject().get("model"))
                            .map(JsonElement::getAsString);
                    if (model.isPresent()) {
                        prov.withExistingParent(ctx.getName(), model.get());
                    } else {
                        prov.blockItem(asSupplier());
                    }
                });
    }

    /**
     * Create a {@link BlockEntity} for this block, which is created by the given factory, and assigned this block as its one and only valid block.
     * 
     * @param <BE>
     *            The type of the tile entity
     * @param factory
     *            A factory for the tile entity
     * @return this {@link BlockBuilder}
     */
    public <BE extends BlockEntity> BlockBuilder<T, P> simpleTileEntity(BlockEntityFactory<BE> factory) {
        return tileEntity(factory).build();
    }

    /**
     * Create a {@link BlockEntity} for this block, which is created by the given factory, and assigned this block as its one and only valid block.
     * <p>
     * The created {@link TileEntityBuilder} is returned for further configuration.
     * 
     * @param <BE>
     *            The type of the tile entity
     * @param factory
     *            A factory for the tile entity
     * @return the {@link TileEntityBuilder}
     */
    public <BE extends BlockEntity> TileEntityBuilder<BE, BlockBuilder<T, P>> tileEntity(BlockEntityFactory<BE> factory) {
        return getOwner().<BE, BlockBuilder<T, P>> tileEntity(this, getName(), factory).validBlock(asSupplier());
    }
    
    /**
     * Register a block color handler for this block. The {@link BlockColor} instance can be shared across many blocks.
     * 
     * @param colorHandler
     *            The color handler to register for this block
     * @return this {@link BlockBuilder}
     */
    // TODO it might be worthwhile to abstract this more and add the capability to automatically copy to the item
    public BlockBuilder<T, P> color(NonNullSupplier<Supplier<BlockColor>> colorHandler) {
        if (this.colorHandler == null) {
            DistExecutor.runWhenOn(Dist.CLIENT, () -> this::registerBlockColor);
        }
        this.colorHandler = colorHandler;
        return this;
    }
    
    protected void registerBlockColor() {
        OneTimeEventReceiver.addModListener(ColorHandlerEvent.Block.class, e -> {
            NonNullSupplier<Supplier<BlockColor>> colorHandler = this.colorHandler;
            if (colorHandler != null) {
                e.getBlockColors().register(colorHandler.get().get(), getEntry());
            }
        });
    }

    /**
     * Assign the default blockstate, which maps all states to a single model file (via {@link RegistrateBlockstateProvider#simpleBlock(Block)}). This is the default, so it is generally not necessary
     * to call, unless for undoing previous changes.
     * 
     * @return this {@link BlockBuilder}
     */
    public BlockBuilder<T, P> defaultBlockstate() {
        return blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry()));
    }

    /**
     * Configure the blockstate/models for this block.
     * 
     * @param cons
     *            The callback which will be invoked during data generation.
     * @return this {@link BlockBuilder}
     * @see #setData(ProviderType, NonNullBiConsumer)
     */
    public BlockBuilder<T, P> blockstate(NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> cons) {
        return setData(ProviderType.BLOCKSTATE, cons);
    }

    /**
     * Assign the default translation, as specified by {@link RegistrateLangProvider#getAutomaticName(NonNullSupplier)}. This is the default, so it is generally not necessary to call, unless for undoing
     * previous changes.
     * 
     * @return this {@link BlockBuilder}
     */
    public BlockBuilder<T, P> defaultLang() {
        return lang(Block::getDescriptionId);
    }

    /**
     * Set the translation for this block.
     * 
     * @param name
     *            A localized English name
     * @return this {@link BlockBuilder}
     */
    public BlockBuilder<T, P> lang(String name) {
        return lang(Block::getDescriptionId, name);
    }

    /**
     * Assign the default loot table, as specified by {@link RegistrateBlockLootTables#registerDropSelfLootTable(Block)}. This is the default, so it is generally not necessary to call, unless for
     * undoing previous changes.
     * 
     * @return this {@link BlockBuilder}
     */
    public BlockBuilder<T, P> defaultLoot() {
        return loot(RegistrateBlockLootTables::dropSelf);
    }

    /**
     * Configure the loot table for this block. This is different than most data gen callbacks as the callback does not accept a {@link DataGenContext}, but instead a
     * {@link RegistrateBlockLootTables}, for creating specifically block loot tables.
     * <p>
     * If the block does not have a loot table (i.e. {@link Block.Properties#noDrops()} is called) this action will be <em>skipped</em>.
     * 
     * @param cons
     *            The callback which will be invoked during block loot table creation.
     * @return this {@link BlockBuilder}
     */
    public BlockBuilder<T, P> loot(NonNullBiConsumer<RegistrateBlockLootTables, T> cons) {
        return setData(ProviderType.LOOT, (ctx, prov) -> prov.addLootAction(LootType.BLOCK, tb -> {
            if (!ctx.getEntry().getLootTable().equals(BuiltInLootTables.EMPTY)) {
                cons.accept(tb, ctx.getEntry());
            }
        }));
    }

    /**
     * Configure the recipe(s) for this block.
     * 
     * @param cons
     *            The callback which will be invoked during data generation.
     * @return this {@link BlockBuilder}
     * @see #setData(ProviderType, NonNullBiConsumer)
     */
    public BlockBuilder<T, P> recipe(NonNullBiConsumer<DataGenContext<Block, T>, RegistrateRecipeProvider> cons) {
        return setData(ProviderType.RECIPE, cons);
    }

    /**
     * Assign {@link Tag.Named}{@code s} to this block. Multiple calls will add additional tags.
     * 
     * @param tags
     *            The tags to assign
     * @return this {@link BlockBuilder}
     */
    @SafeVarargs
    public final BlockBuilder<T, P> tag(Tag.Named<Block>... tags) {
        return tag(ProviderType.BLOCK_TAGS, tags);
    }

    @Override
    protected T createEntry() {
        @Nonnull BlockBehaviour.Properties properties = this.initialProperties.get();
        properties = propertiesCallback.apply(properties);
        return factory.apply(properties);
    }
    
    @Override
    protected RegistryEntry<T> createEntryWrapper(RegistryObject<T> delegate) {
        return new BlockEntry<>(getOwner(), delegate);
    }
    
    @Override
    public BlockEntry<T> register() {
        return (BlockEntry<T>) super.register();
    }
}
