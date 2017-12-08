package crazypants.enderio.machines.machine.generator.zombie;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
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
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
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

public class BlockZombieGenerator extends AbstractMachineBlock<TileZombieGenerator> implements IHaveTESR {

  public static final TextureSupplier textureHead1 = TextureRegistry.registerTexture("blocks/zombie_gen_head");
  public static final TextureSupplier textureHead2 = TextureRegistry.registerTexture("blocks/zombie_gen_head2");

  private static final double px = 1d / 16d;
  public static final @Nonnull AxisAlignedBB AABB = new AxisAlignedBB(2 * px, 0 * px, 2 * px, 14 * px, 16 * px, 14 * px);

  public static BlockZombieGenerator create() {
    BlockZombieGenerator gen = new BlockZombieGenerator();
    gen.init();
    return gen;
  }

  protected BlockZombieGenerator() {
    super(MachineObject.block_zombie_generator, TileZombieGenerator.class, new Material(MapColor.IRON) {

      @Override
      public boolean isOpaque() {
        return false;
      }

    });
    setLightOpacity(5);
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
        for (int i = 0; i < 2; i++) {
          float xOffset = 0.5f + (world.rand.nextFloat() * 2.0F - 1.0F) * 0.3f;
          float yOffset = 0.1f;
          float zOffset = 0.5f + (world.rand.nextFloat() * 2.0F - 1.0F) * 0.3f;

          BubbleFX fx = new BubbleFX(world, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, 0, 0.5, 0);
          Minecraft.getMinecraft().effectRenderer.addEffect(fx);

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
    ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.class, new ZombieGeneratorRenderer());
  }
}
