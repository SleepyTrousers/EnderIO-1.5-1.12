package crazypants.enderio.machine.enchanter;

import com.enderio.core.client.render.RenderUtil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EnchanterModelRenderer extends TileEntitySpecialRenderer<TileEnchanter> {

  private static final String TEXTURE = "enderio:models/BookStand.png";

  private EnchanterModel model = new EnchanterModel();

  @Override
  public void renderTileEntityAt(TileEnchanter te, double x, double y, double z, float tick, int b) {
    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, z);
    EnumFacing facing = EnumFacing.NORTH;
    if (te != null) {
      facing = te.getFacing();
    }
    renderModel(facing);
    GlStateManager.popMatrix();
  }

  private void renderModel(EnumFacing facing) {

    GlStateManager.pushMatrix();

    GlStateManager.translate(0.5, 1.5, 0.5);
    GlStateManager.rotate(180, 1, 0, 0);

    GlStateManager.rotate(facing.getHorizontalIndex() * 90f, 0, 1, 0);

    RenderUtil.bindTexture(TEXTURE);
    model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

    GlStateManager.translate(-0.5, -1.5, -0.5);
    GlStateManager.popMatrix();
  }

}
