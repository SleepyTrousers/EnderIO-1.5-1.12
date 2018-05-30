package crazypants.enderio.base.material.material;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import static crazypants.enderio.base.init.ModObject.itemMaterial;

public enum Material {

  SIMPLE_MACHINE_CHASSI("simpleMachineChassi"),
  MACHINE_CHASSI("machineChassi"),
  CHASSIPARTS("chassiParts"),

  PLATE_PHOTOVOLTAIC("platePhotovoltaic"),
  CONDUIT_BINDER("conduitBinder"),
  SILICON("silicon"),
  GLIDER_WING("gliderWing"),
  GLIDER_WINGS("gliderWings"),
  NUTRITIOUS_STICK("nutritiousStick"),

  GEAR_WOOD("gear_wood", "gearWood"),
  GEAR_STONE("gear_stone", "gearStone"),
  GEAR_IRON("gear_iron", "gearIronInfinity"),
  GEAR_ENERGIZED("gear_energized", "gearEnergized"),
  GEAR_VIBRANT("gear_vibrant", "gearVibrant"),

  PULSATING_CYSTAL("pulsatingCrystal", true),
  VIBRANT_CYSTAL("vibrantCrystal", true),
  ENDER_CRYSTAL("enderCrystal", true),
  ATTRACTOR_CRYSTAL("attractorCrystal", true),
  WEATHER_CRYSTAL("weatherCrystal", true),
  PRECIENT_CRYSTAL("precientCrystal", true),

  POWDER_INFINITY("powder_infinity", "dustBedrock"),
  POWDER_FLOUR("dust_wheat", "dustWheat"),
  POWDER_BINDER_COMPOSITE("binderComposite"),
  POWDER_COAL("powder_coal", "dustCoal"),
  POWDER_IRON("powder_iron", "dustIron"),
  POWDER_GOLD("powder_gold", "dustGold"),
  POWDER_COPPER("powder_copper", "dustCopper", "ingotCopper"),
  POWDER_TIN("powder_tin", "dustTin", "ingotTin"),
  POWDER_ENDER("powder_ender", "nuggetEnderpearl"), // "nugget" because it is 1/9th pearl
  POWDER_OBSIDIAN("powder_obsidian", "dustObsidian"),
  POWDER_ARDITE("powder_ardite", "dustArdite", "oreArdite"),
  POWDER_COBALT("powder_cobalt", "dustCobalt", "oreCobalt"),
  POWDER_LAPIS("powder_lapis_lazuli", "dustLapis"),
  POWDER_QUARTZ("powder_quartz", "dustNetherQuartz"),
  POWDER_PRECIENT("precientPowder", true),
  POWDER_VIBRANT("vibrantPowder", true),
  POWDER_PULSATING("pulsatingPowder", true),
  POWDER_ENDER_CYSTAL("enderCrystalPowder", true),
  POWDER_PHOTOVOLTAIC("powderPhotovoltaic"),

  INGOT_ENDERIUM_BASE("ingot_enderium_base", "ingotEnderiumBase", "ingotEnderium"),

  ZOMBIE_ELECTRODE("skullZombieElectrode", "skullZombieElectrode"),
  ZOMBIE_CONTROLLER("skullZombieController", "skullZombieController"),
  FRANKEN_ZOMBIE("skullZombieFrankenstien", "skullZombieFrankenstein", true),
  ENDER_RESONATOR("skullEnderResonator", "skullEnderResonator"),
  SENTIENT_ENDER("skullSentientEnder", "skullSentientEnder", true),
  SKELETAL_CONTRACTOR("skullSkeletalContractor", "skullSkeletalContractor"),

  PLANT_GREEN("plantgreen"),
  PLANT_BROWN("plantbrown"),

  DYE_GREEN("organic_green_dye", "dyeGreen"),
  DYE_BROWN("organic_brown_dye", "dyeBrown"),
  DYE_BLACK("organic_black_dye", "dyeBlack"),
  DYE_MACHINE("machine_dye", "dyeMachine"),
  DYE_SOUL_MACHINE("soul_machine_dye", "dyeSoulMachine"),
  SOUL_MACHINE_CHASSIS("soulMachineChassi"),
  ENHANCED_MACHINE_CHASSIS("enhancedMachineChassi"),
  UNSOULED_MACHINE_CHASSIS("unsouledMachineChassi"),

  GUARDIAN_DIODE("skullGuardianDiode", "skullGuardianDiode"),
  GRINDING_BALL_SIGNALUM("grindingBallSignalum"),
  GRINDING_BALL_ENDERIUM("grindingBallEnderium"),
  GRINDING_BALL_LUMIUM("grindingBallLumium"),

  REDSTONE_FILTER_BASE("redstoneFilterBase"),

  POWDER_CONFUSION("confusingDust"),
  SHARD_ENDER("enderFragment"),
  POWDER_WITHERING("witheringDust"),

  REMOTE_AWARENESS_UPGRADE("remoteAwarenessUpgrade"),

  ;

  public final boolean hasEffect;

  private final @Nonnull String baseName;
  private final @Nonnull String oreDict;
  private final String dependency;

  private Material(@Nonnull String baseName) {
    this(baseName, "item" + StringUtils.capitalize(baseName), false);
  }

  private Material(@Nonnull String baseName, @Nonnull String oreDict) {
    this(baseName, oreDict, false);
  }

  private Material(@Nonnull String baseName, boolean hasEffect) {
    this(baseName, "item" + StringUtils.capitalize(baseName), hasEffect);
  }

  private Material(@Nonnull String baseName, @Nonnull String oreDict, boolean hasEffect) {
    this(baseName, oreDict, hasEffect, null);
  }

  private Material(@Nonnull String baseName, @Nonnull String oreDict, @Nullable String dependency) {
    this(baseName, oreDict, false, dependency);
  }

  private Material(@Nonnull String baseName, @Nonnull String oreDict, boolean hasEffect, @Nullable String dependency) {
    this.baseName = baseName.replaceAll("([A-Z])", "_$0").toLowerCase(Locale.ENGLISH);
    this.oreDict = oreDict;
    this.hasEffect = hasEffect;
    this.dependency = dependency;
  }

  public @Nonnull String getBaseName() {
    return baseName;
  }

  /**
   * 
   * @return The {@link OreDictionary} name of this material.
   */
  public @Nonnull String getOreDict() {
    return oreDict;
  }

  public static @Nonnull Material getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(@Nonnull Material value) {
    return value.ordinal();
  }

  public @Nonnull ItemStack getStack() {
    return getStack(1);
  }

  public @Nonnull ItemStack getStack(int size) {
    return new ItemStack(itemMaterial.getItemNN(), size, ordinal());
  }

  public boolean isDependencyMet() {
    return dependency == null || !OreDictionary.getOres(dependency).isEmpty();
  }

  public boolean hasDependency() {
    return dependency != null;
  }

  public static @Nonnull NNList<Material> getActiveMaterials() {
    NNList<Material> result = new NNList<Material>();
    NNIterator<Material> iterator = NNList.of(Material.class).iterator();
    while (iterator.hasNext()) {
      Material material = iterator.next();
      if (material.isDependencyMet()) {
        result.add(material);
      }
    }
    return result;
  }

}
