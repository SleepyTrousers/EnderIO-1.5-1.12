package crazypants.enderio.base.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public enum CapacitorKey implements ICapacitorKey {

  NO_POWER(ModObject.block_machine_base, CapacitorKeyType.ENERGY_INTAKE, "no_power"),

  LEGACY_ENERGY_INTAKE(ModObject.block_machine_base, CapacitorKeyType.ENERGY_INTAKE, "legacy_intake"),
  LEGACY_ENERGY_BUFFER(ModObject.block_machine_base, CapacitorKeyType.ENERGY_BUFFER, "legacy_buffer"),
  LEGACY_ENERGY_USE(ModObject.block_machine_base, CapacitorKeyType.ENERGY_USE, "legacy_use"),

  ;

  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //

  private final @Nonnull ResourceLocation registryName;
  private final @Nonnull IModObject owner;
  private final @Nonnull CapacitorKeyType valueType;

  private @Nonnull Scaler scaler = Scaler.Factory.INVALID;
  private int baseValue = Integer.MIN_VALUE;

  private CapacitorKey(@Nonnull IModObject owner, @Nonnull CapacitorKeyType valueType, @Nonnull String shortname) {
    this.owner = owner;
    this.valueType = valueType;
    this.registryName = new ResourceLocation(owner.getRegistryName().getResourceDomain(),
        owner.getRegistryName().getResourcePath() + "/" + shortname.toLowerCase(Locale.ENGLISH));
  }

  @Override
  public int get(@Nonnull ICapacitorData capacitor) {
    return (int) (baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this)));
  }

  @Override
  public float getFloat(@Nonnull ICapacitorData capacitor) {
    return baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this));
  }

  @Override
  public @Nonnull IModObject getOwner() {
    return owner;
  }

  @Override
  public @Nonnull CapacitorKeyType getValueType() {
    return valueType;
  }

  @Override
  public @Nonnull String getLegacyName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  @Override
  public void setScaler(@Nonnull Scaler scaler) {
    this.scaler = scaler;
  }

  @Override
  public void setBaseValue(int baseValue) {
    this.baseValue = baseValue;
  }

  @Override
  public void validate() {
    if (scaler == Scaler.Factory.INVALID || baseValue == Integer.MIN_VALUE) {
      throw new RuntimeException(
          "CapacitorKey " + getRegistryName() + " has not been configured. This should not be possible and may be caused by a 3rd-party addon mod.");
    }
  }

  public final ICapacitorKey setRegistryName(String name) {
    throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());
  }

  @Override
  public final ICapacitorKey setRegistryName(ResourceLocation name) {
    throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());
  }

  @Override
  public final @Nonnull ResourceLocation getRegistryName() {
    return registryName;
  }

  @Override
  public final Class<ICapacitorKey> getRegistryType() {
    return ICapacitorKey.class;
  };

  @SubscribeEvent
  public static void register(RegistryEvent.Register<ICapacitorKey> event) {
    for (CapacitorKey key : values()) {
      event.getRegistry().register(key);
      Log.debug("<capacitor key=\"", key.getRegistryName() + "\" base=\"\" scaler=\"\" />");
    }

    CapacitorKeyRegistry.setScaler(NO_POWER.getRegistryName(), Scaler.Factory.FIXED);
    CapacitorKeyRegistry.setBaseValue(NO_POWER.getRegistryName(), 0);
  }

}
