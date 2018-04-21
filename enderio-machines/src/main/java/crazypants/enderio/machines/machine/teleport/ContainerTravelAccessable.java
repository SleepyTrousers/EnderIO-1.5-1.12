package crazypants.enderio.machines.machine.teleport;

import java.awt.Point;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.api.teleport.ITravelAccessable.AccessMode;
import crazypants.enderio.base.teleport.packet.PacketPassword;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerTravelAccessable extends ContainerEnder<IInventory> implements ITravelAccessableRemoteExec.Container {

  final @Nonnull ITravelAccessable ta;
  final TileEntity te;
  final @Nonnull World world;
  final @Nonnull EntityPlayer player;
  final int offset;

  public ContainerTravelAccessable(@Nonnull InventoryPlayer playerInv, final @Nonnull ITravelAccessable travelAccessable, @Nonnull World world, int guiOffset) {
    super(playerInv, playerInv);
    ta = travelAccessable;
    this.world = world;
    this.player = playerInv.player;
    this.offset = guiOffset;
    if (ta instanceof TileEntity) {
      te = ((TileEntity) ta);
    } else {
      te = null;
    }
  }

  @Override
  protected void addSlots(@Nonnull InventoryPlayer playerInv) {
  }

  public void addGhostSlots(List<GhostSlot> ghostSlots) {
    int x = 44 + offset;
    int y = 73 + offset;
    for (int i = 0; i < 5; i++) {
      ghostSlots.add(new CtaGhostSlot(ta, i, x, y, true));
      x += 18;
    }

    x = 125 + offset;
    y = 10 + offset;
    ghostSlots.add(new CtaGhostSlot(ta, 0, x, y, false));
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 103);
  }

  @Override
  public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer entityPlayer, int slotIndex) {
    return ItemStack.EMPTY;
  }

  private static class CtaGhostSlot extends GhostSlot {

    private @Nonnull ITravelAccessable ta;
    boolean isAuth;

    public CtaGhostSlot(@Nonnull ITravelAccessable ta, int slotIndex, int x, int y, boolean isAuth) {
      this.setSlot(slotIndex);
      this.setX(x);
      this.setY(y);
      this.setDisplayStdOverlay(false);
      this.setGrayOut(true);
      this.setStackSizeLimit(1);
      this.ta = ta;
      this.isAuth = isAuth;
    }

    @Override
    public @Nonnull ItemStack getStack() {
      if (isAuth) {
        return ta.getPassword().get(getSlot());
      } else {
        return ta.getItemLabel();
      }
    }

    @Override
    public void putStack(@Nonnull ItemStack stack, int realsize) {
      if (isAuth) {
        if (ta instanceof TileEntityBase) {
          PacketHandler.INSTANCE.sendToServer(PacketPassword.setPassword((TileEntityBase) ta, getSlot(), stack));
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

  @SuppressWarnings("null") // gah, Eclipse thinks a final field can go null from one side of the comma to the next
  @Override
  public IMessage doSetAccessMode(@Nonnull AccessMode accesmode) {
    ta.setAccessMode(accesmode);
    if (te != null) { // TODO what's this? overkill?
      IBlockState bs = te.getWorld().getBlockState(te.getPos());
      te.getWorld().notifyBlockUpdate(te.getPos(), bs, bs, 3);
      te.getWorld().markChunkDirty(te.getPos(), te);
    }
    return null;
  }

  @SuppressWarnings("null") // gah, Eclipse thinks a final field can go null from one side of the comma to the next
  @Override
  public IMessage doSetLabel(@Nullable String label) {
    ta.setLabel(label);
    if (te != null) { // TODO what's this? overkill?
      IBlockState bs = te.getWorld().getBlockState(te.getPos());
      te.getWorld().notifyBlockUpdate(te.getPos(), bs, bs, 3);
      te.getWorld().markChunkDirty(te.getPos(), te);
    }
    return null;
  }

  @SuppressWarnings("null") // gah, Eclipse thinks a final field can go null from one side of the comma to the next
  @Override
  public IMessage doSetVisible(boolean visible) {
    ta.setVisible(visible);
    if (te != null) { // TODO what's this? overkill?
      IBlockState bs = te.getWorld().getBlockState(te.getPos());
      te.getWorld().notifyBlockUpdate(te.getPos(), bs, bs, 3);
      te.getWorld().markChunkDirty(te.getPos(), te);
    }
    return null;
  }

  @Override
  public IMessage doCloseGui() {
    player.closeScreen();
    return null;
  }

}
