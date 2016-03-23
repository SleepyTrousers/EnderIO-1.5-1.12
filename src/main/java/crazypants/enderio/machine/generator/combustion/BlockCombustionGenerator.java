package crazypants.enderio.machine.generator.combustion;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.RenderMappers;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;

public class BlockCombustionGenerator extends AbstractMachineBlock<TileCombustionGenerator> implements IPaintable.INonSolidBlockPaintableBlock,
    IPaintable.IWrenchHideablePaint {

  
  public static BlockCombustionGenerator create() {
    PacketHandler.INSTANCE.registerMessage(PacketCombustionTank.class, PacketCombustionTank.class, PacketHandler.nextID(), Side.CLIENT);

    BlockCombustionGenerator gen = new BlockCombustionGenerator();
    gen.init();
    return gen;
  } 
 
  protected BlockCombustionGenerator() {
    super(ModObject.blockCombustionGenerator, TileCombustionGenerator.class);
  }

  @Override
  public int getLightOpacity() {
    return 0;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileCombustionGenerator) {
      return new ContainerCombustionEngine(player.inventory, (TileCombustionGenerator) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileCombustionGenerator) {
      return new GuiCombustionGenerator(player.inventory, (TileCombustionGenerator) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_COMBUSTION_GEN;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
      // If active, randomly throw some smoke around
    if(isActive(world, pos)) {

      TileEntity te = world.getTileEntity(pos);
      EnumFacing facing = EnumFacing.SOUTH;
      if(te instanceof AbstractMachineEntity) {
        AbstractMachineEntity me = (AbstractMachineEntity) te;
        facing = me.facing;
      }
      EnumFacing dir = facing;
      float startX = pos.getX() + (dir.getFrontOffsetX() == 0 ? 0.5f : 0f);
      float startY = pos.getY() + 0.5f;
      float startZ = pos.getZ() + (dir.getFrontOffsetZ() == 0 ? 0.5f : 0f);

      if(dir.getFrontOffsetX() == 1) {
        startX++;
      } else if(dir.getFrontOffsetZ() == 1) {
        startZ++;
      }

      for (int i = 0; i < 2; i++) {
        float xOffset = 0;
        float yOffset = 0;
        float zOffset = 0;
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
      }
    }
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileCombustionGenerator tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
