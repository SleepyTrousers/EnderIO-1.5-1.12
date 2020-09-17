package crazypants.enderio.machines.machine.generator.zombie;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.machine.baselegacy.AbstractGeneratorBlock;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper.IBlockRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.sound.SoundHelper;
import crazypants.enderio.base.sound.SoundRegistry;
import crazypants.enderio.machines.config.config.ClientConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.killera.KillerJoeRenderMapper;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockZombieGenerator<T extends TileZombieGenerator> extends AbstractGeneratorBlock<T> implements IHaveTESR {

  public static final @Nonnull TextureSupplier textureHead1 = TextureRegistry.registerTexture("blocks/zombie_gen_head");
  public static final @Nonnull TextureSupplier textureHead2 = TextureRegistry.registerTexture("blocks/zombie_gen_head2");
  public static final @Nonnull TextureSupplier textureHeadEnder1 = TextureRegistry.registerTexture("blocks/ender_gen_head");
  public static final @Nonnull TextureSupplier textureHeadEnder2 = TextureRegistry.registerTexture("blocks/ender_gen_head2");

  private static final double px = 1d / 16d;
  public static final @Nonnull AxisAlignedBB AABB = new AxisAlignedBB(2 * px, 0 * px, 2 * px, 14 * px, 16 * px, 14 * px);

  public static Block create(@Nonnull IModObject modObject) {
    BlockZombieGenerator<TileZombieGenerator> gen = new BlockZombieGenerator<>(modObject);
    gen.init();
    return gen;
  }

  public static Block create_franken(@Nonnull IModObject modObject) {
    BlockFrankNZombieGenerator gen = new BlockFrankNZombieGenerator(modObject);
    gen.init();
    return gen;
  }

  public static Block create_ender(@Nonnull IModObject modObject) {
    BlockEnderGenerator gen = new BlockEnderGenerator(modObject);
    gen.init();
    return gen;
  }

  private static class BlockFrankNZombieGenerator extends BlockZombieGenerator<TileZombieGenerator.TileFrankenZombieGenerator> {

    public BlockFrankNZombieGenerator(@Nonnull IModObject modObject) {
      super(modObject);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void bindTileEntitySpecialRenderer() {
      ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.TileFrankenZombieGenerator.class,
          new ZombieGeneratorRenderer(MachineObject.block_franken_zombie_generator.getBlockNN()));
    }
  }

  private static class BlockEnderGenerator extends BlockZombieGenerator<TileZombieGenerator.TileEnderGenerator> {

    public BlockEnderGenerator(@Nonnull IModObject modObject) {
      super(modObject);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void bindTileEntitySpecialRenderer() {
      ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.TileEnderGenerator.class,
          new ZombieGeneratorRenderer(MachineObject.block_ender_generator.getBlockNN()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public @Nonnull IItemRenderMapper getItemRenderMapper() {
      return KillerJoeRenderMapper.enderGen;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBlockRenderMapper getBlockRenderMapper() {
      return KillerJoeRenderMapper.enderGen;
    }
  }

  protected BlockZombieGenerator(@Nonnull IModObject modObject) {
    super(modObject, new Material(MapColor.IRON) {

      @Override
      public boolean isOpaque() {
        return false;
      }

    });
    setLightOpacity(5);
    setShape(mkShape(BlockFaceShape.MIDDLE_POLE_THICK, BlockFaceShape.MIDDLE_POLE_THICK, BlockFaceShape.UNDEFINED));
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
    return AABB;
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileZombieGenerator te) {
    return new ContainerZombieGenerator(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileZombieGenerator te) {
    return new GuiZombieGenerator(player.inventory, te);
  }

  @Override
  public int getLightOpacity(@Nonnull IBlockState bs) {
    return 0;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState bs) {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    if (rand.nextInt(3) == 0) {
      TileZombieGenerator te = getTileEntity(world, pos);
      if (te != null && te.isActive()) {
        if (PersonalConfig.machineParticlesEnabled.get()) {
          for (int i = 0; i < 2; i++) {
          float xOffset = 0.5f + (world.rand.nextFloat() * 2.0F - 1.0F) * 0.3f;
          float yOffset = 0.1f;
          float zOffset = 0.5f + (world.rand.nextFloat() * 2.0F - 1.0F) * 0.3f;

          BubbleFX fx = new BubbleFX(world, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, 0, 0.5, 0);
          Minecraft.getMinecraft().effectRenderer.addEffect(fx);
        }
      }

        if (ClientConfig.machineSoundsEnabled.get()) {
          SoundHelper.playSound(world, pos, SoundHelper.BLOCK_TOP, SoundRegistry.ZOMBIE_BUBBLE, ClientConfig.machineSoundVolume.get() * 0.045f,
              world.rand.nextFloat() * 0.75f);
        }
      }
    }
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileZombieGenerator tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return KillerJoeRenderMapper.zombieGen;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockRenderMapper getBlockRenderMapper() {
    return KillerJoeRenderMapper.zombieGen;
  }

  @Override
  public boolean canRenderInLayer(@Nonnull IBlockState state, @Nonnull BlockRenderLayer layer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.class, new ZombieGeneratorRenderer(MachineObject.block_zombie_generator.getBlockNN()));
  }

}
