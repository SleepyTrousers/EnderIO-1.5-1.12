package crazypants.enderio.machines.machine.vacuum.xp;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerXPVacuum extends ContainerEnderCap<EnderInventory, TileXPVacuum> implements IVacuumRangeRemoteExec.Container {

  public ContainerXPVacuum(@Nonnull InventoryPlayer playerInv, @Nonnull TileXPVacuum te) {
    super(playerInv, new EnderInventory(), te);
  }

  @Override
  protected void addSlots() {
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 70);
  }

  private int guiId = -1;

  @Override
  public void setGuiID(int id) {
    this.guiId = id;
  }

  @Override
  public int getGuiID() {
    return guiId;
  }

  @Override
  public IMessage doSetVacuumRange(int range) {
    TileXPVacuum te = getTileEntity();
    if (te != null) {
      te.setRange(range);
    }
    return null;
  }

}
