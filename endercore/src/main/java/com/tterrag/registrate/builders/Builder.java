package com.tterrag.registrate.builders;

import java.util.function.Function;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * A Builder creates registry entries. A Builder instance has a constant name which will be used for the resultant object, they cannot be reused for different names. It holds a parent object that will
 * be returned from some final methods.
 * <p>
 * When a builder is completed via {@link #register()} or {@link #build()}, the object will be lazily registered (through the owning {@link AbstractRegistrate} object).
 * 
 * @param <R>
 *            Type of the registry for the current object. This is the concrete base class that all registry entries must extend, and the type used for the forge registry itself.
 * @param <T>
 *            Actual type of the object being built.
 * @param <P>
 *            Type of the parent object, this is returned from {@link #build()} and {@link #getParent()}.
 * @param <S>
 *            Self type
 */
public interface Builder<R extends IForgeRegistryEntry<R>, T extends R, P, S extends Builder<R, T, P, S>> extends NonNullSupplier<RegistryEntry<T>> {

    /**
     * Complete the current entry, and return the {@link RegistryEntry} that will supply the built entry once it is available. The builder can be used afterwards, and changes made will reflect the
     * output, as long as it is before registration takes place (before forge registry events).
     * 
     * @return The {@link RegistryEntry} supplying the built entry.
     */
    RegistryEntry<T> register();

    /**
     * The owning {@link AbstractRegistrate} that created this builder.
     * 
     * @return the owner {@link AbstractRegistrate}
     */
    AbstractRegistrate<?> getOwner();

    /**
     * The parent object.
     * 
     * @return the parent object of this builder
     */
    P getParent();

    /**
     * The name of the entry being created, and combined with the mod ID of the parent {@link AbstractRegistrate}, the registry name.
     * 
     * @return the name of the current entry
     */
    String getName();
    
    Class<? super R> getRegistryType();

    /**
     * Get the {@link RegistryEntry} representing the entry built by this builder. Cannot be called before the builder is built.
     * 
     * @return An {@link RegistryEntry} for this builder's entry
     * @throws IllegalArgumentException
     *             If this builder has not been built yet
     */
    @Override
    default RegistryEntry<T> get() {
        return getOwner().<R, T> get(getName(), getRegistryType());
    }

    /**
     * Get the actual entry built by this builder. Cannot be called before registration.
     * 
     * @return This builder's entry
     * @throws IllegalArgumentException
     *             If this builder has not been built yet
     * @throws NullPointerException
     *             If the entry from this builder has not been registered yet
     */
    default T getEntry() {
        return get().get();
    }
    
    /**
     * Get a supplier for the entry created by this builder, which will not reference the builder after it has been resolved.
     * 
     * @return A supplier for the entry
     */
    NonNullSupplier<T> asSupplier();

    /**
     * Set the data provider callback for this entry for the given provider type, which will be invoked when the provider of the given type executes.
     * <p>
     * If called multiple times for the same type, the existing callback will be <em>overwritten</em>.
     * <p>
     * This is mostly unneeded, and instead helper methods for specific data types should be used when possible.
     * 
     * @param <D>
     *            The type of provider
     * @param type
     *            The {@link ProviderType} for the desired provider
     * @param cons
     *            The callback to execute when the provider is run
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    default <D extends RegistrateProvider> S setData(ProviderType<? extends D> type, NonNullBiConsumer<DataGenContext<R, T>, D> cons) {
        getOwner().setDataGenerator(this, type, prov -> cons.accept(DataGenContext.from(this, getRegistryType()), prov));
        return (S) this;
    }

    /**
     * Add a data provider callback which will be invoked when the provider of the given type executes.
     * <p>
     * Calling this multiple times for the same type will <em>not</em> overwrite an existing callback.
     * 
     * @param <D>
     *            The type of provider
     * @param type
     *            The {@link ProviderType} for the desired provider
     * @param cons
     *            The callback to execute when the provider is run
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    default <D extends RegistrateProvider> S addMiscData(ProviderType<? extends D> type, NonNullConsumer<? extends D> cons) {
        getOwner().addDataGenerator(type, cons);
        return (S) this;
    }

    /**
     * Add a callback to be invoked when this entry is registered. Can be called multiple times to add multiple callbacks.
     * <p>
     * Builders which have had this method used on them (or another method which calls this one, such as {@link EntityBuilder#spawnEgg(int, int)}), <strong>must</strong> be registered, via
     * {@link #register()}, or errors will be thrown when these "dangling" register callbacks are discovered at register time.
     * 
     * @param callback
     *            the callback to invoke
     * @return this {@link Builder}
     */
    @SuppressWarnings("unchecked")
    default S onRegister(NonNullConsumer<? super T> callback) {
        getOwner().<R, T>addRegisterCallback(getName(), getRegistryType(), callback);
        return (S) this;
    }

    /**
     * Add a callback to be invoked when this entry is registered, but only after some other registry type has been registered as well. Can be called multiple times to add multiple callbacks.
     * <p>
     * Builders which have had this method used on them (or another method which calls this one, such as {@link EntityBuilder#spawnEgg(int, int)}), <strong>must</strong> be registered, via
     * {@link #register()}, or errors will be thrown when these "dangling" register callbacks are discovered at register time.
     * 
     * @param <OR>
     *            The dependency registry type
     * @param dependencyType
     *            the base class for objects of the dependency registry. The callback will be invoked only after this registry has fired its registry events.
     * @param callback
     *            the callback to invoke
     * @return this {@link Builder}
     */
    default <OR extends IForgeRegistryEntry<OR>> S onRegisterAfter(Class<? super OR> dependencyType, NonNullConsumer<? super T> callback) {
        return onRegister(e -> {
            if (getOwner().<OR>isRegistered(dependencyType)) {
                callback.accept(e);
            } else {
                getOwner().<OR>addRegisterCallback(dependencyType, () -> callback.accept(e));
            }
        });
    }

    /**
     * Apply a transformation to this {@link Builder}. Useful to apply helper methods within a fluent chain, e.g.
     * 
     * <pre>
     * {@code
     * public static final RegistryObject<MyBlock> MY_BLOCK = REGISTRATE.object("my_block")
     *         .block(MyBlock::new)
     *         .transform(Utils::defaultBlockProperties)
     *         .register();
     * }
     * </pre>
     * 
     * @param <R2>
     *            Registry type
     * @param <T2>
     *            Entry type
     * @param <P2>
     *            Parent type
     * @param <S2>
     *            Self type
     * @param func
     *            The {@link Function function} to apply
     * @return the {@link Builder} returned by the given function
     */
    @SuppressWarnings("unchecked")
    default <R2 extends IForgeRegistryEntry<R2>, T2 extends R2, P2, S2 extends Builder<R2, T2, P2, S2>> S2 transform(NonNullFunction<S, S2> func) {
        return func.apply((S) this);
    }

    /**
     * Register the entry and return the parent object. The {@link RegistryObject} will be created but not returned. It can be retrieved later with {@link AbstractRegistrate#get(Class)} or
     * {@link AbstractRegistrate#get(String, Class)}.
     * 
     * @return the parent object
     */
    default P build() {
        register(); // Ignore return value
        return getParent();
    }
}
