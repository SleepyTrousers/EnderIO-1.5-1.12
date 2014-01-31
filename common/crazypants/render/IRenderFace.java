package crazypants.render;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import crazypants.vecmath.Vertex;

public interface IRenderFace {

  void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, Icon texture, List<Vertex> refVertices,
      boolean translateToXyz);

}
