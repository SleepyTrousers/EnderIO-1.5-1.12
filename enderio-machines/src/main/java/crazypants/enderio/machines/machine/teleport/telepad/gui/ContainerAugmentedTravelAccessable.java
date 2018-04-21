package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.awt.Point;

import javax.annotation.Nonnull;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.machines.machine.teleport.ContainerTravelAccessable;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class ContainerAugmentedTravelAccessable extends ContainerTravelAccessable {

  public ContainerAugmentedTravelAccessable(@Nonnull InventoryPlayer playerInv, @Nonnull ITravelAccessable travelAccessable, @Nonnull World world,
      int guiOffset) {
    super(playerInv, travelAccessable, world, guiOffset);
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(14, 109);
  }

}
