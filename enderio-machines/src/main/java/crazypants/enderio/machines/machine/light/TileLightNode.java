package crazypants.enderio.machines.machine.light;

import javax.annotation.Nonnull;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.util.FuncUtil;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.Constants;

import static crazypants.enderio.machines.init.MachineObject.block_electric_light;
import static crazypants.enderio.machines.init.MachineObject.block_light_node;

@Storable
public class TileLightNode extends TileEntityEio {

  @Store(NBTAction.SAVE)
  private @Nonnull NNList<BlockPos> parents = new NNList<>();

  public void onNeighbourChanged() {
    parents.apply((NNList.Callback<BlockPos>) parent -> FuncUtil.doIf(BlockEnder.getAnyTileEntitySafe(world, parent, TileElectricLight.class),
        light -> light.nodeNeighbourChanged(this)));
    checkSelf();
  }

  public void onBlockRemoved() {
    parents.apply((NNList.Callback<BlockPos>) parent -> FuncUtil.doIf(BlockEnder.getAnyTileEntitySafe(world, parent, TileElectricLight.class),
        light -> light.nodeRemoved(this)));
  }

  @Override
  public String toString() {
    return "TileLightNode [parents=" + parents + ",  pos=" + pos + ", tileEntityInvalid=" + tileEntityInvalid + "]";
  }

  public void addParent(@Nonnull BlockPos parent) {
    parents.add(parent.toImmutable());
  }

  public void removeParent(@Nonnull BlockPos parent) {
    parents.remove(parent.toImmutable());
    checkSelf();
  }

  protected void checkSelf() {
    if (!world.isRemote) {
      for (NNIterator<BlockPos> iterator = parents.iterator(); iterator.hasNext();) {
        BlockPos parent = iterator.next();
        if (world.isBlockLoaded(parent) && world.getBlockState(parent).getBlock() != block_electric_light.getBlock()) {
          iterator.remove();
        }
      }
      if (parents.isEmpty()) {
        IBlockState lnbs = world.getBlockState(pos);
        if (lnbs.getBlock() == block_light_node.getBlockNN()) {
          world.setBlockToAir(pos);
        } else if (world.getTileEntity(pos) == this) {
          world.removeTileEntity(pos);
        } else {
          // Now stuff got really weird...
        }
      }
    }
  }

  public boolean isParent(@Nonnull BlockPos parent) {
    return parents.contains(parent.toImmutable());
  }

  public void calculateLight() {
    if (!world.isRemote) {
      boolean isPowered = false;
      for (NNIterator<BlockPos> iterator = parents.iterator(); !isPowered && iterator.hasNext();) {
        BlockPos parent = iterator.next();
        TileElectricLight light = BlockEnder.getAnyTileEntitySafe(world, parent, TileElectricLight.class);
        if (light != null) {
          isPowered = light.providesPoweredLight();
        }
      }
      IBlockState lnbs = world.getBlockState(pos);
      if (lnbs.getBlock() != block_light_node.getBlockNN()) {
        if (lnbs.getBlock() == Blocks.AIR) {
          lnbs = block_light_node.getBlockNN().getDefaultState();
        } else {
          if (world.getTileEntity(pos) == this) {
            world.removeTileEntity(pos);
          }
          return;
        }
      }
      IBlockState lnbsnew = lnbs.withProperty(BlockLightNode.ACTIVE, isPowered);
      world.setBlockState(pos, lnbsnew, Constants.BlockFlags.SEND_TO_CLIENTS);
      world.notifyBlockUpdate(pos, lnbs, lnbsnew, Constants.BlockFlags.DEFAULT);
      world.checkLightFor(EnumSkyBlock.BLOCK, pos);
    }
  }

}
