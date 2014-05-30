package crazypants.enderio.machine.generator.zombie;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.util.FluidUtil;
import crazypants.util.Util;

public class BlockZombieGenerator extends AbstractMachineBlock<TileZombieGenerator> {

  public static BlockZombieGenerator create() {
    
    EnderIO.packetPipeline.registerPacket(PacketTank.class);
    
    BlockZombieGenerator gen = new BlockZombieGenerator();
    gen.init();
    return gen;
  }

  protected BlockZombieGenerator() {
    super(ModObject.blockZombieGenerator, TileZombieGenerator.class, Material.water);
  }
  
  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
  
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileZombieGenerator)) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    TileZombieGenerator gen = (TileZombieGenerator) te;
    ItemStack item = entityPlayer.inventory.getCurrentItem();
    if(item == null) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    //check for filled fluid containers and see if we can empty them into our tanks
    FluidStack fluid = FluidUtil.getFluidFromItem(item);
    if(fluid != null) {
      int filled = gen.fill(ForgeDirection.UP, fluid, false);
      if(filled >= fluid.amount) {
        gen.fill(ForgeDirection.UP, fluid, true);
        if(!entityPlayer.capabilities.isCreativeMode) {
          entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, Util.consumeItem(item));
        }
        return true;
      }
    }

    return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
  }  
  
  @Override
  public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    return true;
  }

  @Override
  public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
    return false;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new ContainerZombieGenerator(player.inventory, (TileZombieGenerator) world.getTileEntity(x, y, z));
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiZombieGenerator(player.inventory, (TileZombieGenerator) world.getTileEntity(x, y, z));
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
  public int getRenderType() {
    return -1;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:stirlingGenFrontOn";
    }
    return "enderio:stirlingGenFrontOff";
  }

  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileZombieGenerator && ((TileZombieGenerator)te).isActive()) {
     //see RenderGlobal.doSpawnParticle
      for (int i = 0; i < 1; i++) {
        float xOffset = 0.5f + (world.rand.nextFloat() * 2.0F - 1.0F) * 0.125f;
        float yOffset = 0.1f;
        float zOffset = 0.5f + (world.rand.nextFloat() * 2.0F - 1.0F) * 0.125f;
        world.spawnParticle("bubble", x + xOffset, y + yOffset, z + zOffset, -0.1D, 0.5D, 0.0D);
      }
    }
    
  }

}
