package crazypants.enderio.machine.soul;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.client.ClientUtil;

import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.xp.PacketDrainPlayerXP;
import crazypants.enderio.xp.PacketExperianceContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSoulBinder extends AbstractMachineBlock<TileSoulBinder>
    implements IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR {

  public static BlockSoulBinder create() {
    PacketDrainPlayerXP.register();
    PacketExperianceContainer.register();
    BlockSoulBinder result = new BlockSoulBinder();
    result.init();
    return result;
  }

  protected BlockSoulBinder() {
    super(ModObject.blockSoulBinder, TileSoulBinder.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileSoulBinder) {
      return new ContainerSoulBinder(player.inventory, (TileSoulBinder) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileSoulBinder) {
      return new GuiSoulBinder(player.inventory, (TileSoulBinder) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_SOUL_BINDER;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  public int getLightOpacity(IBlockState bs) {
    return 0;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    // If active, randomly throw some smoke around
    if (isActive(world, pos)) {
      float startX = x + 1.0F;
      float startY = y + 1.0F;
      float startZ = z + 1.0F;
      for (int i = 0; i < 2; i++) {
        float xOffset = -0.2F - rand.nextFloat() * 0.6F;
        float yOffset = -0.1F + rand.nextFloat() * 0.2F;
        float zOffset = -0.2F - rand.nextFloat() * 0.6F;

        Particle fx = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), startX + xOffset, startY + yOffset,
            startZ + zOffset, 0.0D, 0.0D, 0.0D, 0);
        if (fx != null) {
          fx.setRBGColorF(0.2f, 0.2f, 0.8f);
          ClientUtil.setParticleVelocityY(fx, ClientUtil.getParticleVelocityY(fx) * 0.5);
        }

      }
    }
  }

  @Override
  public IItemRenderMapper getItemRenderMapper() {
    return SoulBinderBlockRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return SoulBinderBlockRenderMapper.instance;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileSoulBinder tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isWorking());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileSoulBinder.class, new SoulBinderTESR());
  }

}
