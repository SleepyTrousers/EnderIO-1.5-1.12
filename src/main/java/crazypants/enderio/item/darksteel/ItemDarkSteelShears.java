package crazypants.enderio.item.darksteel;

import cofh.api.energy.IEnergyContainerItem;
import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ItemUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.machine.farm.farmers.HarvestResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ItemDarkSteelShears extends ItemShears
        implements IEnergyContainerItem, IAdvancedTooltipProvider, IDarkSteelItem {

    public static boolean isEquipped(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack equipped = player.getCurrentEquippedItem();
        if (equipped == null) {
            return false;
        }
        return equipped.getItem() instanceof ItemDarkSteelShears;
    }

    public static boolean isEquippedAndPowered(EntityPlayer player, int requiredPower) {
        return getStoredPower(player) > requiredPower;
    }

    public static int getStoredPower(EntityPlayer player) {
        if (!isEquipped(player)) {
            return 0;
        }
        return EnergyUpgrade.getEnergyStored(player.getCurrentEquippedItem());
    }

    public static ItemDarkSteelShears create() {
        ItemDarkSteelShears res = new ItemDarkSteelShears();
        MinecraftForge.EVENT_BUS.register(res);
        res.init();
        return res;
    }

    protected final MultiHarvestComparator harvestComparator = new MultiHarvestComparator();
    protected final EntityComparator entityComparator = new EntityComparator();
    protected String name;

    protected ItemDarkSteelShears(String name) {
        super();
        this.name = name;
        this.setMaxDamage(this.getMaxDamage() * Config.darkSteelShearsDurabilityFactor);
        setCreativeTab(EnderIOTab.tabEnderIO);
        String str = name + "_shears";
        setUnlocalizedName(str);
        setTextureName("enderIO:" + str);
    }

    protected ItemDarkSteelShears() {
        this("darkSteel");
    }

    @Override
    public int getIngotsRequiredForFullRepair() {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
        ItemStack is = new ItemStack(this);
        par3List.add(is);

        is = new ItemStack(this);
        EnergyUpgrade.EMPOWERED_FOUR.writeToItem(is);
        EnergyUpgrade.setPowerFull(is);
        par3List.add(is);
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        if (player.worldObj.isRemote) {
            return false;
        }

        int powerStored = getStoredPower(player);
        if (powerStored < Config.darkSteelShearsPowerUsePerDamagePoint) {
            return super.onBlockStartBreak(itemstack, x, y, z, player);
        }

        Block block = player.worldObj.getBlock(x, y, z);
        if (block instanceof IShearable && ((IShearable) block).isShearable(itemstack, player.worldObj, x, y, z)) {
            BlockCoord bc = new BlockCoord(x, y, z);
            HarvestResult res = new HarvestResult(null, bc);

            for (int dx = -Config.darkSteelShearsBlockAreaBoostWhenPowered;
                    dx <= Config.darkSteelShearsBlockAreaBoostWhenPowered;
                    dx++) {
                for (int dy = -Config.darkSteelShearsBlockAreaBoostWhenPowered;
                        dy <= Config.darkSteelShearsBlockAreaBoostWhenPowered;
                        dy++) {
                    for (int dz = -Config.darkSteelShearsBlockAreaBoostWhenPowered;
                            dz <= Config.darkSteelShearsBlockAreaBoostWhenPowered;
                            dz++) {
                        Block block2 = player.worldObj.getBlock(x + dx, y + dy, z + dz);
                        if (block2 instanceof IShearable
                                && ((IShearable) block2)
                                        .isShearable(itemstack, player.worldObj, x + dx, y + dy, z + dz)) {
                            res.getHarvestedBlocks().add(new BlockCoord(x + dx, y + dy, z + dz));
                        }
                    }
                }
            }

            List<BlockCoord> sortedTargets = new ArrayList<BlockCoord>(res.getHarvestedBlocks());
            harvestComparator.refPoint = bc;
            Collections.sort(sortedTargets, harvestComparator);

            int maxBlocks = Math.min(sortedTargets.size(), powerStored / Config.darkSteelShearsPowerUsePerDamagePoint);
            for (int i = 0; i < maxBlocks; i++) {
                BlockCoord bc2 = sortedTargets.get(i);
                super.onBlockStartBreak(itemstack, bc2.x, bc2.y, bc2.z, player);
                if (bc2 != bc) {
                    player.worldObj.setBlockToAir(bc2.x, bc2.y, bc2.z);
                }
            }
        }
        return false;
    }

    IEntitySelector selectShearable = new IEntitySelector() {
        @Override
        public boolean isEntityApplicable(Entity entity) {
            return entity instanceof IShearable
                    && ((IShearable) entity)
                            .isShearable(
                                    null, entity.worldObj, (int) entity.posX, (int) entity.posY, (int) entity.posZ);
        }
    };

    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity) {
        if (entity.worldObj.isRemote) {
            return false;
        }

        int powerStored = getStoredPower(player);
        if (powerStored < Config.darkSteelShearsPowerUsePerDamagePoint) {
            return super.itemInteractionForEntity(itemstack, player, entity);
        }

        if (entity instanceof IShearable) {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(
                    entity.posX - Config.darkSteelShearsEntityAreaBoostWhenPowered,
                    entity.posY - Config.darkSteelShearsEntityAreaBoostWhenPowered,
                    entity.posZ - Config.darkSteelShearsEntityAreaBoostWhenPowered,
                    entity.posX + Config.darkSteelShearsEntityAreaBoostWhenPowered,
                    entity.posY + Config.darkSteelShearsEntityAreaBoostWhenPowered,
                    entity.posZ + Config.darkSteelShearsEntityAreaBoostWhenPowered);
            List<Entity> sortedTargets = new ArrayList<Entity>(
                    entity.worldObj.selectEntitiesWithinAABB(IShearable.class, bb, selectShearable));
            entityComparator.refPoint = entity;
            Collections.sort(sortedTargets, entityComparator);

            boolean result = false;
            int maxSheep = Math.min(sortedTargets.size(), powerStored / Config.darkSteelShearsPowerUsePerDamagePoint);
            for (int i = 0; i < maxSheep; i++) {
                Entity entity2 = sortedTargets.get(i);
                if (entity2 instanceof EntityLivingBase
                        && super.itemInteractionForEntity(itemstack, player, (EntityLivingBase) entity2)) {
                    result = true;
                }
            }
            return result;
        }
        return false;
    }

    @SubscribeEvent
    public void onBreakSpeedEvent(PlayerEvent.BreakSpeed evt) {
        if (evt.originalSpeed > 2.0
                && isEquippedAndPowered(evt.entityPlayer, Config.darkSteelShearsPowerUsePerDamagePoint)) {
            evt.newSpeed = evt.originalSpeed * Config.darkSteelShearsEffeciencyBoostWhenPowered;
        }
    }

    @Override
    public void setDamage(ItemStack stack, int newDamage) {
        int oldDamage = getDamage(stack);
        if (newDamage <= oldDamage) {
            super.setDamage(stack, newDamage);
        }
        int damage = newDamage - oldDamage;

        EnergyUpgrade eu = EnergyUpgrade.loadFromItem(stack);
        if (eu != null && eu.isAbsorbDamageWithPower(stack) && eu.getEnergy() > 0) {
            eu.extractEnergy(damage * Config.darkSteelShearsPowerUsePerDamagePoint, false);
        } else {
            super.setDamage(stack, newDamage);
        }
        if (eu != null) {
            eu.writeToItem(stack);
        }
    }

    protected void init() {
        GameRegistry.registerItem(this, getUnlocalizedName());
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return EnergyUpgrade.receiveEnergy(container, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return EnergyUpgrade.extractEnergy(container, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return EnergyUpgrade.getEnergyStored(container);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return EnergyUpgrade.getMaxEnergyStored(container);
    }

    @Override
    public boolean getIsRepairable(ItemStack i1, ItemStack i2) {
        // return i2 != null && i2.getItem() == EnderIO.itemAlloy && i2.getItemDamage() == Alloy.DARK_STEEL.ordinal();
        return false;
    }

    @Override
    public int getItemEnchantability() {
        return ItemDarkSteelSword.MATERIAL.getEnchantability();
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        DarkSteelRecipeManager.instance.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
    }

    @Override
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        DarkSteelRecipeManager.instance.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
    }

    @Override
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        if (!Config.addDurabilityTootip) {
            list.add(ItemUtil.getDurabilityString(itemstack));
        }
        String str = EnergyUpgrade.getStoredEnergyString(itemstack);
        if (str != null) {
            list.add(str);
        }
        if (EnergyUpgrade.itemHasAnyPowerUpgrade(itemstack)) {
            list.add(EnderIO.lang.localize("item." + name + "_shears.tooltip.multiHarvest"));
            list.add(EnumChatFormatting.WHITE + "+" + Config.darkSteelShearsEffeciencyBoostWhenPowered + " "
                    + EnderIO.lang.localize("item." + name + "_pickaxe.tooltip.effPowered"));
        }
        DarkSteelRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
    }

    public ItemStack createItemStack() {
        return new ItemStack(this);
    }

    private static class MultiHarvestComparator implements Comparator<BlockCoord> {

        BlockCoord refPoint;

        @Override
        public int compare(BlockCoord arg0, BlockCoord arg1) {
            int d1 = refPoint.getDistSq(arg0);
            int d2 = refPoint.getDistSq(arg1);
            return compare(d1, d2);
        }

        // NB: Copy of Integer.compare, which is only in Java 1.7+
        public static int compare(int x, int y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }

    private static class EntityComparator implements Comparator<Entity> {

        Entity refPoint;

        @Override
        public int compare(Entity paramT1, Entity paramT2) {
            double distanceSqToEntity1 = refPoint.getDistanceSqToEntity(paramT1);
            double distanceSqToEntity2 = refPoint.getDistanceSqToEntity(paramT2);
            if (distanceSqToEntity1 < distanceSqToEntity2) return -1;
            if (distanceSqToEntity1 > distanceSqToEntity2) return 1;
            // Double.compare() does something with bits now, but for distances it's clear:
            // if it's neither farther nor nearer is same.
            return 0;
        }
    }
}
