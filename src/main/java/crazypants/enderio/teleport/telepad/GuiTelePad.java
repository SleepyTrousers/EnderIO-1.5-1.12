package crazypants.enderio.teleport.telepad;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.teleport.GuiTravelAccessable;

public class GuiTelePad extends GuiTravelAccessable {

  public GuiTelePad(InventoryPlayer playerInv, ITravelAccessable te, World world) {
    super(playerInv, te, world);
  }

}
