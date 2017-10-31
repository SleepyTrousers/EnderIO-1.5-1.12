package crazypants.enderio.conduit;

import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.render.IBlockStateWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IConduitComponent {

  @SideOnly(Side.CLIENT)
  public void hashCodeForModelCaching(IBlockStateWrapper wrapper, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes);

}
