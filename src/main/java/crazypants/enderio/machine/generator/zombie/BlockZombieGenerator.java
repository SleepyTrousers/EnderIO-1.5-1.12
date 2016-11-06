package crazypants.enderio.machine.generator.zombie;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.killera.KillerJoeRenderMapper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.IRenderMapper.IBlockRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.registry.TextureRegistry;
import crazypants.enderio.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.sound.SoundHelper;
import crazypants.enderio.sound.SoundRegistry;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
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

  public static final TextureSupplier textureHead1 = TextureRegistry.registerTexture("blocks/zombieGen_head");
  public static final TextureSupplier textureHead2 = TextureRegistry.registerTexture("blocks/zombieGen_head2");

  private static final Double px = 1d / 16d;
  public static final AxisAlignedBB AABB = new AxisAlignedBB(2 * px, 0 * px, 2 * px, 14 * px, 16 * px, 14 * px);

  public static BlockZombieGenerator create() {
    BlockZombieGenerator gen = new BlockZombieGenerator();
    gen.init();
    return gen;
  }

  protected BlockZombieGenerator() {
    super(ModObject.blockZombieGenerator, TileZombieGenerator.class, new Material(MapColor.IRON) {

      @Override
      public boolean isOpaque() {
        return false;
      }

    });
    setLightOpacity(5);
  }
  
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return AABB;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new ContainerZombieGenerator(player.inventory, (TileZombieGenerator) world.getTileEntity(new BlockPos(x, y, z)));
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiZombieGenerator(player.inventory, (TileZombieGenerator) world.getTileEntity(new BlockPos(x, y, z)));
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_ZOMBIE_GEN;
  }

  @Override
  public int getLightOpacity(IBlockState bs) {
    return 0;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(IBlockState bs) {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
    if(rand.nextInt(3) == 0) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileZombieGenerator && ((TileZombieGenerator) te).isActive()) {
        for (int i = 0; i < 2; i++) {
          float xOffset = 0.5f + (world.rand.nextFloat() * 2.0F - 1.0F) * 0.3f;
          float yOffset = 0.1f;
          float zOffset = 0.5f + (world.rand.nextFloat() * 2.0F - 1.0F) * 0.3f;
          
          BubbleFX fx = new BubbleFX(world, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, 0, 0.5, 0);
          Minecraft.getMinecraft().effectRenderer.addEffect(fx);

        }

        if(Config.machineSoundsEnabled) {
          SoundHelper.playSound(world, pos, SoundHelper.BLOCK_TOP, SoundRegistry.ZOMBIE_BUBBLE, Config.machineSoundVolume * 0.045f,
              world.rand.nextFloat() * 0.75f);
        }
      }
    }
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te != null && te instanceof TileZombieGenerator) {
      tooltip.add(((TileZombieGenerator) te).getFluidStored(EnumFacing.NORTH) + " " + EnderIO.lang.localize("fluid.millibucket.abr"));
    }
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileZombieGenerator tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return KillerJoeRenderMapper.zombieGen;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IBlockRenderMapper getBlockRenderMapper() {
    return KillerJoeRenderMapper.zombieGen;
  }

  @Override
  public boolean canRenderInLayer(BlockRenderLayer layer) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileZombieGenerator.class, new ZombieGeneratorRenderer());
  }
}
