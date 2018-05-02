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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class AbstractInventoryMachineBlock<T extends AbstractInventoryMachineEntity> extends AbstractMachineBlock<T> {

  protected AbstractInventoryMachineBlock(@Nonnull IModObject mo, @Nonnull Material mat) {
    super(mo, mat);
  }

  protected AbstractInventoryMachineBlock(@Nonnull IModObject mo) {
    super(mo);
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

  // Comparator

  @Override
  public int getComparatorInputOverride(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
    // Note: enable with hasComparatorInputOverride()
    T inv = getTileEntity(worldIn, pos);
    if (inv == null) {
      return 0;
    } else {
      int i = 0;
      float f = 0.0F;

      for (int j = 0; j < inv.getSizeInventory(); ++j) {
        ItemStack itemstack = inv.getStackInSlot(j);

        if (!itemstack.isEmpty()) {
          f += (float) itemstack.getCount() / (float) Math.min(inv.getInventoryStackLimit(j), itemstack.getMaxStackSize());
          ++i;
        }
      }

      f = f / inv.getSizeInventory();
      return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
    }
  }

}
