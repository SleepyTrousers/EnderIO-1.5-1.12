package crazypants.enderio.machine.vat;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.client.ClientUtil;

import crazypants.enderio.GuiID;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.machine.render.RenderMappers;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayer;
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
  public int getLightOpacity(IBlockState state) {
    return 0;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (world != null) {
      TileVat te = getTileEntity(world, new BlockPos(x, y, z));
      if (te != null) {
        return new ContainerVat(player.inventory, te);
      }
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (world != null) {
      TileVat te = getTileEntity(world, new BlockPos(x, y, z));
      if (te != null) {
        return new GuiVat(player.inventory, te);
      }
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_VAT;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos, EnumFacing side) {
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
  public IItemRenderMapper getItemRenderMapper() {
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
