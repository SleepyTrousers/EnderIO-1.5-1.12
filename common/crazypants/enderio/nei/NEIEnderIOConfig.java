package crazypants.enderio.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.alloy.GuiAlloySmelter;

public class NEIEnderIOConfig implements IConfigureNEI {

  @Override
  public void loadConfig() {
    API.registerRecipeHandler(new AlloySmelterRecipeHandler());
    API.registerRecipeHandler(new SagMillRecipeHandler());
    API.registerUsageHandler(new AlloySmelterRecipeHandler());
    API.registerUsageHandler(new SagMillRecipeHandler());
    API.setGuiOffset(GuiAlloySmelter.class, 5, 3);
    API.hideItem(EnderIO.blockConduitFacade.blockID);
    API.hideItem(EnderIO.itemEnderface.itemID);
    if(!Config.photovoltaicCellEnabled) {
      API.hideItem(EnderIO.blockSolarPanel.blockID);
    }
    if(!Config.travelAnchorEnabled) {
      API.hideItem(EnderIO.itemTravelStaff.itemID);
    }
  }

  @Override
  public String getName() {
    return "Ender IO NEI Plugin";
  }

  @Override
  public String getVersion() {
    return "0.0.1";
  }

}
