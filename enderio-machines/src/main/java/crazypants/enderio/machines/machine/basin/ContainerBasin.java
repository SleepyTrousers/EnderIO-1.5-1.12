package crazypants.enderio.machines.machine.basin;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.util.stackable.Things;
import com.enderio.core.common.inventory.EnderSlot;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.gui.AbstractCapabilityMachineContainer;
import crazypants.enderio.machines.machine.basin.TileBasin.Slots;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;

public class ContainerBasin extends AbstractCapabilityMachineContainer<TileBasin> {
  
  private static final @Nonnull Things pickaxes = new Things()
      .add(Items.WOODEN_PICKAXE).add(Items.STONE_PICKAXE).add(Items.IRON_PICKAXE).add(Items.DIAMOND_PICKAXE)
      .add(ModObject.itemDarkSteelPickaxe.getItem()).add(ModObject.itemEndSteelPickaxe.getItem());

  public ContainerBasin(@Nonnull InventoryPlayer playerInv, @Nonnull TileBasin te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots() {
    addSlotToContainer(new EnderSlot(Type.OUTPUT, getItemHandler().getSlot(Slots.OUTPUT), 81, 41));
    addSlotToContainer(new EnderSlot(Type.INPUT, getItemHandler().getSlot(Slots.TOOL), 152, 73));
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 99);
  }

  @Override
  public @Nonnull Point getUpgradeOffset() {
    return new Point(8, 75);
  }

  public void createGhostSlots(GhostSlotHandler ghostSlotHandler) {
    ghostSlotHandler.add(new GhostBackgroundItemSlot(pickaxes.getItemStacks(), getSlot(1)));
  }
}
