package crazypants.enderio.machines.machine.generator.combustion;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.BlockMachineExtension;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.machine.baselegacy.AbstractGeneratorBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCombustionGenerator<T extends TileCombustionGenerator> extends AbstractGeneratorBlock<T>
    implements IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  protected boolean isEnhanced = false;

  public static BlockCombustionGenerator<TileCombustionGenerator> create(@Nonnull IModObject modObject) {
    BlockCombustionGenerator<TileCombustionGenerator> gen = new BlockCombustionGenerator<>(modObject);
    gen.init();
    return gen;
  }

  public static BlockCombustionGenerator<TileCombustionGenerator.Enhanced> create_enhanced(@Nonnull IModObject modObject) {
    BlockCombustionGenerator<TileCombustionGenerator.Enhanced> gen = new BlockCombustionGenerator<>(modObject);
    gen.init();
    gen.isEnhanced = true;
    return gen;
  }

  public static BlockMachineExtension create_extension(@Nonnull IModObject modObject) {
    return new BlockMachineExtension(modObject, MachineObject.block_enhanced_combustion_generator, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 11D / 16D, 1.0D));
  }

  protected BlockCombustionGenerator(@Nonnull IModObject modObject) {
    super(modObject);
  }

  @Override
  public int getLightOpacity(@Nonnull IBlockState bs) {
    return 0;
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull T te) {
    return new ContainerCombustionGenerator<T>(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull T te) {
    return new GuiCombustionGenerator<T>(player.inventory, te);
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    // If active, randomly throw some smoke around
    if (isActive(world, pos)) {

      TileEntity te = world.getTileEntity(pos);
      EnumFacing facing = EnumFacing.SOUTH;
      if (te instanceof AbstractMachineEntity) {
        AbstractMachineEntity me = (AbstractMachineEntity) te;
        facing = me.facing;
      }
      for (int j = 0; j < (isEnhanced ? 3 : 1); j++) {

        boolean toTop = rand.nextBoolean();
        float offsetA = rand.nextFloat(); // top:front<->back or side:bottom<->top
        float offsetB = .5f + rand.nextFloat() * .2f - rand.nextFloat() * .2f; // right<->left

        float startX = pos.getX(), startY = pos.getY(), startZ = pos.getZ();

        if (toTop) {
          startY += 0.95f;
          switch (facing) {
          case NORTH:
          case SOUTH:
            startX += offsetB;
            startZ += offsetA;
            break;
          case EAST:
          case WEST:
          default:
            startX += offsetA;
            startZ += offsetB;
            break;
          }
        } else {
          boolean swap = rand.nextBoolean();
          startY += offsetA;
          switch (facing) {
          case NORTH:
          case SOUTH:
            startX += offsetB;
            startZ += swap ? 0.05f : 0.95f;
            break;
          case EAST:
          case WEST:
          default:
            startX += swap ? 0.05f : 0.95f;
            startZ += offsetB;
            break;
          }
        }

        for (int i = 0; i < (isEnhanced ? 5 : 2); i++) {
          ParticleManager er = Minecraft.getMinecraft().effectRenderer;
          Particle fx = er.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), startX, startY, startZ, 0.0D, 0.0D, 0.0D);
          if (fx != null && rand.nextFloat() > .75f) {
            fx.setRBGColorF(1 - (rand.nextFloat() * 0.2f), 1 - (rand.nextFloat() * 0.1f), 1 - (rand.nextFloat() * 0.2f));
          }
          startX += rand.nextFloat() * .1f - rand.nextFloat() * .1f;
          startY += rand.nextFloat() * .1f - rand.nextFloat() * .1f;
          startZ += rand.nextFloat() * .1f - rand.nextFloat() * .1f;
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
      @Nonnull TileCombustionGenerator tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

  @Override
  public boolean canPlaceBlockAt(@Nonnull World world, @Nonnull BlockPos pos) {
    return super.canPlaceBlockAt(world, pos) && (!isEnhanced || (pos.getY() < 255 && super.canPlaceBlockAt(world, pos.up())));
  }

  @Override
  public void onBlockPlaced(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase player, @Nonnull T te) {
    super.onBlockPlaced(world, pos, state, player, te);
    if (isEnhanced) {
      world.setBlockState(pos.up(), MachineObject.block_enhanced_combustion_generator_top.getBlockNN().getDefaultState());
    }
  }

  @Override
  public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
    if (isEnhanced) {
      if (world.getBlockState(pos.up()).getBlock() != MachineObject.block_enhanced_combustion_generator_top.getBlockNN()) {
        if (super.canPlaceBlockAt(world, pos.up())) {
          world.setBlockState(pos.up(), MachineObject.block_enhanced_combustion_generator_top.getBlockNN().getDefaultState());
        } else {
          // impossible error state a.k.a. someone ripped the machine apart. And what do combustion engines that are ripped apart do? They combust. Violently.
          world.createExplosion(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 3f, true); // 3 == normal Creeper
        }
      }
    }

    super.neighborChanged(state, world, pos, blockIn, fromPos);
  }

}
