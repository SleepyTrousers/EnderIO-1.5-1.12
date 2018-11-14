package crazypants.enderio.base.machine.base.block;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderInventory.View;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class AbstractCapabilityMachineBlock<T extends AbstractCapabilityMachineEntity> extends AbstractMachineBlock<T> {

  protected AbstractCapabilityMachineBlock(@Nonnull IModObject mo) {
    super(mo);
  }

  public AbstractCapabilityMachineBlock(@Nonnull IModObject mo, @Nonnull Material mat) {
    super(mo, mat);
  }

  // Comparator

  @Override
  public int getComparatorInputOverride(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
    // Note: enable with hasComparatorInputOverride()
    T te = getTileEntity(worldIn, pos);

    if (te == null) {
      return 0;
    } else {
      View inv = te.getInventory().getView(Type.INOUT);
      int i = 0;
      float f = 0.0F;

      for (int j = 0; j < inv.getSlots(); ++j) {
        ItemStack stack = inv.getStackInSlot(j);

        if (!stack.isEmpty()) {
          f += (float) stack.getCount() / (float) Math.min(inv.getSlotLimit(j), stack.getMaxStackSize());
          ++i;
        }
      }

      f = f / inv.getSlots();
      return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
    }
  }
}
