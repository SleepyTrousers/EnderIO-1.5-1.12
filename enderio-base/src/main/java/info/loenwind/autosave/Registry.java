package info.loenwind.autosave;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.recipe.sagmill.GrindingMultiplierNBT;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.endercore.HandleEnderInventory;
import info.loenwind.autosave.handlers.endercore.HandleSmartTank;
import info.loenwind.autosave.handlers.endercore.HandleUserIdent;
import info.loenwind.autosave.handlers.enderio.HandleCapturedMob;
import info.loenwind.autosave.handlers.enderio.HandleChannelList;
import info.loenwind.autosave.handlers.enderio.HandleExperienceContainer;
import info.loenwind.autosave.handlers.enderio.HandleIMachineRecipe;
import info.loenwind.autosave.handlers.enderio.HandlePoweredTask;
import info.loenwind.autosave.handlers.forge.HandleFluid;
import info.loenwind.autosave.handlers.forge.HandleFluidStack;
import info.loenwind.autosave.handlers.internal.HandleStorable;
import info.loenwind.autosave.handlers.java.HandleBoolean;
import info.loenwind.autosave.handlers.java.HandleEnum;
import info.loenwind.autosave.handlers.java.HandleFloat;
import info.loenwind.autosave.handlers.java.HandleFloatArray;
import info.loenwind.autosave.handlers.java.HandleInteger;
import info.loenwind.autosave.handlers.java.HandleString;
import info.loenwind.autosave.handlers.minecraft.HandleBlockPos;
import info.loenwind.autosave.handlers.minecraft.HandleIBlockState;
import info.loenwind.autosave.handlers.minecraft.HandleItem;
import info.loenwind.autosave.handlers.minecraft.HandleItemStack;
import info.loenwind.autosave.handlers.minecraft.HandleItemStackArray;

/**
 * A registry for {@link IHandler}s.
 * 
 * <p>
 * Registries use Java-like inheritance. That means any registry, except the base registry {@link Registry#GLOBAL_REGISTRY}, has exactly one super-registry.
 * When looking for handlers, all handlers from this registry and all its super-registries will be returned in order.
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Registry {

  /**
   * This is the super-registry of all registries. It contains handlers for Java primitives, Java classes, Minecraft classes and Forge classes.
   * <p>
   * You can register new handlers here if you want other mods to be able to store your objects. Otherwise please use your own registry.
   */
  @Nonnull
  public static final Registry GLOBAL_REGISTRY = new Registry(true);

  static {
    // Java primitives
    GLOBAL_REGISTRY.register(new HandleFloat());
    GLOBAL_REGISTRY.register(new HandleInteger());
    GLOBAL_REGISTRY.register(new HandleBoolean());
    GLOBAL_REGISTRY.register(new HandleFloatArray());
    GLOBAL_REGISTRY.register(new HandleEnum());
    GLOBAL_REGISTRY.register(new HandleString());

    // Minecraft basic types
    GLOBAL_REGISTRY.register(new HandleItemStackArray());
    GLOBAL_REGISTRY.register(new HandleItemStack());
    GLOBAL_REGISTRY.register(new HandleItem());
    GLOBAL_REGISTRY.register(new HandleBlockPos());
    GLOBAL_REGISTRY.register(new HandleIBlockState());

    // Forge basic types
    GLOBAL_REGISTRY.register(new HandleFluidStack());
    GLOBAL_REGISTRY.register(new HandleFluid());

    // Annotated objects
    GLOBAL_REGISTRY.register(new HandleStorable<Object>());

    // EnderCore types

    // Ender IO types
    GLOBAL_REGISTRY.register(new HandleSmartTank());
    GLOBAL_REGISTRY.register(new HandleIMachineRecipe());
    GLOBAL_REGISTRY.register(new HandlePoweredTask());
    GLOBAL_REGISTRY.register(new HandleCapturedMob());
    GLOBAL_REGISTRY.register(new GrindingMultiplierNBT());
    GLOBAL_REGISTRY.register(new HandleExperienceContainer());
    GLOBAL_REGISTRY.register(new HandleUserIdent());
    GLOBAL_REGISTRY.register(new HandleEnderInventory());
    GLOBAL_REGISTRY.register(new HandleChannelList());
  }

  @Nonnull
  private final List<IHandler> handlers = new ArrayList<IHandler>();
  @Nullable
  private final Registry parent;

  /**
   * Creates the {@link Registry#GLOBAL_REGISTRY}.
   * 
   * @param root
   *          A placeholder
   */
  private Registry(boolean root) {
    parent = root ? null : null;
  }

  /**
   * Crates a new registry which extends {@link Registry#GLOBAL_REGISTRY}.
   */
  public Registry() {
    this(GLOBAL_REGISTRY);
  }

  /**
   * Creates a new registry which extends the given parent.
   * 
   * @param parent
   *          The parent to extend
   */
  public Registry(@Nonnull Registry parent) {
    this.parent = parent;
  }

  /**
   * Registers a new {@link IHandler}.
   * 
   * @param handler
   *          The {@link IHandler} to register
   */
  public void register(@Nonnull IHandler handler) {
    handlers.add(handler);
  }

  /**
   * Registers a new {@link IHandler} that has higher priority than all existing handlers.
   * 
   * @param handler
   *          The {@link IHandler} to register
   */
  public void registerPriority(@Nonnull IHandler handler) {
    handlers.add(0, handler);
  }

  /**
   * Finds all {@link IHandler}s from this registry and all its parents that can handle the given class.
   * 
   * <p>
   * Handlers will be returned in this order:
   * <ol>
   * <li>The annotated special handler of the given class
   * <li>The annotated special handler(s) of its superclass(es)
   * <li>The registered handlers from this registry
   * <li>The registered handlers from this registry's super-registries
   * <li>{@link HandleStorable} if the class is annotated {@link Storable} without a special handler
   * </ol>
   * 
   * Note: If a class is annotated {@link Storable}, then all subclasses must be annotated {@link Storable}, too.
   * <p>
   * Note 2: If a class is annotated {@link Storable} without a special handler, all subclasses must either also be annotated {@link Storable} without a special
   * handler or their handlers must be able to handle the inheritance because {@link HandleStorable} will <i>not</i> be added to this list in this case.
   * <p>
   * Note 3: If a handler can handle a class but not its subclasses, it will not be added to this list for the subclasses.
   * 
   * @param clazz
   *          The class that should be handled
   * @return A list of all {@link IHandler}s that can handle the class. If none are found, an empty list is returned.
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  @Nonnull
  public List<IHandler> findHandlers(Class<?> clazz) throws InstantiationException, IllegalAccessException {
    List<IHandler> result = new ArrayList<IHandler>();

    Storable annotation = clazz.getAnnotation(Storable.class);
    while (annotation != null) {
      if (annotation.handler() != HandleStorable.class) {
        result.add(annotation.handler().newInstance());
      }
      Class<?> superclass = clazz.getSuperclass();
      if (superclass != null) {
        annotation = superclass.getAnnotation(Storable.class);
      }
    }

    findRegisteredHandlers(clazz, result);

    return result;
  }

  /**
   * Helper method for {@link #findHandlers(Class)}. Looks up only registered handlers and adds them to the end of the given list.
   * 
   * @param clazz
   * @param result
   */
  private void findRegisteredHandlers(Class<?> clazz, List<IHandler> result) {
    for (IHandler handler : handlers) {
      if (handler.canHandle(clazz)) {
        result.add(handler);
      }
    }
    final Registry thisParent = parent;
    if (thisParent != null) {
      thisParent.findRegisteredHandlers(clazz, result);
    }
  }

}
