package crazypants.enderio;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import static crazypants.enderio.EnderIO.MODID;

public class EnderIOTab extends CreativeTabs {

  public static final CreativeTabs tabEnderIO = new EnderIOTab();

  public EnderIOTab() {
    super(MODID);
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
