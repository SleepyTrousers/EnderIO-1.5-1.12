package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.machine.teleport.telepad.TileDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketSetTarget;
import crazypants.enderio.machines.network.PacketHandler;
import info.loenwind.processor.RemoteCall;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;

import static crazypants.enderio.base.init.ModObject.itemLocationPrintout;

@RemoteCall
public class ContainerDialingDevice extends ContainerEnderCap<EnderInventory, TileDialingDevice> {

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

  @RemoteCall
  public void doTeleport(@Nonnull BlockPos telepad, int targetID, boolean initiateTeleport) {
    final TileDialingDevice dialer = getTileEntity();
    if (dialer != null) {
      TileTelePad tp = getTileEntityNN().findTelepad();
      if (tp != null && tp.getPos().equals(telepad)) {
        ArrayList<TelepadTarget> targets = dialer.getTargets();
        if (targetID >= 0 && targetID < targets.size()) {
          tp.setTarget(targets.get(targetID));
          PacketHandler.sendToAllAround(new PacketSetTarget(tp, tp.getTarget()), tp);
          if (initiateTeleport && dialer.getEnergy().canUseEnergy(CapacitorKey.DIALING_DEVICE_POWER_USE_TELEPORT)) {
            tp.teleportAll();
            dialer.getEnergy().useEnergy(CapacitorKey.DIALING_DEVICE_POWER_USE_TELEPORT);
          }
        }
      }
    }
  }

}
