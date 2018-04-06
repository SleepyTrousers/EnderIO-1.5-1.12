package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;

import crazypants.enderio.base.gui.IconEIO;
//import crazypants.enderio.base.gui.ToggleTravelButton;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.teleport.GuiTravelAccessable;
import crazypants.enderio.machines.machine.teleport.telepad.BlockTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketOpenServerGui;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiAugmentedTravelAccessible extends GuiTravelAccessable<TileTelePad> {

  private static final int ID_CLOSE_WINDOW_BUTTON = 99;

  //ToggleTravelButton switchButton;
  private final IconButton closeWindowButton;

  public GuiAugmentedTravelAccessible(@Nonnull InventoryPlayer playerInv, @Nonnull TileTelePad te, @Nonnull World world) {
    super(playerInv, te, world);
    //switchButton = new ToggleTravelButton(this, ID_SWITCH_BUTTON, GuiTelePad.SWITCH_X, GuiTelePad.SWITCH_Y, IconEIO.IO_WHATSIT);
    //switchButton.setToolTip(Lang.GUI_TELEPAD_TO_MAIN.get());
    closeWindowButton = new IconButton(this, ID_CLOSE_WINDOW_BUTTON, 3, 3, IconEIO.ARROW_LEFT);
    closeWindowButton.setToolTip("TODO: TOOLTIP LANG", "MORE TODO");
  
  }

  @Override
  public void initGui() {
    super.initGui();
    
    closeWindowButton.onGuiInit();
    //switchButton.onGuiInit();
  }
  
  @Override
  public void actionPerformed(@Nonnull GuiButton button) {
	  super.actionPerformed(button);
	  
	  if(button.id == ID_CLOSE_WINDOW_BUTTON) {
		  
		  PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, BlockTelePad.GUI_ID_TELEPAD));
		  
	  }
    
  }

}
