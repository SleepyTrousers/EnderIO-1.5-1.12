package crazypants.enderio.teleport.telepad;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.obj.GroupObject;
import crazypants.render.TechneUtil;

public class TelePadSpecialRenderer extends TileEntitySpecialRenderer {

  // This will probably need to be a Map<String, GroupObject>
  private List<GroupObject> model = TechneUtil.getModel("models/telePadAnimation");

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float p_147500_8_) {
    // TODO spin the spinny thingies
    if(((TileTelePad) te).isMaster()) {
      GL11.glPushMatrix();
      GL11.glTranslated(x, y, z);
      // not working
      TechneUtil.renderWithIcon(model, ((BlockTelePad) te.getBlockType()).animationIcon, null, Tessellator.instance, te.getWorldObj(), 0, 0, 0);
      GL11.glPopMatrix();
    }
  }
}
