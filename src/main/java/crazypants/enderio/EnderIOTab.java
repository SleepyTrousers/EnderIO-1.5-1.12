package crazypants.enderio;

import static crazypants.enderio.EnderIO.MODID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

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
