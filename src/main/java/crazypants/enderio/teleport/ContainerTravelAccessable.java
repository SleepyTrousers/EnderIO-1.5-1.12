package crazypants.enderio.teleport;

import java.awt.Point;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.TileEntityEnder;
import com.enderio.core.common.util.ArrayInventory;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.packet.PacketPassword;

public class ContainerTravelAccessable extends ContainerEnder<IInventory> {

  ITravelAccessable ta;
  TileEntity te;
  World world;

  public ContainerTravelAccessable(InventoryPlayer playerInv, final ITravelAccessable travelAccessable, World world) {
    super(playerInv, playerInv);
    ta = travelAccessable;
    this.world = world;
    if (ta instanceof TileEntity) {
      te = ((TileEntity) ta);
    }
  }

  @Override
  protected void addSlots(InventoryPlayer playerInv) {
  }

  public void addGhostSlots(List<GhostSlot> ghostSlots) {
    int x = 44;
    int y = 73;
    for (int i = 0; i < 5; i++) {
      ghostSlots.add(new CtaGhostSlot(ta, i, x, y, true));
      x += 18;
    }

    x = 125;
    y = 10;
    ghostSlots.add(new CtaGhostSlot(ta, 0, x, y, false));
  }

  @Override
  public Point getPlayerInventoryOffset() {
    return new Point(8, 103);
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    return null;
  }

  private static class CtaGhostSlot extends GhostSlot {

    private ITravelAccessable ta;
    boolean isAuth;

    public CtaGhostSlot(ITravelAccessable ta, int slotIndex, int x, int y, boolean isAuth) {
      this.slot = slotIndex;
      this.x = x;
      this.y = y;
      this.displayStdOverlay = false;
      this.grayOut = true;
      this.stackSizeLimit = 1;
      this.ta = ta;
      this.isAuth = isAuth;
    }

    @Override
    public ItemStack getStack() {
      if (isAuth) {
        return ta.getPassword()[slot];
      } else {
        return ta.getItemLabel();
      }
    }

    @Override
    public void putStack(ItemStack stack) {
      if (isAuth) {
        if (ta instanceof TileEntityEnder) {
          PacketHandler.INSTANCE.sendToServer(PacketPassword.setPassword((TileEntityEnder) ta, slot, stack));
        }
      } else {
        if (ta instanceof TileEntityEnder) {
          PacketHandler.INSTANCE.sendToServer(PacketPassword.setLabel((TileEntityEnder) ta, stack));
        }
      }
    }

  }

}
