package crazypants.enderio.nei;

import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;

public class NEIEnderIOConfig implements IConfigureNEI {

  @Override
  public void loadConfig() {
    API.registerRecipeHandler(new AlloySmelterRecipeHandler());
    API.registerRecipeHandler(new SagMillRecipeHandler());
    API.registerUsageHandler(new AlloySmelterRecipeHandler());
    API.registerUsageHandler(new SagMillRecipeHandler());

    API.registerRecipeHandler(new VatRecipeHandler());
    API.registerUsageHandler(new VatRecipeHandler());

    API.hideItem(new ItemStack(EnderIO.blockConduitFacade));
    API.hideItem(new ItemStack(EnderIO.itemEnderface));
    API.hideItem(new ItemStack(EnderIO.blockFarmStation));
    if(!Config.photovoltaicCellEnabled) {
      API.hideItem(new ItemStack(EnderIO.blockSolarPanel));
    }
    if(!Config.travelAnchorEnabled) {
      API.hideItem(new ItemStack(EnderIO.itemTravelStaff));
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
