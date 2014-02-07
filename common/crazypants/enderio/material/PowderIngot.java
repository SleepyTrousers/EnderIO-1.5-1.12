package crazypants.enderio.material;


public enum PowderIngot {

  POWDER_COAL("powderCoal"),
  POWDER_IRON("powderIron"),
  POWDER_GOLD("powderGold"),
  POWDER_COPPER("powderCopper"),
  POWDER_TIN("powderTin"),
  POWDER_ENDER("powderEnder");
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

  private PowderIngot(String unlocalisedName) {
    this.unlocalisedName = "enderio." + unlocalisedName;
    this.iconKey = "enderio:" + unlocalisedName;
  }

}
