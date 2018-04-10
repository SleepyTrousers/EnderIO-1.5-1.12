package crazypants.enderio.machines.machine.teleport.telepad.gui;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;

import crazypants.enderio.base.gui.IconEIO;
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

  private final IconButton closeWindowButton;

  public GuiAugmentedTravelAccessible(@Nonnull InventoryPlayer playerInv, @Nonnull TileTelePad te, @Nonnull World world) {
    super(playerInv, te, world);

    closeWindowButton = new IconButton(this, ID_CLOSE_WINDOW_BUTTON, 3, 3, IconEIO.ARROW_LEFT);
    closeWindowButton.setToolTip(Lang.GUI_TELEPAD_TRAVEL_SETTINGS_CLOSE.get(), Lang.GUI_TELEPAD_TRAVEL_SETTINGS_CLOSE_2.get());
  
  }

  @Override
  public void initGui() {
    super.initGui();
    
    closeWindowButton.onGuiInit();

  }
  
  @Override
  public void actionPerformed(@Nonnull GuiButton button) {
	  super.actionPerformed(button);
	  
	  if(button.id == ID_CLOSE_WINDOW_BUTTON) {
		  
		  PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, BlockTelePad.GUI_ID_TELEPAD));
		  
	  }
    
  }

}
