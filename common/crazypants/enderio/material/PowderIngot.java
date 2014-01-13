package crazypants.enderio.material;

import crazypants.enderio.EnderIO;

public enum PowderIngot {

  POWDER_COAL("powderCoal", "powderCoal"),
  POWDER_IRON("powderIron", "powderIron"),
  POWDER_GOLD("powderGold", "powderGold"),
  POWDER_COPPER("powderCopper", "powderCopper"),
  POWDER_TIN("powderTin", "powderTin"),
  POWDER_ENDER("powderEnder", "powderEnder");
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
  public final String uiName;
  public final String iconKey;

  private PowderIngot(String unlocalisedName, String iconKey) {
    this.unlocalisedName = unlocalisedName;
    this.uiName = EnderIO.localize(unlocalisedName);
    this.iconKey = "enderio:" + iconKey;
  }

}
