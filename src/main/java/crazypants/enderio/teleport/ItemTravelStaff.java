package crazypants.enderio.teleport;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import cofh.api.energy.ItemEnergyContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class ItemTravelStaff extends ItemEnergyContainer implements IItemOfTravel, IResourceTooltipProvider {

    public static boolean isEquipped(EntityPlayer ep) {
        if (ep == null || ep.getCurrentEquippedItem() == null) {
            return false;
        }
        return ep.getCurrentEquippedItem().getItem() == EnderIO.itemTravelStaff;
    }

    private long lastBlickTick = 0;

    public static ItemTravelStaff create() {
        ItemTravelStaff result = new ItemTravelStaff();
        result.init();
        return result;
    }

    protected ItemTravelStaff() {
        super(Config.darkSteelPowerStorageLevelTwo, Config.darkSteelPowerStorageLevelTwo / 100, 0);
        setCreativeTab(EnderIOTab.tabEnderIO);
        setUnlocalizedName(ModObject.itemTravelStaff.name());
        setMaxDamage(16);
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemTravelStaff.unlocalisedName);
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        setEnergy(itemStack, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        itemIcon = IIconRegister.registerIcon("enderio:itemTravelStaff");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            long ticksSinceBlink = EnderIO.proxy.getTickCount() - lastBlickTick;
            if (ticksSinceBlink < 0) {
                lastBlickTick = -1;
            }
            if (Config.travelStaffBlinkEnabled && world.isRemote
                    && ticksSinceBlink >= Config.travelStaffBlinkPauseTicks) {
                if (TravelController.instance.doBlink(equipped, player)) {
                    player.swingItem();
                    lastBlickTick = EnderIO.proxy.getTickCount();
                }
            }
            return equipped;
        }

        if (world.isRemote) {
            TravelController.instance.activateTravelAccessable(equipped, world, player, TravelSource.STAFF);
        }
        player.swingItem();
        return equipped;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, par2EntityPlayer, list, par4);
        String str = PowerDisplayUtil.formatPower(getEnergyStored(itemStack)) + "/"
                + PowerDisplayUtil.formatPower(getMaxEnergyStored(itemStack))
                + " "
                + PowerDisplayUtil.abrevation();
        list.add(str);
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (maxReceive < 0) {
            return 0;
        }

        int res = super.receiveEnergy(container, maxReceive, simulate);
        if (res != 0 && !simulate) {
            updateDamage(container);
        }
        return res;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (maxExtract < 0) {
            return 0;
        }

        int res = super.extractEnergy(container, maxExtract, simulate);
        if (res != 0 && !simulate) {
            updateDamage(container);
        }
        return res;
    }

    @Override
    public void extractInternal(ItemStack item, int powerUse) {
        int res = Math.max(0, getEnergyStored(item) - powerUse);
        setEnergy(item, res);
    }

    @Override
    public int canExtractInternal(ItemStack equipped, int power) {
        return Math.min(getEnergyStored(equipped), power);
    }

    void setEnergy(ItemStack container, int energy) {
        if (container.stackTagCompound == null) {
            container.stackTagCompound = new NBTTagCompound();
        }
        container.stackTagCompound.setInteger("Energy", energy);
        updateDamage(container);
    }

    public void setFull(ItemStack container) {
        setEnergy(container, Config.darkSteelPowerStorageLevelTwo);
    }

    private void updateDamage(ItemStack stack) {
        float r = (float) getEnergyStored(stack) / getMaxEnergyStored(stack);
        int res = 16 - (int) (r * 16);
        stack.setItemDamage(res);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
        ItemStack is = new ItemStack(this);
        setFull(is);
        par3List.add(is);

        is = new ItemStack(this);
        setEnergy(is, 0);
        par3List.add(is);
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack stack) {
        return getUnlocalizedName();
    }

    @Override
    public boolean isActive(EntityPlayer ep, ItemStack equipped) {
        return isEquipped(ep);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return Config.renderDurabilityBar && super.showDurabilityBar(stack);
    }
}
