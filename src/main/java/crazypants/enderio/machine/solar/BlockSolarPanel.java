package crazypants.enderio.machine.solar;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.waila.IWailaInfoProvider;

public class BlockSolarPanel extends BlockEio implements IResourceTooltipProvider, IWailaInfoProvider {

    public static int renderId;

    public static BlockSolarPanel create() {
        BlockSolarPanel result = new BlockSolarPanel();
        result.init();
        return result;
    }

    private static final float BLOCK_HEIGHT = 0.15f;

    IIcon sideIcon;
    IIcon advancedSideIcon;
    IIcon advancedIcon;

    IIcon borderIcon;
    IIcon advancedBorderIcon;

    IIcon vibrantIcon;
    IIcon vibrantSideIcon;
    IIcon vibrantBorderIcon;

    private BlockSolarPanel() {
        super(ModObject.blockSolarPanel.unlocalisedName, TileEntitySolarPanel.class);
        if (!Config.photovoltaicCellEnabled) {
            setCreativeTab(null);
        }
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
    }

    @Override
    protected void init() {
        GameRegistry.registerBlock(this, BlockItemSolarPanel.class, name);
        if (teClass != null) {
            GameRegistry.registerTileEntity(teClass, name + "TileEntity");
        }
    }

    @Override
    public int damageDropped(int damage) {
        return damage;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == ForgeDirection.UP.ordinal()) {
            switch (meta) {
                case 0:
                    return blockIcon;
                case 1:
                    return advancedIcon;
                case 2:
                    return vibrantIcon;
                default:
                    return blockIcon;
            }
        }
        switch (meta) {
            case 0:
                return sideIcon;
            case 1:
                return advancedSideIcon;
            case 2:
                return vibrantSideIcon;
            default:
                return sideIcon;
        }
    }

    public IIcon getBorderIcon(int i, int meta) {
        switch (meta) {
            case 0:
                return borderIcon;
            case 1:
                return advancedBorderIcon;
            case 2:
                return vibrantBorderIcon;
            default:
                return borderIcon;
        }
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block par5) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntitySolarPanel) {
            ((TileEntitySolarPanel) te).onNeighborBlockChange();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon("enderio:solarPanelTop");
        advancedIcon = register.registerIcon("enderio:solarPanelAdvancedTop");
        vibrantIcon = register.registerIcon("enderio:solarPanelVibrantTop");
        sideIcon = register.registerIcon("enderio:solarPanelSide");
        advancedSideIcon = register.registerIcon("enderio:solarPanelAdvancedSide");
        vibrantSideIcon = register.registerIcon("enderio:solarPanelVibrantSide");
        borderIcon = register.registerIcon("enderio:solarPanelBorder");
        advancedBorderIcon = register.registerIcon("enderio:solarPanelAdvancedBorder");
        vibrantBorderIcon = register.registerIcon("enderio:solarPanelVibrantBorder");
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, BLOCK_HEIGHT, 1.0F);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB,
            List par6List, Entity par7Entity) {
        setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return getUnlocalizedName();
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntitySolarPanel) {
            TileEntitySolarPanel solar = (TileEntitySolarPanel) te;
            float efficiency = solar.calculateLightRatio();
            if (!solar.canSeeSun()) {
                tooltip.add(EnumChatFormatting.RED + EnderIO.lang.localize("tooltip.sunlightBlocked"));
            } else {
                tooltip.add(
                        String.format(
                                "%s : %s%.0f%%",
                                EnumChatFormatting.WHITE + EnderIO.lang.localize("tooltip.efficiency")
                                        + EnumChatFormatting.RESET,
                                EnumChatFormatting.WHITE,
                                efficiency * 100));
            }
        }
    }

    @Override
    public int getDefaultDisplayMask(World world, int x, int y, int z) {
        return 0;
    }
}
