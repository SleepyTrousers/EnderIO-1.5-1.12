package crazypants.enderio.item.darksteel.upgrade;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionHelper;

import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;

public class NightVisionUpgrade extends AbstractUpgrade {

    private static String UPGRADE_NAME = "nightVision";

    public static final NightVisionUpgrade INSTANCE = new NightVisionUpgrade();

    public static NightVisionUpgrade loadFromItem(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.stackTagCompound == null) {
            return null;
        }
        if (!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
            return null;
        }
        return new NightVisionUpgrade(
                (NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
    }

    private static ItemStack createUpgradeItem() {
        ItemStack pot = new ItemStack(Items.potionitem, 1, 0);
        int res = PotionHelper.applyIngredient(0, Items.nether_wart.getPotionEffect(new ItemStack(Items.nether_wart)));
        res = PotionHelper.applyIngredient(res, PotionHelper.goldenCarrotEffect);
        pot.setItemDamage(res);
        return pot;
    }

    public NightVisionUpgrade(NBTTagCompound tag) {
        super(UPGRADE_NAME, tag);
    }

    public NightVisionUpgrade() {
        super(
                UPGRADE_NAME,
                "enderio.darksteel.upgrade.nightVision",
                createUpgradeItem(),
                Config.darkSteelNightVisionCost);
    }

    @Override
    public boolean canAddToItem(ItemStack stack) {
        if (stack == null || !DarkSteelItems.isArmorPart(stack.getItem(), 0)) {
            return false;
        }
        NightVisionUpgrade up = loadFromItem(stack);
        if (up == null) {
            return true;
        }
        return false;
    }

    @Override
    public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {}
}
