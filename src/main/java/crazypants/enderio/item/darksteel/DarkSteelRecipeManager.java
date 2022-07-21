package crazypants.enderio.item.darksteel;

import com.enderio.core.common.util.OreDictionaryHelper;
import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.EnderIO;
import crazypants.enderio.item.darksteel.IDarkSteelItem.IEndSteelItem;
import crazypants.enderio.item.darksteel.IDarkSteelItem.IStellarItem;
import crazypants.enderio.item.darksteel.upgrade.ApiaristArmorUpgrade;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.GliderUpgrade;
import crazypants.enderio.item.darksteel.upgrade.IDarkSteelUpgrade;
import crazypants.enderio.item.darksteel.upgrade.JumpUpgrade;
import crazypants.enderio.item.darksteel.upgrade.NaturalistEyeUpgrade;
import crazypants.enderio.item.darksteel.upgrade.NightVisionUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SolarUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SoundDetectorUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpeedUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpoonUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SwimUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TravelUpgrade;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.endergy.AlloyEndergy;
import crazypants.enderio.thaumcraft.ThaumcraftCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.AnvilUpdateEvent;

public class DarkSteelRecipeManager {

    public static DarkSteelRecipeManager instance = new DarkSteelRecipeManager();

    private List<IDarkSteelUpgrade> upgrades = new ArrayList<IDarkSteelUpgrade>();

    public DarkSteelRecipeManager() {
        upgrades.add(EnergyUpgrade.EMPOWERED);
        upgrades.add(EnergyUpgrade.EMPOWERED_TWO);
        upgrades.add(EnergyUpgrade.EMPOWERED_THREE);
        upgrades.add(EnergyUpgrade.EMPOWERED_FOUR);
        upgrades.add(EnergyUpgrade.EMPOWERED_FIVE);
        upgrades.add(JumpUpgrade.JUMP_ONE);
        upgrades.add(JumpUpgrade.JUMP_TWO);
        upgrades.add(JumpUpgrade.JUMP_THREE);
        upgrades.add(SpeedUpgrade.SPEED_ONE);
        upgrades.add(SpeedUpgrade.SPEED_TWO);
        upgrades.add(SpeedUpgrade.SPEED_THREE);
        upgrades.add(GliderUpgrade.INSTANCE);
        upgrades.add(SoundDetectorUpgrade.INSTANCE);
        upgrades.add(SwimUpgrade.INSTANCE);
        upgrades.add(NightVisionUpgrade.INSTANCE);
        upgrades.add(TravelUpgrade.INSTANCE);
        upgrades.add(SpoonUpgrade.INSTANCE);
        upgrades.add(SolarUpgrade.SOLAR_ONE);
        upgrades.add(SolarUpgrade.SOLAR_TWO);
        upgrades.add(SolarUpgrade.SOLAR_THREE);
        if (Loader.isModLoaded("Thaumcraft")) {
            ThaumcraftCompat.loadUpgrades(upgrades);
        }
        if (Loader.isModLoaded("Forestry")) {
            upgrades.add(NaturalistEyeUpgrade.INSTANCE);
            upgrades.add(ApiaristArmorUpgrade.HELMET);
            upgrades.add(ApiaristArmorUpgrade.CHEST);
            upgrades.add(ApiaristArmorUpgrade.LEGS);
            upgrades.add(ApiaristArmorUpgrade.BOOTS);
        }
    }

    @SubscribeEvent
    public void handleAnvilEvent(AnvilUpdateEvent evt) {
        if (evt.left == null || evt.right == null) {
            return;
        }

        if (evt.left.getItem() instanceof IStellarItem
                && OreDictionaryHelper.hasName(evt.right, AlloyEndergy.STELLAR_ALLOY.getOreIngot())) {
            handleRepair(evt);
        } else if (evt.left.getItem() instanceof IEndSteelItem
                && OreDictionaryHelper.hasName(evt.right, Alloy.END_STEEL.getOreIngot())) {
            handleRepair(evt);
        } else if (evt.left.getItem() instanceof IDarkSteelItem
                && OreDictionaryHelper.hasName(evt.right, Alloy.DARK_STEEL.getOreIngot())) {
            handleRepair(evt);
        } else {
            handleUpgrade(evt);
        }
    }

    private void handleRepair(AnvilUpdateEvent evt) {
        ItemStack targetStack = evt.left;
        ItemStack ingots = evt.right;

        // repair event
        IDarkSteelItem targetItem = (IDarkSteelItem) targetStack.getItem();
        int maxIngots = targetItem.getIngotsRequiredForFullRepair();

        double damPerc = (double) targetStack.getItemDamage() / targetStack.getMaxDamage();
        int requiredIngots = (int) Math.ceil(damPerc * maxIngots);
        if (ingots.stackSize > requiredIngots) {
            return;
        }

        int damageAddedPerIngot = (int) Math.ceil((double) targetStack.getMaxDamage() / maxIngots);
        int totalDamageRemoved = damageAddedPerIngot * ingots.stackSize;

        ItemStack resultStack = targetStack.copy();
        resultStack.setItemDamage(Math.max(0, resultStack.getItemDamage() - totalDamageRemoved));

        evt.output = resultStack;
        evt.cost = ingots.stackSize + (int) Math.ceil(getEnchantmentRepairCost(resultStack) / 2);
    }

    private void handleUpgrade(AnvilUpdateEvent evt) {
        for (IDarkSteelUpgrade upgrade : upgrades) {
            if (upgrade.isUpgradeItem(evt.right) && upgrade.canAddToItem(evt.left)) {
                ItemStack res = new ItemStack(evt.left.getItem(), 1, evt.left.getItemDamage());
                if (evt.left.stackTagCompound != null) {
                    res.stackTagCompound = (NBTTagCompound) evt.left.stackTagCompound.copy();
                }
                upgrade.writeToItem(res);
                evt.output = res;
                evt.cost = upgrade.getLevelCost();
                return;
            }
        }
    }

    public static int getEnchantmentRepairCost(ItemStack itemStack) {
        // derived from ContainerRepair
        int res = 0;
        Map map1 = EnchantmentHelper.getEnchantments(itemStack);
        Iterator iter = map1.keySet().iterator();
        while (iter.hasNext()) {
            int i1 = ((Integer) iter.next()).intValue();
            Enchantment enchantment = Enchantment.enchantmentsList[i1];

            int level = ((Integer) map1.get(Integer.valueOf(i1))).intValue();
            if (enchantment.canApply(itemStack)) {
                if (level > enchantment.getMaxLevel()) {
                    level = enchantment.getMaxLevel();
                }
                int costPerLevel = 0;
                switch (enchantment.getWeight()) {
                    case 1:
                        costPerLevel = 8;
                        break;
                    case 2:
                        costPerLevel = 4;
                    case 3:
                    case 4:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    default:
                        break;
                    case 5:
                        costPerLevel = 2;
                        break;
                    case 10:
                        costPerLevel = 1;
                }
                res += costPerLevel * level;
            }
        }
        return res;
    }

    public List<IDarkSteelUpgrade> getUpgrades() {
        return upgrades;
    }

    public void addCommonTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        for (IDarkSteelUpgrade upgrade : upgrades) {
            if (upgrade.hasUpgrade(itemstack)) {
                upgrade.addCommonEntries(itemstack, entityplayer, list, flag);
            }
        }
    }

    public void addBasicTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        for (IDarkSteelUpgrade upgrade : upgrades) {
            if (upgrade.hasUpgrade(itemstack)) {
                upgrade.addBasicEntries(itemstack, entityplayer, list, flag);
            }
        }
    }

    public void addAdvancedTooltipEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {

        List<IDarkSteelUpgrade> applyableUpgrades = new ArrayList<IDarkSteelUpgrade>();
        for (IDarkSteelUpgrade upgrade : upgrades) {
            if (upgrade.hasUpgrade(itemstack)) {
                upgrade.addDetailedEntries(itemstack, entityplayer, list, flag);
            } else if (upgrade.canAddToItem(itemstack)) {
                applyableUpgrades.add(upgrade);
            }
        }
        if (!applyableUpgrades.isEmpty()) {
            list.add(EnumChatFormatting.YELLOW + EnderIO.lang.localize("tooltip.anvilupgrades") + " ");
            for (IDarkSteelUpgrade up : applyableUpgrades) {
                list.add(EnumChatFormatting.DARK_AQUA + "" + ""
                        + EnderIO.lang.localizeExact(up.getUnlocalizedName() + ".name") + ": ");
                list.add(EnumChatFormatting.DARK_AQUA + "" + EnumChatFormatting.ITALIC + "  " + up.getUpgradeItemName()
                        + " + " + up.getLevelCost() + " " + EnderIO.lang.localize("item.darkSteel.tooltip.lvs"));
            }
        }
    }

    public Iterator<IDarkSteelUpgrade> recipeIterator() {
        return ImmutableList.copyOf(upgrades).iterator();
    }
}
