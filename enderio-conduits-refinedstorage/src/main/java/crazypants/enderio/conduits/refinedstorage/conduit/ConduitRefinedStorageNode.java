package crazypants.enderio.conduits.refinedstorage.conduit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;

import crazypants.enderio.conduits.refinedstorage.RSHelper;
import crazypants.enderio.conduits.refinedstorage.init.ConduitRefinedStorageObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConduitRefinedStorageNode implements INetworkNode {

  @Nullable
  protected INetwork rsNetwork;
  protected World world;
  protected BlockPos pos;

  public ConduitRefinedStorageNode(World world, BlockPos pos) {
    this.world = world;
    this.pos = pos;
  }

  @Override
  public int getEnergyUsage() {
    return 0;
  }

  @Nonnull
  @Override
  public ItemStack getItemStack() {
    return new ItemStack(ConduitRefinedStorageObject.item_refined_storage_conduit.getItemNN(), 1);
  }

  @Override
  public void onConnected(INetwork network) {
    rsNetwork = network;
  }

  @Override
  public void onDisconnected(INetwork network) {
    rsNetwork = null;
  }

  @Override
  public boolean canUpdate() {
    return true;
  }

  @Nullable
  @Override
  public INetwork getNetwork() {
    return rsNetwork;
  }

  @Override
  public void update() {

  }

  @Override
  public NBTTagCompound write(NBTTagCompound tag) {
    return tag;
  }

  @Override
  public BlockPos getPos() {
    return pos;
  }

  @Override
  public World getWorld() {
    return world;
  }

  @Override
  public void markDirty() {
    if (!world.isRemote) {
      RSHelper.API.getNetworkNodeManager(world).markForSaving();
    }
  }

  @Override
  public String getId() {
    return "id_here";
  }

  @Override
  public boolean equals(Object right) {
    // Currently doing this to avoid a class cast exception in the log
    if (!(right instanceof INetworkNode)) {
      return false;
    }

    if (this == right) {
      return true;
    }

    if (right instanceof ConduitRefinedStorageNode) {
      return false;
    }

    return RSHelper.API.isNetworkNodeEqual(this, right);
  }

  @Override
  public int hashCode() {
    return RSHelper.API.getNetworkNodeHashCode(this);
  }

}
