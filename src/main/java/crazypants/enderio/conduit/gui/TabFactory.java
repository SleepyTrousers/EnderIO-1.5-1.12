package crazypants.enderio.conduit.gui;

import com.enderio.core.api.client.gui.ITabPanel;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.gas.IGasConduit;
import crazypants.enderio.conduit.gui.item.ItemSettings;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.me.IMEConduit;
import crazypants.enderio.conduit.oc.IOCConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;

public class TabFactory {

    public static final TabFactory instance = new TabFactory();

    private TabFactory() {}

    public ITabPanel createPanelForConduit(GuiExternalConnection gui, IConduit con) {
        Class<? extends IConduit> baseType = con.getBaseConduitType();
        if (baseType.isAssignableFrom(IPowerConduit.class)) {
            return new PowerSettings(gui, con);
        } else if (baseType.isAssignableFrom(ILiquidConduit.class)) {
            return new LiquidSettings(gui, con);
        } else if (baseType.isAssignableFrom(IItemConduit.class)) {
            return new ItemSettings(gui, con);
        } else if (baseType.isAssignableFrom(IRedstoneConduit.class)) {
            return new RedstoneSettings(gui, con);
        } else if (baseType.isAssignableFrom(IGasConduit.class)) {
            return new GasSettings(gui, con);
        } else if (baseType.isAssignableFrom(IMEConduit.class)) {
            return new MESettings(gui, con);
        } else if (baseType.isAssignableFrom(IOCConduit.class)) {
            return new OCSettings(gui, con);
        }
        return null;
    }
}
