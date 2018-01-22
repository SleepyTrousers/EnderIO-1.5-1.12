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

  // TODO 1.12: Sort this list

  MACHINE_CHASSI("machineChassi"),
  CHASSIPARTS("chassiParts"),

  GEAR_WOOD("gear_wood", "gearWood"),
  GEAR_STONE("gear_stone", "gearStone"),
  GEAR_IRON("gear_iron", "gearIron"),
  GEAR_ENERGIZED("gear_energized", "gearEnergized"),
  GEAR_VIBRANT("gear_vibrant", "gearVibrant"),

  GLIDER_WING("gliderWing"),
  GLIDER_WINGS("gliderWings"),

  DARK_GRINDING_BALL("darkGrindingBall"),
  SILICON("silicon"),
  CONDUIT_BINDER("conduitBinder"),
  BINDER_COMPOSITE("binderComposite"),

  PULSATING_CYSTAL("pulsatingCrystal", true),
  VIBRANT_CYSTAL("vibrantCrystal", true),
  ENDER_CRYSTAL("enderCrystal", true),
  ATTRACTOR_CRYSTAL("attractorCrystal", true),
  WEATHER_CRYSTAL("weatherCrystal", true),
  PRECIENT_CRYSTAL("precientCrystal", true),
  PRECIENT_POWDER("precientPowder", true),
  VIBRANT_POWDER("vibrantPowder", true),
  PULSATING_POWDER("pulsatingPowder", true),
  ENDER_CYSTAL_POWDER("enderCrystalPowder", true),
  NUTRITIOUS_STICK("nutritiousStick", false),

  PLANTGREEN("plantgreen", false),
  PLANTBROWN("plantbrown", false),

  POWDER_COAL("powder_coal", "dustCoal"),
  POWDER_IRON("powder_iron", "dustIron"),
  POWDER_GOLD("powder_gold", "dustGold"),
  POWDER_COPPER("powder_copper", "dustCopper", "ingotCopper"),
  POWDER_TIN("powder_tin", "dustTin", "ingotTin"),
  POWDER_ENDER("powder_ender", "nuggetEnderpearl"), // "nugget" because it is 1/9th pearl
  POWDER_OBSIDIAN("powder_obsidian", "dustObsidian"),
  POWDER_ARDITE("powder_ardite", "dustArdite", "oreArdite"),
  POWDER_COBALT("powder_cobalt", "dustCobalt", "oreCobalt"),
  POWDER_INFINITY("powder_infinity", "dustBedrock"),

  INGOT_ENDERIUM_BASE("ingot_enderium_base", "ingotEnderiumBase", "ingotEnderium"),
  FLOUR("dust_wheat", "dustWheat"),

  ZOMBIE_ELECTRODE("skullZombieElectrode", "skullZombieElectrode"),
  ZOMBIE_CONTROLLER("skullZombieController", "skullZombieController"),
  FRANKEN_ZOMBIE("skullZombieFrankenstien", "skullZombieFrankenstein", true),
  ENDER_RESONATOR("skullEnderResonator", "skullEnderResonator"),
  SENTIENT_ENDER("skullSentientEnder", "skullSentientEnder", true),
  SKELETAL_CONTRACTOR("skullSkeletalContractor", "skullSkeletalContractor"),

  DYE_GREEN("organic_green_dye", "dyeGreen"),
  DYE_BROWN("organic_brown_dye", "dyeBrown"),

  POWDER_PHOTOVOLTAIC("powderPhotovoltaic", false),
  PLATE_PHOTOVOLTAIC("platePhotovoltaic", false),

  POWDER_LAPIS("powder_lapis_lazuli", "dustLapis"),
  POWDER_QUARTZ("powder_quartz", "dustQuartz"),
  DYE_MACHINE("machine_dye", "dyeMachine"),
  SIMPLE_MACHINE_CHASSI("simpleMachineChassi"),

  DYE_BLACK("organic_black_dye", "dyeBlack"),

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
