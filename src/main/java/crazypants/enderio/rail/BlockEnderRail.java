package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.MetadataUtil;
import com.enderio.core.common.util.RoundRobinIterator;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.transceiver.Channel;
import crazypants.enderio.machine.transceiver.ChannelType;
import crazypants.enderio.machine.transceiver.ServerChannelRegister;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.ToolUtil;

public class BlockEnderRail extends BlockRail implements IResourceTooltipProvider {

    public static boolean isReverse(int meta) {
        return MetadataUtil.isBitSet(3, meta);
    }

    public static ForgeDirection getDirection(int meta) {
        ForgeDirection result;
        if (isEastWest(meta)) {
            result = ForgeDirection.EAST;
        } else {
            result = ForgeDirection.SOUTH;
        }
        if (isReverse(meta)) {
            result = result.getOpposite();
        }
        return result;
    }

    private static boolean isEastWest(int meta) {
        return MetadataUtil.isBitSet(0, meta);
    }

    public static BlockEnderRail create() {
        PacketHandler.INSTANCE.registerMessage(
                PacketTeleportEffects.class,
                PacketTeleportEffects.class,
                PacketHandler.nextID(),
                Side.CLIENT);
        BlockEnderRail res = new BlockEnderRail();
        res.init();

        if (Config.enderRailTeleportPlayers) {
            FMLCommonHandler.instance().bus().register(PlayerTeleportHandler.instance);
        }
        return res;
    }

    private IIcon iconEastWest;
    private IIcon iconEastWestTurned;

    private int linkId;

    protected BlockEnderRail() {
        setBlockName(ModObject.blockEnderRail.unlocalisedName);
        setStepSound(Block.soundTypeMetal);
        if (Config.transceiverEnabled && Config.enderRailEnabled) {
            setCreativeTab(EnderIOTab.tabEnderIO);
        }
        setBlockTextureName("enderio:blockEnderRail");
        setHardness(0.7F);
        setStepSound(soundTypeMetal);
    }

    private void init() {
        GameRegistry.registerBlock(this, ModObject.blockEnderRail.unlocalisedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        super.registerBlockIcons(register);
        iconEastWest = register.registerIcon("enderio:blockEnderRailEastWest");
        iconEastWestTurned = register.registerIcon("enderio:blockEnderRailEastWest_turned");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (!isEastWest(meta)) {
            return super.getIcon(side, meta);
        } else if (isReverse(meta)) {
            return iconEastWestTurned;
        }
        return iconEastWest;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
            float par8, float par9) {
        if (ToolUtil.isToolEquipped(player)) {
            if (!world.isRemote) {
                int meta = world.getBlockMetadata(x, y, z);
                meta = MetadataUtil.setBit(3, !MetadataUtil.isBitSet(3, meta), meta);
                world.setBlockMetadataWithNotify(x, y, z, meta, 2);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
        return false;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y - 1, z);
            if (te instanceof TileTransceiver) {
                ((TileTransceiver) te).getRailController().dropNonSpawnedCarts();
            }
        }
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public int getBasicRailMetadata(IBlockAccess world, EntityMinecart cart, int x, int y, int z) {
        // Ignore turning bit, used for receive direction
        return world.getBlockMetadata(x, y, z) & 7;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y - 1, z) == EnderIO.blockTransceiver;
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return canPlaceBlockAt(world, x, y, z);
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, int y, int x, int z) {
        return false;
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return getUnlocalizedName();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.isRemote) {
            return;
        }

        int origMeta = world.getBlockMetadata(x, y, z);
        int newMeta = origMeta;
        if (field_150053_a) {
            newMeta = origMeta & 7;
        }

        if (!canBlockStay(world, x, y, z)) {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        } else {
            func_150048_a(world, x, y, z, origMeta, newMeta, block);
        }
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        if (world.isRemote) {
            return;
        }
        TileEntity te = world.getTileEntity(x, y - 1, z);
        if (!(te instanceof TileTransceiver)) {
            return;
        }
        TileTransceiver ter = (TileTransceiver) te;
        if (ter.getRailController().isRecievedCart(cart)) {
            return;
        }
        tryTeleport(world, cart, x, y, z);
    }

    private void tryTeleport(World world, EntityMinecart cart, int x, int y, int z) {

        TileEntity te = world.getTileEntity(x, y - 1, z);
        if (!(te instanceof TileTransceiver)) {
            return;
        }
        TileTransceiver sender = (TileTransceiver) te;
        if (!sender.isRedstoneChecksPassed()) {
            return;
        }
        if (!sender.hasPower()) {
            return;
        }
        Set<Channel> sendChannels = sender.getSendChannels(ChannelType.RAIL);
        for (Channel channel : sendChannels) {
            RoundRobinIterator<TileTransceiver> iter = ServerChannelRegister.instance.getIterator(channel);
            for (TileTransceiver reciever : iter) {
                if (isValidDestination(sender, channel, reciever)) {
                    int requiredPower = getPowerRequired(cart, sender, reciever);
                    if (sender.getEnergyStored() >= requiredPower) {
                        if (teleportCart(world, cart, sender, reciever)) {
                            sender.usePower(requiredPower);
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean isValidDestination(TileTransceiver sender, Channel channel, TileTransceiver reciever) {
        if (reciever == sender) {
            return false;
        }
        if (!reciever.getRecieveChannels(ChannelType.RAIL).contains(channel)) {
            return false;
        }
        if (!reciever.isRedstoneChecksPassed() || !sender.isRedstoneChecksPassed()) {
            return false;
        }
        if (!reciever.hasPower()) {
            return false;
        }
        Block blk = reciever.getWorldObj().getBlock(reciever.xCoord, reciever.yCoord + 1, reciever.zCoord);
        if (blk != EnderIO.blockEnderRail) {
            return false;
        }
        return reciever.getRailController().isClear();
    }

    private int getPowerRequired(EntityMinecart cart, TileTransceiver sender, TileTransceiver reciever) {
        int powerPerCart = getPowerRequiredForSingleCart(sender, reciever);
        int numCarts = CartLinkUtil.instance.getNumberOfCartsInTrain(cart);
        return powerPerCart * numCarts;
    }

    private int getPowerRequiredForSingleCart(TileTransceiver sender, TileTransceiver reciever) {
        int powerRequired = 0;
        if (sender.getWorldObj().provider.dimensionId != reciever.getWorldObj().provider.dimensionId) {
            powerRequired = Config.enderRailPowerRequireCrossDimensions;
        } else {
            powerRequired += sender.getLocation().getDist(reciever.getLocation())
                    * Config.enderRailPowerRequiredPerBlock;
            if (Config.enderRailCapSameDimensionPowerAtCrossDimensionCost) {
                powerRequired = Math.min(powerRequired, Config.enderRailPowerRequireCrossDimensions);
            }
        }
        return powerRequired;
    }

    private boolean teleportCart(World world, EntityMinecart cart, TileTransceiver sender, TileTransceiver reciever) {

        List<EntityMinecart> allCarts = CartLinkUtil.instance.getCartsInTrain(cart);
        if (allCarts.size() > 1) {
            CartLinkUtil.instance.updateCartLinks(world, cart);
        }

        List<List<Entity>> toTeleport = new ArrayList<List<Entity>>(allCarts.size());
        List<EntityMinecart> toDespawn = new ArrayList<EntityMinecart>(allCarts.size());
        EntityPlayerMP playerToTP = null;
        EntityMinecart playerToMount = null;
        for (EntityMinecart cartInTrain : allCarts) {
            if (cartInTrain != null) {
                List<Entity> entities = TeleportUtil.createEntitiesForReciever(cartInTrain, sender, reciever);
                if (entities != null) {
                    toTeleport.add(entities);
                    toDespawn.add(cartInTrain);
                    if (Config.enderRailTeleportPlayers && cartInTrain.riddenByEntity instanceof EntityPlayerMP) {
                        playerToTP = (EntityPlayerMP) cartInTrain.riddenByEntity;
                        playerToMount = getCart(entities);
                    }
                }
            }
        }
        for (EntityMinecart despawnCart : toDespawn) {
            TeleportUtil.spawnTeleportEffects(world, despawnCart);
            TeleportUtil.despawn(sender.getWorldObj(), despawnCart);
        }
        reciever.getRailController().onTrainRecieved(toTeleport);
        if (playerToTP != null) {
            PlayerTeleportHandler.instance.teleportPlayer(reciever, playerToTP, playerToMount);
        }
        return true;
    }

    private EntityMinecart getCart(List<Entity> entities) {
        for (Entity ent : entities) {
            if (ent instanceof EntityMinecart) {
                return (EntityMinecart) ent;
            }
        }
        return null;
    }
}
