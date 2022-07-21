package crazypants.enderio.machine.hypercube;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.PlayerUtil;
import com.enderio.core.common.util.Util;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.hypercube.TileHyperCube.IoMode;
import crazypants.enderio.machine.hypercube.TileHyperCube.SubChannel;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.PowerHandlerUtil;
import java.text.NumberFormat;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockHyperCube extends BlockEio implements IGuiHandler, IResourceTooltipProvider {

    static final NumberFormat NF = NumberFormat.getIntegerInstance();

    public static BlockHyperCube create() {

        PacketHandler.INSTANCE.registerMessage(
                PacketChannelList.class, PacketChannelList.class, PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(
                PacketClientState.class, PacketClientState.class, PacketHandler.nextID(), Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(
                PacketAddRemoveChannel.class, PacketAddRemoveChannel.class, PacketHandler.nextID(), Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(
                PacketAddRemoveChannel.class, PacketAddRemoveChannel.class, PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(
                PacketStoredPower.class, PacketStoredPower.class, PacketHandler.nextID(), Side.CLIENT);

        BlockHyperCube result = new BlockHyperCube();
        result.init();

        // TODO:1.7 Not getting the client event
        ConnectionHandler ch = new ConnectionHandler();
        FMLCommonHandler.instance().bus().register(ch);
        MinecraftForge.EVENT_BUS.register(ch);

        return result;
    }

    private BlockHyperCube() {
        super(ModObject.blockHyperCube.unlocalisedName, TileHyperCube.class);
        setCreativeTab(null);
    }

    @Override
    protected void init() {
        super.init();
        EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_HYPER_CUBE, this);
    }

    public IIcon getPortalIcon() {
        return blockIcon;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister IIconRegister) {
        blockIcon = IIconRegister.registerIcon("enderio:tesseractPortal");
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
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return 8;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (world.isRemote) {
            return;
        }
        TileHyperCube tr = (TileHyperCube) world.getTileEntity(x, y, z);
        tr.onBlockAdded();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId) {
        if (world.isRemote) {
            return;
        }
        TileHyperCube te = (TileHyperCube) world.getTileEntity(x, y, z);
        te.onNeighborBlockChange();
    }

    private void setChannelOnItem(TileHyperCube hc, ItemStack itemStack) {
        Channel chan = hc.getChannel();
        if (chan != null) {
            NBTTagCompound tag = itemStack.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
                itemStack.setTagCompound(tag);
            }
            tag.setString("channelName", chan.name);
            tag.setBoolean("channelIsPublic", chan.isPublic());
            if (!chan.isPublic()) {
                tag.setString("channelUser", chan.user.toString());
            }
        }
    }

    private void setIoOnItem(TileHyperCube hc, ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTagCompound(tag);
        }
        for (SubChannel sc : SubChannel.values()) {
            tag.setShort("sendRecieve" + sc.ordinal(), (short)
                    hc.getModeForChannel(sc).ordinal());
        }
    }

    private void setIoOnTransciever(TileHyperCube hc, ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) {
            return;
        }
        for (SubChannel sc : SubChannel.values()) {
            if (tag.hasKey("sendRecieve" + sc.ordinal())) {
                hc.setModeForChannel(sc, IoMode.values()[tag.getShort("sendRecieve" + sc.ordinal())]);
            }
        }
    }

    private Channel getChannelFromItem(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) {
            return null;
        }

        String channelName = tag.getString("channelName");
        if (channelName == null || channelName.trim().isEmpty()) {
            return null;
        }

        UUID user = null;
        if (!tag.getBoolean("channelIsPublic")) {
            user = PlayerUtil.getPlayerUIDUnstable(tag.getString("channelUser"));
        }
        return new Channel(channelName, user);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileHyperCube) {
                TileHyperCube hc = (TileHyperCube) te;
                hc.onBreakBlock();
                ItemStack itemStack = new ItemStack(this);
                PowerHandlerUtil.setStoredEnergyForItem(itemStack, hc.getEnergyStored());
                setChannelOnItem(hc, itemStack);
                setIoOnItem(hc, itemStack);
                float f = 0.7F;
                double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
                entityitem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityitem);

                ItemRecieveBuffer rb = hc.getRecieveBuffer();
                Util.dropItems(world, rb, x, y, z, true);
            }
        }
        return super.removedByPlayer(world, player, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        if (world.isRemote) {
            return;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileHyperCube) {
            TileHyperCube cb = (TileHyperCube) te;
            cb.setEnergyStored(PowerHandlerUtil.getStoredEnergyForItem(stack));
            if (player instanceof EntityPlayerMP) {
                cb.setOwner(PlayerUtil.getPlayerUUID(
                        ((EntityPlayerMP) player).getGameProfile().getName()));
            }
            cb.setChannel(getChannelFromItem(stack));
            setIoOnTransciever(cb, stack);
        }
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public int quantityDropped(Random r) {
        return 0;
    }

    @Override
    protected boolean openGui(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
        if (!world.isRemote) {
            entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_HYPER_CUBE, world, x, y, z);
        }
        return true;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerHyperCube();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileHyperCube) {
            TileHyperCube hc = (TileHyperCube) te;
            return new GuiHyperCube(hc);
        }
        return null;
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack stack) {
        return getUnlocalizedName();
    }
}
