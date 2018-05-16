package crazypants.enderio.conduits.oc.conduit;

import com.enderio.core.common.transform.SimpleMixin;

import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SimpleMixin(TileConduitBundle.class)
@InterfaceList({ 
    @Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputersapi|network"),
    @Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = "opencomputersapi|network") })
public interface OCMixin extends IConduitBundle, Environment, SidedEnvironment {
  
  // == Environment == //

  @Override
  @Method(modid = "opencomputersapi|network")
  default Node node() {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      return cond.node();
    } else {
      return null;
    }
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  default void onConnect(Node node) {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      cond.onConnect(node);
    }
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  default void onDisconnect(Node node) {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      cond.onDisconnect(node);
    }
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  default void onMessage(Message message) {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      cond.onMessage(message);
    }
  }
  
  // == SidedEnvironment == //

  @Override
  @Method(modid = "opencomputersapi|network")
  default Node sidedNode(EnumFacing side) {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      return cond.sidedNode(side);
    } else {
      return null;
    }
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  @SideOnly(Side.CLIENT)
  default boolean canConnect(EnumFacing side) {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      return cond.canConnect(side);
    } else {
      return false;
    }
  }  
}
