package crazypants.enderio.conduit.redstone;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.VertexRotation;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

public class RedstoneSwitchRenderer extends DefaultConduitRenderer {

  public static RedstoneSwitchRenderer instance = new RedstoneSwitchRenderer();
  
  private static final float SIZE = 0.2f;
  private static final float HW = SIZE/3;
  private static final float HH = SIZE/2;
  private static final float DEPTH = 0.05f;
  private static final float HD = DEPTH/2;
  
  private static final float DIST = 0.25f;
  
  private static final float CON_HW = 0.015f;
  
  
  private final VertexTransform[] xForms;
  private final BoundingBox switchBounds; 
  private final BoundingBox connectorBounds;
  
  private final BoundingBox[] aabb;
  
  private RedstoneSwitchRenderer() {
    BoundingBox bb = new BoundingBox(0.5- HW, 0.5- HH, 0.5- HD, 0.5+ HW, 0.5+ HH, 0.5 + HD);
    switchBounds = bb.translate(0, 0, DIST);    
        
    connectorBounds = new BoundingBox(0.5- CON_HW, 0.5- CON_HW, 0.5- CON_HW, 0.5+ CON_HW, 0.5+ CON_HW, 0.5 + DIST);
    
    Vector3d axis = new Vector3d(0,1,0);
    Vector3d p = new Vector3d(0.5,0.5,0.5);
    xForms = new VertexTransform[4];
    double angle = Math.toRadians(45);
    for(int i=0; i < xForms.length; i++) {
      //TODO
      //xForms[i] = new Rotation(angle, axis, p);
      xForms[i] = new VertexRotation(angle, axis, p);
      angle += Math.toRadians(90);
    }    
    
    aabb = new BoundingBox[xForms.length];
    for(int i=0; i < xForms.length; i++) {
      aabb[i] = switchBounds.transform(xForms[i]);
    }
    
  }
  
  public BoundingBox[] getAABB() {
    return aabb;
  }
  
  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit.getClass() == RedstoneSwitch.class;
  }

  @Override
  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle bundle, IConduit conduit, double x, double y, double z, float partialTick,
      float worldLight) {
        
    super.renderEntity(conduitBundleRenderer, bundle, conduit, x, y, z, partialTick, worldLight);
    
    RedstoneSwitch sw = (RedstoneSwitch)conduit;
    
    Tessellator tessellator = Tessellator.instance;
    float selfIllum = Math.max(worldLight, conduit.getSelfIlluminationForState(null));
    tessellator.setColorOpaque_F(selfIllum, selfIllum, selfIllum);
    
    Icon[] icons = new Icon[6];
    for(int i=0;i<icons.length;i++) {
      icons[i] = EnderIO.blockConduitBundle.getConnectorIcon();
    }
    icons[1] = sw.getSwitchIcon();;
    
    
    Vector3d trans = ConduitGeometryUtil.instance.getTranslation(ForgeDirection.UNKNOWN, bundle.getOffset(IRedstoneConduit.class, ForgeDirection.UNKNOWN));
    BoundingBox bb = switchBounds.translate(trans);
    
    for(VertexTransform tf : xForms) {
      CubeRenderer.render(bb, icons, tf);
    }    
    bb = connectorBounds.translate(trans);
    for(VertexTransform tf : xForms) {
      CubeRenderer.render(bb, icons[0], tf);
    }
    
  }
  
  @Override
  protected boolean renderComponent(CollidableComponent component) {
    return !RedstoneSwitch.SWITCH_TAG.equals(component.data);
  }
  
  

}
