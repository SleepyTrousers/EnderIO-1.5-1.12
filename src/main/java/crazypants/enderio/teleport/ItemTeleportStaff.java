package crazypants.enderio.teleport;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTeleportStaff extends ItemTravelStaff {

    protected ItemTeleportStaff() {
        super();
        setUnlocalizedName(ModObject.itemTeleportStaff.name());
    }

    public static ItemTeleportStaff create() {
        ItemTeleportStaff result = new ItemTeleportStaff();
        result.init();
        return result;
    }

    public static boolean isEquipped(EntityPlayer ep) {
        if (ep == null || ep.getCurrentEquippedItem() == null) {
            return false;
        }
        return ep.getCurrentEquippedItem().getItem() == EnderIO.itemTeleportStaff;
    }

    @Override
    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemTeleportStaff.unlocalisedName);
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        setFull(itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        itemIcon = IIconRegister.registerIcon("enderio:itemTeleportStaff");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {
        if (world.isRemote) {
            if (player.isSneaking()) {
                TravelController.instance.activateTravelAccessable(
                        equipped, world, player, TravelSource.TELEPORT_STAFF);
            } else {
                TravelController.instance.doTeleport(player);
            }
        }
        player.swingItem();
        return equipped;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        // Don't allow extracting energy.
        return 0;
    }

    @Override
    public void extractInternal(ItemStack item, int powerUse) {
        // Do nothing, as we have infinite energy.
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
}
