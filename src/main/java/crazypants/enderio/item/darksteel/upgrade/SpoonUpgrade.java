package crazypants.enderio.item.darksteel.upgrade;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;

public class SpoonUpgrade extends AbstractUpgrade {

    private static String UPGRADE_NAME = "spoon";

    public static final SpoonUpgrade INSTANCE = new SpoonUpgrade();

    public static SpoonUpgrade loadFromItem(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.stackTagCompound == null) {
            return null;
        }
        if (!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
            return null;
        }
        return new SpoonUpgrade((NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
    }

    public SpoonUpgrade(NBTTagCompound tag) {
        super(UPGRADE_NAME, tag);
    }

    public SpoonUpgrade() {
        super(
                UPGRADE_NAME,
                "enderio.darksteel.upgrade.spoon",
                new ItemStack(Items.diamond_shovel),
                Config.darkSteelSpoonCost);
    }

    @Override
    public boolean canAddToItem(ItemStack stack) {
        if (stack == null
                || (stack.getItem() != DarkSteelItems.itemDarkSteelPickaxe
                        && stack.getItem() != DarkSteelItems.itemEndSteelPickaxe)
                || !EnergyUpgrade.itemHasAnyPowerUpgrade(stack)) {
            return false;
        }
        SpoonUpgrade up = loadFromItem(stack);
        if (up == null) {
            return true;
        }
        return false;
    }

    @Override
    public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {}
}
