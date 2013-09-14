package crazypants.enderio.material;

public enum PowderIngot {

  POWDER_COAL("powderCoal","Coal Powder","powderCoal"),
  POWDER_IRON("powderIron", "Iron Powder", "powderIron"),
  POWDER_GOLD("powderGold", "Gold Powder", "powderGold"),
  POWDER_COPPER("powderCopper", "Copper Powder", "powderCopper"),
  POWDER_TIN("powderTin", "Tin Powder", "powderTin"),
  POWDER_LEAD("powderLead", "Lead Powder", "powderLead"),
  POWDER_SILVER("powderSilver", "Silver Powder", "powderSilver"),
  POWDER_BRONZE("powderBronze", "Bronze Powder", "powderBronze"),
  INGOT_COPPER("ingotCopper", "Copper Ingot", "ingotCopper"),
  INGOT_TIN("ingotTin", "Tin Ingot", "ingotTin"),
  INGOT_LEAD("ingotLead", "Lead Ingot", "ingotLead"),
  INGOT_SILVER("ingotSilver", "Silver Ingot", "ingotSilver"),
  INGOT_BRONZE("ingotBronze", "Bronze Ingot", "ingotBronze"),
  INGOT_ELECTRUM("ingotElectrum", "Electrum Ingot", "ingotElectrum");
  
  public final String unlocalisedName;
  public final String uiName;
  public final String iconKey;
    
  private PowderIngot(String unlocalisedName, String uiName, String iconKey) {
    this.unlocalisedName = unlocalisedName;
    this.uiName = uiName;
    this.iconKey = "enderio:" + iconKey;
  }
  
  
}
