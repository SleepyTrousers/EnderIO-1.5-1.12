package crazypants.enderio.base.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.CapacitorKeyType;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.capacitor.Scaler;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
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
  LEGACY_ENERGY_EFFICIENCY(ModObject.block_machine_base, CapacitorKeyType.ENERGY_EFFICIENCY, "legacy_efficiency"),

  DARK_STEEL_SWORD_ENERGY_BUFFER(ModObject.itemDarkSteelSword, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_SWORD_ENERGY_INPUT(ModObject.itemDarkSteelSword, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_SWORD_ENERGY_USE(ModObject.itemDarkSteelSword, CapacitorKeyType.ENERGY_USE),
  DARK_STEEL_SWORD_ABSORPTION_RATIO(ModObject.itemDarkSteelSword, CapacitorKeyType.ABSORPTION),

  DARK_STEEL_ARMOR_ENERGY_BUFFER(ModObject.itemDarkSteelChestplate, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_ARMOR_ENERGY_INPUT(ModObject.itemDarkSteelChestplate, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_ARMOR_ENERGY_USE(ModObject.itemDarkSteelChestplate, CapacitorKeyType.ENERGY_USE),
  DARK_STEEL_ARMOR_ABSORPTION_RATIO(ModObject.itemDarkSteelChestplate, CapacitorKeyType.ABSORPTION),

  DARK_STEEL_AXE_ENERGY_BUFFER(ModObject.itemDarkSteelAxe, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_AXE_ENERGY_INPUT(ModObject.itemDarkSteelAxe, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_AXE_ENERGY_USE(ModObject.itemDarkSteelAxe, CapacitorKeyType.ENERGY_USE),
  DARK_STEEL_AXE_ABSORPTION_RATIO(ModObject.itemDarkSteelAxe, CapacitorKeyType.ABSORPTION),

  DARK_STEEL_CROOK_ENERGY_BUFFER(ModObject.itemDarkSteelCrook, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_CROOK_ENERGY_INPUT(ModObject.itemDarkSteelCrook, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_CROOK_ENERGY_USE(ModObject.itemDarkSteelCrook, CapacitorKeyType.ENERGY_USE),
  DARK_STEEL_CROOK_ABSORPTION_RATIO(ModObject.itemDarkSteelCrook, CapacitorKeyType.ABSORPTION),

  DARK_STEEL_BOW_ENERGY_BUFFER(ModObject.itemDarkSteelBow, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_BOW_ENERGY_INPUT(ModObject.itemDarkSteelBow, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_BOW_ENERGY_USE(ModObject.itemDarkSteelBow, CapacitorKeyType.ENERGY_USE),
  DARK_STEEL_BOW_ABSORPTION_RATIO(ModObject.itemDarkSteelBow, CapacitorKeyType.ABSORPTION),

  DARK_STEEL_PICKAXE_ENERGY_BUFFER(ModObject.itemDarkSteelPickaxe, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_PICKAXE_ENERGY_INPUT(ModObject.itemDarkSteelPickaxe, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_PICKAXE_ENERGY_USE(ModObject.itemDarkSteelPickaxe, CapacitorKeyType.ENERGY_USE),
  DARK_STEEL_PICKAXE_ABSORPTION_RATIO(ModObject.itemDarkSteelPickaxe, CapacitorKeyType.ABSORPTION),

  DARK_STEEL_SHEARS_ENERGY_BUFFER(ModObject.itemDarkSteelShears, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_SHEARS_ENERGY_INPUT(ModObject.itemDarkSteelShears, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_SHEARS_ENERGY_USE(ModObject.itemDarkSteelShears, CapacitorKeyType.ENERGY_USE),
  DARK_STEEL_SHEARS_ABSORPTION_RATIO(ModObject.itemDarkSteelShears, CapacitorKeyType.ABSORPTION),

  DARK_STEEL_TREETAP_ENERGY_BUFFER(ModObject.itemDarkSteelTreetap, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_TREETAP_ENERGY_INPUT(ModObject.itemDarkSteelTreetap, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_TREETAP_ENERGY_USE(ModObject.itemDarkSteelTreetap, CapacitorKeyType.ENERGY_USE),
  DARK_STEEL_TREETAP_ABSORPTION_RATIO(ModObject.itemDarkSteelTreetap, CapacitorKeyType.ABSORPTION),

  DARK_STEEL_LEVITY_ENERGY_BUFFER(ModObject.itemStaffOfLevity, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_LEVITY_ENERGY_INPUT(ModObject.itemStaffOfLevity, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_LEVITY_ENERGY_USE(ModObject.itemStaffOfLevity, CapacitorKeyType.ENERGY_USE),

  DARK_STEEL_TRAVEL_ENERGY_BUFFER(ModObject.itemTravelStaff, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_TRAVEL_ENERGY_INPUT(ModObject.itemTravelStaff, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_TRAVEL_ENERGY_USE(ModObject.itemTravelStaff, CapacitorKeyType.ENERGY_USE),

  DARK_STEEL_CHARGER_SIMPLE_ENERGY_BUFFER(ModObject.itemInventoryChargerSimple, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_CHARGER_SIMPLE_ENERGY_INPUT(ModObject.itemInventoryChargerSimple, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_CHARGER_SIMPLE_ENERGY_USE(ModObject.itemInventoryChargerSimple, CapacitorKeyType.ENERGY_USE),

  DARK_STEEL_CHARGER_BASIC_ENERGY_BUFFER(ModObject.itemInventoryChargerBasic, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_CHARGER_BASIC_ENERGY_INPUT(ModObject.itemInventoryChargerBasic, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_CHARGER_BASIC_ENERGY_USE(ModObject.itemInventoryChargerBasic, CapacitorKeyType.ENERGY_USE),

  DARK_STEEL_CHARGER_ENERGY_BUFFER(ModObject.itemInventoryCharger, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_CHARGER_ENERGY_INPUT(ModObject.itemInventoryCharger, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_CHARGER_ENERGY_USE(ModObject.itemInventoryCharger, CapacitorKeyType.ENERGY_USE),

  DARK_STEEL_CHARGER_VIBRANT_ENERGY_BUFFER(ModObject.itemInventoryChargerVibrant, CapacitorKeyType.ENERGY_BUFFER),
  DARK_STEEL_CHARGER_VIBRANT_ENERGY_INPUT(ModObject.itemInventoryChargerVibrant, CapacitorKeyType.ENERGY_INTAKE),
  DARK_STEEL_CHARGER_VIBRANT_ENERGY_USE(ModObject.itemInventoryChargerVibrant, CapacitorKeyType.ENERGY_USE),

  ;

  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //

  private final @Nonnull ResourceLocation registryName;
  private final @Nonnull IModObject owner;
  private final @Nonnull CapacitorKeyType valueType;

  private @Nonnull Scaler scaler = ScalerFactory.INVALID;
  private int baseValue = Integer.MIN_VALUE;

  private CapacitorKey(@Nonnull IModObject owner, @Nonnull CapacitorKeyType valueType, @Nonnull String shortname) {
    this.owner = owner;
    this.valueType = valueType;
    this.registryName = new ResourceLocation(owner.getRegistryName().getResourceDomain(),
        owner.getRegistryName().getResourcePath() + "/" + shortname.toLowerCase(Locale.ENGLISH));
  }

  private CapacitorKey(@Nonnull IModObject owner, @Nonnull CapacitorKeyType valueType) {
    this(owner, valueType, valueType.name().toLowerCase(Locale.ENGLISH));
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

    CapacitorKeyRegistry.setScaler(NO_POWER.getRegistryName(), ScalerFactory.FIXED);
    CapacitorKeyRegistry.setBaseValue(NO_POWER.getRegistryName(), 0);
  }

}
