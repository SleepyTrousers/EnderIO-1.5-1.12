package crazypants.enderio.machines.machine.spawner.creative;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;

import crazypants.enderio.base.EnderIO;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ContainerCreativeSpawnerUser extends ContainerEnderCap<EnderInventory, TileCreativeSpawner> {

  public ContainerCreativeSpawnerUser(@Nonnull InventoryPlayer playerInv, @Nonnull TileCreativeSpawner te) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    addSlotToContainer(new EnderSlot(getItemHandler().getView(Type.INPUT), TileCreativeSpawner.SLOT.OFFERING, 26, 30));
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

}
