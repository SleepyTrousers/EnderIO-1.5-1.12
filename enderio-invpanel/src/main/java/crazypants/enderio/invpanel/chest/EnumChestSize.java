package crazypants.enderio.invpanel.chest;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.CapacitorKeyType;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.capacitor.Scaler;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.capacitor.ScalerFactory;
import crazypants.enderio.invpanel.EnderIOInvPanel;
import crazypants.enderio.invpanel.init.InvpanelObject;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOInvPanel.MODID)
public enum EnumChestSize implements IStringSerializable {
  // Simple
  TINY(InvpanelObject.blockInventoryChestTiny),
  SMALL(InvpanelObject.blockInventoryChestSmall),
  MEDIUM(InvpanelObject.blockInventoryChestMedium),
  // Normal
  BIG(InvpanelObject.blockInventoryChestBig),
  LARGE(InvpanelObject.blockInventoryChestLarge),
  HUGE(InvpanelObject.blockInventoryChestHuge),
  // Enhanced
  ENORMOUS(InvpanelObject.blockInventoryChestEnormous),
  WAREHOUSE(InvpanelObject.blockInventoryChestWarehouse),
  WAREHOUSE13(InvpanelObject.blockInventoryChestWarehouse13),

  // Be honest, you expected a bra size joke here, didn't you?
  // TODO add bad bra joke here
  ;

  private final @Nonnull IModObject owner;
  private final @Nonnull ICapacitorKey intake, buffer, use, size;

  private EnumChestSize(@Nonnull IModObject owner) {
    this.owner = owner;
    intake = new CapacitorKey(CapacitorKeyType.ENERGY_INTAKE, "intake");
    buffer = new CapacitorKey(CapacitorKeyType.ENERGY_BUFFER, "buffer");
    use = new CapacitorKey(CapacitorKeyType.ENERGY_USE, "use");
    size = new CapacitorKey(CapacitorKeyType.AMOUNT, "size");
  }

  public int getSlots() {
    return size.getDefault();
  }

  public static final @Nonnull PropertyEnum<EnumChestSize> SIZE = PropertyEnum.<EnumChestSize> create("size", EnumChestSize.class);

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  @Nonnull
  public String getUnlocalizedName(Item me) {
    return me.getUnlocalizedName() + "_" + getName();
  }

  public static int getMetaFromType(EnumChestSize value) {
    return value.ordinal();
  }

  public @Nonnull ICapacitorKey getIntake() {
    return intake;
  }

  public @Nonnull ICapacitorKey getBuffer() {
    return buffer;
  }

  public @Nonnull ICapacitorKey getUse() {
    return use;
  }

  private class CapacitorKey implements ICapacitorKey {

    private final @Nonnull ResourceLocation registryName;
    private final @Nonnull CapacitorKeyType valueType;

    private @Nonnull Scaler scaler = ScalerFactory.INVALID;
    private int baseValue = Integer.MIN_VALUE;

    private CapacitorKey(@Nonnull CapacitorKeyType valueType, @Nonnull String shortname) {
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
      if (scaler == ScalerFactory.INVALID || baseValue == Integer.MIN_VALUE) {
        throw new UnconfiguredCapKeyException(
            "CapacitorKey " + getRegistryName() + " has not been configured. This should not be possible and may be caused by a 3rd-party addon mod.");
      }
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

  }

  @SubscribeEvent
  public static void register(RegistryEvent.Register<ICapacitorKey> event) {
    for (EnumChestSize key : values()) {
      event.getRegistry().register(key.intake);
      Log.debug("<capacitor key=\"", key.intake.getRegistryName() + "\" base=\"\" scaler=\"FIXED\" />");
      event.getRegistry().register(key.buffer);
      Log.debug("<capacitor key=\"", key.buffer.getRegistryName() + "\" base=\"\" scaler=\"FIXED\" />");
      event.getRegistry().register(key.use);
      Log.debug("<capacitor key=\"", key.use.getRegistryName() + "\" base=\"\" scaler=\"FIXED\" />");
      event.getRegistry().register(key.size);
      Log.debug("<capacitor key=\"", key.size.getRegistryName() + "\" base=\"\" scaler=\"FIXED\" />");
    }
  }

}
