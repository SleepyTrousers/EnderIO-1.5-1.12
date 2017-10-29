package crazypants.enderio.machine.generator.stirling;

import crazypants.enderio.GuiID;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockStirlingGenerator extends AbstractMachineBlock<TileEntityStirlingGenerator> implements IPaintable.ISolidBlockPaintableBlock,
    IPaintable.IWrenchHideablePaint {

  public static BlockStirlingGenerator create() {

    PacketHandler.INSTANCE.registerMessage(PacketBurnTime.class, PacketBurnTime.class, PacketHandler.nextID(), Side.CLIENT);

    BlockStirlingGenerator gen = new BlockStirlingGenerator();
    gen.init();
    return gen;
  }

  protected BlockStirlingGenerator() {
    super(MachineObject.blockStirlingGenerator, TileEntityStirlingGenerator.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new StirlingGeneratorContainer(player.inventory, (TileEntityStirlingGenerator) world.getTileEntity(new BlockPos(x, y, z)));
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiStirlingGenerator(player.inventory, (TileEntityStirlingGenerator) world.getTileEntity(new BlockPos(x, y, z)));
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_STIRLING_GEN;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
    TileEntityStirlingGenerator te = (TileEntityStirlingGenerator) world.getTileEntity(pos);
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
      @Nonnull TileEntityStirlingGenerator tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
