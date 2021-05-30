package crazypants.enderio.base.block.grave;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.entity.player.EntityPlayer;
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

  @Override // protected -> public
  public boolean mergeOutput(@Nonnull ItemStack stack) {
    return super.mergeOutput(stack);
  }

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  private @Nullable UserIdent bodyInGrave;

  @Override
  public void setOwner(@Nonnull EntityPlayer player) {
    super.setOwner(player);
    this.bodyInGrave = UserIdent.create(player.getGameProfile());
  }

  @Override
  public @Nonnull UserIdent getOwner() {
    return NullHelper.first(bodyInGrave, super::getOwner);
  }

}
