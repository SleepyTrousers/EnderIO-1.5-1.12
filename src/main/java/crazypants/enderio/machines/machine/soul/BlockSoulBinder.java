package crazypants.enderio.machines.machine.soul;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.client.ClientUtil;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.render.SoulBinderBlockRenderMapper;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSoulBinder extends AbstractMachineBlock<TileSoulBinder>
    implements IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR {

  public static BlockSoulBinder create(@Nonnull IModObject modObject) {
    // PacketDrainPlayerXP.register();//TODO Fix
    BlockSoulBinder result = new BlockSoulBinder(modObject);
    result.init();
    return result;
  }

  protected BlockSoulBinder(@Nonnull IModObject modObject) {
    super(modObject, TileSoulBinder.class);
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileSoulBinder te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerSoulBinder(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileSoulBinder te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiSoulBinder(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_SOUL_BINDER;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public int getLightOpacity(@Nonnull IBlockState bs) {
    return 0;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
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
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
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
