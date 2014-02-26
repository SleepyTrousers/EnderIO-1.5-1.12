package crazypants.enderio;

import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;

public class EnderIOTab extends CreativeTabs {

  public static final CreativeTabs tabEnderIO = new EnderIOTab();

  public EnderIOTab() {
    super("EnderIO");
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

    @Override
    public Item getTabIconItem() {
        return EnderIO.itemEnderface;
    }

}
