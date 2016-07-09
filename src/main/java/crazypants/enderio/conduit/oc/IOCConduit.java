package crazypants.enderio.conduit.oc;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduit.IConduit;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;

@InterfaceList({ @Interface(iface = "li.cil.oc.api.network.Environment", modid = "OpenComputersAPI|Network"),
    @Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = "OpenComputersAPI|Network") })
public interface IOCConduit extends IConduit, Environment, SidedEnvironment {

  public static final String COLOR_CONTROLLER_ID = "ColorController";

  void invalidate();

  public abstract void setSignalColor(EnumFacing dir, DyeColor col);

  public abstract DyeColor getSignalColor(EnumFacing dir);

}
