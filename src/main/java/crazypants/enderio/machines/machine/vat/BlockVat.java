package crazypants.enderio.machines.machine.vat;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.ClientUtil;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVat extends AbstractMachineBlock<TileVat> implements IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockVat create(@Nonnull IModObject modObject) {
    PacketHandler.INSTANCE.registerMessage(PacketTanks.class, PacketTanks.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketVatProgress.class, PacketVatProgress.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketDumpTank.class, PacketDumpTank.class, PacketHandler.nextID(), Side.SERVER);
    BlockVat res = new BlockVat(modObject);
    res.init();
    return res;
  }

  public BlockVat(@Nonnull IModObject modObject) {
    super(modObject, TileVat.class);
  }

  @Override
  public int getLightOpacity(@Nonnull IBlockState state) {
    return 0;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileVat te) {
    return new ContainerVat(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileVat te) {
    return new GuiVat(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    // Spit some "steam" out the spout
    TileVat te = getTileEntity(world, pos);
    if (te != null && te.isActive()) {
      float pX = pos.getX() + 0.5f;
      float pY = pos.getY() + 0.7f;
      float pZ = pos.getZ() + 0.5f;

      EnumFacing dir = te.getFacing();
      pX += 0.6f * dir.getFrontOffsetX();
      pZ += 0.6f * dir.getFrontOffsetZ();

      double velX = ((rand.nextDouble() * 0.075) + 0.025) * dir.getFrontOffsetX();
      double velZ = ((rand.nextDouble() * 0.075) + 0.025) * dir.getFrontOffsetZ();
      int num = rand.nextInt(4) + 2;
      for (int k = 0; k < num; k++) {
        ParticleManager er = Minecraft.getMinecraft().effectRenderer;
        Particle fx = er.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), pX, pY, pZ, 1, 1, 1, 0);
        if (fx != null) {
          fx.setRBGColorF(1 - (rand.nextFloat() * 0.2f), 1 - (rand.nextFloat() * 0.1f), 1 - (rand.nextFloat() * 0.2f));
          ClientUtil.setParticleVelocity(fx, velX, -0.06, velZ);
        }
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileVat tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
