package crazypants.enderio.invpanel.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.capacitor.CapacitorKeyType;
import crazypants.enderio.base.capacitor.ICapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.Scaler;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.invpanel.EnderIOInvPanel;
import crazypants.enderio.invpanel.init.InvpanelObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOInvPanel.MODID)
public enum CapacitorKey implements ICapacitorKey {

  INV_CHEST_ENERGY_INTAKE(InvpanelObject.blockInventoryChestTiny, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  INV_CHEST_ENERGY_BUFFER(InvpanelObject.blockInventoryChestTiny, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  INV_CHEST_ENERGY_USE(InvpanelObject.blockInventoryChestTiny, CapacitorKeyType.ENERGY_USE, "use"),

  INV_SENSOR_ENERGY_INTAKE(InvpanelObject.blockInventoryPanelSensor, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  INV_SENSOR_ENERGY_BUFFER(InvpanelObject.blockInventoryPanelSensor, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  INV_SENSOR_ENERGY_USE(InvpanelObject.blockInventoryPanelSensor, CapacitorKeyType.ENERGY_USE, "use"),

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
  public float getFloat(float level) {
    return baseValue * scaler.scaleValue(level);
  }
  
  @Override
  public int getBaseValue() {
    return baseValue;
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
  }

}
