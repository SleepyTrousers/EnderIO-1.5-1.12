package crazypants.enderio.machine.spawner;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemBrokenSpawner extends Item {

    private static final String[] CREATIVE_TYPES = new String[] { "Skeleton", "Zombie", "Spider", "CaveSpider", "Blaze",
            "Enderman", "Chicken" };

    public static String getMobTypeFromStack(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("mobType")) {
            return null;
        }
        return stack.stackTagCompound.getString("mobType");
    }

    public static ItemStack createStackForMobType(String mobType) {
        if (mobType == null) {
            return null;
        }
        ItemStack res = new ItemStack(EnderIO.itemBrokenSpawner);
        res.stackTagCompound = new NBTTagCompound();
        res.stackTagCompound.setString("mobType", mobType);
        return res;
    }

    public static ItemBrokenSpawner create() {
        ItemBrokenSpawner result = new ItemBrokenSpawner();
        result.init();
        return result;
    }

    protected ItemBrokenSpawner() {
        setCreativeTab(EnderIOTab.tabEnderIO);
        setUnlocalizedName(ModObject.itemBrokenSpawner.unlocalisedName);
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(64);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemBrokenSpawner.unlocalisedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iIconRegister) {
        itemIcon = iIconRegister.registerIcon("enderio:itemBrokenSpawner");
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (String mobType : CREATIVE_TYPES) {
            par3List.add(createStackForMobType(mobType));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        if (par1ItemStack != null && par1ItemStack.stackTagCompound != null) {
            String mobName = getMobTypeFromStack(par1ItemStack);
            if (mobName != null) {
                par3List.add(StatCollector.translateToLocal("entity." + mobName + ".name"));
            }
            if (!SpecialTooltipHandler.showAdvancedTooltips()) {
                SpecialTooltipHandler.addShowDetailsTooltip(par3List);
            } else {
                SpecialTooltipHandler.addDetailedTooltipFromResources(par3List, par1ItemStack);
            }
        }
    }
}
