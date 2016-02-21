package crazypants.enderio.machine.light;

import java.util.List;

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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockItemElectricLight extends ItemBlock implements IResourceTooltipProvider {

  public enum Type {
    ELECTRIC("item.itemElectricLight", false, true, false),
    ELECTRIC_INV("item.itemElectricLightInverted", true, true, false),
    BASIC("item.itemLight", false, false, false),
    BASIC_INV("item.itemLightInverted", true, false, false),
    WIRELESS("item.itemWirelessLight", false, true, true),
    WIRELESS_INV("item.itemWirelessLightInverted", true, true, true);

    final String unlocName;
    final boolean isInverted;
    final boolean isPowered;
    final boolean isWireless;

    private Type(String unlocName, boolean isInverted, boolean isPowered, boolean isWireless) {
      this.unlocName = unlocName;
      this.isInverted = isInverted;
      this.isPowered = isPowered;
      this.isWireless = isWireless;
    }

  }

  public BlockItemElectricLight(Block block) {
    super(block);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHasSubtypes(true);
    setMaxDamage(0);
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    meta = MathHelper.clamp_int(meta, 0, Type.values().length - 1);
    return Type.values()[meta].unlocName;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for(Type type : Type.values()) {
      par3List.add(new ItemStack(this,1,type.ordinal()));
    }
  }

  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
    
    if (!world.setBlockState(pos, newState, 3)) return false;

    IBlockState state = world.getBlockState(pos);
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
        el.setFace(onFace);
        Type t= Type.values()[block.getMetaFromState(bs)];
        el.setInverted(t.isInverted);
        el.setRequiresPower(t.isPowered);
        el.setWireless(t.isWireless);
      }
    }
    return true;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }
}
