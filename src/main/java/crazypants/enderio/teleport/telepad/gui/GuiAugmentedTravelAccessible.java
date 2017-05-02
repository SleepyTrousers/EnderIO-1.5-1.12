package crazypants.enderio.teleport.telepad.gui;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.ContainerTravelAccessable;
import crazypants.enderio.teleport.GuiTravelAccessable;
import crazypants.enderio.teleport.telepad.TileTelePad;
import crazypants.enderio.teleport.telepad.packet.PacketOpenServerGui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiAugmentedTravelAccessible extends GuiTravelAccessable implements IToggleableGui {

  private static final int ID_SWITCH_BUTTON = 99;

  ToggleTravelButton switchButton;

  private TileTelePad te;
  
  public GuiAugmentedTravelAccessible(InventoryPlayer playerInv, TileTelePad te, World world) {
    super(playerInv, te, world);
    this.te = te;
    switchButton = new ToggleTravelButton(this, ID_SWITCH_BUTTON, GuiTelePad.SWITCH_X, GuiTelePad.SWITCH_Y, IconEIO.IO_WHATSIT);
    switchButton.setToolTip(EnderIO.lang.localize("gui.telepad.configure.telepad"));
  }
  
  public GuiAugmentedTravelAccessible(ContainerTravelAccessable container) {
    super(container);
  }

  @Override
  public void initGui() {
    super.initGui();
    switchButton.onGuiInit();
  }

  @Override
  public void switchGui() {
    GuiID.GUI_ID_TELEPAD.openClientGui(world, te.getTileEntity().getPos(), mc.player, null);
    PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, GuiID.GUI_ID_TELEPAD));
  }
}
