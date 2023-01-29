package crazypants.enderio.material;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemPowderIngot extends Item {

    private final IIcon[] icons;

    public static ItemPowderIngot create() {
        ItemPowderIngot mp = new ItemPowderIngot();
        mp.init();
        return mp;
    }

    private ItemPowderIngot() {
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(EnderIOTab.tabEnderIO);
        setUnlocalizedName(ModObject.itemPowderIngot.unlocalisedName);

        icons = new IIcon[PowderIngot.values().length];
    }

    private void init() {
        GameRegistry.registerItem(this, ModObject.itemPowderIngot.unlocalisedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        damage = MathHelper.clamp_int(damage, 0, PowderIngot.values().length - 1);
        return icons[damage];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        int numParts = PowderIngot.values().length;
        for (int i = 0; i < numParts; i++) {
            icons[i] = IIconRegister.registerIcon(PowderIngot.values()[i].iconKey);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, PowderIngot.values().length - 1);
        return PowderIngot.values()[i].unlocalisedName;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int j = 0; j < PowderIngot.values().length; ++j) {
            if (PowderIngot.values()[j].isDependancyMet()) {
                par3List.add(new ItemStack(par1, 1, j));
            }
        }
    }
}
