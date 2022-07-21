package crazypants.enderio.item.darksteel;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.IDarkSteelItem.IStellarItem;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TravelUpgrade;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

public class ItemStellarSword extends ItemDarkSteelSword implements IStellarItem {

    static final ToolMaterial MATERIAL =
            EnumHelper.addToolMaterial("stellarAlloy", Config.darkSteelPickMinesTiCArdite ? 5 : 3, 5000, 16, 11, 25);

    public static boolean isEquipped(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack equipped = player.getCurrentEquippedItem();
        if (equipped == null) {
            return false;
        }
        return equipped.getItem() == DarkSteelItems.itemStellarSword;
    }

    public static ItemStellarSword create() {
        ItemStellarSword res = new ItemStellarSword();
        res.init();
        MinecraftForge.EVENT_BUS.register(res);
        return res;
    }

    public ItemStellarSword() {
        super("stellar", MATERIAL);
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
