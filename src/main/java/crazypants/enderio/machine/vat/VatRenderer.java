package crazypants.enderio.machine.vat;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.machine.OverlayRenderer;
import crazypants.enderio.machine.generator.combustion.TranslatedCubeRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public class VatRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

  private VertXForm xform = new VertXForm();

  private CustomCubeRenderer ccr = new CustomCubeRenderer();

  private OverlayRenderer overlayRenderer = new OverlayRenderer();

  private TileVat vat;

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    renderInventoryBlock(Block.getBlockFromItem(item.getItem()), item.getItemDamage(), 0, (RenderBlocks) data[0]);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    if(world != null) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileVat) {
        vat = (TileVat) te;
      } else {
        vat = null;
      }
    }

    float fudge = 1f;

    IIcon override = renderer.overrideBlockTexture;

    //-x side
    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(0.334, fudge, fudge);
    bb = bb.translate(0.5f - (0.334f / 2), 0, 0);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, override, world != null);

    bb = BoundingBox.UNIT_CUBE.scale(0.334, fudge, fudge);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, override, world != null);

    bb = BoundingBox.UNIT_CUBE.scale(0.334, fudge, fudge);
    bb = bb.translate(-0.5f + (0.334f / 2), 0, 0);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, override, world != null);

    if(vat != null) {
      overlayRenderer.setTile(vat);
      ccr.renderBlock(world, block, x, y, z, overlayRenderer);
    }
    vat = null;

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    GL11.glDisable(GL11.GL_LIGHTING);
    Tessellator tes = Tessellator.instance;
    tes.startDrawingQuads();
    tes.addTranslation(0, -0.1f, 0);
    renderWorldBlock(null, 0, 0, 0, block, 0, renderer);
    tes.addTranslation(0, 0.1f, 0);
    tes.draw();
    GL11.glEnable(GL11.GL_LIGHTING);
  }

  @Override
  public int getRenderId() {
    return BlockVat.renderId;
  }

  private static class VertXForm implements VertexTransform {

    public VertXForm() {
    }

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      if(vec.x > 0.9 || vec.x < 0.1) {
        vec.z -= 0.5;
        vec.z *= 0.42;
        vec.z += 0.5;
      }
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }
}
