package crazypants.enderio.material;

import static com.enderio.core.common.util.OreDictionaryHelper.*;

public enum PowderIngot {

  POWDER_COAL("powderCoal", null),
  POWDER_IRON("powderIron", null),
  POWDER_GOLD("powderGold", null),
  POWDER_COPPER("powderCopper", INGOT_COPPER),
  POWDER_TIN("powderTin", INGOT_TIN),
 POWDER_ENDER("powderEnder", DUST_ENDERPEARL, true),
  INGOT_ENDERIUM_BASE("ingotEnderiumBase", INGOT_ENDERIUM),
  POWDER_OBSIDIAN("powderObsidian", null),
  FLOUR("dustWheat", null);
  // POWDER_LEAD("powderLead", "Lead Powder", "powderLead"),
  // POWDER_SILVER("powderSilver", "Silver Powder", "powderSilver"),
  // POWDER_BRONZE("powderBronze", "Bronze Powder", "powderBronze"),
  // INGOT_COPPER("ingotCopper", "Copper Ingot", "ingotCopper");
  // INGOT_TIN("ingotTin", "Tin Ingot", "ingotTin"),
  // INGOT_LEAD("ingotLead", "Lead Ingot", "ingotLead"),
  // INGOT_SILVER("ingotSilver", "Silver Ingot", "ingotSilver"),
  // INGOT_BRONZE("ingotBronze", "Bronze Ingot", "ingotBronze"),
  // INGOT_ELECTRUM("ingotElectrum", "Electrum Ingot", "ingotElectrum");

  public final String unlocalisedName;
  public final String iconKey;
  public final String oreDictDependancy;
  public final boolean reverseDependency;
  public boolean ignoreRuntimeDependencyCheck = false;

  private PowderIngot(String unlocalisedName, String oreDictDependancy, boolean reverseDependency) {
    this.unlocalisedName = "enderio." + unlocalisedName;
    iconKey = "enderio:" + unlocalisedName;
    this.oreDictDependancy = oreDictDependancy;
    this.reverseDependency = reverseDependency;
  }

  private PowderIngot(String unlocalisedName, String oreDictDependancy) {
    this(unlocalisedName, oreDictDependancy, false);
  }

  public boolean isDependancyMet() {
    if (oreDictDependancy == null || ignoreRuntimeDependencyCheck) {
      return true;
    }
    return isRegistered(oreDictDependancy) == !reverseDependency;
  }

}
