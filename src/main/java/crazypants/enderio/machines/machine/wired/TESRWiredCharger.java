package crazypants.enderio.machines.machine.wired;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TESRWiredCharger<T extends TileWiredCharger> extends ManagedTESR<T> {

  private final @Nonnull Random rand = new Random();

  public TESRWiredCharger(Block block) {
    super(block);
  }

  @Override
  protected boolean shouldRender(@Nonnull T te, @Nonnull IBlockState blockState, int renderPass) {
    return Prep.isValid(getFloatingItem(te));
  }

  @Override
  protected void renderTileEntity(@Nonnull T te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    renderItemStack(te.getFacing(), te.getPos(), getFloatingItem(te), te.getWorld(), partialTicks);
  }

  protected void renderItemStack(@Nonnull EnumFacing facing, @Nonnull BlockPos pos, @Nonnull ItemStack floatingItem, @Nonnull World world, float tick) {

    RenderUtil.bindBlockTexture();

    rand.setSeed(pos.getX() + pos.getY() + pos.getZ());
    rand.nextBoolean();

    int i = world.getCombinedLight(pos.offset(facing), 0);
    int j = i % 65536;
    int k = (int) (180 + (60 * MathHelper.sin((float) ((EnderIO.proxy.getTickCount() * 0.145D + (tick * 0.145D) + rand.nextDouble()) % (Math.PI * 2)))));
    if (EnderIO.proxy.getTickCount() % 10 == 0) {
      // j = k = 240;
    }
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);

    GlStateManager.pushMatrix();
    EnumFacing offsetDir = facing.rotateY();
    double offsetX = 0.5 + (offsetDir.getFrontOffsetX() * 2.5 / 16D) + (facing.getFrontOffsetX() * 3.0 / 16D);
    double offsetY = 5 / 16D;
    double offsetZ = 0.5 + (offsetDir.getFrontOffsetZ() * 2.5 / 16D) + (facing.getFrontOffsetZ() * 3.0 / 16D);
    GlStateManager.translate(offsetX, offsetY, offsetZ);
    GlStateManager.scale(0.9, 0.9, 0.9);
    // glScalef(1.1f, 1.1f, 1.1f);

    if (Minecraft.getMinecraft().gameSettings.fancyGraphics) {
      GlStateManager.rotate(rand.nextFloat() * 360f, 0, 1, 0);
    }
    double rot = (EnderIO.proxy.getTickCount() * 0.05D + (tick * 0.05D) + rand.nextDouble()) % (Math.PI * 2);

    doRender(floatingItem, rot);

    GlStateManager.popMatrix();
  }

  protected @Nonnull ItemStack getFloatingItem(@Nonnull T te) {
    return te.getStackInSlot(te.getSlotDefinition().minInputSlot);
  }

  public void doRender(@Nonnull ItemStack itemstack, double rot) {
    GlStateManager.enableLighting();
    GlStateManager.enableRescaleNormal();
    GlStateManager.alphaFunc(516, 0.1F);
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.pushMatrix();
    IBakedModel ibakedmodel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(itemstack);
    float f2 = ibakedmodel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
    GlStateManager.translate(0, 0.25F * f2, 0);

    double f3 = rot * (180F / (float) Math.PI);
    GlStateManager.rotate((float) f3, 0.0F, 1.0F, 0.0F);

    ibakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND, false);
    if (ibakedmodel != null) {
      Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
    }

    GlStateManager.popMatrix();
    GlStateManager.disableRescaleNormal();
    GlStateManager.disableBlend();
  }

}
