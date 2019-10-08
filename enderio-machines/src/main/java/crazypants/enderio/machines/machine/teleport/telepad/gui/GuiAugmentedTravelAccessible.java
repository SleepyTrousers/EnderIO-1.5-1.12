package crazypants.enderio.machines.machine.teleport.telepad.gui;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.teleport.GuiTravelAccessable;
import crazypants.enderio.machines.machine.teleport.telepad.BlockTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketOpenServerGui;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiAugmentedTravelAccessible extends GuiTravelAccessable<TileTelePad> {

  private static final int ID_CLOSE_WINDOW_BUTTON = 99;

  private final IconButton closeWindowButton;

  public GuiAugmentedTravelAccessible(@Nonnull InventoryPlayer playerInv, @Nonnull TileTelePad te, @Nonnull World world) {
    super(playerInv, new ContainerAugmentedTravelAccessable(playerInv, te, world, 6), te, world);

    xSize = 184;
    ySize = 200;

    closeWindowButton = new IconButton(this, ID_CLOSE_WINDOW_BUTTON, 3, 3, IconEIO.ARROW_LEFT);
    closeWindowButton.setToolTip(Lang.GUI_TELEPAD_TRAVEL_SETTINGS_CLOSE.get(), Lang.GUI_TELEPAD_TRAVEL_SETTINGS_CLOSE_2.get());
  }

  @Override
  protected @Nonnull ResourceLocation getGuiTexture(int i) {
    return EnderIO.proxy.getGuiTexture("augmented_travel_accessable");
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton button) {
    if (button.id == ID_CLOSE_WINDOW_BUTTON) {
      PacketHandler.INSTANCE.sendToServer(new PacketOpenServerGui(getOwner(), BlockTelePad.GUI_ID_TELEPAD));
      return;
    }
    super.actionPerformed(button);
  }

  @Override
  public void initGui() {
    super.initGui();
    closeWindowButton.onGuiInit();
  }

  @Override
  protected int getGuiOffset() {
    return 6;
  }

}
