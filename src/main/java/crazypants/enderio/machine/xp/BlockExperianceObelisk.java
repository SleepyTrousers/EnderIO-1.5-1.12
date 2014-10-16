package crazypants.enderio.machine.xp;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.xp.XpUtil;

public class BlockExperianceObelisk extends BlockEio implements IResourceTooltipProvider {

  public static BlockExperianceObelisk create() {
    BlockExperianceObelisk res = new BlockExperianceObelisk();
    res.init();
    return res;
  }

  public static int renderId;
  
  private BlockExperianceObelisk() {
    super(ModObject.blockExperienceObelisk.unlocalisedName, TileExperianceOblisk.class);
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int side, int meta) {
    if(ForgeDirection.getOrientation(side) == ForgeDirection.UP){
      return EnderIO.blockAttractor.getIcon(side,0);
    }
    return EnderIO.blockAttractor.getOnIcon();
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
  
  @Override
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int x, int y, int z) {
    return AxisAlignedBB.getBoundingBox(x + 0.1, y, z + 0.1, x + 0.9, y + 0.5, z + 0.9);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
    ItemXpTransfer.onActivated(player, world, x, y - 1, z, side);
    return true;
  }
  
  @Override
  public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
    super.onBlockClicked(world, x, y, z, player);
        
    // copypasta from ItemXpTransfer.transferFromPlayerToBlock
    
    if(world.isRemote || player.experienceTotal <= 0) {
      return;
    }
    
    y--;
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof IFluidHandler)) {
      return;
    }
    
    IFluidHandler fh = (IFluidHandler) te;
    ForgeDirection dir = ForgeDirection.UP; // no side passed :(
    if(!fh.canFill(dir, EnderIO.fluidXpJuice)) {
      return;
    }

    int canTake = player.experienceTotal - XpUtil.getExperienceForLevel(player.experienceLevel - 1);
    
    int fluidVolume = XpUtil.experianceToLiquid(canTake);
    FluidStack fs = new FluidStack(EnderIO.fluidXpJuice, fluidVolume);
    int takenVolume = fh.fill(dir, fs, true);
    if(takenVolume <= 0) {
      return;
    }
    
    int xpToTake = XpUtil.liquidToExperiance(takenVolume);
    XpUtil.addPlayerXP(player, -xpToTake);
    ItemXpTransfer.sendXPUpdate(player, world, x, y, z, false);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }
  
  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    //Not actualy used, but give it something so it doesn't print an error
    blockIcon = iIconRegister.registerIcon("enderio:blockAttractorSide");
  }
}
