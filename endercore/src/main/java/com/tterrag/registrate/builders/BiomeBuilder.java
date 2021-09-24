//package com.tterrag.registrate.builders;
//
//import java.util.Arrays;
//import java.util.EnumSet;
//
//import javax.annotation.Nonnull;
//
//import com.google.common.collect.HashMultimap;
//import com.google.common.collect.Multimap;
//import com.tterrag.registrate.AbstractRegistrate;
//import com.tterrag.registrate.providers.RegistrateLangProvider;
//import com.tterrag.registrate.util.nullness.NonNullConsumer;
//import com.tterrag.registrate.util.nullness.NonNullFunction;
//import com.tterrag.registrate.util.nullness.NonNullSupplier;
//import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
//import com.tterrag.registrate.util.nullness.NonnullType;
//
//import net.minecraft.entity.EntityClassification;
//import net.minecraft.entity.EntityType;
//import net.minecraft.world.biome.Biome;
//import net.minecraft.world.biome.Biome.SpawnListEntry;
//import net.minecraft.world.gen.GenerationStage.Carving;
//import net.minecraft.world.gen.GenerationStage.Decoration;
//import net.minecraft.world.gen.carver.ConfiguredCarver;
//import net.minecraft.world.gen.carver.EmptyCarverConfig;
//import net.minecraft.world.gen.carver.ICarverConfig;
//import net.minecraft.world.gen.carver.WorldCarver;
//import net.minecraft.world.gen.feature.ConfiguredFeature;
//import net.minecraft.world.gen.feature.Feature;
//import net.minecraft.world.gen.feature.IFeatureConfig;
//import net.minecraft.world.gen.feature.NoFeatureConfig;
//import net.minecraft.world.gen.placement.IPlacementConfig;
//import net.minecraft.world.gen.placement.NoPlacementConfig;
//import net.minecraft.world.gen.placement.Placement;
//import net.minecraftforge.common.BiomeDictionary;
//import net.minecraftforge.common.BiomeManager;
//
///**
// * A builder for biomes, allows for customization of the {@link Biome.Builder biome properties}, and configuration of data associated with biomes (lang).
// * 
// * @param <T>
// *            The type of biome being built
// * @param <P>
// *            Parent object type
// */
//public class BiomeBuilder<T extends Biome, P> extends AbstractBuilder<Biome, T, P, BiomeBuilder<T, P>> {
//
//    /**
//     * Create a new {@link BiomeBuilder} and configure data. Used in lieu of adding side-effects to constructor, so that alternate initialization strategies can be done in subclasses.
//     * <p>
//     * The biome will be assigned the following data:
//     * <ul>
//     * <li>The default translation (via {@link #defaultLang()})</li>
//     * </ul>
//     * 
//     * @param <T>
//     *            The type of the builder
//     * @param <P>
//     *            Parent object type
//     * @param owner
//     *            The owning {@link AbstractRegistrate} object
//     * @param parent
//     *            The parent object
//     * @param name
//     *            Name of the entry being built
//     * @param callback
//     *            A callback used to actually register the built entry
//     * @param factory
//     *            Factory to create the biome
//     * @return A new {@link BiomeBuilder} with reasonable default data generators.
//     */
//    public static <T extends Biome, P> BiomeBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<Biome.Builder, T> factory) {
//        return new BiomeBuilder<>(owner, parent, name, callback, factory)
//                .defaultLang();
//    }
//
//    private final NonNullFunction<Biome.Builder, T> factory;
//    
//    private NonNullSupplier<Biome.Builder> initialProperties = Biome.Builder::new;
//    private NonNullFunction<Biome.Builder, Biome.Builder> propertiesCallback = NonNullUnaryOperator.identity();
//    
//    private final Multimap<Decoration, NonNullSupplier<ConfiguredFeature<?, ?>>> features = HashMultimap.create();
//    private final Multimap<Carving, NonNullSupplier<ConfiguredCarver<?>>> carvers = HashMultimap.create();
//    private final Multimap<EntityClassification, NonNullSupplier<SpawnListEntry>> spawns = HashMultimap.create();
//
//    @SuppressWarnings("null")
//    private final EnumSet<BiomeManager.BiomeType> configuredTypes = EnumSet.noneOf(BiomeManager.BiomeType.class);
//
//    protected BiomeBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<Biome.Builder, T> factory) {
//        super(owner, parent, name, callback, Biome.class);
//        this.factory = factory;
//    }
//
//    /**
//     * Modify the properties of the biome. Modifications are done lazily, but the passed function is composed with the current one, and as such this method can be called multiple times to perform
//     * different operations.
//     * <p>
//     * If a different properties instance is returned, it will replace the existing one entirely.
//     * 
//     * @param func
//     *            The action to perform on the properties
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> properties(NonNullUnaryOperator<Biome.Builder> func) {
//        propertiesCallback = propertiesCallback.andThen(func);
//        return this;
//    }
//
//    /**
//     * Replace the initial state of the biome properties, without replacing or removing any modifications done via {@link #properties(NonNullUnaryOperator)}.
//     * 
//     * @param properties
//     *            A supplier to to create the initial properties
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> initialProperties(NonNullSupplier<Biome.Builder> properties) {
//        initialProperties = properties;
//        return this;
//    }
//
//    /**
//     * Set the weight for this biome to generate in the overworld in regions matching the given type. This can only be called once per type.
//     * 
//     * @param type
//     *            The type that controls which climates the biome will spawn in
//     * @param weight
//     *            The weight, or how common this biome should be in that climate
//     * @return this {@link BiomeBuilder}
//     * @see BiomeManager
//     * @throws IllegalArgumentException
//     *             if this type has already had its weight set
//     */
//    public BiomeBuilder<T, P> typeWeight(BiomeManager.BiomeType type, int weight) {
//        if (!configuredTypes.add(type)) {
//            throw new IllegalArgumentException("Cannot set a type weight more than once.");
//        }
//        this.onRegister(b -> BiomeManager.addBiome(type, new BiomeManager.BiomeEntry(b, weight)));
//        return this;
//    }
//
//    /**
//     * Add types to the {@link BiomeDictionary} for this biome. Can be called multiple times to add more types.
//     * 
//     * @param types
//     *            The types to add
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> addDictionaryTypes(BiomeDictionary.Type... types) {
//        this.onRegister(b -> BiomeDictionary.addTypes(b, types));
//        return this;
//    }
//
//    /**
//     * Manually add what would have been the "best guess" types from the {@link BiomeDictionary} for this biome. This has no effect if no types are added via
//     * {@link #addDictionaryTypes(net.minecraftforge.common.BiomeDictionary.Type...)}.
//     * 
//     * @return this {@link BiomeBuilder}
//     * @see BiomeDictionary#makeBestGuess(Biome)
//     */
//    public BiomeBuilder<T, P> forceAutomaticDictionaryTypes() {
//        this.onRegister(BiomeDictionary::makeBestGuess);
//        return this;
//    }
//
//    /**
//     * Copy all {@link Feature features} from another biome. Does not check for duplicates.
//     * 
//     * @param biome
//     *            The biome to copy features from
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> copyFeatures(NonNullSupplier<Biome> biome) {
//        addFeatures(b -> Arrays.stream(Decoration.values())
//                .forEach(d -> biome.get().getFeatures(d).stream()
//                        .forEach(f -> b.addFeature(d, f))));
//        return this;
//    }
//    
//    /**
//     * Add a feature to this biome, where neither the feature nor the placement have a config.
//     * 
//     * @param stage
//     *            The stage which the feature will generate in
//     * @param feature
//     *            The feature to add
//     * @param placement
//     *            How the feature will be placed
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> addFeature(Decoration stage, NonNullSupplier<Feature<NoFeatureConfig>> feature, NonNullSupplier<Placement<NoPlacementConfig>> placement) {
//        return addFeature(stage, feature, IFeatureConfig.NO_FEATURE_CONFIG, placement);
//    }
//    
//    /**
//     * Add a feature to this biome, where the placement does not have a config.
//     * 
//     * @param <FC>
//     *            The type of config the feature requires
//     * @param stage
//     *            The stage which the feature will generate in
//     * @param feature
//     *            The feature to add
//     * @param featureConfig
//     *            The config for the feature
//     * @param placement
//     *            How the feature will be placed
//     * @return this {@link BiomeBuilder}
//     */
//    public <FC extends IFeatureConfig> BiomeBuilder<T, P> addFeature(Decoration stage, NonNullSupplier<Feature<FC>> feature, FC featureConfig, NonNullSupplier<Placement<NoPlacementConfig>> placement) {
//        return addFeature(stage, feature, featureConfig, placement, IPlacementConfig.NO_PLACEMENT_CONFIG);
//    }
//    
//    /**
//     * Add a feature to this biome, where the feature does not have a config.
//     * @param <PC>
//     *            The type of config the placement requires
//     * @param stage
//     *            The stage which the feature will generate in
//     * @param feature
//     *            The feature to add
//     * @param placement
//     *            How the feature will be placed
//     * @param placementConfig
//     *            The config for the placement
//     * @return this {@link BiomeBuilder}
//     */
//    public <PC extends IPlacementConfig> BiomeBuilder<T, P> addFeature(Decoration stage, NonNullSupplier<Feature<NoFeatureConfig>> feature, NonNullSupplier<Placement<PC>> placement, PC placementConfig) {
//        return addFeature(stage, feature, IFeatureConfig.NO_FEATURE_CONFIG, placement, placementConfig);
//    }
//    
//    /**
//     * Add a feature to this biome.
//     * 
//     * @param <FC>
//     *            The type of config the feature requires
//     * @param <PC>
//     *            The type of config the placement requires
//     * @param stage
//     *            The stage which the feature will generate in
//     * @param feature
//     *            The feature to add
//     * @param featureConfig
//     *            The config for the feature
//     * @param placement
//     *            How the feature will be placed
//     * @param placementConfig
//     *            The config for the placement
//     * @return this {@link BiomeBuilder}
//     */
//    public <FC extends IFeatureConfig, PC extends IPlacementConfig> BiomeBuilder<T, P> addFeature(Decoration stage, NonNullSupplier<Feature<FC>> feature, FC featureConfig, NonNullSupplier<Placement<PC>> placement, PC placementConfig) {
//        return addConfiguredFeature(stage, () -> feature.get().withConfiguration(featureConfig).withPlacement(placement.get().configure(placementConfig)));
//    }
//
//    /**
//     * Add a pre-configured feature to this biome.
//     * 
//     * @param stage
//     *            The stage which the feature will generate in
//     * @param feature
//     *            The feature to add
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> addConfiguredFeature(Decoration stage, NonNullSupplier<ConfiguredFeature<?, ?>> feature) {
//        if (this.features.isEmpty()) {
//            addFeatures(b -> this.features.forEach((d, f) -> b.addFeature(d, f.get())));
//        }
//        this.features.put(stage, feature);
//        return this;
//    }
//
//    /**
//     * Add a callback that will be invoked after all {@link Feature Features} are registered, for the purpose of adding them to this biome.
//     * <p>
//     * Any {@link Feature} object can be safely referenced here and added to the biome via {@link Biome#addFeature(Decoration, ConfiguredFeature)}
//     * 
//     * @param action
//     *            A {@link NonNullConsumer} which will be called to add features to this biome.
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> addFeatures(NonNullConsumer<? super T> action) {
//        this.<Feature<?>>onRegisterAfter(Feature.class, action);
//        return this;
//    }
//    
//    /**
//     * Copy all {@link WorldCarver carvers} from another biome. Does not check for duplicates.
//     * 
//     * @param biome
//     *            The biome to copy carvers from
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> copyCarvers(NonNullSupplier<Biome> biome) {
//        addCarvers(b -> Arrays.stream(Carving.values())
//                .forEach(s -> biome.get().getCarvers(s).stream()
//                        .forEach(c -> b.addCarver(s, c))));
//        return this;
//    }
//
//    /**
//     * Add a carver to this biome, where the carver does not have a config.
//     * 
//     * @param type
//     *            The type of carving to be done
//     * @param carver
//     *            The carver to add
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> addCarver(Carving type, NonNullSupplier<WorldCarver<EmptyCarverConfig>> carver) {
//        return addCarver(type, carver, ICarverConfig.field_214644_a);
//    }
//
//    /**
//     * Add a carver to this biome.
//     * 
//     * @param <CC> The type of config the carver requires
//     * @param type
//     *            The type of carving to be done
//     * @param carver
//     *            The carver to add
//     * @param carverConfig
//     *            The config for the carver
//     * @return this {@link BiomeBuilder}
//     */
//    public <CC extends ICarverConfig> BiomeBuilder<T, P> addCarver(Carving type, NonNullSupplier<WorldCarver<CC>> carver, CC carverConfig) {
//        return addConfiguredCarver(type, () -> Biome.createCarver(carver.get(), carverConfig));
//    }
//
//    /**
//     * Add a pre-configured carver to this biome.
//     * 
//     * @param type
//     *            The type of carving to be done
//     * @param carver
//     *            The carver to add
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> addConfiguredCarver(Carving type, NonNullSupplier<ConfiguredCarver<?>> carver) {
//        if (this.features.isEmpty()) {
//            addCarvers(b -> this.carvers.forEach((s, f) -> b.addCarver(s, f.get())));
//        }
//        this.carvers.put(type, carver);
//        return this;
//    }
//
//    /**
//     * Add a callback that will be invoked after all {@link WorldCarver WorldCarvers} are registered, for the purpose of adding them to this biome.
//     * <p>
//     * Any {@link WorldCarver} object can be safely referenced here and added to the biome via {@link Biome#addCarver(Carving, ConfiguredCarver)}
//     * 
//     * @param action
//     *            A {@link NonNullConsumer} which will be called to add carvers to this biome.
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> addCarvers(NonNullConsumer<? super T> action) {
//        this.<WorldCarver<?>> onRegisterAfter(WorldCarver.class, action);
//        return this;
//    }
//    
//    /**
//     * Copy all {@link SpawnListEntry spawns} from another biome. Does not check for duplicates.
//     * 
//     * @param biome
//     *            The biome to copy spawns from
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> copySpawns(NonNullSupplier<Biome> biome) {
//        addSpawns(b -> Arrays.stream(EntityClassification.values())
//                .forEach(ec -> biome.get().getSpawns(ec).stream()
//                        .forEach(s -> b.getSpawns(ec).add(s))));
//        return this;
//    }
//    
//    /**
//     * Add an entity spawn to this biome.
//     * 
//     * @param type
//     *            The classification of the spawn, which controls how and when the entity is spawned
//     * @param entity
//     *            The entity to spawn
//     * @param weight
//     *            The weight of the spawn, i.e. how likely it is compared to other spawn entries to be selected
//     * @param minGroupSize
//     *            A minimum size of entities to spawn at once
//     * @param maxGroupSize
//     *            A maximum size of entities to spawn at once
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> addSpawn(EntityClassification type, NonNullSupplier<? extends EntityType<?>> entity, int weight, int minGroupSize, int maxGroupSize) {
//        return addSpawn(type, () -> new SpawnListEntry(entity.get(), weight, minGroupSize, maxGroupSize));
//    }
//    
//    /**
//     * Add an entity spawn to this biome.
//     * 
//     * @param type
//     *            The classification of the spawn, which controls how and when the entity is spawned
//     * @param spawn
//     *            The spawn entry to add
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> addSpawn(EntityClassification type, NonNullSupplier<SpawnListEntry> spawn) {
//        if (this.spawns.isEmpty()) {
//            this.addSpawns(b -> this.spawns.forEach((c, s) -> b.getSpawns(c).add(s.get())));
//        }
//        this.spawns.put(type, spawn);
//        return this;
//    }
//
//    /**
//     * Add a callback that will be invoked after all {@link EntityType Entities} are registered, for the purpose of adding entity spawns to this biome.
//     * <p>
//     * Any {@link EntityType} object can be safely referenced here and added to the biome via {@link Biome#getSpawns(EntityClassification)}.
//     * 
//     * @param action
//     *            A {@link NonNullConsumer} which will be called to add spawns to this biome.
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> addSpawns(NonNullConsumer<? super T> action) {
//        this.<EntityType<?>> onRegisterAfter(EntityType.class, action);
//        return this;
//    }
//
//    /**
//     * Assign the default translation, as specified by {@link RegistrateLangProvider#getAutomaticName(NonNullSupplier)}. This is the default, so it is generally not necessary to call, unless for
//     * undoing previous changes.
//     * 
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> defaultLang() {
//        return lang(Biome::getTranslationKey);
//    }
//
//    /**
//     * Set the translation for this biome.
//     * 
//     * @param name
//     *            A localized English name
//     * @return this {@link BiomeBuilder}
//     */
//    public BiomeBuilder<T, P> lang(String name) {
//        return lang(Biome::getTranslationKey, name);
//    }
//
//    @Override
//    protected @NonnullType T createEntry() {
//        @Nonnull Biome.Builder properties = this.initialProperties.get();
//        properties = propertiesCallback.apply(properties);
//        return factory.apply(properties);
//    }
//}
