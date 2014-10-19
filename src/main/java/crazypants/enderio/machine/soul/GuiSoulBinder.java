package crazypants.enderio.machine.soul;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.painter.PainterContainer;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.xp.ExperienceBarRenderer;
import crazypants.enderio.xp.PacketDrainPlayerXP;
import crazypants.enderio.xp.XpUtil;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.SoundUtil;
import crazypants.vecmath.Vector4f;

public class GuiSoulBinder extends GuiMachineBase {

  private static final int PLAYER_XP_ID = 985162394;

  private TileSoulBinder tileEntity;
  
  private IconButtonEIO usePlayerXP;

  public GuiSoulBinder(InventoryPlayer par1InventoryPlayer, TileSoulBinder te) {
    super(te, new ContainerSoulBinder(par1InventoryPlayer, te));
    tileEntity = te;
    usePlayerXP = new IconButtonEIO(this, PLAYER_XP_ID, 125, 57, IconEIO.XP);
    usePlayerXP.visible = false;
    usePlayerXP.setToolTip("Use Player XP");    
  }

  @Override
  public void initGui() {    
    super.initGui();
    usePlayerXP.onGuiInit();
  }

  @Override
  protected void actionPerformed(GuiButton b) {    
    super.actionPerformed(b);
    if(b.id == PLAYER_XP_ID) {
      int xp = XpUtil.getPlayerXP(Minecraft.getMinecraft().thePlayer);
      if(xp > 0 || Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode) {
        PacketHandler.INSTANCE.sendToServer(new PacketDrainPlayerXP(tileEntity, tileEntity.getCurrentlyRequiredLevel(), true));
        SoundUtil.playClientSoundFX("random.orb", tileEntity);        
      }
    }
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/soulFuser.png");
    int k = (width - xSize) / 2;
    int l = (height - ySize) / 2;

    drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    int i1;

    i1 = tileEntity.getProgressScaled(24);
    drawTexturedModalRect(k + 80, l + 34, 176, 14, i1 + 1, 16);    

    boolean needsXp = tileEntity.getCurrentlyRequiredLevel() > 0 && tileEntity.getCurrentlyRequiredLevel() > tileEntity.getContainer().getExperienceLevel();
    usePlayerXP.visible = needsXp;        
    
    ExperienceBarRenderer.render(this, getGuiLeft() + 56, getGuiTop() + 68, 65, tileEntity.getContainer(), tileEntity.getCurrentlyRequiredLevel());
    
    RenderUtil.bindTexture("enderio:textures/gui/soulFuser.png");
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    
  }

}
