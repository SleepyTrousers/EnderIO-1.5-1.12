package crazypants.enderio.machines.machine.generator.stirling;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.baselegacy.AbstractGeneratorBlock;
import crazypants.enderio.base.machine.fuel.ISolidFuelHandler;
import crazypants.enderio.base.machine.fuel.SolidFuelCenter;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStirlingGenerator<T extends TileStirlingGenerator> extends AbstractGeneratorBlock<T>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockStirlingGenerator<TileStirlingGenerator> create(@Nonnull IModObject modObject) {
    BlockStirlingGenerator<TileStirlingGenerator> gen = new BlockStirlingGenerator<>(modObject, TileStirlingGenerator.class);
    gen.init();
    return gen;
  }

  public static BlockStirlingGenerator<TileStirlingGenerator.Simple> create_simple(@Nonnull IModObject modObject) {
    BlockStirlingGenerator<TileStirlingGenerator.Simple> gen = new BlockStirlingGenerator<TileStirlingGenerator.Simple>(modObject,
        TileStirlingGenerator.Simple.class) {
      @Override
      @SideOnly(Side.CLIENT)
      public @Nonnull IRenderMapper.IItemRenderMapper getItemRenderMapper() {
        return RenderMappers.SIMPLE_BODY_MAPPER;
      }

      @Override
      @SideOnly(Side.CLIENT)
      public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
        return RenderMappers.SIMPLE_BODY_MAPPER;
      }
    };
    gen.init();
    return gen;
  }

  protected BlockStirlingGenerator(@Nonnull IModObject modObject, @Nonnull Class<T> teClass) {
    super(modObject, teClass);
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull T te) {
    return new ContainerStirlingGenerator<T>(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull T te) {
    return new GuiStirlingGenerator<T>(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    T te = getTileEntity(world, pos);
    if (te != null && te.isActive()) {
      EnumFacing front = te.facing;
      for (int i = 0; i < 2; i++) {
        double px = pos.getX() + 0.5 + front.getFrontOffsetX() * 0.6;
        double pz = pos.getZ() + 0.5 + front.getFrontOffsetZ() * 0.6;
        double v = 0.05;
        double vx = 0;
        double vz = 0;

        if (front == EnumFacing.NORTH || front == EnumFacing.SOUTH) {
          px += world.rand.nextFloat() * 0.9 - 0.45;
          vz += front == EnumFacing.NORTH ? -v : v;
        } else {
          pz += world.rand.nextFloat() * 0.9 - 0.45;
          vx += front == EnumFacing.WEST ? -v : v;
        }

        if (te.isLavaFired && rand.nextInt(40) == 0) {
          world.spawnParticle(EnumParticleTypes.LAVA, px, pos.getY() + 0.1, pz, 0, 0, 0);
        }
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, px, pos.getY() + 0.1, pz, vx, 0, vz);
      }
    }
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull T tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

  static {
    SolidFuelCenter.registerSolidFuelHandler(new ISolidFuelHandler() {
      @Override
      public long getBurnTime(@Nonnull ItemStack itemstack) {
        return TileStirlingGenerator.getBurnTimeGeneric(itemstack);
      }
    });
  }

}
