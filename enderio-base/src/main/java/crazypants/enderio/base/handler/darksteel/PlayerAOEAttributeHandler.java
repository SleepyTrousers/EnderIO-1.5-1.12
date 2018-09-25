package crazypants.enderio.base.handler.darksteel;

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.blockiterators.AbstractBlockIterator;
import com.enderio.core.common.util.blockiterators.CubicBlockIterator;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelPickaxe;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID)
public class PlayerAOEAttributeHandler {

  public static final @Nonnull IAttribute AOE_XZ = new RangedAttribute(null, "enderio.aoe.xz", 0.0D, 0.0D, 16).setShouldWatch(true);
  public static final @Nonnull IAttribute AOE_Y = new RangedAttribute(null, "enderio.aoe.y", 0.0D, 0.0D, 16).setShouldWatch(true);
  public static final @Nonnull IAttribute AOE_XYZ = new RangedAttribute(null, "enderio.aoe.xyz", 0.0D, 0.0D, 16).setShouldWatch(true);

  @SubscribeEvent
  public static void handleConstruct(@Nonnull net.minecraftforge.event.entity.EntityEvent.EntityConstructing event) {
    /*
     * This event is fired in the constructor of Entity. The attribute map we add values to belongs to EntityLiving. So this should not actually work, as
     * EntityLiving has not yet been constructed fully when we are called. But luckily for us, EntityLiving does not initialize the attribute map field with
     * anything. So our value gets put in too early but is then not overwritten. However, I doubt the JVM specs actually allow this loophole. So this may stop
     * working at any time.
     * 
     * And when it does, there will be three warnings about ignored unknown SharedMonsterAttributes when a player is loaded from the server's save file. Which
     * can be ignored because they are of no consequence.
     * 
     */
    handleAttributes(event);
  }

  @SubscribeEvent
  public static void handleJoin(@Nonnull EntityJoinWorldEvent event) {
    handleAttributes(event);
  }

  private static void handleAttributes(@Nonnull EntityEvent event) {
    if (event.getEntity() instanceof EntityPlayer) {
      final AbstractAttributeMap map = ((EntityLivingBase) event.getEntity()).getAttributeMap();
      if (NullHelper.untrust(map.getAttributeInstance(AOE_XZ)) == null) {
        map.registerAttribute(AOE_XZ).setBaseValue(0);
        map.registerAttribute(AOE_Y).setBaseValue(0);
        map.registerAttribute(AOE_XYZ).setBaseValue(0);
      }
    }
  }

  public static boolean hasAOE(@Nonnull EntityPlayer player) {
    return player.getEntityAttribute(AOE_XZ).getAttributeValue() > 0 || player.getEntityAttribute(AOE_Y).getAttributeValue() > 0
        || player.getEntityAttribute(AOE_XYZ).getAttributeValue() > 0;
  }

  public static @Nonnull AxisAlignedBB expandBBbyAOE(@Nonnull EntityPlayer player, @Nonnull AxisAlignedBB bb, RayTraceResult rtr) {
    int xz = (int) player.getEntityAttribute(AOE_XZ).getAttributeValue();
    int y = (int) player.getEntityAttribute(AOE_Y).getAttributeValue();
    int xyz = (int) player.getEntityAttribute(AOE_XYZ).getAttributeValue();
    switch (rtr.sideHit) {
    case DOWN:
      return bb.grow(xz, 0, xz).expand(0, y, 0).grow(xyz);
    case UP:
      return bb.grow(xz, 0, xz).expand(0, -y, 0).grow(xyz);
    default:
      int shift = BlockCoord.get(player).up().getY() == bb.minY ? (xz + xyz) - 1 : 0;
      switch (rtr.sideHit) {
      case EAST:
        return bb.grow(0, xz, xz).expand(-y, 0, 0).grow(xyz).offset(0, shift, 0);
      case WEST:
        return bb.grow(0, xz, xz).expand(y, 0, 0).grow(xyz).offset(0, shift, 0);
      case NORTH:
        return bb.grow(xz, xz, 0).expand(0, 0, y).grow(xyz).offset(0, shift, 0);
      case SOUTH:
        return bb.grow(xz, xz, 0).expand(0, 0, -y).grow(xyz).offset(0, shift, 0);
      default:
        return bb.grow(xz, xz, xz).grow(xyz).offset(0, shift, 0);
      }
    }
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onHighlight(@Nonnull DrawBlockHighlightEvent event) {
    final RayTraceResult movingObjectPositionIn = event.getTarget();
    if (movingObjectPositionIn.typeOfHit == RayTraceResult.Type.BLOCK) {
      final EntityPlayer player = event.getPlayer();
      if (player != null && !player.isSneaking() && hasAOE(player)) {
        final BlockPos blockpos = movingObjectPositionIn.getBlockPos();
        final World world = player.world;
        if (world.getWorldBorder().contains(blockpos)) {
          final IBlockState iblockstate = world.getBlockState(blockpos);
          if (iblockstate.getMaterial() != Material.AIR) {
            float partialTicks = event.getPartialTicks();
            final double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            final double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            final double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            final AxisAlignedBB origin = new AxisAlignedBB(blockpos);
            final AxisAlignedBB bb = expandBBbyAOE(player, origin, movingObjectPositionIn).grow(0.0020000000949949026D).offset(-d3, -d4, -d5);

            drawSelectionBoundingBox(origin.grow(0.0020000000949949026D).offset(-d3, -d4, -d5), bb, 0.25F, 0.0F, 0.0F, 0.4F);
          }
        }
      }
    }
  }

  public static @Nullable RayTraceResult rayTrace(@Nonnull EntityPlayer player) {
    return ((ItemDarkSteelPickaxe) ModObject.itemDarkSteelPickaxe.getItemNN()).rayTrace(player.world, player, false);
  }

  private static class BBIterator extends CubicBlockIterator {

    public BBIterator(@Nonnull BlockPos origin, @Nonnull BoundingBox bb) {
      super(origin, (int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX - 1, (int) bb.maxY - 1, (int) bb.maxZ - 1);
    }

    @Override
    public @Nonnull BlockPos next() {
      final BlockPos next = super.next();
      return next.equals(base) && hasNext() ? super.next() : next;
    }

  }

  public static Iterator<BlockPos> getAOE(@Nonnull BlockPos origin, @Nonnull EntityPlayer player) {
    RayTraceResult movingObjectPositionIn = rayTrace(player);
    if (movingObjectPositionIn != null && movingObjectPositionIn.typeOfHit == RayTraceResult.Type.BLOCK) {
      if (origin.equals(movingObjectPositionIn.getBlockPos())) {
        return new BBIterator(origin, new BoundingBox(expandBBbyAOE(player, new AxisAlignedBB(origin), movingObjectPositionIn)));
      }
    }
    return new AbstractBlockIterator(origin) {

      @Override
      public BlockPos next() {
        return null;
      }

      @Override
      public boolean hasNext() {
        return false;
      }
    };
  }

  /**
   * See {@link RenderGlobal#drawSelectionBoundingBox(AxisAlignedBB, float, float, float, float)}
   */
  @SideOnly(Side.CLIENT)
  public static void drawSelectionBoundingBox(@Nonnull AxisAlignedBB origin, @Nonnull AxisAlignedBB box, float red, float green, float blue, float alpha) {
    final double minXO = origin.minX, minYO = origin.minY, minZO = origin.minZ, maxXO = origin.maxX, maxYO = origin.maxY, maxZO = origin.maxZ;
    final double minX = box.minX, minY = box.minY, minZ = box.minZ, maxX = box.maxX, maxY = box.maxY, maxZ = box.maxZ;

    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
        GlStateManager.DestFactor.ZERO);
    GlStateManager.glLineWidth(2.0F);
    GlStateManager.disableTexture2D();
    GlStateManager.depthMask(false);

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();
    bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
    bufferbuilder.pos(minXO, minYO, minZO).color(red, green, blue, 0.0F).endVertex();
    bufferbuilder.pos(minX, minY, minZ).color(red, green, blue, alpha * .5f).endVertex();
    bufferbuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(minXO, maxYO, minZO).color(red, green, blue, alpha * .5f).endVertex();

    bufferbuilder.pos(minX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
    bufferbuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(minXO, minYO, maxZO).color(red, green, blue, alpha * .5f).endVertex();

    bufferbuilder.pos(maxX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
    bufferbuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(maxXO, minYO, maxZO).color(red, green, blue, alpha * .5f).endVertex();

    bufferbuilder.pos(maxX, maxY, minZ).color(red, green, blue, 0.0F).endVertex();
    bufferbuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
    bufferbuilder.pos(maxXO, minYO, minZO).color(red, green, blue, alpha * .5f).endVertex();

    bufferbuilder.pos(maxXO, maxYO, minZO).color(red, green, blue, 0.0F).endVertex();
    bufferbuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha * .5f).endVertex();

    bufferbuilder.pos(minXO, maxYO, maxZO).color(red, green, blue, 0.0F).endVertex();
    bufferbuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha * .5f).endVertex();

    bufferbuilder.pos(maxXO, maxYO, maxZO).color(red, green, blue, 0.0F).endVertex();
    bufferbuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha * .5f).endVertex();

    tessellator.draw();

    GlStateManager.depthMask(true);
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }

}
