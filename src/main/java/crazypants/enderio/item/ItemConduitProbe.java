package crazypants.enderio.item;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.ChatUtil;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.tool.IHideFacades;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.network.PacketHandler;
import java.text.NumberFormat;
import java.util.Collection;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemConduitProbe extends Item implements IResourceTooltipProvider, IHideFacades {

    private static final NumberFormat NF = NumberFormat.getIntegerInstance();

    public static ItemConduitProbe create() {

        PacketHandler.INSTANCE.registerMessage(
                PacketConduitProbe.class, PacketConduitProbe.class, PacketHandler.nextID(), Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(
                PacketConduitProbeMode.class, PacketConduitProbeMode.class, PacketHandler.nextID(), Side.SERVER);

        ItemConduitProbe result = new ItemConduitProbe();
        result.init();
        return result;
    }

    public static boolean copyPasteSettings(
            EntityPlayer player, ItemStack stack, IConduitBundle bundle, ForgeDirection dir) {
        boolean isCopy = player.isSneaking();
        boolean clearedData = false;
        NBTTagCompound nbt = stack.stackTagCompound;

        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.stackTagCompound = nbt;
        }

        boolean performedAction = false;
        Collection<IConduit> conduits = bundle.getConduits();
        for (IConduit conduit : conduits) {
            if (conduit.getExternalConnections().contains(dir)) {
                if (isCopy && !clearedData) {
                    nbt = new NBTTagCompound();
                    stack.stackTagCompound = nbt;
                    clearedData = true;
                }
                if (isCopy) {
                    performedAction |= conduit.writeConnectionSettingsToNBT(dir, nbt);
                } else {
                    performedAction |= conduit.readConduitSettingsFromNBT(dir, nbt);
                }
            }
        }

        if (isCopy && performedAction && player.worldObj.isRemote) {
            ChatUtil.sendNoSpamClient("Copied conduit settings");
        }

        return performedAction;
    }

    protected ItemConduitProbe() {
        setCreativeTab(EnderIOTab.tabEnderIO);
        setUnlocalizedName("enderio." + ModObject.itemConduitProbe.name());
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public boolean onItemUse(
            ItemStack itemStack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float par8,
            float par9,
            float par10) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof IConduitBundle)) {
            return false;
        }
        IConduitBundle cb = (IConduitBundle) te;
        if (itemStack.getItemDamage() == 0) {
            if (PacketConduitProbe.canCreatePacket(world, x, y, z)) {
                if (world.isRemote) {
                    PacketHandler.INSTANCE.sendToServer(new PacketConduitProbe(x, y, z, side));
                }
                return true;
            }
        }
        return false;
    }

    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemConduitProbe.unlocalisedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        itemIcon = IIconRegister.registerIcon("enderio:mJReader");
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack stack) {
        return getUnlocalizedName();
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
        return true;
    }
}
