package crazypants.enderio.machine.generator.combustion;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.util.FluidUtil;
import crazypants.util.Util;

public class BlockCombustionGenerator extends AbstractMachineBlock<TileCombustionGenerator> {

  public static int renderId = -1;

  public static BlockCombustionGenerator create() {
    EnderIO.packetPipeline.registerPacket(PacketTanks.class);
    
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
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {

    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileCombustionGenerator)) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    TileCombustionGenerator gen = (TileCombustionGenerator) te;
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
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileCombustionGenerator) {
      return new ContainerCombustionEngine(player.inventory, (TileCombustionGenerator)te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
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
  public int getRenderType() {
    return renderId;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  public IIcon getBackIcon() {
    return iconBuffer[0][2];
  }

  @Override
  public String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:combustionGenFrontOn";
    }
    return "enderio:combustionGenFront";
  }

  @Override
  public String getBackIconKey(boolean active) {
    return "enderio:blankMachinePanel";
  }

  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    // If active, randomly throw some smoke around
    if(isActive(world, x, y, z)) {

      TileEntity te = world.getTileEntity(x, y, z);
      int facing = 3;
      if(te instanceof AbstractMachineEntity) {
        AbstractMachineEntity me = (AbstractMachineEntity) te;
        facing = me.facing;
      }
      ForgeDirection dir = ForgeDirection.getOrientation(facing);
      float startX = x + (dir.offsetX == 0 ? 0.5f : 0f);
      float startY = y + 0.5f;
      float startZ = z + (dir.offsetZ == 0 ? 0.5f : 0f);

      if(dir.offsetX == 1) {
        startX++;
      } else if (dir.offsetZ == 1) {
        startZ++;
      }

      for (int i = 0; i < 2; i++) {
        float xOffset = 0;
        float yOffset = 0;
        float zOffset = 0;
        world.spawnParticle("smoke", startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
      }
    }
  }

}
