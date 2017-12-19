package crazypants.enderio.machines.machine.teleport.telepad.gui;

import javax.annotation.Nonnull;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.teleport.GuiTravelAccessable;
import crazypants.enderio.machines.machine.teleport.telepad.BlockTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketOpenServerGui;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiAugmentedTravelAccessible extends GuiTravelAccessable<TileTelePad> implements IToggleableGui {

  private static final int ID_SWITCH_BUTTON = 99;

  ToggleTravelButton switchButton;

  public GuiAugmentedTravelAccessible(@Nonnull InventoryPlayer playerInv, @Nonnull TileTelePad te, @Nonnull World world) {
    super(playerInv, te, world);
    switchButton = new ToggleTravelButton(this, ID_SWITCH_BUTTON, GuiTelePad.SWITCH_X, GuiTelePad.SWITCH_Y, IconEIO.IO_WHATSIT);
    switchButton.setToolTip(Lang.GUI_TELEPAD_TO_MAIN.get());
  }
  
  @Override
  public void initGui() {
    super.initGui();
    switchButton.onGuiInit();
  }

  @Override
  public void switchGui() {
    PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, BlockTelePad.GUI_ID_TELEPAD));
  }
}
