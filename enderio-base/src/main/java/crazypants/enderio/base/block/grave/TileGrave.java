package crazypants.enderio.base.block.grave;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.InventorySlot;

import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

@Storable
public class TileGrave extends AbstractCapabilityMachineEntity {

  private static final int ITEM_SLOTS = 5 * 9;

  private TileEntitySkull renderDummy = null;

  public TileGrave() {
    for (int i = 0; i < ITEM_SLOTS; i++) {
      getInventory().add(EnderInventory.Type.INOUT, "slot" + i, new InventorySlot());
    }

    redstoneControlMode = RedstoneControlMode.IGNORE;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @SuppressWarnings("null")
  @Nonnull
  TileEntitySkull getRenderDummy() {
    if (renderDummy == null) {
      renderDummy = new TileEntitySkull() {
        @Override
        public int getBlockMetadata() {
          return EnumFacing.UP.ordinal();
        }
      };
      renderDummy.setPlayerProfile(getOwner().getAsGameProfile());
      renderDummy.setSkullRotation(world.rand.nextInt(16));
    }
    return renderDummy;
  }

  @Override
  public boolean mergeOutput(@Nonnull ItemStack stack) {
    return super.mergeOutput(stack);
  }

}
