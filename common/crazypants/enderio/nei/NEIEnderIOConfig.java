package crazypants.enderio.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.alloy.GuiAlloySmelter;

public class NEIEnderIOConfig implements IConfigureNEI {

  @Override
  public void loadConfig() {
    API.registerRecipeHandler(new AlloySmelterRecipeHandler());
    API.registerRecipeHandler(new SagMillRecipeHandler());
    API.setGuiOffset(GuiAlloySmelter.class, 50, 50);
    API.hideItem(EnderIO.blockConduitFacade.blockID);
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
