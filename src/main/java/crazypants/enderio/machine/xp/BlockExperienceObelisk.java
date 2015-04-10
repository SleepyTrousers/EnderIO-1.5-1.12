package crazypants.enderio.machine.xp;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.init.EIOBlocks;
import crazypants.enderio.machine.AbstractMachineBlock;

public class BlockExperienceObelisk extends AbstractMachineBlock<TileExperienceOblisk> {

  public static BlockExperienceObelisk create() {
    BlockExperienceObelisk res = new BlockExperienceObelisk();
    res.init();
    return res;
  }

  public static int renderId;

  private BlockExperienceObelisk() {
    super(ModObject.blockExperienceObelisk, TileExperienceOblisk.class);
    setObeliskBounds();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
    return getIcon(blockSide, 0);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int side, int meta) {
    if(ForgeDirection.getOrientation(side) == ForgeDirection.UP){
      return EIOBlocks.blockAttractor.getIcon(side,0);
    }
    return EIOBlocks.blockAttractor.getOnIcon();
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
  public int getLightOpacity() {
    return 0;
  }

  @Override
  public int getRenderType() {
    return renderId;
  }

//  @Override
//  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
//    ItemXpTransfer.onActivated(player, world, x, y - 1, z, side);
//    return true;
//  }
//
//  @Override
//  public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
//    super.onBlockClicked(world, x, y, z, player);
//
//    // copypasta from ItemXpTransfer.transferFromPlayerToBlock
//
//    if(world.isRemote || player.experienceTotal <= 0) {
//      return;
//    }
//
//    y--;
//    TileEntity te = world.getTileEntity(x, y, z);
//    if(!(te instanceof IFluidHandler)) {
//      return;
//    }
//
//    IFluidHandler fh = (IFluidHandler) te;
//    ForgeDirection dir = ForgeDirection.UP; // no side passed :(
//    if(!fh.canFill(dir, EnderIO.fluidXpJuice)) {
//      return;
//    }
//
//    int canTake = player.experienceTotal - XpUtil.getExperienceForLevel(player.experienceLevel - 1);
//
//    int fluidVolume = XpUtil.experianceToLiquid(canTake);
//    FluidStack fs = new FluidStack(EnderIO.fluidXpJuice, fluidVolume);
//    int takenVolume = fh.fill(dir, fs, true);
//    if(takenVolume <= 0) {
//      return;
//    }
//
//    int xpToTake = XpUtil.liquidToExperiance(takenVolume);
//    XpUtil.addPlayerXP(player, -xpToTake);
//    ItemXpTransfer.sendXPUpdate(player, world, x, y, z, false);
//  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  //  @Override
  //  public void registerBlockIcons(IIconRegister iIconRegister) {
  //
  //    blockIcon = iIconRegister.registerIcon("enderio:blockAttractorSide");
  //  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new ContainerExperianceObelisk();
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiExperianceObelisk(player.inventory, (TileExperienceOblisk)world.getTileEntity(x, y, z));
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_XP_OBELISK;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    //Not actualy used, but give it something so it doesn't print an error
    return "enderio:blockAttractorSide";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
  }

}
