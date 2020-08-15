package crazypants.enderio.base.machine.entity;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.base.render.IBlockStateWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * See {@link RenderFallingBlock}
 *
 */
@SideOnly(Side.CLIENT)
public class RenderFallingMachine extends Render<EntityFallingMachine> {

  public static final Factory FACTORY = new Factory();

  public static class Factory implements IRenderFactory<EntityFallingMachine> {

    @Override
    public Render<? super EntityFallingMachine> createRenderFor(RenderManager manager) {
      return new RenderFallingMachine(manager);
    }
  }

  public RenderFallingMachine(RenderManager renderManagerIn) {
    super(renderManagerIn);
    this.shadowSize = 0.5F;
  }

  @Override
  public void doRender(@Nonnull EntityFallingMachine entity, double x, double y, double z, float entityYaw, float partialTicks) {
    IBlockState iblockstate = entity.getBlock();
    IBlockState extendedState = iblockstate;
    if (iblockstate instanceof IBlockStateWrapper) {
      extendedState = iblockstate;
      // only reason to copy this whole class: we need to dig out the real blockstate from the wrapper so blockrendererdispatcher.getModelForState() below
      // will work
      iblockstate = ((IBlockStateWrapper) iblockstate).getState();
    }

    if (extendedState != null && iblockstate != null) {
      if (iblockstate.getRenderType() == EnumBlockRenderType.MODEL) {
        World world = entity.getWorldObj();

        if (iblockstate != world.getBlockState(new BlockPos(entity)) && iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
          this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
          GlStateManager.pushMatrix();
          GlStateManager.disableLighting();
          Tessellator tessellator = Tessellator.getInstance();
          BufferBuilder bufferbuilder = tessellator.getBuffer();

          if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
          }

          bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
          BlockPos blockpos = new BlockPos(entity.posX, entity.getEntityBoundingBox().maxY, entity.posZ);
          GlStateManager.translate((float) (x - blockpos.getX() - 0.5D), (float) (y - blockpos.getY()), (float) (z - blockpos.getZ() - 0.5D));
          BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
          blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(iblockstate), extendedState, blockpos,
              bufferbuilder, false, MathHelper.getPositionRandom(entity.getOrigin()));
          tessellator.draw();

          if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
          }

          GlStateManager.enableLighting();
          GlStateManager.popMatrix();
          super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
      }
    }
  }

  @Override
  protected ResourceLocation getEntityTexture(@Nonnull EntityFallingMachine entity) {
    return TextureMap.LOCATION_BLOCKS_TEXTURE;
  }
}