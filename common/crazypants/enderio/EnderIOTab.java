package crazypants.enderio;

import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnderIOTab extends CreativeTabs {

  public static final CreativeTabs tabEnderIO = new EnderIOTab();

  public EnderIOTab() {
    super("EnderIO");
  }

  /**
   * the itemID for the item to be displayed on the tab
   */
  @Override
  public int getTabIconItemIndex() {
    return EnderIO.itemEnderface.itemID;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getTabLabel() {
    return "EnderIO";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getTranslatedTabLabel() {
    return "EnderIO";
  }

}
