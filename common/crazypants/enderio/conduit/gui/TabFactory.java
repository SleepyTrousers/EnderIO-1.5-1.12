package crazypants.enderio.conduit.gui;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;

public class TabFactory {

  public static final TabFactory instance = new TabFactory();

  private TabFactory() {
  }

  public ISettingsPanel createPanelForConduit(GuiExternalConnection gui, IConduit con) {
    Class<? extends IConduit> baseType = con.getBaseConduitType();
    if(baseType.isAssignableFrom(IPowerConduit.class)) {
      return new PowerSettings(gui, con);
    } else if(baseType.isAssignableFrom(ILiquidConduit.class)) {
      return new LiquidSettings(gui, con);
    } else if(baseType.isAssignableFrom(IItemConduit.class)) {
      return new ItemSettings(gui, con);
    } else if(baseType.isAssignableFrom(IRedstoneConduit.class)) {
      return new RedstoneSettings(gui, con);
    }
    return null;
  }

}
