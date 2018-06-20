package crazypants.enderio.conduits.oc.conduit;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;

@InterfaceList({ @Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputersapi|network"),
    @Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = "opencomputersapi|network") })
public interface IOCConduit extends IClientConduit, IServerConduit, Environment, SidedEnvironment {

  public static final String COLOR_CONTROLLER_ID = "ColorController";

  public abstract void setSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col);

  public abstract @Nonnull DyeColor getSignalColor(@Nonnull EnumFacing dir);

}
