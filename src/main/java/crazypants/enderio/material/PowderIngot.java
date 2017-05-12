package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.EnderIO;
import net.minecraft.util.ResourceLocation;

import static com.enderio.core.common.util.OreDictionaryHelper.INGOT_COPPER;
import static com.enderio.core.common.util.OreDictionaryHelper.INGOT_ENDERIUM;
import static com.enderio.core.common.util.OreDictionaryHelper.INGOT_TIN;
import static com.enderio.core.common.util.OreDictionaryHelper.isRegistered;

public enum PowderIngot {

  POWDER_COAL("powder_coal", null, "dustCoal"), //
  POWDER_IRON("powder_iron", null, "dustIron"), //
  POWDER_GOLD("powder_gold", null, "dustGold"), //
  POWDER_COPPER("powder_copper", INGOT_COPPER, "dustCopper"), //
  POWDER_TIN("powder_tin", INGOT_TIN, "dustTin"), //
  POWDER_ENDER("powder_ender", "nuggetEnderpearl", "nuggetEnderpearl", true), // "nugget" because it is 1/9th pearl
  INGOT_ENDERIUM_BASE("ingot_enderium_base", INGOT_ENDERIUM, "ingotEnderiumBase"), //
  POWDER_OBSIDIAN("powder_obsidian", null, "dustObsidian"), //
  FLOUR("dust_wheat", null, "dustWheat"), //
  POWDER_ARDITE("powder_ardite", "oreArdite", "dustArdite"), //
  POWDER_COBALT("powder_cobalt", "oreCobalt", "dustCobalt"),
  POWDER_INFINITY("powder_infinity", null, "dustBedrock"), //
  // POWDER_LEAD("powderLead", "Lead Powder", "powderLead"),
  // POWDER_SILVER("powderSilver", "Silver Powder", "powderSilver"),
  // POWDER_BRONZE("powderBronze", "Bronze Powder", "powderBronze"),
  // INGOT_COPPER("ingotCopper", "Copper Ingot", "ingotCopper");
  // INGOT_TIN("ingotTin", "Tin Ingot", "ingotTin"),
  // INGOT_LEAD("ingotLead", "Lead Ingot", "ingotLead"),
  // INGOT_SILVER("ingotSilver", "Silver Ingot", "ingotSilver"),
  // INGOT_BRONZE("ingotBronze", "Bronze Ingot", "ingotBronze"),
  // INGOT_ELECTRUM("ingotElectrum", "Electrum Ingot", "ingotElectrum");
  ;

  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for (PowderIngot c : values()) {
      res.add(c.getIconKey());
    }
    return res;
  }

  final private @Nonnull String baseName;
  final private @Nonnull String unlocalisedName;
  final private @Nullable String oreDictName;
  final private @Nonnull ResourceLocation iconKey;
  final private @Nullable String oreDictDependancy;
  private final boolean reverseDependency;
  private boolean ignoreRuntimeDependencyCheck = false;

  private PowderIngot(@Nonnull String baseName, @Nullable String oreDictDependancy, @Nullable String oreDictName, boolean reverseDependency) {
    this.baseName = baseName;
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = new ResourceLocation(EnderIO.DOMAIN, baseName);
    this.oreDictName = oreDictName;
    this.oreDictDependancy = oreDictDependancy;
    this.reverseDependency = reverseDependency;
  }

  private PowderIngot(@Nonnull String unlocalisedName, @Nullable String oreDictDependancy, boolean reverseDependency) {
    this(unlocalisedName, oreDictDependancy, unlocalisedName, reverseDependency);
  }

  private PowderIngot(@Nonnull String unlocalisedName, @Nullable String oreDictDependancy, @Nullable String oreDictName) {
    this(unlocalisedName, oreDictDependancy, oreDictName, false);
  }

  private PowderIngot(@Nonnull String unlocalisedName, @Nullable String oreDictDependancy) {
    this(unlocalisedName, oreDictDependancy, unlocalisedName, false);
  }

  public boolean isDependancyMet() {
    if (getOreDictDependancy() == null || ignoreRuntimeDependencyCheck) {
      return true;
    }
    return isRegistered(getOreDictDependancy()) == !reverseDependency;
  }

  public boolean hasDependancy() {
    return getOreDictDependancy() != null;
  }

  public void setRegistered() {
    ignoreRuntimeDependencyCheck = true;
  }

  public @Nonnull String getBaseName() {
    return baseName;
  }

  public @Nonnull String getUnlocalisedName() {
    return unlocalisedName;
  }

  public @Nullable String getOreDictName() {
    return oreDictName;
  }

  public @Nonnull ResourceLocation getIconKey() {
    return iconKey;
  }

  public @Nullable String getOreDictDependancy() {
    return oreDictDependancy;
  }
}
