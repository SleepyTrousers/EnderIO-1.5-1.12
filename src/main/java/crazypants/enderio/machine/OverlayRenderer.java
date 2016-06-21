package crazypants.enderio.machine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.api.client.render.IRenderFace;
import com.enderio.core.client.render.CustomCubeRenderer;
import com.enderio.core.client.render.CustomRenderBlocks;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vertex;

public class OverlayRenderer implements IRenderFace {

  private static final CustomCubeRenderer ccr = CustomCubeRenderer.instance;
  private AbstractMachineEntity te;
  
  public void setTile(AbstractMachineEntity te) {
    this.te = te;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, IIcon texture, List<Vertex> refVertices,
      boolean translateToXyz) {

    if(te != null && par1Block instanceof AbstractMachineBlock) {
      BlockCoord bc = new BlockCoord(x, y, z);
      if(par1Block.isOpaqueCube()) {
        bc = bc.getLocation(face);
      }
      RenderUtil.setTesselatorBrightness(Minecraft.getMinecraft().theWorld, bc.x, bc.y, bc.z);
      Vector3d offset = ForgeDirectionOffsets.offsetScaled(face, 0.001);
      Tessellator.instance.addTranslation((float) offset.x, (float) offset.y, (float) offset.z);

      IoMode mode = te.getIoMode(face);
      IIcon tex = ((AbstractMachineBlock<AbstractMachineEntity>) par1Block).getOverlayIconForMode(te, face, mode);
      if(tex != null) {
        ccr.getCustomRenderBlocks().setRenderBoundsFromBlock(par1Block);
        ccr.getCustomRenderBlocks().doDefaultRenderFace(face, par1Block, x, y, z, tex);
      }

      Tessellator.instance.addTranslation(-(float) offset.x, -(float) offset.y, -(float) offset.z);
    }
  }
}
