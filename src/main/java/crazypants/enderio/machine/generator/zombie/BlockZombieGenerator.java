package crazypants.enderio.machine.generator.zombie;

import java.util.List;
import java.util.Random;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockZombieGenerator extends AbstractMachineBlock<TileZombieGenerator> {

  public static BlockZombieGenerator create() {
    BlockZombieGenerator gen = new BlockZombieGenerator();
    gen.init();
    return gen;
  }

  protected BlockZombieGenerator() {
    super(ModObject.blockZombieGenerator, TileZombieGenerator.class, Material.anvil);
  }
  
  @Override
  public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {  
    return true;
  }

  @Override
  public boolean isReplaceable(World worldIn, BlockPos pos) {  
    return false;
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
  protected int getGuiId() {
    return GuiHandler.GUI_ID_ZOMBIE_GEN;
  }

  @Override
  public int getLightOpacity() {
    return 0;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
    if(rand.nextInt(3) == 0) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileZombieGenerator && ((TileZombieGenerator) te).isActive()) {
        for (int i = 0; i < 2; i++) {
          float xOffset = 0.5f + (world.rand.nextFloat() * 2.0F - 1.0F) * 0.3f;
          float yOffset = 0.1f;
          float zOffset = 0.5f + (world.rand.nextFloat() * 2.0F - 1.0F) * 0.3f;
          
          EntityFX fx = new BubbleFX(world, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, 0, 0.5, 0);
          Minecraft.getMinecraft().effectRenderer.addEffect(fx);

        }

        if(Config.machineSoundsEnabled) {
          float volume = (Config.machineSoundVolume * 0.045f);
          world.playSound(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, EnderIO.DOMAIN + ":generator.zombie.bubble", volume, world.rand.nextFloat() * 0.75f, false);
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

}
