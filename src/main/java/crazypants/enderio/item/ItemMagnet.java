package crazypants.enderio.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cofh.api.energy.ItemEnergyContainer;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.ItemUtil;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.util.BaublesUtil;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles|API")
public class ItemMagnet extends ItemEnergyContainer implements IResourceTooltipProvider, IBauble {

    private static final String ACTIVE_KEY = "magnetActive";

    public static void setActive(ItemStack item, boolean active) {
        if (item == null) {
            return;
        }
        NBTTagCompound nbt = ItemUtil.getOrCreateNBT(item);
        nbt.setBoolean(ACTIVE_KEY, active);
    }

    public static boolean isActive(ItemStack item) {
        if (item == null) {
            return false;
        }
        if (item.stackTagCompound == null) {
            return false;
        }
        if (!item.stackTagCompound.hasKey(ACTIVE_KEY)) {
            return false;
        }
        return item.stackTagCompound.getBoolean(ACTIVE_KEY);
    }

    public static boolean hasPower(ItemStack itemStack) {
        int energyStored = DarkSteelItems.itemMagnet.getEnergyStored(itemStack);
        return energyStored > 0 && energyStored >= Config.magnetPowerUsePerSecondRF;
    }

    public static void drainPerSecondPower(ItemStack itemStack) {
        DarkSteelItems.itemMagnet.extractEnergyInternal(itemStack, Config.magnetPowerUsePerSecondRF, false);
    }

    static MagnetController controller = new MagnetController();

    public static ItemMagnet create() {
        ItemMagnet result = new ItemMagnet();
        result.init();
        FMLCommonHandler.instance().bus().register(controller);
        return result;
    }

    protected ItemMagnet() {
        super(Config.magnetPowerCapacityRF, Config.magnetPowerCapacityRF / 100);
        setCreativeTab(EnderIOTab.tabEnderIO);
        setUnlocalizedName(ModObject.itemMagnet.unlocalisedName);
        setMaxDamage(16);
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemMagnet.unlocalisedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        itemIcon = IIconRegister.registerIcon("enderio:magnet");
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
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, par2EntityPlayer, list, par4);
        String str = PowerDisplayUtil.formatPower(getEnergyStored(itemStack)) + "/"
                + PowerDisplayUtil.formatPower(getMaxEnergyStored(itemStack)) + " " + PowerDisplayUtil.abrevation();
        list.add(str);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack item, int pass) {
        return isActive(item);
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        setEnergy(itemStack, 0);
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        int res = super.receiveEnergy(container, maxReceive, simulate);
        if (res != 0 && !simulate) {
            updateDamage(container);
        }
        return res;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (Config.magnetAllowPowerExtraction) {
            return extractEnergyInternal(container, maxExtract, simulate);
        } else {
            return 0;
        }
    }

    public int extractEnergyInternal(ItemStack container, int maxExtract, boolean simulate) {
        int res = super.extractEnergy(container, maxExtract, simulate);
        if (res != 0 && !simulate) {
            updateDamage(container);
        }
        return res;
    }

    void setEnergy(ItemStack container, int energy) {
        if (container.stackTagCompound == null) {
            container.stackTagCompound = new NBTTagCompound();
        }
        container.stackTagCompound.setInteger("Energy", energy);
        updateDamage(container);
    }

    void setFull(ItemStack container) {
        setEnergy(container, Config.magnetPowerCapacityRF);
    }

    private void updateDamage(ItemStack stack) {
        float r = (float) getEnergyStored(stack) / getMaxEnergyStored(stack);
        int res = 16 - (int) (r * 16);
        stack.setItemDamage(res);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            setActive(equipped, !isActive(equipped));
        }
        return equipped;
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack stack) {
        return getUnlocalizedName();
    }

    @Override
    @Method(modid = "Baubles|API")
    public BaubleType getBaubleType(ItemStack itemstack) {
        BaubleType t = null;
        try {
            t = BaubleType.valueOf(Config.magnetBaublesType);
        } catch (Exception e) {
            // NOP
        }
        return t != null ? t : BaubleType.AMULET;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayer
                && isActive(itemstack)
                && hasPower(itemstack)
                && ((EntityPlayer) player).getHealth() > 0f) {
            controller.doHoover((EntityPlayer) player);
            if (!player.worldObj.isRemote && player.worldObj.getTotalWorldTime() % 20 == 0) {
                ItemMagnet.drainPerSecondPower(itemstack);
                IInventory baubles = BaublesUtil.instance().getBaubles((EntityPlayer) player);
                if (baubles != null) {
                    baubles.markDirty();
                }
            }
        }
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return Config.magnetAllowInBaublesSlot && (Config.magnetAllowDeactivatedInBaublesSlot || isActive(itemstack));
    }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}
