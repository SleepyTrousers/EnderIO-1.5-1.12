package crazypants.enderio;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.init.ModObject.itemEnderface;

public class EnderIOTab extends CreativeTabs {

  public static final @Nonnull CreativeTabs tabNoTab, tabEnderIO, tabEnderIOItems, tabEnderIOMaterials, tabEnderIOMachines;

  static {
    tabNoTab = new EnderIOTab(0, "invalid");
    tabEnderIOMachines = new EnderIOTab(3, CreativeTabs.CREATIVE_TAB_ARRAY.length - 1, "machines");
    tabEnderIOItems = new EnderIOTab(1, "items");
    tabEnderIOMaterials = new EnderIOTab(2, "materials");
    tabEnderIO = new EnderIOTab(0, "main");
  }

  private final int meta;

  public EnderIOTab(int meta, String name) {
    super(getUnloc(name));
    this.meta = meta;
  }

  public EnderIOTab(int meta, int index, String name) {
    super(index, getUnloc(name));
    this.meta = meta;
  }
  
  private static String getUnloc(String name) {
    return EnderIO.DOMAIN + "." + name;
  }

  @SuppressWarnings("null")
  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull ItemStack getTabIconItem() {
    return new ItemStack(itemEnderface.getItem(), 1, meta);
  }

}
