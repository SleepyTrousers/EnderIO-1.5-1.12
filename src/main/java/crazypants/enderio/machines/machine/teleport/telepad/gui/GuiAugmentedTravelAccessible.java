package crazypants.enderio.machines.machine.teleport.telepad.gui;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.machines.machine.teleport.GuiTravelAccessable;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketOpenServerGui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiAugmentedTravelAccessible extends GuiTravelAccessable<TileTelePad> implements IToggleableGui {

  private static final int ID_SWITCH_BUTTON = 99;

  ToggleTravelButton switchButton;

  public GuiAugmentedTravelAccessible(InventoryPlayer playerInv, TileTelePad te, World world) {
    super(playerInv, te, world);
    switchButton = new ToggleTravelButton(this, ID_SWITCH_BUTTON, GuiTelePad.SWITCH_X, GuiTelePad.SWITCH_Y, IconEIO.IO_WHATSIT);
    switchButton.setToolTip(EnderIO.lang.localize("gui.telepad.configure.telepad"));
  }
  
  @Override
  public void initGui() {
    super.initGui();
    switchButton.onGuiInit();
  }

  @Override
  public void switchGui() {
    GuiID.GUI_ID_TELEPAD.openClientGui(world, te.getPos(), mc.player, null);
    PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, GuiID.GUI_ID_TELEPAD));
  }
}
