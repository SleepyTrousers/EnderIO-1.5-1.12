package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.util.ClientUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIngotStorage extends BlockEio<TileEntityEio> implements IAdvancedTooltipProvider {
  
  public static BlockIngotStorage create() {
    BlockIngotStorage res = new BlockIngotStorage();
    res.init();
    return res;
  }
  
  public static final PropertyEnum<Alloy> VARIANT = PropertyEnum.<Alloy>create("variant", Alloy.class);
  
  private BlockIngotStorage() {
    super(ModObject.blockIngotStorage.unlocalisedName, null, Material.iron);
    setStepSound(soundTypeMetal);
  }
  
  @Override
  protected void init() {
    GameRegistry.registerBlock(this, BlockItemIngotStorage.class, ModObject.blockIngotStorage.unlocalisedName);
  }
  
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    List<ResourceLocation> variants = new ArrayList<ResourceLocation>();
    for(Alloy alloy : Alloy.values()) {        
      variants.add(new ResourceLocation(EnderIO.MODID, alloy.baseName + "Block"));
    }
    
    Item item = Item.getItemFromBlock(this);
    // need to add the variants to the bakery so it knows what models are available for rendering the different subtypes
    ModelBakery.registerItemVariants(item, variants.toArray(new ResourceLocation[variants.size()]));
    
    int numAlloys = Alloy.values().length;
    for (int i = 0; i < numAlloys; i++) {
      ClientUtil.regRenderer(item, i, Alloy.values()[i].baseName + "Block");
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
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] {VARIANT});
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
