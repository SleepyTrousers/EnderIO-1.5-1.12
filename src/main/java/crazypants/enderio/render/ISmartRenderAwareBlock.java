package crazypants.enderio.render;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISmartRenderAwareBlock {

  /**
   * Return a render mapper for the item stack.
   * <p>
   * This is called in a render thread.
   */
  @SideOnly(Side.CLIENT)
  @Nonnull
  IRenderMapper.IItemRenderMapper getItemRenderMapper();

}
