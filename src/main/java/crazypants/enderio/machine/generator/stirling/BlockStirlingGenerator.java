package crazypants.enderio.machine.generator.stirling;

import java.util.Random;

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
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.render.BlockStateWrapper;

public class BlockStirlingGenerator extends AbstractMachineBlock<TileEntityStirlingGenerator> {

  public static BlockStirlingGenerator create() {

    PacketHandler.INSTANCE.registerMessage(PacketBurnTime.class, PacketBurnTime.class, PacketHandler.nextID(), Side.CLIENT);

    BlockStirlingGenerator gen = new BlockStirlingGenerator();
    gen.init();
    return gen;
  }

  protected BlockStirlingGenerator() {
    super(ModObject.blockStirlingGenerator, TileEntityStirlingGenerator.class);
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
  protected int getGuiId() {
    return GuiHandler.GUI_ID_STIRLING_GEN;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
    TileEntityStirlingGenerator te = (TileEntityStirlingGenerator) world.getTileEntity(pos);
    if (te != null && te.isActive()) {
      EnumFacing front = te.facing;
      for (int i = 0; i < 2; i++) {
        double px = pos.getX() + 0.5 + front.getFrontOffsetX() * 0.6;
        double pz = pos.getY() + 0.5 + front.getFrontOffsetZ() * 0.6;
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

        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, px, pos.getY() + 0.1, pz, vx, 0, vz);
      }
    }
  }

  @Override
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    BlockStateWrapper extendedState = (BlockStateWrapper) super.getExtendedState(state, world, pos);
    TileEntity tileEntity = extendedState.getTileEntity();
    if (tileEntity instanceof AbstractMachineEntity) {
      extendedState.setCacheKey(((AbstractMachineEntity) tileEntity).getFacing(), ((AbstractMachineEntity) tileEntity).isActive());
    }
    return extendedState;
  }

}
