package crazypants.enderio.machine.obelisk.render;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;

import crazypants.enderio.EnderIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
@SideOnly(Side.CLIENT)
public class ObeliskSpecialRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

  private ItemStack floatingStack;

  private Random rand = new Random();

  private RenderEntityItem rei;

  public ObeliskSpecialRenderer(ItemStack itemStack) {
    this.floatingStack = itemStack;
  }

  private EntityItem ei = null;

  @SuppressWarnings("unchecked")
  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tick, int b) {

    World world = null;
    if (te != null) {
      world = te.getWorld();
      RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
    } else {
      world = Minecraft.getMinecraft().theWorld;    
    }

    

    if (te == null) {
      // Being rendered as an Item
      GlStateManager.pushMatrix();
      GlStateManager.color(1, 1, 1);
            
      
      GlStateManager.translate(0.5, y, z);
      GlStateManager.scale(0.625, 0.625, 0.625);
      GlStateManager.rotate(30, 1, 0, 0);
      GlStateManager.rotate(135, 0, 1, 0);

      GlStateManager.pushMatrix();
      renderItemStack((T) te, world, x, y, z, tick);
      GlStateManager.popMatrix();
      
      GlStateManager.disableLighting();
      Tessellator tessellator = Tessellator.getInstance();
      VertexBuffer worldrenderer = tessellator.getBuffer();
      worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
      for (BakedQuad bakedQuad : ObeliskBakery.bake(ObeliskRenderManager.INSTANCE.getActiveTextures())) {
        net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(worldrenderer, bakedQuad, -1);
      }
      tessellator.draw();

      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
    } else {
      renderItemStack((T) te, world, x, y, z, tick);  
    }
  }

  protected void renderItemStack(T te, World world, double x, double y, double z, float tick) {
    if (ei == null) {
      ei = new EntityItem(world, 0, 0, 0, getFloatingItem(te));
    }
    ei.setEntityItemStack(getFloatingItem(te));
    ei.hoverStart = (float) ((EnderIO.proxy.getTickCount() * 0.05f + (tick * 0.05f)) % (Math.PI * 2));

    RenderUtil.bindBlockTexture();

    GlStateManager.pushMatrix();
    glTranslated(x + 0.5, y + 0.7, z + 0.5);
    glScalef(1.1f, 1.1f, 1.1f);

    BlockPos p;
    if (te != null) {
      p = te.getPos();
    } else {
      p = new BlockPos(0, 0, 0);
    }

    rand.setSeed(p.getX() + p.getY() + p.getZ());
    rand.nextBoolean();
    if (Minecraft.getMinecraft().gameSettings.fancyGraphics) {
      GlStateManager.rotate(rand.nextFloat() * 360f, 0, 1, 0);
    }
    ei.hoverStart += rand.nextFloat();

    GlStateManager.translate(0, -0.15f, 0);
    if (rei == null) {
      rei = new InnerRenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem());
    }
    rei.doRender(ei, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

    GlStateManager.popMatrix();

  }

  /**
   * @param te
   *          CAN BE NULL
   */
  protected ItemStack getFloatingItem(T te) {
    return floatingStack;
  }

  //Required to prevent bobbing
  private static class InnerRenderEntityItem extends RenderEntityItem {

    private Random random = new Random();
    private RenderItem itemRenderer;

    public InnerRenderEntityItem(RenderManager renderManagerIn, RenderItem renderItem) {
      super(renderManagerIn, renderItem);
      itemRenderer = renderItem;
    }

    @Override
    public boolean shouldBob() {
      return false;
    }

    //This method is copied straight from the parent, solely to disable 'setBlurMipmap' 
    @Override
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
      ItemStack itemstack = entity.getEntityItem();
      random.setSeed(187L);
      boolean flag = false;

      if (bindEntityTexture(entity)) {
        //Must be removed to prevent strange rendering artifacts
//        this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
        flag = true;
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
       GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.pushMatrix();
      IBakedModel ibakedmodel = this.itemRenderer.getItemModelMesher().getItemModel(itemstack);
      int i = this.func_177077_a(entity, x, y, z, partialTicks, ibakedmodel);

      for (int j = 0; j < i; ++j) {
        {
          GlStateManager.pushMatrix();

          if (j > 0) {
            float f = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
            float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
            float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
            GlStateManager.translate(shouldSpreadItems() ? f : 0.0F, shouldSpreadItems() ? f1 : 0.0F, f2);
          }

          if (ibakedmodel.isGui3d()) {
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
          }
          ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
          this.itemRenderer.renderItem(itemstack, ibakedmodel);
          GlStateManager.popMatrix();
        }
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      bindEntityTexture(entity);

      if (flag) {
      //Must be removed to prevent strange rendering artifacts
//        this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
      }
      
      
    }

    private int func_177077_a(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_) {
      ItemStack itemstack = itemIn.getEntityItem();
      Item item = itemstack.getItem();

      if (item == null) {
        return 0;
      } else {
        boolean flag = p_177077_9_.isGui3d();
        int i = this.getModelCount(itemstack);        
        float f1 = shouldBob() ? MathHelper.sin((itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F : 0;
        float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
        GlStateManager.translate((float) p_177077_2_, (float) p_177077_4_ + f1 + 0.25F * f2, (float) p_177077_6_);

        if (flag || this.renderManager.options != null) {
          float f3 = ((itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * (180F / (float) Math.PI);
          GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
        }

        if (!flag) {
          float f6 = -0.0F * (i - 1) * 0.5F;
          float f4 = -0.0F * (i - 1) * 0.5F;
          float f5 = -0.046875F * (i - 1) * 0.5F;
          GlStateManager.translate(f6, f4, f5);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        return i;
      }
    }
  }

}
