package crazypants.enderio.machines.machine.enchanter;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiEnchanter extends GuiContainerBaseEIO {

  private TileEnchanter te;
  private ContainerEnchanter container;
  private IconButton recipeButton;

  public GuiEnchanter(EntityPlayer player, @Nonnull InventoryPlayer inventory, @Nonnull TileEnchanter te) {
    super(new ContainerEnchanter(player, inventory, te), "enchanter");
    container = (ContainerEnchanter) inventorySlots;
    this.te = te;

    if (PersonalConfig.recipeButtonInMachineGuis.get()) {
      recipeButton = new IconButton(this, 100, 154, 8, IconEIO.RECIPE_BOOK);
      recipeButton.visible = false;
    } else {
      recipeButton = new IconButton(this, 100, 154, 8, IconEIO.RECIPE);
      recipeButton.visible = false;
      recipeButton.setIconMargin(1, 1);
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    recipeButton.onGuiInit();
    recipeButton.visible = EnderIO.proxy.isAnEiInstalled();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    int curCost = te.getCurrentEnchantmentCost();
    if (curCost > 0) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      int col;
      if (container.playerHasEnoughLevels(Minecraft.getMinecraft().player)) {
        col = 8453920; // all good
      } else {
        col = 16736352; // not enough levels
        bindGuiTexture();
        drawTexturedModalRect(sx + 99, sy + 33, 176, 0, 28, 21);
      }
      String s = Lang.GUI_VANILLA_REPAIR_COST.get(curCost);
      drawCenteredString(Minecraft.getMinecraft().fontRenderer, s, sx + xSize / 2, sy + 57, col);
    }

    super.drawGuiContainerBackgroundLayer(var1, var2, var3);
  }

}
