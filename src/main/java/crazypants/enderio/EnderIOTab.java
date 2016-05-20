package crazypants.enderio;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.EnderIO.MODID;

public class EnderIOTab extends CreativeTabs {

  public static final CreativeTabs tabNoTab, tabEnderIO;

  static {
    tabNoTab = new EnderIOTab();
    tabEnderIO = new EnderIOTab(CreativeTabs.CREATIVE_TAB_ARRAY.length - 1);
  }

  public EnderIOTab() {
    super(MODID);
  }

  public EnderIOTab(int index) {
    super(index, MODID);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getTabLabel() {
    return MODID;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getTranslatedTabLabel() {
    return MODID;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Item getTabIconItem() {
    return EnderIO.itemEnderface;
  }

}
