package crazypants.enderio.machine.wireless;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.TileEntityEnder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.network.PacketHandler;

public class BlockWirelessCharger extends BlockEio implements IResourceTooltipProvider /* IGuiHandler */ {

    public static BlockWirelessCharger create() {

        PacketHandler.INSTANCE.registerMessage(
                PacketStoredEnergy.class,
                PacketStoredEnergy.class,
                PacketHandler.nextID(),
                Side.CLIENT);

        BlockWirelessCharger res = new BlockWirelessCharger();
        res.init();
        return res;
    }

    public static int renderId = 0;

    private IIcon centerOn;
    private IIcon centerOff;

    protected BlockWirelessCharger() {
        super(ModObject.blockWirelessCharger.unlocalisedName, TileWirelessCharger.class);
        setLightOpacity(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iIconRegister) {
        centerOn = iIconRegister.registerIcon("enderio:blockWirelessChargerOn");
        centerOff = iIconRegister.registerIcon("enderio:blockWirelessChargerOff");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int p_149673_5_) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileWirelessCharger) {
            TileWirelessCharger twc = (TileWirelessCharger) te;
            if (twc.isActive()) {
                return centerOn;
            }
        }
        return centerOff;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return centerOff;
    }

    public IIcon getCenterOn() {
        return centerOn;
    }

    public IIcon getCenterOff() {
        return centerOff;
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    // @Override
    // public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // TileEntity te = world.getTileEntity(x, y, z);
    // if(te instanceof TileWire) {
    // return new ContainerVacuumChest(player, player.inventory, (TileVacuumChest) te);
    // }
    // return null;
    // }
    //
    // @Override
    // public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // TileEntity te = world.getTileEntity(x, y, z);
    // if(te instanceof TileVacuumChest) {
    // return new GuiVacuumChest(player, player.inventory, (TileVacuumChest) te);
    // }
    // return null;
    // }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return getUnlocalizedName();
    }

    @Override
    public boolean doNormalDrops(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);

        if (stack.stackTagCompound != null) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileWirelessCharger) {
                ((TileWirelessCharger) te).readCustomNBT(stack.stackTagCompound);
            }
        }
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityEnder te, ItemStack drop) {
        drop.stackTagCompound = new NBTTagCompound();
        if (te instanceof TileWirelessCharger) {
            ((TileWirelessCharger) te).writeCustomNBT(drop.stackTagCompound);
        }
    }
}
