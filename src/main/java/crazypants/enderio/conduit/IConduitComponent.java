package crazypants.enderio.conduit;

import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IConduitComponent {

  @SideOnly(Side.CLIENT)
  public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes);

}
