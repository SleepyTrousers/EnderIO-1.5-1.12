package crazypants.enderio.item.darksteel.upgrade;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.ItemDarkSteelArmor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ApiaristArmorUpgrade extends AbstractUpgrade {

    private static String UPGRADE_NAME = "apiaristArmor";

    private static final String forestryItemNames[] = {
        "apiaristHelmet", "apiaristChest", "apiaristLegs", "apiaristBoots"
    };

    public static final ApiaristArmorUpgrade HELMET = new ApiaristArmorUpgrade(0);
    public static final ApiaristArmorUpgrade CHEST = new ApiaristArmorUpgrade(1);
    public static final ApiaristArmorUpgrade LEGS = new ApiaristArmorUpgrade(2);
    public static final ApiaristArmorUpgrade BOOTS = new ApiaristArmorUpgrade(3);

    public static ItemStack getApiaristArmor(int slot) {
        Item i = GameRegistry.findItem("Forestry", forestryItemNames[slot]);
        if (i != null) {
            return new ItemStack(i);
        }
        return null;
    }

    public static ApiaristArmorUpgrade loadFromItem(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.stackTagCompound == null) {
            return null;
        }
        if (!stack.stackTagCompound.hasKey(KEY_UPGRADE_PREFIX + UPGRADE_NAME)) {
            return null;
        }
        return new ApiaristArmorUpgrade(
                (NBTTagCompound) stack.stackTagCompound.getTag(KEY_UPGRADE_PREFIX + UPGRADE_NAME));
    }

    private final int slot;

    public ApiaristArmorUpgrade(NBTTagCompound tag) {
        super(UPGRADE_NAME, tag);
        this.slot = tag.getInteger("slot");
    }

    public ApiaristArmorUpgrade(int slot) {
        super(
                UPGRADE_NAME,
                "enderio.darksteel.upgrade.apiaristArmor.".concat(ItemDarkSteelArmor.NAMES[slot]),
                getApiaristArmor(slot),
                Config.darkSteelApiaristArmorCost);
        this.slot = slot;
    }

    @Override
    public boolean canAddToItem(ItemStack stack) {
        if (stack == null || !DarkSteelItems.isArmorPart(stack.getItem(), slot) || getUpgradeItem() == null) {
            return false;
        }
        ApiaristArmorUpgrade up = loadFromItem(stack);
        return up == null;
    }

    @Override
    public boolean hasUpgrade(ItemStack stack) {
        return super.hasUpgrade(stack) && DarkSteelItems.isArmorPart(stack.getItem(), slot);
    }

    @Override
    public void writeUpgradeToNBT(NBTTagCompound upgradeRoot) {
        upgradeRoot.setByte("slot", (byte) slot);
    }

    @Override
    public ItemStack getUpgradeItem() {
        if (upgradeItem != null) {
            return upgradeItem;
        }
        upgradeItem = getApiaristArmor(slot);
        return upgradeItem;
    }

    @Override
    public String getUpgradeItemName() {
        if (getUpgradeItem() == null) {
            return "Apiarist Armor";
        }
        return super.getUpgradeItemName();
    }
}
