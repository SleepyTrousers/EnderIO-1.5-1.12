package crazypants.enderio.conduit;

import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IConduitComponent {

  @SideOnly(Side.CLIENT)
  public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes);

  public interface IConduitComponentProvider {

    @SideOnly(Side.CLIENT)
    public void hashCodeForModelCaching(IBlockStateWrapper wrapper, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes);

  }

}
