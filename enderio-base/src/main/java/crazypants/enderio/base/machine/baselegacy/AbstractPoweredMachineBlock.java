package crazypants.enderio.base.machine.baselegacy;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.util.Prep;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractPoweredMachineBlock<T extends AbstractPoweredMachineEntity> extends AbstractMachineBlock<T> {

  AbstractPoweredMachineBlock(@Nonnull IModObject mo, Class<T> teClass, @Nonnull Material mat) {
    super(mo, teClass, mat);
  }

  AbstractPoweredMachineBlock(@Nonnull IModObject mo, Class<T> teClass) {
    super(mo, teClass);
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer entityPlayer,
      @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    T machine = getTileEntity(world, pos);
    ItemStack heldItem = entityPlayer.getHeldItem(hand);
    if (Prep.isValid(heldItem) && machine != null) {
      if (machine.isValidUpgrade(heldItem)) {
        int slot = machine.getSlotDefinition().getMinUpgradeSlot();
        ItemStack temp = machine.getStackInSlot(slot);
        if (Prep.isInvalid(temp)) {
          machine.setInventorySlotContents(slot, heldItem.splitStack(1));
          entityPlayer.inventory.markDirty();
          return true;
        } else if (!ItemStack.areItemsEqual(heldItem, temp)) {
          machine.setInventorySlotContents(slot, heldItem.splitStack(1));
          entityPlayer.inventory.markDirty();
          if (!entityPlayer.inventory.addItemStackToInventory(temp)) {
            entityPlayer.dropItem(temp, true);
          }
          return true;
        }
      }
    }

    return super.onBlockActivated(world, pos, state, entityPlayer, hand, side, hitX, hitY, hitZ);
  }

}
