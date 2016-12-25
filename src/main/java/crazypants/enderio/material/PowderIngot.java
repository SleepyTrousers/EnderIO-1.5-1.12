package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import static com.enderio.core.common.util.OreDictionaryHelper.INGOT_COPPER;
import static com.enderio.core.common.util.OreDictionaryHelper.INGOT_ENDERIUM;
import static com.enderio.core.common.util.OreDictionaryHelper.INGOT_TIN;
import static com.enderio.core.common.util.OreDictionaryHelper.isRegistered;

public enum PowderIngot {

  POWDER_COAL("powderCoal", null, "dustCoal"), //
  POWDER_IRON("powderIron", null, "dustIron"), //
  POWDER_GOLD("powderGold", null, "dustGold"), //
  POWDER_COPPER("powderCopper", INGOT_COPPER, "dustCopper"), //
  POWDER_TIN("powderTin", INGOT_TIN, "dustTin"), //
  POWDER_ENDER("powderEnder", "nuggetEnderpearl", "nuggetEnderpearl", true), // "nugget" because it is 1/9th pearl
  INGOT_ENDERIUM_BASE("ingotEnderiumBase", INGOT_ENDERIUM, "ingotEnderiumBase"), //
  POWDER_OBSIDIAN("powderObsidian", null, "dustObsidian"), //
  FLOUR("dustWheat", null), //
  POWDER_ARDITE("powderArdite", "oreArdite", "dustArdite"), //
  POWDER_COBALT("powderCobalt", "oreCobalt", "dustCobalt");
  // POWDER_LEAD("powderLead", "Lead Powder", "powderLead"),
  // POWDER_SILVER("powderSilver", "Silver Powder", "powderSilver"),
  // POWDER_BRONZE("powderBronze", "Bronze Powder", "powderBronze"),
  // INGOT_COPPER("ingotCopper", "Copper Ingot", "ingotCopper");
  // INGOT_TIN("ingotTin", "Tin Ingot", "ingotTin"),
  // INGOT_LEAD("ingotLead", "Lead Ingot", "ingotLead"),
  // INGOT_SILVER("ingotSilver", "Silver Ingot", "ingotSilver"),
  // INGOT_BRONZE("ingotBronze", "Bronze Ingot", "ingotBronze"),
  // INGOT_ELECTRUM("ingotElectrum", "Electrum Ingot", "ingotElectrum");

  
  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for(PowderIngot c : values()) {
      res.add(new ResourceLocation(c.iconKey));
    }
    return res;
  }
  
  public final String baseName;
  public final String unlocalisedName;
  public final String oreDictName;
  public final String iconKey;
  public final String oreDictDependancy;
  public final boolean reverseDependency;
  public boolean ignoreRuntimeDependencyCheck = false;

  private PowderIngot(String baseName, String oreDictDependancy, String oreDictName, boolean reverseDependency) {
    this.baseName = baseName;
    this.unlocalisedName = "enderio." + baseName;
    iconKey = "enderio:" + baseName;
    this.oreDictName = oreDictName;
    this.oreDictDependancy = oreDictDependancy;
    this.reverseDependency = reverseDependency;
  }

  private PowderIngot(String unlocalisedName, String oreDictDependancy, boolean reverseDependency) {
    this(unlocalisedName, oreDictDependancy, unlocalisedName, reverseDependency);
  }

  private PowderIngot(String unlocalisedName, String oreDictDependancy, String oreDictName) {
    this(unlocalisedName, oreDictDependancy, oreDictName, false);
  }

  private PowderIngot(String unlocalisedName, String oreDictDependancy) {
    this(unlocalisedName, oreDictDependancy, unlocalisedName, false);
  }

  public boolean isDependancyMet() {
    if (oreDictDependancy == null || ignoreRuntimeDependencyCheck) {
      return true;
    }
    return isRegistered(oreDictDependancy) == !reverseDependency;
  }

  public boolean hasDependancy() {
    return oreDictDependancy != null;
  }

  public void setRegistered() {
    ignoreRuntimeDependencyCheck = true;
  }
}
