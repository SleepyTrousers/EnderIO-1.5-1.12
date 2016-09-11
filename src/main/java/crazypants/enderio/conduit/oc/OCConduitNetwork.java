package crazypants.enderio.conduit.oc;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.Method;
import li.cil.oc.api.Network;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.conduit.AbstractConduitNetwork;

@Interface(iface = "li.cil.oc.api.network.ManagedEnvironment", modid = "OpenComputersAPI|Network")
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
  @Method(modid = "OpenComputersAPI|Network")
  public Node node() {
    return node[DyeColor.SILVER.ordinal()];
  }

  public Node node(DyeColor subnet) {
    return node[subnet.ordinal()];
  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public void onConnect(Node node) {

  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public void onDisconnect(Node node) {

  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public void onMessage(Message message) {

  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public void load(NBTTagCompound nbt) {

  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public void save(NBTTagCompound nbt) {

  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
  public boolean canUpdate() {
    return false;
  }

  @Override
  @Method(modid = "OpenComputersAPI|Network")
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
