package crazypants.enderio.conduit.oc.conduit;

import com.enderio.core.common.transform.SimpleMixin;

import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SimpleMixin(value = TileConduitBundle.class, dependencies = "opencomputersapi|network")
public interface OCMixin extends IConduitBundle, Environment, SidedEnvironment {
  
  // == Environment == //

  @Override
  default Node node() {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      return cond.node();
    } else {
      return null;
    }
  }

  @Override
  default void onConnect(Node node) {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      cond.onConnect(node);
    }
  }

  @Override
  default void onDisconnect(Node node) {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      cond.onDisconnect(node);
    }
  }

  @Override
  default void onMessage(Message message) {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      cond.onMessage(message);
    }
  }
  
  // == SidedEnvironment == //

  @Override
  default Node sidedNode(EnumFacing side) {
    IOCConduit cond = getConduit(IOCConduit.class);
    if (cond != null) {
      return cond.sidedNode(side);
    } else {
      return null;
    }
  }

  @Override
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
