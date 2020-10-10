package crazypants.enderio.nei;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.gas.GasUtil;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.invpanel.GuiInventoryPanel;
import crazypants.enderio.machine.invpanel.client.InventoryPanelNEIOverlayHandler;
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

    API.registerGuiOverlayHandler(GuiInventoryPanel.class, new InventoryPanelNEIOverlayHandler(), "crafting");

    API.hideItem(new ItemStack(EnderIO.blockConduitFacade));
    API.hideItem(new ItemStack(EnderIO.blockLightNode));
    API.hideItem(new ItemStack(EnderIO.itemEnderface));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EnderIO.blockPaintedCarpet)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EnderIO.blockPaintedSlab)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EnderIO.blockPaintedFence)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EnderIO.blockPaintedFenceGate)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EnderIO.blockPaintedGlowstone)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EnderIO.blockPaintedStair)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EnderIO.blockPaintedWall)));
    API.hideItem(PainterUtil.applyDefaultPaintedState(new ItemStack(EnderIO.blockPaintedDoubleSlab)));

    if(!Config.photovoltaicCellEnabled) {
      API.hideItem(new ItemStack(EnderIO.blockSolarPanel));
    }
    if(!Config.travelAnchorEnabled) {
      API.hideItem(new ItemStack(EnderIO.itemTravelStaff));
    }
    if(!Config.reinforcedObsidianEnabled) {
      API.hideItem(new ItemStack(EnderIO.blockReinforcedObsidian));
    }
    if((!Config.transceiverEnabled || !Config.enderRailEnabled) && EnderIO.blockEnderRail != null) {
      API.hideItem(new ItemStack(EnderIO.blockEnderRail));
    }
    if(!Config.transceiverEnabled && EnderIO.blockTransceiver != null) {
      API.hideItem(new ItemStack(EnderIO.blockTransceiver));
    }
    if(!Config.reservoirEnabled) {
      API.hideItem(new ItemStack(EnderIO.blockReservoir));
    }
    if(!GasUtil.isGasConduitEnabled()) {
      API.hideItem(new ItemStack(EnderIO.itemGasConduit));
    }
    API.hideItem(new ItemStack(EnderIO.blockHyperCube));
    API.hideItem(new ItemStack(EnderIO.blockCapacitorBank, 1, OreDictionary.WILDCARD_VALUE));
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
