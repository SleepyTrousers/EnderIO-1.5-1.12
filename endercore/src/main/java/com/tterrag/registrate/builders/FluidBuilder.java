package com.tterrag.registrate.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.NonNullLazyValue;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fmllegacy.RegistryObject;

/**
 * A builder for fluids, allows for customization of the {@link ForgeFlowingFluid.Properties} and {@link FluidAttributes}, and creation of the source variant, fluid block, and bucket item, as well as
 * data associated with fluids (tags, etc.).
 * 
 * @param <T>
 *            The type of fluid being built
 * @param <P>
 *            Parent object type
 */
public class FluidBuilder<T extends ForgeFlowingFluid, P> extends AbstractBuilder<Fluid, T, P, FluidBuilder<T, P>> {
    
    private static class Builder extends FluidAttributes.Builder {
        
        protected Builder(ResourceLocation still, ResourceLocation flowing, BiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory) {
            super(still, flowing, attributesFactory);
        }
    }

    /**
     * Create a new {@link FluidBuilder} and configure data. The created builder will use the default attributes class ({@link FluidAttributes}) and fluid class ({@link ForgeFlowingFluid.Flowing}).
     * 
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
     * @param stillTexture
     *            The texture to use for still fluids
     * @param flowingTexture
     *            The texture to use for flowing fluids
     * @return A new {@link FluidBuilder} with reasonable default data generators.
     * @see #create(AbstractRegistrate, Object, String, BuilderCallback, ResourceLocation, ResourceLocation, NonNullBiFunction, NonNullFunction)
     */
    public static <P> FluidBuilder<ForgeFlowingFluid.Flowing, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return create(owner, parent, name, callback, stillTexture, flowingTexture, (NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes>) null);
    }
    
    /**
     * Create a new {@link FluidBuilder} and configure data. The created builder will use the default fluid class ({@link ForgeFlowingFluid.Flowing}).
     * 
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
     * @param stillTexture
     *            The texture to use for still fluids
     * @param flowingTexture
     *            The texture to use for flowing fluids
     * @param attributesFactory
     *            A factory that creates the fluid attributes instance
     * @return A new {@link FluidBuilder} with reasonable default data generators.
     * @see #create(AbstractRegistrate, Object, String, BuilderCallback, ResourceLocation, ResourceLocation, NonNullBiFunction, NonNullFunction)
     */
    public static <P> FluidBuilder<ForgeFlowingFluid.Flowing, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ResourceLocation stillTexture, ResourceLocation flowingTexture,
            @Nullable NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory) {
        return create(owner, parent, name, callback, stillTexture, flowingTexture, attributesFactory, ForgeFlowingFluid.Flowing::new);
    }
    
    /**
     * Create a new {@link FluidBuilder} and configure data. The created builder will use the default attributes class ({@link FluidAttributes}).
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
     * @param stillTexture
     *            The texture to use for still fluids
     * @param flowingTexture
     *            The texture to use for flowing fluids
     * @param factory
     *            A factory that creates the flowing fluid
     * @return A new {@link FluidBuilder} with reasonable default data generators.
     * @see #create(AbstractRegistrate, Object, String, BuilderCallback, ResourceLocation, ResourceLocation, NonNullBiFunction, NonNullFunction)
     */
    public static <T extends ForgeFlowingFluid, P> FluidBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ResourceLocation stillTexture, ResourceLocation flowingTexture,
            NonNullFunction<ForgeFlowingFluid.Properties, T> factory) {
        return create(owner, parent, name, callback, stillTexture, flowingTexture, null, factory);
    }
    
    /**
     * Create a new {@link FluidBuilder} and configure data. Used in lieu of adding side-effects to constructor, so that alternate initialization strategies can be done in subclasses.
     * <p>
     * The fluid will be assigned the following data:
     * <ul>
     * <li>The default translation (via {@link #defaultLang()})</li>
     * <li>A default {@link ForgeFlowingFluid.Source source fluid} (via {@link #defaultSource})</li>
     * <li>A default block for the fluid, with its own default blockstate and model that configure the particle texture (via {@link #defaultBlock()})</li>
     * <li>A default bucket item, that uses a simple generated item model with a texture of the same name as this fluid (via {@link #defaultBucket()})</li>
     * <li>Tagged with {@link FluidTags#WATER}</li>
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
     * @param stillTexture
     *            The texture to use for still fluids
     * @param flowingTexture
     *            The texture to use for flowing fluids
     * @param attributesFactory
     *            A factory that creates the fluid attributes instance
     * @param factory
     *            A factory that creates the flowing fluid
     * @return A new {@link FluidBuilder} with reasonable default data generators.
     */
    public static <T extends ForgeFlowingFluid, P> FluidBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ResourceLocation stillTexture, ResourceLocation flowingTexture,
            @Nullable NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory, NonNullFunction<ForgeFlowingFluid.Properties, T> factory) {
        FluidBuilder<T, P> ret = new FluidBuilder<>(owner, parent, name, callback, stillTexture, flowingTexture, attributesFactory, factory)
                .defaultLang().defaultSource().defaultBlock().defaultBucket()
                .tag(FluidTags.WATER);

        return ret;
    }

    private final ResourceLocation stillTexture;
    private final String sourceName;
    private final String bucketName;
    private final NonNullSupplier<FluidAttributes.Builder> attributes;
    private final NonNullFunction<ForgeFlowingFluid.Properties, T> factory;

    @Nullable
    private Boolean defaultSource, defaultBlock, defaultBucket;

    private NonNullConsumer<FluidAttributes.Builder> attributesCallback = $ -> {};
    private NonNullConsumer<ForgeFlowingFluid.Properties> properties;
    @Nullable
    private NonNullLazyValue<? extends ForgeFlowingFluid> source;
    private List<Named<Fluid>> tags = new ArrayList<>();

    protected FluidBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, ResourceLocation stillTexture, ResourceLocation flowingTexture,
            @Nullable BiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> attributesFactory, NonNullFunction<ForgeFlowingFluid.Properties, T> factory) {
        super(owner, parent, "flowing_" + name, callback, Fluid.class);
        this.stillTexture = stillTexture;
        this.sourceName = name;
        this.bucketName = name + "_bucket";
        this.attributes = () -> attributesFactory == null ? FluidAttributes.builder(stillTexture, flowingTexture) : new Builder(stillTexture, flowingTexture, attributesFactory);
        this.factory = factory;
        
        String bucketName = this.bucketName;
        this.properties = p -> p.bucket(() -> owner.get(bucketName, Item.class).get())
                .block(() -> owner.<Block, LiquidBlock>get(name, Block.class).get());
    }
    
    /**
     * Modify the attributes of the fluid. Modifications are done lazily, but the passed function is composed with the current one, and as such this method can be called multiple times to perform
     * different operations.
     * 
     * @param cons
     *            The action to perform on the attributes
     * @return this {@link FluidBuilder}
     */
    public FluidBuilder<T, P> attributes(NonNullConsumer<FluidAttributes.Builder> cons) {
        attributesCallback = attributesCallback.andThen(cons);
        return this;
    }
    
    /**
     * Modify the properties of the fluid. Modifications are done lazily, but the passed function is composed with the current one, and as such this method can be called multiple times to perform
     * different operations.
     *
     * @param cons
     *            The action to perform on the properties
     * @return this {@link FluidBuilder}
     */
    public FluidBuilder<T, P> properties(NonNullConsumer<ForgeFlowingFluid.Properties> cons) {
        properties = properties.andThen(cons);
        return this;
    }

    /**
     * Assign the default translation, as specified by {@link RegistrateLangProvider#getAutomaticName(NonNullSupplier)}. This is the default, so it is generally not necessary to call, unless for
     * undoing previous changes.
     * 
     * @return this {@link FluidBuilder}
     */
    public FluidBuilder<T, P> defaultLang() {
        return lang(f -> f.getAttributes().getTranslationKey(), RegistrateLangProvider.toEnglishName(sourceName));
    }

    /**
     * Set the translation for this fluid.
     * 
     * @param name
     *            A localized English name
     * @return this {@link FluidBuilder}
     */
    public FluidBuilder<T, P> lang(String name) {
        return lang(f -> f.getAttributes().getTranslationKey(), name);
    }

    /**
     * Create a standard {@link ForgeFlowingFluid.Source} for this fluid which will be built and registered along with this fluid.
     * 
     * @return this {@link FluidBuilder}
     * @see #source(NonNullFunction)
     * @throws IllegalStateException
     *             If {@link #source(NonNullFunction)} has been called before this method
     */
    public FluidBuilder<T, P> defaultSource() {
        if (this.defaultSource != null) {
            throw new IllegalStateException("Cannot set a default source after a custom source has been created");
        }
        this.defaultSource = true;
        return this;
    }

    /**
     * Create a {@link ForgeFlowingFluid} for this fluid, which is created by the given factory, and which will be built and registered along with this fluid.
     * 
     * @param factory
     *            A factory for the fluid, which accepts the properties and returns a new fluid
     * @return this {@link FluidBuilder}
     */
    public FluidBuilder<T, P> source(NonNullFunction<ForgeFlowingFluid.Properties, ? extends ForgeFlowingFluid> factory) {
        this.defaultSource = false;
        this.source = new NonNullLazyValue<>(() -> factory.apply(makeProperties()));
        return this;
    }

    /**
     * Create a standard {@link FlowingFluidBlock} for this fluid, building it immediately, and not allowing for further configuration.
     * 
     * @return this {@link FluidBuilder}
     * @see #block()
     * @throws IllegalStateException
     *             If {@link #block()} or {@link #block(NonNullBiFunction)} has been called before this method
     */
    public FluidBuilder<T, P> defaultBlock() {
        if (this.defaultBlock != null) {
            throw new IllegalStateException("Cannot set a default block after a custom block has been created");
        }
        this.defaultBlock = true;
        return this;
    }

    /**
     * Create a standard {@link FlowingFluidBlock} for this fluid, and return the builder for it so that further customization can be done.
     * 
     * @return the {@link BlockBuilder} for the {@link FlowingFluidBlock}
     */
    public BlockBuilder<LiquidBlock, FluidBuilder<T, P>> block() {
        return block(LiquidBlock::new);
    }

    /**
     * Create a {@link FlowingFluidBlock} for this fluid, which is created by the given factory, and return the builder for it so that further customization can be done.
     * 
     * @param <B>
     *            The type of the block
     * @param factory
     *            A factory for the block, which accepts the block object and properties and returns a new block
     * @return the {@link BlockBuilder} for the {@link FlowingFluidBlock}
     */
    public <B extends LiquidBlock> BlockBuilder<B, FluidBuilder<T, P>> block(NonNullBiFunction<NonNullSupplier<? extends T>, BlockBehaviour.Properties, ? extends B> factory) {
        if (this.defaultBlock == Boolean.FALSE) {
            throw new IllegalStateException("Only one call to block/noBlock per builder allowed");
        }
        this.defaultBlock = false;
        NonNullSupplier<T> supplier = asSupplier();
        return getOwner().<B, FluidBuilder<T, P>>block(this, sourceName, p -> factory.apply(supplier, p))
                .properties(p -> BlockBehaviour.Properties.copy(Blocks.WATER).noDrops())
                .properties(p -> {
                    // TODO is this ok?
                    FluidAttributes attrs = this.attributes.get().build(Fluids.WATER);
                    return p.lightLevel($ -> attrs.getLuminosity());
                })
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().getBuilder(sourceName)
                                .texture("particle", stillTexture)));
    }

    @Beta
    public FluidBuilder<T, P> noBlock() {
        if (this.defaultBlock == Boolean.FALSE) {
            throw new IllegalStateException("Only one call to block/noBlock per builder allowed");
        }
        this.defaultBlock = false;
        return this;
    }

    /**
     * Create a standard {@link BucketItem} for this fluid, building it immediately, and not allowing for further configuration.
     * 
     * @return this {@link FluidBuilder}
     * @see #bucket()
     * @throws IllegalStateException
     *             If {@link #bucket()} or {@link #bucket(NonNullBiFunction)} has been called before this method
     */
    public FluidBuilder<T, P> defaultBucket() {
        if (this.defaultBucket != null) {
            throw new IllegalStateException("Cannot set a default bucket after a custom bucket has been created");
        }
        defaultBucket = true;
        return this;
    }

    /**
     * Create a standard {@link BucketItem} for this fluid, and return the builder for it so that further customization can be done.
     * 
     * @return the {@link ItemBuilder} for the {@link BucketItem}
     */
    public ItemBuilder<BucketItem, FluidBuilder<T, P>> bucket() {
        return bucket(BucketItem::new);
    }

    /**
     * Create a {@link BucketItem} for this fluid, which is created by the given factory, and return the builder for it so that further customization can be done.
     * 
     * @param <I>
     *            The type of the bucket item
     * @param factory
     *            A factory for the bucket item, which accepts the fluid object supplier and properties and returns a new item
     * @return the {@link ItemBuilder} for the {@link BucketItem}
     */
    public <I extends BucketItem> ItemBuilder<I, FluidBuilder<T, P>> bucket(NonNullBiFunction<Supplier<? extends ForgeFlowingFluid>, Item.Properties, ? extends I> factory) {
        if (this.defaultBucket == Boolean.FALSE) {
            throw new IllegalStateException("Only one call to bucket/noBucket per builder allowed");
        }
        this.defaultBucket = false;
        NonNullLazyValue<? extends ForgeFlowingFluid> source = this.source;
        if (source == null) {
            throw new IllegalStateException("Cannot create a bucket before creating a source block");
        }
        return getOwner().<I, FluidBuilder<T, P>>item(this, bucketName, p -> factory.apply(source::get, p))
                .properties(p -> p.craftRemainder(Items.BUCKET).stacksTo(1))
                .model((ctx, prov) -> prov.generated(ctx::getEntry, new ResourceLocation(getOwner().getModid(), "item/" + bucketName)));
    }

    @Beta
    public FluidBuilder<T, P> noBucket() {
        if (this.defaultBucket == Boolean.FALSE) {
            throw new IllegalStateException("Only one call to bucket/noBucket per builder allowed");
        }
        this.defaultBucket = false;
        return this;
    }

    /**
     * Assign {@link Tag.Named}{@code s} to this fluid and its source fluid. Multiple calls will add additional tags.
     * 
     * @param tags
     *            The tags to assign
     * @return this {@link FluidBuilder}
     */
    @SafeVarargs
    public final FluidBuilder<T, P> tag(Tag.Named<Fluid>... tags) {
        FluidBuilder<T, P> ret = this.tag(ProviderType.FLUID_TAGS, tags);
        if (this.tags.isEmpty()) {
            ret.getOwner().<RegistrateTagsProvider<Fluid>, Fluid> setDataGenerator(ret.sourceName, getRegistryType(), ProviderType.FLUID_TAGS,
                    prov -> this.tags.stream().map(prov::tag).forEach(p -> p.add(getSource())));
        }
        this.tags.addAll(Arrays.asList(tags));
        return ret;
    }

    /**
     * Remove {@link Tag.Named}{@code s} from this fluid and its source fluid. Multiple calls will remove additional tags.
     * 
     * @param tags
     *            The tags to remove
     * @return this {@link FluidBuilder}
     */
    @SafeVarargs
    public final FluidBuilder<T, P> removeTag(Tag.Named<Fluid>... tags) {
        this.tags.removeAll(Arrays.asList(tags));
        return this.removeTag(ProviderType.FLUID_TAGS, tags);
    }

    private ForgeFlowingFluid getSource() {
        NonNullLazyValue<? extends ForgeFlowingFluid> source = this.source;
        Preconditions.checkNotNull(source, "Fluid has no source block: " + sourceName);
        return source.get();
    }
    
    private ForgeFlowingFluid.Properties makeProperties() {
        FluidAttributes.Builder attributes = this.attributes.get();
        RegistryEntry<Block> block = getOwner().getOptional(sourceName, Block.class);
        attributesCallback.accept(attributes);
        // Force the translation key after the user callback runs
        // This is done because we need to remove the lang data generator if using the block key,
        // and if it was possible to undo this change, it might result in the user translation getting
        // silently lost, as there's no good way to check whether the translation key was changed.
        // TODO improve this?
        if (block.isPresent()) {
            attributes.translationKey(block.get().getDescriptionId());
            setData(ProviderType.LANG, NonNullBiConsumer.noop());
        } else {
            attributes.translationKey(Util.makeDescriptionId("fluid", new ResourceLocation(getOwner().getModid(), sourceName)));
        }
        NonNullLazyValue<? extends ForgeFlowingFluid> source = this.source;
        ForgeFlowingFluid.Properties ret = new ForgeFlowingFluid.Properties(source == null ? null : source::get, asSupplier(), attributes);
        properties.accept(ret);
        return ret;
    }

    @Override
    protected T createEntry() {
        return factory.apply(makeProperties());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Additionally registers the source fluid.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public FluidEntry<T> register() {
        if (defaultSource == Boolean.TRUE) {
            source(ForgeFlowingFluid.Source::new);
        }
        if (defaultBlock == Boolean.TRUE) {
            block().register();
        }
        if (defaultBucket == Boolean.TRUE) {
            bucket().register();
        }
        NonNullLazyValue<? extends ForgeFlowingFluid> source = this.source;
        if (source != null) {
            getCallback().accept(sourceName, Fluid.class, (FluidBuilder) this, source::get);
        } else {
            throw new IllegalStateException("Fluid must have a source version: " + getName());
        }
        return (FluidEntry<T>) super.register();
    }

    @Override
    protected RegistryEntry<T> createEntryWrapper(RegistryObject<T> delegate) {
        return new FluidEntry<>(getOwner(), delegate);
    }
}
