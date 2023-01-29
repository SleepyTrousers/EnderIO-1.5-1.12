package crazypants.enderio.machine.capbank;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.power.PowerHandlerUtil;

public class BlockItemCapBank extends ItemBlock implements IEnergyContainerItem {

    public static ItemStack createItemStackWithPower(int meta, int storedEnergy) {
        ItemStack res = new ItemStack(EnderIO.blockCapBank, 1, meta);
        PowerHandlerUtil.setStoredEnergyForItem(res, storedEnergy);
        CapBankType type = CapBankType.getTypeFromMeta(meta);
        type.writeTypeToNBT(res.stackTagCompound);
        return res;
    }

    public BlockItemCapBank() {
        super(EnderIO.blockCapBank);
        setHasSubtypes(true);
    }

    public BlockItemCapBank(Block block) {
        super(block);
        setHasSubtypes(true);
        setCreativeTab(EnderIOTab.tabEnderIO);
    }

    @Override
    public int getMetadata(int par1) {
        return par1;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return CapBankType.getTypeFromMeta(par1ItemStack.getItemDamage()).getUnlocalizedName();
    }

    @Override
    public boolean showDurabilityBar(ItemStack itemStack) {
        return !CapBankType.getTypeFromMeta(itemStack.getItemDamage()).isCreative();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        int maxStored = CapBankType.getTypeFromMeta(itemStack.getItemDamage()).getMaxEnergyStored();
        double stored = maxStored - getEnergyStored(itemStack) + 1;
        double max = maxStored + 1;
        return stored / max;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (container.stackSize > 1) {
            return 0;
        }
        CapBankType type = CapBankType.getTypeFromMeta(container.getItemDamage());
        int energy = getEnergyStored(container);
        int maxInput = type.getMaxIO();
        int energyReceived = Math.min(type.getMaxEnergyStored() - energy, Math.min(maxReceive, maxInput));

        if (!simulate && !type.isCreative()) {
            energy += energyReceived;
            PowerHandlerUtil.setStoredEnergyForItem(container, energy);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (container.stackSize > 1) {
            return 0;
        }
        CapBankType type = CapBankType.getTypeFromMeta(container.getItemDamage());
        int energy = getEnergyStored(container);
        int maxOutput = type.getMaxIO();
        int energyExtracted = Math.min(energy, Math.min(maxExtract, maxOutput));

        if (!simulate && !type.isCreative()) {
            energy -= energyExtracted;
            PowerHandlerUtil.setStoredEnergyForItem(container, energy);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return PowerHandlerUtil.getStoredEnergyForItem(container);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return CapBankType.getTypeFromMeta(container.getItemDamage()).getMaxEnergyStored();
    }
}
