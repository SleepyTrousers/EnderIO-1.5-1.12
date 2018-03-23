package crazypants.enderio.machines.machine.soul;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.common.util.SoundUtil;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.base.xp.ExperienceBarRenderer;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;

public class GuiSoulBinder extends GuiInventoryMachineBase<TileSoulBinder> implements ISoulBinderRemoteExec.GUI {

  private static final int PLAYER_XP_ID = 985162394;

  private final IconButton usePlayerXP;

  public GuiSoulBinder(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileSoulBinder te) {
    super(te, new ContainerSoulBinder(par1InventoryPlayer, te), "soul_fuser");
    usePlayerXP = new IconButton(this, PLAYER_XP_ID, 125, 57, IconEIO.XP);
    usePlayerXP.visible = false;
    usePlayerXP.setToolTip(Lang.GUI_SOUL_USEPLAYERXP.get());

    addProgressTooltip(80, 34, 24, 16);

    addDrawingElement(new PowerBar(te, this));
  }

  @Override
  public void initGui() {
    super.initGui();
    usePlayerXP.onGuiInit();
    ((ContainerSoulBinder) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b.id == PLAYER_XP_ID) {
      int xp = XpUtil.getPlayerXP(Minecraft.getMinecraft().player);
      if (xp > 0 || Minecraft.getMinecraft().player.capabilities.isCreativeMode) {
        doDrainXP(getTileEntity().getCurrentlyRequiredLevel());
        SoundUtil.playClientSoundFX(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, getTileEntity());
      }
    }
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int k = guiLeft;
    int l = guiTop;

    drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    int i1;

    TileSoulBinder binder = getTileEntity();

    if (shouldRenderProgress()) {
      i1 = getProgressScaled(24);
      drawTexturedModalRect(k + 80, l + 34, 176, 14, i1 + 1, 16);
    }

    usePlayerXP.visible = binder.needsXP();

    ExperienceBarRenderer.render(this, getGuiLeft() + 56, getGuiTop() + 68, 65, binder.getContainer(), binder.getCurrentlyRequiredLevel());

    bindGuiTexture();
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
