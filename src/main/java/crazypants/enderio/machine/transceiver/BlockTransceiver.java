package crazypants.enderio.machine.transceiver;

import com.enderio.core.common.util.Util;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.transceiver.gui.ContainerTransceiver;
import crazypants.enderio.machine.transceiver.gui.GuiTransceiver;
import crazypants.enderio.network.PacketHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockTransceiver extends AbstractMachineBlock<TileTransceiver> {

    public static BlockTransceiver create() {

        PacketHandler.INSTANCE.registerMessage(
                PacketSendRecieveChannel.class, PacketSendRecieveChannel.class, PacketHandler.nextID(), Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(
                PacketAddRemoveChannel.class, PacketAddRemoveChannel.class, PacketHandler.nextID(), Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(
                PacketAddRemoveChannel.class, PacketAddRemoveChannel.class, PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(
                PacketChannelList.class, PacketChannelList.class, PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(
                PacketSendRecieveChannelList.class,
                PacketSendRecieveChannelList.class,
                PacketHandler.nextID(),
                Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(
                PacketItemFilter.class, PacketItemFilter.class, PacketHandler.nextID(), Side.SERVER);

        ConnectionHandler ch = new ConnectionHandler();
        FMLCommonHandler.instance().bus().register(ch);
        MinecraftForge.EVENT_BUS.register(ch);

        BlockTransceiver res = new BlockTransceiver();
        res.init();
        return res;
    }

    private BlockTransceiver() {
        super(ModObject.blockTransceiver, TileTransceiver.class);
        if (!Config.transceiverEnabled) {
            setCreativeTab(null);
        }
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean doHarvest) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileTransceiver) {
                ((TileTransceiver) te).getRailController().dropNonSpawnedCarts();
            }
        }
        return super.removedByPlayer(world, player, x, y, z, doHarvest);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileTransceiver) {
            return new ContainerTransceiver(player.inventory, (TileTransceiver) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        return new GuiTransceiver(player.inventory, (TileTransceiver) te);
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_TRANSCEIVER;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void registerOverlayIcons(IIconRegister iIconRegister) {
        super.registerOverlayIcons(iIconRegister);
        overlayIconPull = iIconRegister.registerIcon("enderio:overlays/transcieverPull");
        overlayIconPush = iIconRegister.registerIcon("enderio:overlays/transcieverPush");
        overlayIconPushPull = iIconRegister.registerIcon("enderio:overlays/transcieverPushPull");
        overlayIconDisabled = iIconRegister.registerIcon("enderio:overlays/transcieverDisabled");
    }

    @Override
    protected String getMachineFrontIconKey(boolean active) {
        if (active) {
            return "enderio:alloySmelterFrontOn";
        }
        return "enderio:alloySmelterFront";
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {}

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileTransceiver && player.isSneaking()) {
            TileTransceiver trans = (TileTransceiver) te;
            for (ChannelType type : ChannelType.VALUES) {

                Set<Channel> recieving = trans.getRecieveChannels(type);
                Set<Channel> sending = trans.getSendChannels(type);
                String recieve = "[" + buildString(recieving) + "]";
                String send = "[" + buildString(sending) + "]";

                if (isEmpty(recieve) && isEmpty(send)) {
                    continue;
                }

                tooltip.add(EnumChatFormatting.WHITE
                        + EnderIO.lang.localize("trans." + type.name().toLowerCase(Locale.US)));

                if (!isEmpty(recieve)) {
                    tooltip.add(String.format(
                            "%s%s " + Util.TAB + ": %s%s",
                            Util.TAB,
                            EnderIO.lang.localize("trans.receiving"),
                            Util.TAB + Util.ALIGNRIGHT + EnumChatFormatting.WHITE,
                            recieve));
                }
                if (!isEmpty(send)) {
                    tooltip.add(String.format(
                            "%s%s " + Util.TAB + ": %s%s",
                            Util.TAB,
                            EnderIO.lang.localize("trans.sending"),
                            Util.TAB + Util.ALIGNRIGHT + EnumChatFormatting.WHITE,
                            send));
                }
            }
        }
    }

    private boolean isEmpty(String str) {
        return "[]".equals(str);
    }

    private String buildString(Set<Channel> recieving) {
        StringBuilder sb = new StringBuilder();
        Iterator<Channel> iter = recieving.iterator();
        while (iter.hasNext()) {
            Channel c = iter.next();
            sb.append(c.getName());
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
