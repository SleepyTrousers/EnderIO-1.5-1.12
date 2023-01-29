package crazypants.enderio.machine.vacuum;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.TileEntityEnder;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.network.PacketHandler;

public class BlockVacuumChest extends BlockEio implements IGuiHandler, IResourceTooltipProvider, IRedstoneConnectable {

    public static BlockVacuumChest create() {
        PacketHandler.INSTANCE
                .registerMessage(PacketVaccumChest.class, PacketVaccumChest.class, PacketHandler.nextID(), Side.SERVER);
        BlockVacuumChest res = new BlockVacuumChest();
        res.init();
        return res;
    }

    public static int renderId;

    protected BlockVacuumChest() {
        super(ModObject.blockVacuumChest.unlocalisedName, TileVacuumChest.class);
        setBlockTextureName("enderio:blockVacuumChest");
    }

    @Override
    public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, ForgeDirection from) {
        return true;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId) {
        TileEntity ent = world.getTileEntity(x, y, z);
        if (ent instanceof TileVacuumChest) {
            ((TileVacuumChest) ent).onNeighborBlockChange(blockId);
        }
    }

    @Override
    protected void init() {
        super.init();
        EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_VACUUM_CHEST, this);
    }

    @Override
    protected boolean openGui(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
        if (!world.isRemote) {
            entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_VACUUM_CHEST, world, x, y, z);
        }
        return true;
    }

    @Override
    public boolean doNormalDrops(World world, int x, int y, int z) {
        return false;
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityEnder te, ItemStack drop) {
        drop.stackTagCompound = new NBTTagCompound();
        if (te != null) {
            ((TileVacuumChest) te).writeContentsToNBT(drop.stackTagCompound);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placedBy, ItemStack stack) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (stack != null && stack.stackTagCompound != null && te instanceof TileVacuumChest) {
                ((TileVacuumChest) te).readContentsFromNBT(stack.stackTagCompound);
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileVacuumChest) {
            return new ContainerVacuumChest(player, player.inventory, (TileVacuumChest) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileVacuumChest) {
            return new GuiVacuumChest(player, player.inventory, (TileVacuumChest) te);
        }
        return null;
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return getUnlocalizedName();
    }
}
