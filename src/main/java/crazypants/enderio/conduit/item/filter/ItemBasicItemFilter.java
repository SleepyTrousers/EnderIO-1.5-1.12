package crazypants.enderio.conduit.item.filter;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.item.FilterRegister;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class ItemBasicItemFilter extends Item implements IItemFilterUpgrade {

    public static ItemBasicItemFilter create() {
        ItemBasicItemFilter result = new ItemBasicItemFilter();
        result.init();
        return result;
    }

    private final IIcon[] icons;

    protected ItemBasicItemFilter() {
        setCreativeTab(EnderIOTab.tabEnderIO);
        setUnlocalizedName(ModObject.itemBasicFilterUpgrade.unlocalisedName);
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(64);

        icons = new IIcon[2];
    }

    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemBasicFilterUpgrade.unlocalisedName);
    }

    @Override
    public IItemFilter createFilterFromStack(ItemStack stack) {
        int damage = MathHelper.clamp_int(stack.getItemDamage(), 0, 1);
        ItemFilter filter;
        if (damage == 0) {
            filter = new ItemFilter(false);
        } else {
            filter = new ItemFilter(true);
        }
        if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("filter")) {
            filter.readFromNBT(stack.stackTagCompound.getCompoundTag("filter"));
        }
        return filter;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        damage = MathHelper.clamp_int(damage, 0, 1);
        return icons[damage];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {

        icons[0] = IIconRegister.registerIcon("enderio:filterUpgradeBasic");
        icons[1] = IIconRegister.registerIcon("enderio:filterUpgradeAdvanced");
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 1);
        return i == 0 ? "enderio.filterUpgradeBasic" : "enderio.filterUpgradeAdvanced";
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int j = 0; j < 2; ++j) {
            par3List.add(new ItemStack(this, 1, j));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        if (FilterRegister.isFilterSet(par1ItemStack)) {
            if (!SpecialTooltipHandler.showAdvancedTooltips()) {
                par3List.add(EnderIO.lang.localize("itemConduitFilterUpgrade"));
                SpecialTooltipHandler.addShowDetailsTooltip(par3List);
            } else {
                par3List.add(EnumChatFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.configured"));
                par3List.add(EnumChatFormatting.ITALIC
                        + EnderIO.lang.localize("itemConduitFilterUpgrade.clearConfigMethod"));
            }
        } else {
            par3List.add(EnderIO.lang.localize("itemConduitFilterUpgrade"));
        }
    }
}
