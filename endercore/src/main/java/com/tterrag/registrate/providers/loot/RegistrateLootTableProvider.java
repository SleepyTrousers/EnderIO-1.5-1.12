package com.tterrag.registrate.providers.loot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class RegistrateLootTableProvider extends LootTableProvider implements RegistrateProvider {
    
    public interface LootType<T extends RegistrateLootTables> {
        
        static LootType<RegistrateBlockLootTables> BLOCK = register("block", LootContextParamSets.BLOCK, RegistrateBlockLootTables::new);
        static LootType<RegistrateEntityLootTables> ENTITY = register("entity", LootContextParamSets.ENTITY, RegistrateEntityLootTables::new);

        T getLootCreator(AbstractRegistrate<?> parent, Consumer<T> callback);
        
        LootContextParamSet getLootSet();
        
        static <T extends RegistrateLootTables> LootType<T> register(String name, LootContextParamSet set, NonNullBiFunction<AbstractRegistrate, Consumer<T>, T> factory) {
            LootType<T> type = new LootType<T>() {
                @Override
                public T getLootCreator(AbstractRegistrate<?> parent, Consumer<T> callback) {
                    return factory.apply(parent, callback);
                }
                
                @Override
                public LootContextParamSet getLootSet() {
                    return set;
                }
            };
            LOOT_TYPES.put(name, type);
            return type;
        }
    }
    
    private static final Map<String, LootType<?>> LOOT_TYPES = new HashMap<>();
    
    private final AbstractRegistrate<?> parent;
    
    private final Multimap<LootType<?>, Consumer<? super RegistrateLootTables>> specialLootActions = HashMultimap.create();
    private final Multimap<LootContextParamSet, Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> lootActions = HashMultimap.create();
    private final Set<RegistrateLootTables> currentLootCreators = new HashSet<>();

    public RegistrateLootTableProvider(AbstractRegistrate<?> parent, DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
        this.parent = parent;
    }

    @Override
    public String getName() {
        return "Loot tables";
    }
    
    @Override
    public LogicalSide getSide() {
        return LogicalSide.SERVER;
    }
    
    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationresults) {
        currentLootCreators.forEach(c -> c.validate(map, validationresults));
    }
    
    @SuppressWarnings("unchecked")
    public <T extends RegistrateLootTables> void addLootAction(LootType<T> type, NonNullConsumer<T> action) {
        this.specialLootActions.put(type, (Consumer<? super RegistrateLootTables>) action);
    }
    
    public void addLootAction(LootContextParamSet set, Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> action) {
        this.lootActions.put(set, action);
    }
    
    private Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>> getLootCreator(AbstractRegistrate<?> parent, LootType<?> type) {
        return () -> {
            RegistrateLootTables creator = type.getLootCreator(parent, cons -> specialLootActions.get(type).forEach(c -> c.accept(cons)));
            currentLootCreators.add(creator);
            return creator;
        };
    }
    
    private static final BiMap<ResourceLocation, LootContextParamSet> SET_REGISTRY = ObfuscationReflectionHelper.getPrivateValue(LootContextParamSets.class, null, "REGISTRY");
    
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        parent.genData(ProviderType.LOOT, this);
        currentLootCreators.clear();
        ImmutableList.Builder<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> builder = ImmutableList.builder();
        for (LootType<?> type : LOOT_TYPES.values()) {
            builder.add(Pair.of(getLootCreator(parent, type), type.getLootSet()));
        }
        for (LootContextParamSet set : SET_REGISTRY.values()) {
            builder.add(Pair.of(() -> callback -> lootActions.get(set).forEach(a -> a.accept(callback)), set));
        }
        return builder.build();
    }
}
