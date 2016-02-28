package crazypants.enderio.conduit.liquid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector2f;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class LiquidConduitRenderer extends DefaultConduitRenderer implements IResourceManagerReloadListener {

  private float downRatio;

  private float flatRatio;

  private float upRatio;

  private LiquidConduitRenderer() {
    super();
  }

  public static LiquidConduitRenderer create() {
    LiquidConduitRenderer result = new LiquidConduitRenderer();
    RenderUtil.registerReloadListener(result);
    return result;
  }

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    if (conduit instanceof LiquidConduit) {
      return true;
    }
    return false;
  }
 
  @Override
  protected void addTransmissionQuads(TextureAtlasSprite tex, IConduit conduit, CollidableComponent component, float selfIllum, List<BakedQuad> quads) {
    //Handled in dynamic render
  }
  
  @Override
  protected void renderConduitDynamic(TextureAtlasSprite tex, IConduit conduit, CollidableComponent component, float brightness) {    
    if (isNSEWUD(component.dir)) {
      LiquidConduit lc = (LiquidConduit) conduit;
      FluidStack fluid = lc.getFluidType();
      if (fluid != null) {
        renderFluidOutline(component, fluid);
      } 
    }
  }

  @Override
  public void renderDynamicEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z,
      float partialTick, float worldLight) {
    calculateRatios((LiquidConduit) conduit);
    super.renderDynamicEntity(conduitBundleRenderer, te, conduit, x, y, z, partialTick, worldLight);
  }

  @Override
  protected void renderTransmissionDynamic(IConduit conduit, TextureAtlasSprite tex, CollidableComponent component, float selfIllum) {  

    if (((LiquidConduit) conduit).getTank().getFilledRatio() <= 0) {
      return;
    }
    
    if (isNSEWUD(component.dir)) {          
      BoundingBox[] cubes = toCubes(component.bound);
      for (BoundingBox cube : cubes) {        
        drawDynamicSection(cube, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
      }

    } else {
      drawDynamicSection(component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
    }    
  }
  

  public static void renderFluidOutline(CollidableComponent component, FluidStack fluid) {
    renderFluidOutline(component, fluid, 1, 13f / 16f);
  }

  public static void renderFluidOutline(CollidableComponent component, FluidStack fluid, double scaleFactor, float outlineWidth) {
    for (CachableRenderStatement elem : computeFluidOutlineToCache(component, fluid.getFluid(), scaleFactor, outlineWidth)) {
      elem.execute();
    }
  }

  private static Map<CollidableComponent, Map<Fluid, List<CachableRenderStatement>>> cache = new WeakHashMap<CollidableComponent, Map<Fluid, List<CachableRenderStatement>>>();

  public static List<CachableRenderStatement> computeFluidOutlineToCache(CollidableComponent component, Fluid fluid, double scaleFactor, float outlineWidth) {

    Map<Fluid, List<CachableRenderStatement>> cache0 = cache.get(component);
    if (cache0 == null) {
      cache0 = new HashMap<Fluid, List<CachableRenderStatement>>();
      cache.put(component, cache0);
    }
    List<CachableRenderStatement> data = cache0.get(fluid);
    if (data != null) {
      return data;
    }
    data = new ArrayList<CachableRenderStatement>();
    cache0.put(fluid, data);

    TextureAtlasSprite texture = RenderUtil.getStillTexture(fluid);
    if (texture == null) {      
      return data;      
    }

    BoundingBox bbb;
    if (scaleFactor == 1) {
      bbb = component.bound;
    } else {
      double xScale = Math.abs(component.dir.getFrontOffsetX()) == 1 ? 1 : scaleFactor;
      double yScale = Math.abs(component.dir.getFrontOffsetY()) == 1 ? 1 : scaleFactor;
      double zScale = Math.abs(component.dir.getFrontOffsetZ()) == 1 ? 1 : scaleFactor;
      bbb = component.bound.scale(xScale, yScale, zScale);
    }

    for (EnumFacing face : EnumFacing.VALUES) {
      if (face != component.dir && face != component.dir.getOpposite()) {

        data.add(new CachableRenderStatement.SetNormal(face.getFrontOffsetX(), face.getFrontOffsetY(), face.getFrontOffsetZ()));
        Vector3d offset = ForgeDirectionOffsets.offsetScaled(face, -0.005);

        Vector2f uv = new Vector2f();
        List<EnumFacing> edges = RenderUtil.getEdgesForFace(face);
        for (EnumFacing edge : edges) {
          if (edge != component.dir && edge != component.dir.getOpposite()) {
            float xLen = 1 - Math.abs(edge.getFrontOffsetX()) * outlineWidth;
            float yLen = 1 - Math.abs(edge.getFrontOffsetY()) * outlineWidth;
            float zLen = 1 - Math.abs(edge.getFrontOffsetZ()) * outlineWidth;
            BoundingBox bb = bbb.scale(xLen, yLen, zLen);

            List<Vector3f> corners = bb.getCornersForFace(face);

            for (Vector3f unitCorn : corners) {
              Vector3d corner = new Vector3d(unitCorn);
              corner.add(offset);

              corner.x += (float) (edge.getFrontOffsetX() * 0.5 * bbb.sizeX()) - (Math.signum(edge.getFrontOffsetX()) * xLen / 2f * bbb.sizeX()) * 2f;
              corner.y += (float) (edge.getFrontOffsetY() * 0.5 * bbb.sizeY()) - (Math.signum(edge.getFrontOffsetY()) * yLen / 2f * bbb.sizeY()) * 2f;
              corner.z += (float) (edge.getFrontOffsetZ() * 0.5 * bbb.sizeZ()) - (Math.signum(edge.getFrontOffsetZ()) * zLen / 2f * bbb.sizeZ()) * 2f;

              //polyOffset

              RenderUtil.getUvForCorner(uv, corner, 0, 0, 0, face, texture);

              data.add(new CachableRenderStatement.AddVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y));
            }
          }

        }
      }
    }
    return data;
  }

  private interface CachableRenderStatement {
    void execute();

    static class SetNormal implements CachableRenderStatement {
      private final float x, y, z;

      private SetNormal(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
      }

      @Override
      public void execute() {
        Tessellator.getInstance().getWorldRenderer().normal(x, y, z);
      }
    }

    static class AddVertexWithUV implements CachableRenderStatement {
      private final double x, y, z, u, v;

      private AddVertexWithUV(double x, double y, double z, double u, double v) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
      }

      @Override
      public void execute() {
        Tessellator.getInstance().getWorldRenderer().pos(x, y, z).tex(u, v).endVertex();
      }
    }
  }

  @Override
  public boolean isDynamic() {
    return true;
  }

  @Override
  protected void setVerticesForTransmission(BoundingBox bound, EnumFacing id) {

    float yScale = getRatioForConnection(id);
    float scale = 0.7f;
    float xs = id.getFrontOffsetX() == 0 ? scale : 1;
    float ys = id.getFrontOffsetY() == 0 ? Math.min(yScale, scale) : yScale;
    float zs = id.getFrontOffsetZ() == 0 ? scale : 1;

    float sizeY = bound.sizeY();
    bound = bound.scale(xs, ys, zs);
    float transY = (bound.sizeY() - sizeY) / 2;
    Vector3d translation = new Vector3d(0, transY + 0.025, 0);
    setupVertices(bound.translate(translation));
  }

  private void calculateRatios(LiquidConduit conduit) {
    ConduitTank tank = conduit.getTank();
    int totalAmount = tank.getFluidAmount();

    int upCapacity = 0;
    if (conduit.containsConduitConnection(EnumFacing.UP) || conduit.containsExternalConnection(EnumFacing.UP)) {
      upCapacity = LiquidConduit.VOLUME_PER_CONNECTION;
    }
    int downCapacity = 0;
    if (conduit.containsConduitConnection(EnumFacing.DOWN) || conduit.containsExternalConnection(EnumFacing.DOWN)) {
      downCapacity = LiquidConduit.VOLUME_PER_CONNECTION;
    }

    int flatCapacity = tank.getCapacity() - upCapacity - downCapacity;

    int usedCapacity = 0;
    if (downCapacity > 0) {
      int inDown = Math.min(totalAmount, downCapacity);
      usedCapacity += inDown;
      downRatio = (float) inDown / downCapacity;
    }
    if (flatCapacity > 0 && usedCapacity < totalAmount) {
      int inFlat = Math.min(flatCapacity, totalAmount - usedCapacity);
      usedCapacity += inFlat;
      flatRatio = (float) inFlat / flatCapacity;
    } else {
      flatRatio = 0;
    }
    if (upCapacity > 0 && usedCapacity < totalAmount) {
      int inUp = Math.min(upCapacity, totalAmount - usedCapacity);
      upRatio = (float) inUp / upCapacity;
    } else {
      upRatio = 0;
    }

  }

  private float getRatioForConnection(EnumFacing id) {
    if (id == EnumFacing.UP) {
      return upRatio;
    }
    if (id == EnumFacing.DOWN) {
      return downRatio;
    }
    return flatRatio;
  }

  @Override
  public void onResourceManagerReload(IResourceManager p_110549_1_) {
    cache.clear();
  }

}
