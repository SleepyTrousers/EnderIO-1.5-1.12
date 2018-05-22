package crazypants.enderio.machines.machine.ihopper;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerImpulseHopper extends ContainerEnderCap<EnderInventory, TileImpulseHopper> implements ImpulseHopperRemoteExec.Container {

  public ContainerImpulseHopper(@Nonnull InventoryPlayer playerInv, @Nonnull TileImpulseHopper te) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    for (int i = 0; i < TileImpulseHopper.SLOTS; i++) {
      addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.INPUT), "INPUT" + i, 44 + i * 18, 9));
      addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.OUTPUT), "OUTPUT" + i, 44 + i * 18, 63));
    }
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.UPGRADE), "cap", 11, 60));
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    for (int i = 0; i < TileImpulseHopper.SLOTS; i++) {
      slots.add(new ImpulseHopperGhostSlot(i, 44 + i * 18, 36));
    }
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

  public class ImpulseHopperGhostSlot extends GhostSlot {

    public ImpulseHopperGhostSlot(int slot, int x, int y) {
      this.setX(x);
      this.setY(y);
      this.setSlot(slot);
      this.setStackSizeLimit(64);
      this.setDisplayStdOverlay(true);
      this.setUpdateServer(true);
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull ItemStack getStack() {
      return getTileEntity().getGhostSlotItems().get(getSlot());
    }

  }

  private int guiId = -1;

  @Override
  public void setGuiID(int id) {
    guiId = id;
  }

  @Override
  public int getGuiID() {
    return guiId;
  }

  @Override
  public IMessage doOpenFilterGui(boolean isLocked) {
    TileImpulseHopper te = getTileEntity();
    if (te != null) {
      te.setOutputLocked(isLocked);
    }
    return null;
  }

}
