package crazypants.enderio.machines.machine.mapomatic;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.gui.GuiCapMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiMapOMatic extends GuiCapMachineBase<TileMapOMatic> {

  private static final int POWERX = 15;
  private static final int POWERY = 9;
  private static final int POWER_HEIGHT = 47;

  protected GuiMapOMatic(@Nonnull InventoryPlayer playerInv, @Nonnull TileMapOMatic te) {
    super(te, new ContainerMapOMatic(playerInv, te), "mapomatic");

    xSize = 176;
    ySize = 166;

    addDrawingElement(new PowerBar(te.getEnergy(), this, POWERX, POWERY, POWER_HEIGHT));
  }
}
