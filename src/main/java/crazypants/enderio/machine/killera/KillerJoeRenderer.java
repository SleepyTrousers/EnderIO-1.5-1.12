package crazypants.enderio.machine.killera;

import static crazypants.enderio.machine.MachineObject.blockKillerJoe;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;

import crazypants.enderio.render.util.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.render.util.TankRenderHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KillerJoeRenderer extends ManagedTESR<TileKillerJoe> {

  public KillerJoeRenderer() {
    super(blockKillerJoe.getBlock());
  }

  @Override
  protected boolean shouldRender(@Nonnull TileKillerJoe te, @Nonnull IBlockState blockState, int renderPass) {
    return (renderPass == 0 && te.getStackInSlot(0) != null) || (renderPass == 1 && !te.tank.isEmpty());
  }

  @Override
  protected void renderTileEntity(@Nonnull TileKillerJoe te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    if (MinecraftForgeClient.getRenderPass() == 0) {       
      renderSword(te.facing, te.getStackInSlot(0), te.getSwingProgress(partialTicks), Minecraft.getMinecraft().player.getPrimaryHand() == EnumHandSide.LEFT);
    } else if (MinecraftForgeClient.getRenderPass() == 1) {
      HalfBakedList buffer = TankRenderHelper.mkTank(te.tank, 2.51, 1, 14, false);
      if (buffer != null) {         
        buffer.render();
      }
    }
  }

  private void renderSword(EnumFacing facing, ItemStack sword, float swingProgress, boolean leftHand) {

    // rotate to facing direction
    GlStateManager.translate(0.5f, 0, 0.5f);
    float offset = 270f;
    if(facing.getFrontOffsetX() != 0) {
      offset *= -1;
    }
    GlStateManager.rotate((facing.getHorizontalIndex() * 90F) + offset, 0F, 1F, 0F);
    GlStateManager.translate(-0.5f, 0, -0.5F);

    // rotate swing progress
    GlStateManager.pushMatrix();
    if(swingProgress > 0) {
      float f6 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float f7 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(f6 * 5.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-f7 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    // translate to side of jar
    GlStateManager.translate(13.6f / 16f, 0.6f, (leftHand ? 1.5f : 14.5f) / 16f);

    // scale to size
    GlStateManager.pushMatrix();    
    float scale = 0.75f;    
    GlStateManager.scale(scale, scale, scale);
    //Adjust rotation so axe is facing the correct way
    GlStateManager.rotate(180, 0, 1, 0);
    GlStateManager.rotate(90, 0, 0, 1);

    // render
    final net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType none = net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.NONE;
    Minecraft.getMinecraft().getRenderItem().renderItem(sword, none);

    // cleanup
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
    
  }

}
