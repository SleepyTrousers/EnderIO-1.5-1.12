package crazypants.enderio.base.conduit;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.geom.CollidableComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IClientConduit extends IConduit {

  // stuff that would be on the class, not the object if there were interfaces for classes...

  /**
   * Creates the gui for the conduit within the external connection gui
   *
   * @param gui
   *          the gui to construct the panel inside of
   * @param con
   *          the conduit that the gui references
   * @return the panel for the conduit's information on the gui
   */
  @SideOnly(Side.CLIENT)
  @Nonnull
  ITabPanel createGuiPanel(@Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con);

  /**
   * Update the gui for updated client conduits.
   * <p>
   * Note that this will be called on all conduits, so you need to test if the panel you get is yours.
   * 
   * @param panel
   *          The panel to be updated
   * @return false if the panel doesn't belong to you or true if the panel was updated
   */
  @SideOnly(Side.CLIENT)
  boolean updateGuiPanel(@Nonnull ITabPanel panel);

  /**
   * Determines the order the panels are shown in the conduit gui tabs
   *
   * @return the integer position of the panel in order (top --> bottom)
   */
  @SideOnly(Side.CLIENT)
  int getGuiPanelTabOrder();

  /**
   * @return true if the conduit is currently in use
   */
  boolean isActive();

  // rendering, only needed if default rendering is used
  interface WithDefaultRendering extends IClientConduit {

    @SideOnly(Side.CLIENT)
    @Nonnull
    TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component);

    @SideOnly(Side.CLIENT)
    @Nonnull
    TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component);

    @SideOnly(Side.CLIENT)
    public @Nonnull Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component);

    @SideOnly(Side.CLIENT)
    float getTransmitionGeometryScale();

    @SideOnly(Side.CLIENT)
    float getSelfIlluminationForState(@Nonnull CollidableComponent component);

    /**
     * Should the texture of the conduit connectors be mirrored around the conduit node?
     */
    @SideOnly(Side.CLIENT)
    boolean shouldMirrorTexture();

  }

}
