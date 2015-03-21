package crazypants.enderio.teleport.telepad;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.obj.GroupObject;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import crazypants.render.TechneUtil;

public class TelePadSpecialRenderer extends TileEntitySpecialRenderer {

  private static List<GroupObject> blades = Lists.newArrayList();
  private static List<GroupObject> glass = Lists.newArrayList(TelePadRenderer.all.get("glass"));
  
  static {
    for (String s : TelePadRenderer.all.keySet()) {
      if(s.contains("blade")) {
        blades.add(TelePadRenderer.all.get(s));
      }
    }
  }

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float p_147500_8_) {
    TileTelePad tp = (TileTelePad) te;
    if(tp.isMaster() && tp.inNetwork()) {
      GL11.glPushMatrix();
      GL11.glTranslated(x + 0.5, y + 0.01, z + 0.5);
      Tessellator tes = Tessellator.instance;
      tes.setBrightness(0xF000F0);
      // TODO spin the spinny thingies properly
      float rot = te.getWorldObj().getTotalWorldTime() + p_147500_8_;
      GL11.glRotatef(rot, 0, 1, 0);
      render(blades, te, tes);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glRotatef(-rot, 0, 1, 0);
      render(glass, te, tes);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glPopMatrix();
    }
  }

  private void render(List<GroupObject> model, TileEntity te, Tessellator tes) {
    tes.startDrawingQuads();
    TechneUtil.renderWithIcon(model, te.getBlockType().getIcon(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, 0), null, Tessellator.instance,
        te.getWorldObj(), 0, 0, 0, null, false);
    tes.draw();
  }
}
