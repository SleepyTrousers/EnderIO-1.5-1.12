package crazypants.enderio.material;

import static crazypants.util.OreDictionaryHelper.DUST_ENDERPEARL;
import static crazypants.util.OreDictionaryHelper.INGOT_COPPER;
import static crazypants.util.OreDictionaryHelper.INGOT_TIN;
import static crazypants.util.OreDictionaryHelper.isRegistered;


public enum PowderIngot {

  POWDER_COAL("powderCoal", null),
  POWDER_IRON("powderIron", null),
  POWDER_GOLD("powderGold", null),
  POWDER_COPPER("powderCopper", INGOT_COPPER),
  POWDER_TIN("powderTin", INGOT_TIN),
  POWDER_ENDER("powderEnder", DUST_ENDERPEARL);
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

  private PowderIngot(String unlocalisedName, String oreDictDependancy) {
    this.unlocalisedName = "enderio." + unlocalisedName;
    iconKey = "enderio:" + unlocalisedName;
    this.oreDictDependancy = oreDictDependancy;
  }

  public boolean isDependancyMet() {
    if(oreDictDependancy == null) {
      return true;
    }
    return isRegistered(oreDictDependancy);
  }

}
