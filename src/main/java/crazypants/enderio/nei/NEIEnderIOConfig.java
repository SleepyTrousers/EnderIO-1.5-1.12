package crazypants.enderio.nei;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.gas.GasUtil;
import crazypants.enderio.config.Config;
import crazypants.enderio.init.EIOBlocks;
import crazypants.enderio.init.EIOItems;
import crazypants.enderio.machine.painter.PainterUtil;

public class NEIEnderIOConfig implements IConfigureNEI {

  @Override
  public void loadConfig() {
    API.registerRecipeHandler(new AlloySmelterRecipeHandler());
    API.registerUsageHandler(new AlloySmelterRecipeHandler());

    API.registerRecipeHandler(new SagMillRecipeHandler());
    API.registerUsageHandler(new SagMillRecipeHandler());

    API.registerRecipeHandler(new VatRecipeHandler());
    API.registerUsageHandler(new VatRecipeHandler());

    API.registerRecipeHandler(new EnchanterRecipeHandler());
    API.registerUsageHandler(new EnchanterRecipeHandler());

    API.registerRecipeHandler(new SliceAndSpliceRecipeHandler());
    API.registerUsageHandler(new SliceAndSpliceRecipeHandler());

    API.registerRecipeHandler(new SoulBinderRecipeHandler());
    API.registerUsageHandler(new SoulBinderRecipeHandler());

    API.hideItem(new ItemStack(EIOBlocks.blockConduitFacade));
    API.hideItem(new ItemStack(EIOItems.itemEnderface));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EIOBlocks.blockPaintedCarpet)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EIOBlocks.blockPaintedSlab)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EIOBlocks.blockPaintedFence)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EIOBlocks.blockPaintedFenceGate)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EIOBlocks.blockPaintedGlowstone)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EIOBlocks.blockPaintedStair)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EIOBlocks.blockPaintedWall)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EIOBlocks.blockPaintedDoubleSlab)));

    if(!Config.photovoltaicCellEnabled) {
      API.hideItem(new ItemStack(EIOBlocks.blockSolarPanel));
    }
    if(!Config.travelAnchorEnabled) {
      API.hideItem(new ItemStack(EIOItems.itemTravelStaff));
    }
    if(!Config.reinforcedObsidianEnabled) {
      API.hideItem(new ItemStack(EIOBlocks.blockReinforcedObsidian));
    }
    if((!Config.transceiverEnabled || !Config.enderRailEnabled) && EIOBlocks.blockEnderRail != null) {
      API.hideItem(new ItemStack(EIOBlocks.blockEnderRail));
    }
    if(!Config.transceiverEnabled && EIOBlocks.blockTransceiver != null) {
      API.hideItem(new ItemStack(EIOBlocks.blockTransceiver));
    }
    if(!Config.reservoirEnabled) {
      API.hideItem(new ItemStack(EIOBlocks.blockReservoir));
    }
    if(!GasUtil.isGasConduitEnabled()) {
      API.hideItem(new ItemStack(EIOItems.itemGasConduit));
    }
    API.hideItem(new ItemStack(EIOBlocks.blockHyperCube));
    API.hideItem(new ItemStack(EIOBlocks.blockCapacitorBank, 1, OreDictionary.WILDCARD_VALUE));
  }

  @Override
  public String getName() {
    return "Ender IO NEI Plugin";
  }

  @Override
  public String getVersion() {
    return EnderIO.VERSION;
  }

}
