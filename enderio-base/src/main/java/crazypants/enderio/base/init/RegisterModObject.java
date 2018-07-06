package crazypants.enderio.base.init;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;
import net.minecraftforge.fml.common.eventhandler.IContextSetter;
import net.minecraftforge.registries.IForgeRegistry;

public class RegisterModObject extends GenericEvent<IModObject> implements IContextSetter {
  private final @Nonnull IForgeRegistry<IModObject> registry;
  private final @Nonnull ResourceLocation name;

  public RegisterModObject(@Nonnull ResourceLocation name, @Nonnull IForgeRegistry<IModObject> registry) {
    super(registry.getRegistrySuperType());
    this.name = name;
    this.registry = registry;
  }

  public @Nonnull IForgeRegistry<IModObject> getRegistry() {
    return registry;
  }

  public @Nonnull ResourceLocation getName() {
    return name;
  }

  public void register(@Nonnull IModObject value) {
    registry.register(value);
  }

  public <T extends Enum<T> & IModObject> void register(Class<T> enumClass) {
    for (T elem : enumClass.getEnumConstants()) {
      registry.register(elem);
    }
  }

}