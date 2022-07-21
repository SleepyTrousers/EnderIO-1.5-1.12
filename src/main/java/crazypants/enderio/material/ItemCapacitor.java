package crazypants.enderio.material;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.ICapacitorItem;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class ItemCapacitor extends Item implements ICapacitorItem {

    private static final BasicCapacitor CAP = new BasicCapacitor();

    public static ItemCapacitor create() {
        ItemCapacitor result = new ItemCapacitor();
        result.init();
        return result;
    }

    private final IIcon[] icons;

    protected ItemCapacitor() {
        setCreativeTab(EnderIOTab.tabEnderIO);
        setUnlocalizedName(ModObject.itemBasicCapacitor.unlocalisedName);
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(64);

        icons = new IIcon[Capacitors.values().length];
    }

    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemBasicCapacitor.unlocalisedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        damage = MathHelper.clamp_int(damage, 0, Capacitors.values().length - 1);
        return icons[damage];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        for (int i = 0; i < Capacitors.values().length; i++) {
            icons[i] = IIconRegister.registerIcon(Capacitors.values()[i].iconKey);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, Capacitors.values().length - 1);
        return Capacitors.values()[i].unlocalisedName;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int j = 0; j < Capacitors.values().length; ++j) {
            par3List.add(new ItemStack(par1, 1, j));
            if (Capacitors.values()[j] == Capacitors.TOTEMIC_CAPACITOR) {
                ItemStack stack = new ItemStack(par1, 1, j);
                stack.addEnchantment(Enchantment.efficiency, 5);
                par3List.add(stack);
            }
        }
    }

    @Override
    public ICapacitor getCapacitor(ItemStack stack) {
        int damage = MathHelper.clamp_int(stack.getItemDamage(), 0, Capacitors.values().length - 1);

        if (Capacitors.values()[damage] == Capacitors.TOTEMIC_CAPACITOR) {
            damage = MathHelper.clamp_int(
                    EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) + 1,
                    1,
                    Capacitors.TOTEMIC_CAPACITOR.ordinal());
        }

        return Capacitors.values()[damage].capacitor;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        if (par1ItemStack != null && par1ItemStack.getItemDamage() > 0) {
            par3List.add(EnderIO.lang.localize("machine.tooltip.upgrade"));
            if (SpecialTooltipHandler.showAdvancedTooltips()) {
                SpecialTooltipHandler.addDetailedTooltipFromResources(par3List, "enderio.machine.tooltip.upgrade");
            } else {
                SpecialTooltipHandler.addShowDetailsTooltip(par3List);
            }
        }
    }
}
