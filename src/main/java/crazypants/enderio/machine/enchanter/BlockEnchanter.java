package crazypants.enderio.machine.enchanter;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.Util;

import cpw.mods.fml.common.network.IGuiHandler;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;

public class BlockEnchanter extends BlockEio implements IGuiHandler, IResourceTooltipProvider {

    public static BlockEnchanter create() {
        BlockEnchanter res = new BlockEnchanter();
        res.init();
        return res;
    }

    public static int renderId = -1;

    protected BlockEnchanter() {
        super(ModObject.blockEnchanter.unlocalisedName, TileEnchanter.class);
        setBlockTextureName("enderio:blockEnchanter");
        setLightOpacity(4);
    }

    @Override
    protected void init() {
        super.init();
        EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_ENCHANTER, this);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
        int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        TileEnchanter te = (TileEnchanter) world.getTileEntity(x, y, z);
        switch (heading) {
            case 0:
                te.setFacing((short) 2);
                break;
            case 1:
                te.setFacing((short) 5);
                break;
            case 2:
                te.setFacing((short) 3);
                break;
            case 3:
                te.setFacing((short) 4);
                break;
            default:
                break;
        }
        if (world.isRemote) {
            return;
        }
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    protected boolean openGui(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
        if (!world.isRemote) {
            entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_ENCHANTER, world, x, y, z);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEnchanter) {
            dropItems(world, x, y, z, (TileEnchanter) te);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    public boolean doNormalDrops(World world, int x, int y, int z) {
        return false;
    }

    private void dropItems(World world, int x, int y, int z, TileEnchanter te) {
        if (te.getStackInSlot(0) != null) {
            Util.dropItems(world, te.getStackInSlot(0), x, y, z, true);
        }
        if (te.getStackInSlot(1) != null) {
            Util.dropItems(world, te.getStackInSlot(1), x, y, z, true);
        }
    }

    @Override
    public int quantityDropped(Random p_149745_1_) {
        return 0;
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
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEnchanter) {
            return new ContainerEnchanter(player, player.inventory, (TileEnchanter) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEnchanter) {
            return new GuiEnchanter(player, player.inventory, (TileEnchanter) te);
        }
        return null;
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return getUnlocalizedName();
    }
}
