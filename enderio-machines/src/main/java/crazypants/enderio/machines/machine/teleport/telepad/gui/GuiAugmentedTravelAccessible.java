package crazypants.enderio.machines.machine.teleport.telepad.gui;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.machine.teleport.GuiTravelAccessable;
import crazypants.enderio.machines.machine.teleport.telepad.BlockTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketOpenServerGui;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiAugmentedTravelAccessible extends GuiTravelAccessable<TileTelePad> {

  public GuiAugmentedTravelAccessible(@Nonnull InventoryPlayer playerInv, @Nonnull TileTelePad te, @Nonnull World world) {
    super(playerInv, te, world);
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton button) {
    if (button.id == ID_CLOSE_WINDOW_BUTTON) {
      PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(te, BlockTelePad.GUI_ID_TELEPAD));
      return;
    }
    super.actionPerformed(button);
  }

}
