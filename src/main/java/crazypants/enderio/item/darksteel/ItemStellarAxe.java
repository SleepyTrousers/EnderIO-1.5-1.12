package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.item.darksteel.IDarkSteelItem.IStellarItem;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TravelUpgrade;

public class ItemStellarAxe extends ItemDarkSteelAxe implements IStellarItem {

    public static boolean isEquipped(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack equipped = player.getCurrentEquippedItem();
        if (equipped == null) {
            return false;
        }
        return equipped.getItem() == DarkSteelItems.itemStellarAxe;
    }

    public static ItemStellarAxe create() {
        ItemStellarAxe res = new ItemStellarAxe();
        res.init();
        MinecraftForge.EVENT_BUS.register(res);
        return res;
    }

    public ItemStellarAxe() {
        super("stellar", ItemStellarSword.MATERIAL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
        ItemStack is = new ItemStack(this);
        par3List.add(is);

        is = new ItemStack(this);
        EnergyUpgrade.EMPOWERED_FIVE.writeToItem(is);
        EnergyUpgrade.setPowerFull(is);
        TravelUpgrade.INSTANCE.writeToItem(is);
        par3List.add(is);
    }
}
