package crazypants.enderio.machine.farm;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.network.PacketHandler;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFarmStation extends AbstractMachineBlock<TileFarmStation> {

    public static BlockFarmStation create() {
        PacketHandler.INSTANCE.registerMessage(
                PacketFarmAction.class, PacketFarmAction.class, PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(
                PacketUpdateNotification.class, PacketUpdateNotification.class, PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(
                PacketFarmLockedSlot.class, PacketFarmLockedSlot.class, PacketHandler.nextID(), Side.SERVER);
        BlockFarmStation result = new BlockFarmStation();
        result.init();
        return result;
    }

    public static int renderId;

    private IIcon[] overlays;

    protected BlockFarmStation() {
        super(ModObject.blockFarmStation, TileFarmStation.class);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileFarmStation) {
            return new FarmStationContainer(player.inventory, (TileFarmStation) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileFarmStation) {
            return new GuiFarmStation(player.inventory, (TileFarmStation) te);
        }
        return null;
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_FARM_STATATION;
    }

    @Override
    protected void registerOverlayIcons(IIconRegister iIconRegister) {
        super.registerOverlayIcons(iIconRegister);
        overlays = new IIcon[IoMode.values().length];
        overlays[IoMode.PULL.ordinal()] = iIconRegister.registerIcon("enderio:overlays/pullSides");
        overlays[IoMode.PUSH.ordinal()] = iIconRegister.registerIcon("enderio:overlays/pushSides");
        overlays[IoMode.PUSH_PULL.ordinal()] = iIconRegister.registerIcon("enderio:overlays/pushPullSides");
        overlays[IoMode.DISABLED.ordinal()] = iIconRegister.registerIcon("enderio:overlays/disabledSides");
    }

    @Override
    public IIcon getOverlayIconForMode(TileFarmStation tile, ForgeDirection face, IoMode mode) {
        if (face.offsetY != 0 || mode == IoMode.NONE) {
            return super.getOverlayIconForMode(tile, face, mode);
        }
        return overlays[mode.ordinal()];
    }

    @Override
    protected String getMachineFrontIconKey(boolean active) {
        return getBackIconKey(active);
    }

    @Override
    protected String getSideIconKey(boolean active) {
        return super.getBackIconKey(active);
    }

    @Override
    protected String getModelIconKey(boolean active) {
        return "enderio:farmModel";
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(
            IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    public IIcon getFrontIcon() {
        return iconBuffer[0][3];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {}

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack stack) {
        return getUnlocalizedName();
    }
}
