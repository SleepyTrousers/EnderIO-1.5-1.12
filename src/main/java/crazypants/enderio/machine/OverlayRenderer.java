package crazypants.enderio.machine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.CustomRenderBlocks;
import crazypants.render.IRenderFace;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vertex;

public class OverlayRenderer implements IRenderFace {

  private static final CustomCubeRenderer ccr = CustomCubeRenderer.instance;
  private AbstractMachineEntity te;
  
  public void setTile(AbstractMachineEntity te) {
    this.te = te;
  }
  
  @Override
  public void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, IIcon texture, List<Vertex> refVertices,
      boolean translateToXyz) {

    if(te != null && par1Block instanceof AbstractMachineBlock) {
      Vector3d offset = ForgeDirectionOffsets.offsetScaled(face, 0.001);
      Tessellator.instance.addTranslation((float) offset.x, (float) offset.y, (float) offset.z);

      IoMode mode = te.getIoMode(face);
      IIcon tex = ((AbstractMachineBlock<?>) par1Block).getOverlayIconForMode(face, mode);
      if(tex != null) {
        ccr.getCustomRenderBlocks().doDefaultRenderFace(face, par1Block, x, y, z, tex);
      }

      Tessellator.instance.addTranslation(-(float) offset.x, -(float) offset.y, -(float) offset.z);
    }
  }
}
