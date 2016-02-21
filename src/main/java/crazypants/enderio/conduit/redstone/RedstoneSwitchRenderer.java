package crazypants.enderio.conduit.redstone;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;

public class RedstoneSwitchRenderer extends DefaultConduitRenderer {

  private static final RedstoneSwitchRenderer instance = new RedstoneSwitchRenderer();

  public static RedstoneSwitchRenderer getInstance() {
    return instance;
  }

  private final VertexTransform[] xForms;
  private final BoundingBox switchBounds;
  private final BoundingBox connectorBounds;

  private RedstoneSwitchRenderer() {
    xForms = RedstoneSwitchBounds.getInstance().xForms;
    switchBounds = RedstoneSwitchBounds.getInstance().switchBounds;
    connectorBounds = RedstoneSwitchBounds.getInstance().connectorBounds;
  }

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit.getClass() == RedstoneSwitch.class;
  }


  @Override
  protected boolean renderComponent(CollidableComponent component) {
    return !RedstoneSwitch.SWITCH_TAG.equals(component.data);
  }

}
