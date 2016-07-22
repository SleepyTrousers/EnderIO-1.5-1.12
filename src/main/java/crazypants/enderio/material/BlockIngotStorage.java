package crazypants.enderio.material;

import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.IHaveRenderers;
import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.util.ClientUtil;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIngotStorage extends BlockEio<TileEntityEio> implements IAdvancedTooltipProvider, IHaveRenderers {
  
  public static BlockIngotStorage create() {
    BlockIngotStorage res = new BlockIngotStorage();
    res.init();
    return res;
  }
  
  public static final PropertyEnum<Alloy> VARIANT = PropertyEnum.<Alloy>create("variant", Alloy.class);
  
  private BlockIngotStorage() {
    super(ModObject.blockIngotStorage.getUnlocalisedName(),null,  Material.IRON);
    setSoundType(SoundType.METAL);
  }
  
  @Override
  protected ItemBlock createItemBlock() {
    return new BlockItemIngotStorage(this, getName());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    for (Alloy alloy : Alloy.values()) {
      ClientUtil.regRenderer(this, Alloy.getMetaFromType(alloy), VARIANT.getName() + "=" + VARIANT.getName(alloy));
    }
  }
  
  @Override
  public int damageDropped(IBlockState state) {
    Alloy enumColour = state.getValue(VARIANT);
    return enumColour.ordinal();
  }
  
  @Override
  public IBlockState getStateFromMeta(int meta) {        
    Alloy alloy = Alloy.values()[meta];
    return getDefaultState().withProperty(VARIANT, alloy);
  }

  @Override
  public int getMetaFromState(IBlockState state) {    
    Alloy colour = state.getValue(VARIANT);    
    return colour.ordinal();
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] {VARIANT});
  }
  
  @Override
  public float getBlockHardness(IBlockState bs, World world, BlockPos pos) {       
    return Alloy.values()[bs.getBlock().getMetaFromState(bs)].getHardness();
  }
    
  @Override
  public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {  
    return getBlockHardness(world.getBlockState(pos), world, pos) * 2.0f; // vanilla default is / 5.0f, this means hardness*2 = resistance
    // TODO 1.9 um, I cannot follow that comment above. Shouldn't this be /5f?
  }
    
  @Override
  public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
    return true;
  }
  
  @Override
  public boolean shouldWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {  
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
