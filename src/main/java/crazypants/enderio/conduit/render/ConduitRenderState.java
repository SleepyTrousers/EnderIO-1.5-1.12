package crazypants.enderio.conduit.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.render.BlockStateWrapper;import crazypants.enderio.render.IBlockStateWrapper;

public class ConduitRenderState extends BlockStateWrapper {

  private final IConduitBundle bundle;

  private final boolean renderFacade;
  private final boolean renderConduit;

  public ConduitRenderState(IBlockState state, IBlockAccess world, BlockPos pos, IConduitBundle bundle) {
    super(state, world, pos);
    this.bundle = bundle;

    if (bundle == null) {
      renderFacade = false;
      renderConduit = false;
    } else {
      EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
      renderFacade = bundle.hasFacade() && !ConduitUtil.isFacadeHidden(bundle, player);
      renderConduit = !renderFacade || !isFacadeOpaqueCube();
    }
  }

  private boolean isFacadeOpaqueCube() {   
    IBlockState b = bundle.getPaintSource();
    if (b != null) {
      return b.getBlock().isOpaqueCube();
    }
    return false;
  }

  public IConduitBundle getBundle() {
    return bundle;
  }

  public boolean getRenderFacade() {
    return renderFacade;
  }

  public boolean getRenderConduit() {
    return renderConduit;
  }

}
