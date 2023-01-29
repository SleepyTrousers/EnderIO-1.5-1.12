package crazypants.enderio.machine.obelisk.xp;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.xp.XpUtil;

public class ItemXpTransfer extends Item implements IResourceTooltipProvider {

    public static ItemXpTransfer create() {
        PacketHandler.INSTANCE.registerMessage(
                PacketXpTransferEffects.class,
                PacketXpTransferEffects.class,
                PacketHandler.nextID(),
                Side.CLIENT);

        ItemXpTransfer result = new ItemXpTransfer();
        result.init();
        return result;
    }

    protected ItemXpTransfer() {
        setCreativeTab(EnderIOTab.tabEnderIO);
        setUnlocalizedName(ModObject.itemXpTransfer.unlocalisedName);
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        return onActivated(player, world, x, y, z, side);
    }

    public static boolean onActivated(EntityPlayer player, World world, int x, int y, int z, int side) {
        if (world.isRemote) {
            return false;
        }
        boolean res;
        boolean swing = false;
        if (player.isSneaking()) {
            res = tranferFromPlayerToBlock(player, world, x, y, z, side);
            swing = res;
        } else {
            res = tranferFromBlockToPlayer(player, world, x, y, z, side);
        }

        if (res) {
            sendXPUpdate(player, world, x, y, z, swing);
        }

        return res;
    }

    public static void sendXPUpdate(EntityPlayer player, World world, int x, int y, int z, boolean swing) {
        Vector3d look = Util.getLookVecEio(player);
        double xP = player.posX + look.x;
        double yP = player.posY + 1.5;
        double zP = player.posZ + look.z;
        TargetPoint tp = new TargetPoint(player.dimension, x, y, z, 32);
        EnderIO.packetPipeline.INSTANCE.sendTo(new PacketXpTransferEffects(swing, xP, yP, zP), (EntityPlayerMP) player);
        world.playSoundEffect(
                x + 0.5,
                y + 0.5,
                z + 0.5,
                "random.orb",
                0.1F,
                0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
    }

    public static boolean tranferFromBlockToPlayer(EntityPlayer player, World world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof IFluidHandler)) {
            return false;
        }
        IFluidHandler fh = (IFluidHandler) te;
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if (!fh.canDrain(dir, EnderIO.fluidXpJuice)) {
            return false;
        }
        int currentXP = XpUtil.getPlayerXP(player);
        int nextLevelXP = XpUtil.getExperienceForLevel(player.experienceLevel + 1) + 1;
        int requiredXP = nextLevelXP - currentXP;

        int fluidVolume = XpUtil.experienceToLiquid(requiredXP);
        FluidStack fs = new FluidStack(EnderIO.fluidXpJuice, fluidVolume);
        FluidStack res = fh.drain(dir, fs, true);
        if (res == null || res.amount <= 0) {
            return false;
        }

        int xpToGive = XpUtil.liquidToExperience(res.amount);
        player.addExperience(xpToGive);

        return true;
    }

    public static boolean tranferFromPlayerToBlock(EntityPlayer player, World world, int x, int y, int z, int side) {

        if (player.experienceTotal <= 0) {
            return false;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof IFluidHandler)) {
            return false;
        }
        IFluidHandler fh = (IFluidHandler) te;
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        if (!fh.canFill(dir, EnderIO.fluidXpJuice)) {
            return false;
        }

        int fluidVolume = XpUtil.experienceToLiquid(XpUtil.getPlayerXP(player));
        FluidStack fs = new FluidStack(EnderIO.fluidXpJuice, fluidVolume);
        int takenVolume = fh.fill(dir, fs, true);
        if (takenVolume <= 0) {
            return false;
        }
        int xpToTake = XpUtil.liquidToExperience(takenVolume);
        XpUtil.addPlayerXP(player, -xpToTake);
        return true;
    }

    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemXpTransfer.unlocalisedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        itemIcon = IIconRegister.registerIcon("enderio:xpTransfer");
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack stack) {
        return getUnlocalizedName();
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }
}
