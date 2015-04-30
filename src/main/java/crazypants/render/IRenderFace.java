package crazypants.render;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import com.enderio.core.common.vecmath.Vertex;

public interface IRenderFace {

  void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, IIcon texture, List<Vertex> refVertices,
      boolean translateToXyz);

}
