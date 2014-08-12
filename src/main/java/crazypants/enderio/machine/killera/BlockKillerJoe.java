package crazypants.enderio.machine.killera;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.generator.zombie.ContainerZombieGenerator;
import crazypants.enderio.machine.generator.zombie.GuiZombieGenerator;
import crazypants.enderio.machine.generator.zombie.PacketZombieTank;
import crazypants.enderio.machine.generator.zombie.TileZombieGenerator;
import crazypants.enderio.machine.tank.BlockTank;
import crazypants.enderio.machine.tank.PacketTank;
import crazypants.enderio.machine.tank.TileTank;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.FluidUtil;
import crazypants.util.Util;

/**
 * Name proudly created by Xaw4
 */
public class BlockKillerJoe extends AbstractMachineBlock<TileKillerJoe> {

  public static BlockKillerJoe create() {
    PacketHandler.INSTANCE.registerMessage(PacketNutrientLevel.class, PacketNutrientLevel.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketSwing.class, PacketSwing.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketExperianceTotal.class, PacketExperianceTotal.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketUseXP.class, PacketUseXP.class, PacketHandler.nextID(), Side.SERVER);
    BlockKillerJoe res = new BlockKillerJoe();
    res.init();
    return res;
  }

  protected BlockKillerJoe() {
    super(ModObject.blockKillerJoe, TileKillerJoe.class);
    setStepSound(Block.soundTypeGlass);
  }
  
  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {

    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileKillerJoe)) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    TileKillerJoe gen = (TileKillerJoe) te;
    ItemStack item = entityPlayer.inventory.getCurrentItem();
    if(item == null) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    //check for filled fluid containers and see if we can empty them into our tank
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
    return new ContainerKillerJoe(player.inventory, (TileKillerJoe) world.getTileEntity(x, y, z));
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiKillerJoe(player.inventory, (TileKillerJoe) world.getTileEntity(x, y, z));
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_KILLER_JOE;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:blankMachinePanel";
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
  
}
