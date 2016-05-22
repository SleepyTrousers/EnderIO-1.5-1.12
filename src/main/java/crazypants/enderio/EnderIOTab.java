package crazypants.enderio;

import javax.annotation.Nonnull;

import static crazypants.enderio.EnderIO.MODID;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnderIOTab extends CreativeTabs {

  public static final @Nonnull CreativeTabs tabNoTab, tabEnderIO;

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
  public @Nonnull String getTabLabel() {
    return MODID;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull String getTranslatedTabLabel() {
    return MODID;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull Item getTabIconItem() {
    return EnderIO.itemEnderface;
  }

}
