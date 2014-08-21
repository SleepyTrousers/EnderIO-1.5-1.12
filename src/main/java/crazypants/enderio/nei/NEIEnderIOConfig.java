package crazypants.enderio.nei;

import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;

public class NEIEnderIOConfig implements IConfigureNEI {

  @Override
  public void loadConfig() {
    API.registerRecipeHandler(new AlloySmelterRecipeHandler());
    API.registerRecipeHandler(new SagMillRecipeHandler());
    API.registerUsageHandler(new AlloySmelterRecipeHandler());
    API.registerUsageHandler(new SagMillRecipeHandler());

    API.registerRecipeHandler(new VatRecipeHandler());
    API.registerUsageHandler(new VatRecipeHandler());
    
    API.registerRecipeHandler(new EnchanterRecipeHandler());
    API.registerUsageHandler(new EnchanterRecipeHandler());

    API.hideItem(new ItemStack(EnderIO.blockConduitFacade));
    API.hideItem(new ItemStack(EnderIO.itemEnderface));

    if(!Config.photovoltaicCellEnabled) {
      API.hideItem(new ItemStack(EnderIO.blockSolarPanel));
    }
    if(!Config.travelAnchorEnabled) {
      API.hideItem(new ItemStack(EnderIO.itemTravelStaff));
    }
    if(!Config.reinforcedObsidianEnabled) {
      API.hideItem(new ItemStack(EnderIO.blockReinforcedObsidian));
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
