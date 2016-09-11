package crazypants.enderio.machine.buffer;

import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;

public class BlockItemBuffer extends ItemBlockWithMetadata {

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
    super(block, block);
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
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
    super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
    if(world.getBlock(x, y, z) == field_150939_a) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileBuffer) {
        TileBuffer buffer = ((TileBuffer) te);
        Type t = Type.values()[metadata];
        buffer.setHasInventory(t.hasInventory);
        buffer.setHasPower(t.hasPower);
        buffer.setCreative(t.isCreative);
      }
    }
    return true;
  }
}
