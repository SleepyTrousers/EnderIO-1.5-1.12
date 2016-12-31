package crazypants.enderio;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.EnderIO.MODID;
import static crazypants.enderio.ModObject.itemEnderface;

public class EnderIOTab extends CreativeTabs {

  public static final @Nonnull CreativeTabs tabNoTab, tabEnderIO, tabEnderIOItems, tabEnderIOMaterials, tabEnderIOMachines;

  static {
    tabNoTab = new EnderIOTab(0);
    tabEnderIOMachines = new EnderIOTab(3, CreativeTabs.CREATIVE_TAB_ARRAY.length - 1);
    tabEnderIOItems = new EnderIOTab(1);
    tabEnderIOMaterials = new EnderIOTab(2);
    tabEnderIO = new EnderIOTab(0);
  }

  private final int meta;

  public EnderIOTab(int meta) {
    super(MODID);
    this.meta = meta;
  }

  public EnderIOTab(int meta, int index) {
    super(index, MODID);
    this.meta = meta;
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

  @SuppressWarnings("null")
  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull Item getTabIconItem() {
    return itemEnderface.getItem();
  }

  @Override
  public int getIconItemDamage() {
    return meta;
  }

}
