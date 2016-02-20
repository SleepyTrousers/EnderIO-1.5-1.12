package crazypants.enderio.machine.buffer;

import java.util.List;
import java.util.Locale;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockItemBuffer extends ItemBlock {

  public enum Type {
    ITEM(true, false, false),
    POWER(false, true, false),
    OMNI(true, true, false),
    CREATIVE(true, true, true);

    final boolean hasInventory;
    final boolean hasPower;
    final boolean isCreative;

    private Type(boolean hasInventory, boolean hasPower, boolean isCreative) {
      this.hasInventory = hasInventory;
      this.hasPower = hasPower;
      this.isCreative = isCreative;
    }

    public static Type get(TileBuffer buffer) {
      return !buffer.hasPower() ? ITEM : !buffer.hasInventory() ? POWER : !buffer.isCreative() ? OMNI : CREATIVE;
    }

    public String getUnlocalizedName() {
      return "tile." + ModObject.blockBuffer.unlocalisedName + "." + name().toLowerCase(Locale.US);
    }

    public static ItemStack getStack(Type type) {
      return new ItemStack(EnderIO.blockBuffer, 1, type.ordinal());
    }
  }

  public BlockItemBuffer(Block block) {
    super(block);
    setHasSubtypes(false);
    setMaxDamage(0);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs tab, List list) {
    for (Type type : Type.values()) {
      list.add(new ItemStack(item, 1, type.ordinal()));
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return Type.values()[stack.getItemDamage()].getUnlocalizedName();
  }

  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      IBlockState newState) {
    super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    
    if(newState.getBlock() == block) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileBuffer) {
        TileBuffer buffer = ((TileBuffer) te);        
        Type t = Type.values()[block.getMetaFromState(newState)];
        buffer.setHasInventory(t.hasInventory);
        buffer.setHasPower(t.hasPower);
        buffer.setCreative(t.isCreative);
      }
    }
    return true;
  }
}
