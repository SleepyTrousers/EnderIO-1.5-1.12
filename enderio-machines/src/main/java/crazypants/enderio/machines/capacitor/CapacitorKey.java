package crazypants.enderio.machines.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.capacitor.CapacitorHelper.SetType;
import crazypants.enderio.base.capacitor.CapacitorKeyType;
import crazypants.enderio.base.capacitor.ICapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.Scaler;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.loot.WeightedUpgrade;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public enum CapacitorKey implements ICapacitorKey {

  SIMPLE_ALLOY_SMELTER_POWER_INTAKE(MachineObject.block_simple_alloy_smelter, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  SIMPLE_ALLOY_SMELTER_POWER_BUFFER(MachineObject.block_simple_alloy_smelter, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  SIMPLE_ALLOY_SMELTER_POWER_USE(MachineObject.block_simple_alloy_smelter, CapacitorKeyType.ENERGY_USE, "use"),
  SIMPLE_ALLOY_SMELTER_POWER_LOSS(MachineObject.block_simple_alloy_smelter, CapacitorKeyType.ENERGY_LOSS, "loss"),

  ALLOY_SMELTER_POWER_INTAKE(MachineObject.block_alloy_smelter, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  ALLOY_SMELTER_POWER_BUFFER(MachineObject.block_alloy_smelter, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  ALLOY_SMELTER_POWER_USE(MachineObject.block_alloy_smelter, CapacitorKeyType.ENERGY_USE, "use"),

  ENHANCED_ALLOY_SMELTER_POWER_INTAKE(MachineObject.block_enhanced_alloy_smelter, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  ENHANCED_ALLOY_SMELTER_POWER_BUFFER(MachineObject.block_enhanced_alloy_smelter, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  ENHANCED_ALLOY_SMELTER_POWER_USE(MachineObject.block_enhanced_alloy_smelter, CapacitorKeyType.ENERGY_USE, "use"),
  ENHANCED_ALLOY_SMELTER_POWER_EFFICIENCY(MachineObject.block_enhanced_alloy_smelter, CapacitorKeyType.ENERGY_BUFFER, "efficiency"),

  CREATIVE_BUFFER_POWER_INTAKE(MachineObject.block_buffer, CapacitorKeyType.ENERGY_INTAKE, "intake_creative"),
  CREATIVE_BUFFER_POWER_BUFFER(MachineObject.block_buffer, CapacitorKeyType.ENERGY_BUFFER, "buffer_creative"),
  CREATIVE_BUFFER_POWER_USE(MachineObject.block_buffer, CapacitorKeyType.ENERGY_USE, "use_creative"),

  BUFFER_POWER_INTAKE(MachineObject.block_buffer, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  BUFFER_POWER_BUFFER(MachineObject.block_buffer, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  BUFFER_POWER_USE(MachineObject.block_buffer, CapacitorKeyType.ENERGY_USE, "use"),

  FARM_POWER_INTAKE(MachineObject.block_farm_station, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  FARM_POWER_BUFFER(MachineObject.block_farm_station, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  FARM_POWER_USE(MachineObject.block_farm_station, CapacitorKeyType.ENERGY_USE, "use"),
  FARM_BASE_SIZE(MachineObject.block_farm_station, CapacitorKeyType.AREA, "base_size"),
  FARM_BONUS_SIZE(MachineObject.block_farm_station, CapacitorKeyType.AREA, "bonus_size"),
  FARM_STACK_LIMIT(MachineObject.block_farm_station, CapacitorKeyType.AMOUNT, "stacksize_limit"),

  COMBUSTION_POWER_LOSS(MachineObject.block_combustion_generator, CapacitorKeyType.ENERGY_LOSS, "loss"),
  COMBUSTION_POWER_BUFFER(MachineObject.block_combustion_generator, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  COMBUSTION_POWER_GEN(MachineObject.block_combustion_generator, CapacitorKeyType.ENERGY_GEN, "gen"),
  COMBUSTION_POWER_EFFICIENCY(MachineObject.block_combustion_generator, CapacitorKeyType.AMOUNT, "efficiency"),

  ENHANCED_COMBUSTION_POWER_LOSS(MachineObject.block_enhanced_combustion_generator, CapacitorKeyType.ENERGY_LOSS, "loss"),
  ENHANCED_COMBUSTION_POWER_BUFFER(MachineObject.block_enhanced_combustion_generator, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  ENHANCED_COMBUSTION_POWER_GEN(MachineObject.block_enhanced_combustion_generator, CapacitorKeyType.ENERGY_GEN, "gen"),
  ENHANCED_COMBUSTION_POWER_EFFICIENCY(MachineObject.block_enhanced_combustion_generator, CapacitorKeyType.AMOUNT, "efficiency"),

  STIRLING_POWER_LOSS(MachineObject.block_stirling_generator, CapacitorKeyType.ENERGY_LOSS, "loss"),
  STIRLING_POWER_BUFFER(MachineObject.block_stirling_generator, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  STIRLING_POWER_GEN(MachineObject.block_stirling_generator, CapacitorKeyType.ENERGY_GEN, "gen"),
  STIRLING_POWER_EFFICIENCY(MachineObject.block_stirling_generator, CapacitorKeyType.SPEED, "efficiency"),

  SIMPLE_STIRLING_POWER_LOSS(MachineObject.block_simple_stirling_generator, CapacitorKeyType.ENERGY_LOSS, "loss"),
  SIMPLE_STIRLING_POWER_BUFFER(MachineObject.block_simple_stirling_generator, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  SIMPLE_STIRLING_POWER_GEN(MachineObject.block_simple_stirling_generator, CapacitorKeyType.ENERGY_GEN, "gen"),

  ZOMBIE_POWER_LOSS(MachineObject.block_zombie_generator, CapacitorKeyType.ENERGY_LOSS, "loss"),
  ZOMBIE_POWER_BUFFER(MachineObject.block_zombie_generator, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  ZOMBIE_POWER_GEN(MachineObject.block_zombie_generator, CapacitorKeyType.ENERGY_GEN, "gen"),

  FRANK_N_ZOMBIE_POWER_LOSS(MachineObject.block_franken_zombie_generator, CapacitorKeyType.ENERGY_LOSS, "loss"),
  FRANK_N_ZOMBIE_POWER_BUFFER(MachineObject.block_franken_zombie_generator, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  FRANK_N_ZOMBIE_POWER_GEN(MachineObject.block_franken_zombie_generator, CapacitorKeyType.ENERGY_GEN, "gen"),

  ENDER_POWER_LOSS(MachineObject.block_ender_generator, CapacitorKeyType.ENERGY_LOSS, "loss"),
  ENDER_POWER_BUFFER(MachineObject.block_ender_generator, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  ENDER_POWER_GEN(MachineObject.block_ender_generator, CapacitorKeyType.ENERGY_GEN, "gen"),

  ATTRACTOR_POWER_INTAKE(MachineObject.block_attractor_obelisk, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  ATTRACTOR_POWER_BUFFER(MachineObject.block_attractor_obelisk, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  ATTRACTOR_POWER_USE(MachineObject.block_attractor_obelisk, CapacitorKeyType.ENERGY_USE, "use"),
  ATTRACTOR_RANGE(MachineObject.block_attractor_obelisk, CapacitorKeyType.AREA, "range"),

  AVERSION_POWER_INTAKE(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  AVERSION_POWER_BUFFER(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  AVERSION_POWER_USE(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_USE, "use"),
  AVERSION_RANGE(MachineObject.block_aversion_obelisk, CapacitorKeyType.AREA, "range"),

  INHIBITOR_POWER_INTAKE(MachineObject.block_inhibitor_obelisk, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  INHIBITOR_POWER_BUFFER(MachineObject.block_inhibitor_obelisk, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  INHIBITOR_POWER_USE(MachineObject.block_inhibitor_obelisk, CapacitorKeyType.ENERGY_USE, "use"),
  INHIBITOR_RANGE(MachineObject.block_inhibitor_obelisk, CapacitorKeyType.AREA, "range"),

  RELOCATOR_POWER_INTAKE(MachineObject.block_relocator_obelisk, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  RELOCATOR_POWER_BUFFER(MachineObject.block_relocator_obelisk, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  RELOCATOR_POWER_USE(MachineObject.block_relocator_obelisk, CapacitorKeyType.ENERGY_USE, "use"),
  RELOCATOR_RANGE(MachineObject.block_relocator_obelisk, CapacitorKeyType.AREA, "range"),

  WEATHER_POWER_INTAKE(MachineObject.block_weather_obelisk, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  WEATHER_POWER_BUFFER(MachineObject.block_weather_obelisk, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  WEATHER_POWER_USE(MachineObject.block_weather_obelisk, CapacitorKeyType.ENERGY_USE, "use"),
  WEATHER_POWER_FLUID_USE(MachineObject.block_weather_obelisk, CapacitorKeyType.ENERGY_USE, "fluid_use"),

  PAINTER_POWER_INTAKE(MachineObject.block_painter, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  PAINTER_POWER_BUFFER(MachineObject.block_painter, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  PAINTER_POWER_USE(MachineObject.block_painter, CapacitorKeyType.ENERGY_USE, "use"),

  SAG_MILL_POWER_INTAKE(MachineObject.block_sag_mill, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  SAG_MILL_POWER_BUFFER(MachineObject.block_sag_mill, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  SAG_MILL_POWER_USE(MachineObject.block_sag_mill, CapacitorKeyType.ENERGY_USE, "use"),

  SIMPLE_SAG_MILL_POWER_INTAKE(MachineObject.block_simple_sag_mill, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  SIMPLE_SAG_MILL_POWER_BUFFER(MachineObject.block_simple_sag_mill, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  SIMPLE_SAG_MILL_POWER_USE(MachineObject.block_simple_sag_mill, CapacitorKeyType.ENERGY_USE, "use"),
  SIMPLE_SAG_MILL_POWER_LOSS(MachineObject.block_simple_sag_mill, CapacitorKeyType.ENERGY_LOSS, "loss"),

  ENHANCED_SAG_MILL_POWER_INTAKE(MachineObject.block_enhanced_sag_mill, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  ENHANCED_SAG_MILL_POWER_BUFFER(MachineObject.block_enhanced_sag_mill, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  ENHANCED_SAG_MILL_POWER_USE(MachineObject.block_enhanced_sag_mill, CapacitorKeyType.ENERGY_USE, "use"),
  ENHANCED_SAG_MILL_POWER_EFFICIENCY(MachineObject.block_enhanced_sag_mill, CapacitorKeyType.ENERGY_EFFICIENCY, "efficiency"),

  SLICE_POWER_INTAKE(MachineObject.block_slice_and_splice, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  SLICE_POWER_BUFFER(MachineObject.block_slice_and_splice, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  SLICE_POWER_USE(MachineObject.block_slice_and_splice, CapacitorKeyType.ENERGY_USE, "use"),

  SOUL_BINDER_POWER_INTAKE(MachineObject.block_soul_binder, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  SOUL_BINDER_POWER_BUFFER(MachineObject.block_soul_binder, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  SOUL_BINDER_POWER_USE(MachineObject.block_soul_binder, CapacitorKeyType.ENERGY_USE, "use"),
  SOUL_BINDER_SOUND_PITCH(MachineObject.block_soul_binder, CapacitorKeyType.AMOUNT, "pitch"),

  SPAWNER_POWER_INTAKE(MachineObject.block_powered_spawner, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  SPAWNER_POWER_BUFFER(MachineObject.block_powered_spawner, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  SPAWNER_POWER_USE(MachineObject.block_powered_spawner, CapacitorKeyType.ENERGY_USE, "use"),
  SPAWNER_SPEEDUP(MachineObject.block_powered_spawner, CapacitorKeyType.SPEED, "speed"),

  TRANSCEIVER_POWER_INTAKE(MachineObject.block_transceiver, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  TRANSCEIVER_POWER_BUFFER(MachineObject.block_transceiver, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  TRANSCEIVER_POWER_USE(MachineObject.block_transceiver, CapacitorKeyType.ENERGY_USE, "use"),

  VAT_POWER_INTAKE(MachineObject.block_vat, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  VAT_POWER_BUFFER(MachineObject.block_vat, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  VAT_POWER_USE(MachineObject.block_vat, CapacitorKeyType.ENERGY_USE, "use"),

  ENHANCED_VAT_POWER_INTAKE(MachineObject.block_enhanced_vat, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  ENHANCED_VAT_POWER_BUFFER(MachineObject.block_enhanced_vat, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  ENHANCED_VAT_POWER_USE(MachineObject.block_enhanced_vat, CapacitorKeyType.ENERGY_USE, "use"),
  ENHANCED_VAT_POWER_EFFICIENCY(MachineObject.block_enhanced_vat, CapacitorKeyType.ENERGY_EFFICIENCY, "efficiency"),

  WIRED_POWER_INTAKE(MachineObject.block_wired_charger, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  WIRED_POWER_BUFFER(MachineObject.block_wired_charger, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  WIRED_POWER_CHARGE(MachineObject.block_wired_charger, CapacitorKeyType.ENERGY_USE, "charge"),

  ENHANCED_WIRED_POWER_INTAKE(MachineObject.block_enhanced_wired_charger, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  ENHANCED_WIRED_POWER_BUFFER(MachineObject.block_enhanced_wired_charger, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  ENHANCED_WIRED_POWER_CHARGE(MachineObject.block_enhanced_wired_charger, CapacitorKeyType.ENERGY_USE, "charge"),

  SIMPLE_WIRED_POWER_INTAKE(MachineObject.block_simple_wired_charger, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  SIMPLE_WIRED_POWER_BUFFER(MachineObject.block_simple_wired_charger, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  SIMPLE_WIRED_POWER_USE(MachineObject.block_simple_wired_charger, CapacitorKeyType.ENERGY_USE, "use"),
  SIMPLE_WIRED_POWER_LOSS(MachineObject.block_simple_wired_charger, CapacitorKeyType.ENERGY_LOSS, "loss"),

  WIRELESS_POWER_INTAKE(MachineObject.block_wireless_charger, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  WIRELESS_POWER_BUFFER(MachineObject.block_wireless_charger, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  WIRELESS_POWER_OUTPUT(MachineObject.block_wireless_charger, CapacitorKeyType.ENERGY_USE, "charge"),

  DIALING_DEVICE_POWER_INTAKE(MachineObject.block_dialing_device, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  DIALING_DEVICE_POWER_BUFFER(MachineObject.block_dialing_device, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  DIALING_DEVICE_POWER_USE(MachineObject.block_dialing_device, CapacitorKeyType.ENERGY_USE, "use"),
  DIALING_DEVICE_POWER_USE_TELEPORT(MachineObject.block_dialing_device, CapacitorKeyType.ENERGY_USE, "use_teleport"),
  DIALING_DEVICE_POWER_USE_PAPER(MachineObject.block_dialing_device, CapacitorKeyType.ENERGY_USE, "use_paper"),

  IMPULSE_HOPPER_POWER_INTAKE(MachineObject.block_impulse_hopper, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  IMPULSE_HOPPER_POWER_BUFFER(MachineObject.block_impulse_hopper, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  IMPULSE_HOPPER_POWER_USE(MachineObject.block_impulse_hopper, CapacitorKeyType.ENERGY_USE, "use"),
  IMPULSE_HOPPER_POWER_USE_PER_ITEM(MachineObject.block_impulse_hopper, CapacitorKeyType.ENERGY_USE, "user_item"),
  IMPULSE_HOPPER_SPEED(MachineObject.block_impulse_hopper, CapacitorKeyType.SPEED, "speed"),

  CRAFTER_POWER_INTAKE(MachineObject.block_crafter, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  CRAFTER_POWER_BUFFER(MachineObject.block_crafter, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  CRAFTER_POWER_USE(MachineObject.block_crafter, CapacitorKeyType.ENERGY_USE, "use"),
  CRAFTER_POWER_CRAFT(MachineObject.block_crafter, CapacitorKeyType.ENERGY_USE, "use_craft"),
  CRAFTER_SPEED(MachineObject.block_crafter, CapacitorKeyType.SPEED, "speed"),

  SIMPLE_CRAFTER_POWER_INTAKE(MachineObject.block_simple_crafter, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  SIMPLE_CRAFTER_POWER_BUFFER(MachineObject.block_simple_crafter, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  SIMPLE_CRAFTER_POWER_USE(MachineObject.block_simple_crafter, CapacitorKeyType.ENERGY_USE, "use"),
  SIMPLE_CRAFTER_POWER_CRAFT(MachineObject.block_simple_crafter, CapacitorKeyType.ENERGY_USE, "use_craft"),
  SIMPLE_CRAFTER_SPEED(MachineObject.block_simple_crafter, CapacitorKeyType.SPEED, "speed"),

  TELEPAD_POWER_INTAKE(MachineObject.block_tele_pad, CapacitorKeyType.ENERGY_INTAKE, "intake"),
  TELEPAD_POWER_BUFFER(MachineObject.block_tele_pad, CapacitorKeyType.ENERGY_BUFFER, "buffer"),
  TELEPAD_POWER_USE(MachineObject.block_tele_pad, CapacitorKeyType.ENERGY_USE, "use"),

  //
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

  static {
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, ALLOY_SMELTER_POWER_USE, "smelting", 10);
    WeightedUpgrade.registerWeightedUpgrade(SetType.TYPE, ATTRACTOR_RANGE, "area", 5);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, FARM_BONUS_SIZE, "green", 10);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, STIRLING_POWER_GEN, "red", 10);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, SPAWNER_SPEEDUP, "mobby", 5);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, SAG_MILL_POWER_USE, "crushed", 15);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, SLICE_POWER_USE, "cleancut", 5);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, SOUL_BINDER_POWER_USE, "tight", 5);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, PAINTER_POWER_USE, "aa", 10);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, VAT_POWER_USE, "wet", 8);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, COMBUSTION_POWER_GEN, "kaboom", 20);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, ENHANCED_COMBUSTION_POWER_GEN, "fatman", 10);
  }

}