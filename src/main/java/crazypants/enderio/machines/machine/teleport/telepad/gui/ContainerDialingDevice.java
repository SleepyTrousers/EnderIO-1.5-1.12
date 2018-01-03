package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.machine.teleport.telepad.TileDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketSetTarget;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import static crazypants.enderio.base.init.ModObject.itemLocationPrintout;

public class ContainerDialingDevice extends ContainerEnderCap<EnderInventory, TileDialingDevice> implements IDialingDeviceRemoteExec.Container {

  public ContainerDialingDevice(@Nonnull InventoryPlayer playerInv, @Nonnull TileDialingDevice itemHandler) {
    super(playerInv, itemHandler.getInventory(), itemHandler);
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 138);
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(new EnderSlot(Type.INPUT, getItemHandler(), "INPUT", 153, 47));
    addSlotToContainer(new EnderSlot(Type.OUTPUT, getItemHandler(), "OUTPUT", 153, 84));
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    final Slot slot = inventorySlots.get(0);
    if (slot != null) {
      slots.add(new GhostBackgroundItemSlot(itemLocationPrintout.getItemNN(), slot));
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
  public IMessage doTeleport(@Nonnull BlockPos telepad, int targetID, boolean initiateTeleport) {
    final TileDialingDevice dialer = getTileEntity();
    if (dialer != null && dialer.getEnergy().canUseEnergy(CapacitorKey.DIALING_DEVICE_POWER_USE_TELEPORT)) {
      TileTelePad tp = BlockEnder.getAnyTileEntitySafe(dialer.getWorld(), telepad, TileTelePad.class);
      if (tp != null) {
        ArrayList<TelepadTarget> targets = dialer.getTargets();
        if (targetID >= 0 && targetID < targets.size()) {
          tp.setTarget(targets.get(targetID));
          if (initiateTeleport) {
            tp.teleportAll();
            dialer.getEnergy().useEnergy(CapacitorKey.DIALING_DEVICE_POWER_USE_TELEPORT);
          }
        }
        return new PacketSetTarget(tp, tp.getTarget());
      }
    }
    return null;
  }

}
