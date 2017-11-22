package crazypants.enderio.teleport;

import java.awt.Point;
import java.util.List;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.network.GuiPacket;
import crazypants.enderio.network.IRemoteExec;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.enderio.teleport.packet.PacketPassword;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerTravelAccessable extends ContainerEnder<IInventory> implements IRemoteExec.IContainer {

  public static final int EXEC_ACCESS_MODE = 0;
  public static final int EXEC_LABEL = 1;

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
    return ItemStack.EMPTY;
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
        return ta.getPassword().get(slot);
      } else {
        return ta.getItemLabel();
      }
    }

    @Override
    public void putStack(ItemStack stack) {
      if (isAuth) {
        if (ta instanceof TileEntityBase) {
          PacketHandler.INSTANCE.sendToServer(PacketPassword.setPassword((TileEntityBase) ta, slot, stack));
        }
      } else {
        if (ta instanceof TileEntityBase) {
          PacketHandler.INSTANCE.sendToServer(PacketPassword.setLabel((TileEntityBase) ta, stack));
        }
      }
    }

  }

  private int guiID = -1;

  @Override
  public void setGuiID(int id) {
    guiID = id;
  }

  @Override
  public int getGuiID() {
    return guiID;
  }

  @Override
  public IMessage networkExec(int id, GuiPacket message) {
    switch (id) {
    case EXEC_ACCESS_MODE:
      ta.setAccessMode(message.getEnum(0, TileTravelAnchor.AccessMode.class));
      break;
    case EXEC_LABEL:
      ta.setLabel(message.getString(0));
      break;
    default:
      return null;
    }
    IBlockState bs = te.getWorld().getBlockState(te.getPos());
    te.getWorld().notifyBlockUpdate(te.getPos(), bs, bs, 3);
    te.getWorld().markChunkDirty(te.getPos(), te);
    return null;
  }

}
