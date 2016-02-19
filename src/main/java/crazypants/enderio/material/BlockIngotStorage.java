package crazypants.enderio.material;

import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockIngotStorage extends BlockEio implements IAdvancedTooltipProvider {
  
  public static BlockIngotStorage create() {
    BlockIngotStorage res = new BlockIngotStorage();
    res.init();
    return res;
  }
  
  private BlockIngotStorage() {
    super(ModObject.blockIngotStorage.unlocalisedName, null, Material.iron);
    setStepSound(soundTypeMetal);
  }
  
  @Override
  protected void init() {
    GameRegistry.registerBlock(this, BlockItemIngotStorage.class, ModObject.blockIngotStorage.unlocalisedName);
  }
  
//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister register) {
//    icons = new IIcon[Alloy.values().length];
//    for (Alloy alloy : Alloy.values()) {
//      icons[alloy.ordinal()] = register.registerIcon(alloy.iconKey + "Block");
//    }
//  }
  

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  
  
  @Override
  public float getBlockHardness(World world, BlockPos pos) {
    IBlockState bs = world.getBlockState(pos);    
    return Alloy.values()[bs.getBlock().getMetaFromState(bs)].getHardness();
  }
    
  @Override
  public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {  
    return getBlockHardness(world, pos) * 2.0f; // vanilla default is / 5.0f, this means hardness*2 = resistance
  }
    
  @Override
  public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {  
    return true;
  }
  
  @Override
  protected boolean shouldWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {  
    return false;
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    list.add(EnderIO.lang.localize("tooltip.isBeaconBase"));
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

  }
}
