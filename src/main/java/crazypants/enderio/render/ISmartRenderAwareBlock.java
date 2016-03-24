package crazypants.enderio.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * An interface for blocks to allow them to be involved in the rendering process.
 * <p>
 * A block must also:
 * <ul>
 * <li>Register with SmartModelAttacher
 * <li>Implement the property EnumRenderMode and have a matching blockstate json:
 * <ul>
 * <li>Defaults (e.g. particles, item transformations) go into defaultState+EnumRenderMode.DEFAULTS, the model doesn't matter but is needed for the
 * transformations to work
 * <li>All states that have EnumRenderMode.AUTO will be replaced with the MachineSmartModel, so their content doesn't matter
 * <li>The other EnumRenderModes should have the matching data, but is it in the decision of the render mapper to use them or not
 * </ul>
 * <li>Force its blockstate to EnumRenderMode.AUTO
 * <li>Return a BlockStateWrapper from getExtendedProperties
 * </ul>
 *
 */
@Deprecated
public interface ISmartRenderAwareBlock {

  /**
   * Return a render mapper for the block or item stack.
   * <p>
   * This is called in a render thread.
   */
  @SideOnly(Side.CLIENT)
  IRenderMapper getRenderMapper();

}
