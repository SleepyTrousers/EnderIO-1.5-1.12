package crazypants.enderio.teleport;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemInfiniteTravelStaff extends ItemTravelStaff {

    protected ItemInfiniteTravelStaff() {
        super();
        setUnlocalizedName(ModObject.itemInfiniteTravelStaff.name());
    }

    public static ItemInfiniteTravelStaff create() {
        ItemInfiniteTravelStaff result = new ItemInfiniteTravelStaff();
        result.init();
        return result;
    }

    public static boolean isEquipped(EntityPlayer ep) {
        if (ep == null || ep.getCurrentEquippedItem() == null) {
            return false;
        }
        return ep.getCurrentEquippedItem().getItem() == EnderIO.itemInfiniteTravelStaff;
    }

    @Override
    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemInfiniteTravelStaff.unlocalisedName);
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        setFull(itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        itemIcon = IIconRegister.registerIcon("enderio:itemInfiniteTravelStaff");
    }

    @Override
    public void extractInternal(ItemStack item, int powerUse) {
        return;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
        ItemStack is = new ItemStack(this);
        setFull(is);
        par3List.add(is);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, par2EntityPlayer, list, par4);
        String str = "Infinite " + PowerDisplayUtil.abrevation();
        list.set(list.size() - 1, str); // Changing charge indicator to infinite RF
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
}
