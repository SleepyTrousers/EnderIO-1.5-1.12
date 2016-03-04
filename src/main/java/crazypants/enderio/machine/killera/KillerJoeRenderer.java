package crazypants.enderio.machine.killera;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.machine.generator.zombie.ModelZombieJar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KillerJoeRenderer extends TileEntitySpecialRenderer<TileKillerJoe> {

  private static final String TEXTURE = "enderio:models/KillerJoe.png";

//  private static final ItemStack DEFAULT_SWORD = new ItemStack(Items.iron_sword);

  private ModelZombieJar model = new ModelZombieJar();

  @Override
  public void renderTileEntityAt(TileKillerJoe te, double x, double y, double z, float tick, int b) {

    if(te != null) {
      RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
    }
    GlStateManager.pushMatrix();    
    GlStateManager.translate((float) x, (float) y, (float) z);
    EnumFacing facing = EnumFacing.WEST;
    if (te != null) {
      facing = te.facing;
    }
    renderModel(facing);
    if(te != null) {
      renderSword(facing, te.getStackInSlot(0), te.getSwingProgress(tick));
      renderFluid(te);
    }
    GlStateManager.popMatrix();

  }

  private void renderSword(EnumFacing facing, ItemStack sword, float swingProgress) {

    if(sword == null) {
      return;
    }

    //Sword
    GlStateManager.pushMatrix();

    GlStateManager.translate(0.5f, 0, 0.5f);
    float offset = 270f;
    if(facing.getFrontOffsetX() != 0) {
      offset *= -1;
    }
    GlStateManager.rotate((facing.getHorizontalIndex() * 90F) + offset, 0F, 1F, 0F);
    GlStateManager.translate(-0.5f, 0, -0.5F);

    GlStateManager.pushMatrix();
    if(swingProgress > 0) {
      float f6 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float f7 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(f6 * 5.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-f7 * 30.0F, 0.0F, 0.0F, 1.0F);
    }
    GlStateManager.translate(0.85f, 0.6f, 0.03f);

    GlStateManager.pushMatrix();
    float scale = 0.75f;    
    GlStateManager.scale(scale, scale, scale);
    Minecraft.getMinecraft().getRenderItem().renderItem(sword, TransformType.NONE);
    GlStateManager.popMatrix();
    
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
    

  }

  protected void renderFluid(TileKillerJoe gen) {
    FluidTank tank = gen.fuelTank;
    if(tank.getFluidAmount() <= 0) {
      return;
    }
    TextureAtlasSprite icon = RenderUtil.getStillTexture(tank.getFluid());
    if(icon != null) {
      RenderUtil.bindBlockTexture();
      

      float fullness = (float) (tank.getFluidAmount()) / (tank.getCapacity());
      BoundingBox bb = BoundingBox.UNIT_CUBE.scale(0.85, 0.96, 0.85);
      bb = bb.scale(1, 0.85 * fullness, 1);
      float ty = -(0.85f - (bb.maxY - bb.minY)) / 2;

      Vector3d offset = ForgeDirectionOffsets.offsetScaled(gen.facing, -0.075);
      bb = bb.translate((float) offset.x, ty, (float) offset.z);

      int brightness;
      if(gen.getWorld() == null) {
        brightness = 15 << 20 | 15 << 4;
      } else {
        brightness = gen.getWorld().getLightFor(EnumSkyBlock.SKY, gen.getPos());
      }

      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);      
      GlStateManager.color(1, 1, 1);
      
      RenderUtil.renderBoundingBox(bb, icon);      
      GlStateManager.depthMask(true);
      
    }
  }

  private void renderModel(EnumFacing facing) {

    GlStateManager.pushMatrix();

    GlStateManager.translate(0.5F, 0, 0.5F);
    GlStateManager.rotate(180F, 1F, 0F, 0F);
    GlStateManager.scale(1.2f, 0.9f, 1.2f);
   
    GlStateManager.rotate(facing.getHorizontalIndex() * 90F, 0F, 1F, 0F);

    RenderUtil.bindTexture(TEXTURE);
    model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

    GlStateManager.translate(-0.5F, 0, -0.5F);
    GlStateManager.popMatrix();

  }

}
