package crazypants.enderio.conduit.oc;

import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import crazypants.enderio.conduit.IConduit;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SidedEnvironment;

@InterfaceList({ @Interface(iface = "li.cil.oc.api.network.Environment", modid = "OpenComputersAPI|Network"),
        @Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = "OpenComputersAPI|Network") })
public interface IOCConduit extends IConduit, Environment, SidedEnvironment {

    public static final String COLOR_CONTROLLER_ID = "ColorController";

    void invalidate();

    public abstract void setSignalColor(ForgeDirection dir, DyeColor col);

    public abstract DyeColor getSignalColor(ForgeDirection dir);
}
