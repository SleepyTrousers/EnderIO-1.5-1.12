package crazypants.enderio.conduit.oc.conduit;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;

@Interface(iface = "li.cil.oc.api.network.ManagedEnvironment", modid = "opencomputersapi|network")
public class OCConduitNetwork extends AbstractConduitNetwork<IOCConduit, IOCConduit> implements ManagedEnvironment {

  private Node[] node = new Node[DyeColor.values().length];

  public OCConduitNetwork() {
    super(IOCConduit.class, IOCConduit.class);
    for (DyeColor dyeColor : DyeColor.values()) {
      node[dyeColor.ordinal()] = Network.newNode(this, Visibility.Network).create();
      Network.joinNewNetwork(node[dyeColor.ordinal()]);
    }
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public Node node() {
    return node[DyeColor.SILVER.ordinal()];
  }

  public Node node(DyeColor subnet) {
    return node[subnet.ordinal()];
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public void onConnect(Node nodeIn) {
    for (IOCConduit conduit : getConduits()) {
      conduit.onConnect(nodeIn);
    }
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public void onDisconnect(Node nodeIn) {
    for (IOCConduit conduit : getConduits()) {
      conduit.onDisconnect(nodeIn);
    }
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public void onMessage(Message message) {

  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public void load(NBTTagCompound nbt) {

  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public void save(NBTTagCompound nbt) {

  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public boolean canUpdate() {
    return false;
  }

  @Override
  @Method(modid = "opencomputersapi|network")
  public void update() {
  }

  @Override
  public void destroyNetwork() {
    for (DyeColor dyeColor : DyeColor.values()) {
      node[dyeColor.ordinal()].remove();
    }
    super.destroyNetwork();
  }

}
