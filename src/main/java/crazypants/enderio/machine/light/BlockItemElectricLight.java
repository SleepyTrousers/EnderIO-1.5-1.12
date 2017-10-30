package crazypants.enderio.machine.light;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.EnderIOTab;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockItemElectricLight extends ItemBlock implements IResourceTooltipProvider {

  public BlockItemElectricLight(Block block, String name) {
    super(block);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
    setHasSubtypes(true);
    setMaxDamage(0);
    setRegistryName(name);
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    meta = MathHelper.clamp(meta, 0, LightType.values().length - 1);
    return LightType.values()[meta].unlocName;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
    for(LightType type : LightType.values()) {
      par3List.add(new ItemStack(this,1,type.ordinal()));
    }
  }

  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
    
    LightType type = LightType.fromMetadata(stack.getItemDamage());    
    IBlockState state = newState.withProperty(BlockElectricLight.TYPE, type);      
    if (!world.setBlockState(pos, state, 3)) {
      return false;
    }
    state = world.getBlockState(pos);    
    if (state.getBlock() == block) {
      setTileEntityNBT(world, player, pos, stack);
      block.onBlockPlacedBy(world, pos, state, player, stack);
    }

    IBlockState bs = world.getBlockState(pos);
    if(bs.getBlock() == block) {
      EnumFacing onFace = side;      
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileElectricLight) {
        TileElectricLight el = ((TileElectricLight) te);
        el.setFace(onFace.getOpposite());        
        el.setInverted(type.isInverted);
        el.setRequiresPower(type.isPowered);
        el.setWireless(type.isWireless);
      }
    }
    return true;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }
}
