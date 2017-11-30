package crazypants.enderio.machines.machine.generator.combustion;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCombustionGenerator extends AbstractMachineBlock<TileCombustionGenerator>
    implements IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockCombustionGenerator create() {
    PacketHandler.INSTANCE.registerMessage(PacketCombustionTank.class, PacketCombustionTank.class, PacketHandler.nextID(), Side.CLIENT);

    BlockCombustionGenerator gen = new BlockCombustionGenerator();
    gen.init();
    return gen;
  }

  protected BlockCombustionGenerator() {
    super(MachineObject.block_combustion_generator, TileCombustionGenerator.class);
  }

  @Override
  public int getLightOpacity(@Nonnull IBlockState bs) {
    return 0;
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileCombustionGenerator te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerCombustionEngine(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileCombustionGenerator te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiCombustionGenerator(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_COMBUSTION_GEN;
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
      EnumFacing dir = facing;
      float startX = pos.getX() + (dir.getFrontOffsetX() == 0 ? 0.5f : 0f);
      float startY = pos.getY() + 0.5f;
      float startZ = pos.getZ() + (dir.getFrontOffsetZ() == 0 ? 0.5f : 0f);

      if (dir.getFrontOffsetX() == 1) {
        startX++;
      } else if (dir.getFrontOffsetZ() == 1) {
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

}
