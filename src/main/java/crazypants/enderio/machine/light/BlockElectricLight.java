package crazypants.enderio.machine.light;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.TileEntityEnder;
import com.enderio.core.common.vecmath.Vector3f;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.redstone.IRedstoneConnectable;

public class BlockElectricLight extends BlockEio implements IRedstoneConnectable {

    static final float BLOCK_HEIGHT = 0.05f;
    static final float BLOCK_WIDTH = 0.3f;

    static final float BLOCK_EDGE_MAX = 0.5f + (BLOCK_WIDTH / 2);
    static final float BLOCK_EDGE_MIN = 0.5f - (BLOCK_WIDTH / 2);

    public static int renderId;

    public static BlockElectricLight create() {
        BlockElectricLight result = new BlockElectricLight();
        result.init();
        return result;
    }

    private IIcon blockIconOff;
    private IIcon blockIconSide;

    public BlockElectricLight() {
        super(ModObject.blockElectricLight.unlocalisedName, TileElectricLight.class);
        setLightOpacity(0);
        setBlockBounds(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN, BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
    }

    @Override
    protected void init() {
        GameRegistry.registerBlock(this, BlockItemElectricLight.class, ModObject.blockElectricLight.unlocalisedName);
        GameRegistry.registerTileEntity(
                TileElectricLight.class,
                ModObject.blockElectricLight.unlocalisedName + "TileEntity");
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iIconRegister) {
        blockIcon = iIconRegister.registerIcon("enderio:blockElectricLightFace");
        blockIconOff = iIconRegister.registerIcon("enderio:blockElectricLightFaceOff");
        blockIconSide = iIconRegister.registerIcon("enderio:conduitConnector");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {

        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if (te instanceof TileElectricLight) {
            ForgeDirection onFace = ((TileElectricLight) te).getFace();
            if (side == (onFace.getOpposite().ordinal())) {
                boolean on = blockAccess.getBlockMetadata(x, y, z) != 0;
                return on ? blockIcon : blockIconOff;
            }
            return blockIconSide;
        }
        return getIcon(side, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int par2) {
        if (side == ForgeDirection.UP.ordinal()) {
            return blockIcon;
        }
        return blockIconSide;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block != null && block != this) {
            return block.getLightValue(world, x, y, z);
        }
        return world.getBlockMetadata(x, y, z) > 0 ? 15 : 0;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        ForgeDirection onFace = ForgeDirection.DOWN;
        TileEntity te = blockAccess.getTileEntity(x, y, z);
        if (te instanceof TileElectricLight) {
            onFace = ((TileElectricLight) te).getFace();
        }

        Vector3f min = new Vector3f();
        Vector3f max = new Vector3f();
        switch (onFace) {
            case UP:
                min.set(BLOCK_EDGE_MIN, 1F - BLOCK_HEIGHT, BLOCK_EDGE_MIN);
                max.set(BLOCK_EDGE_MAX, 1F, BLOCK_EDGE_MAX);
                break;
            case DOWN:
                min.set(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN);
                max.set(BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
                break;
            case EAST:
                min.set(1 - BLOCK_HEIGHT, BLOCK_EDGE_MIN, BLOCK_EDGE_MIN);
                max.set(1, BLOCK_EDGE_MAX, BLOCK_EDGE_MAX);
                break;
            case WEST:
                min.set(0, BLOCK_EDGE_MIN, BLOCK_EDGE_MIN);
                max.set(BLOCK_HEIGHT, BLOCK_EDGE_MAX, BLOCK_EDGE_MAX);
                break;
            case NORTH:
                min.set(BLOCK_EDGE_MIN, BLOCK_EDGE_MIN, 0);
                max.set(BLOCK_EDGE_MAX, BLOCK_EDGE_MAX, BLOCK_HEIGHT);
                break;
            case SOUTH:
                min.set(BLOCK_EDGE_MIN, BLOCK_EDGE_MIN, 1 - BLOCK_HEIGHT);
                max.set(BLOCK_EDGE_MAX, BLOCK_EDGE_MAX, 1);
                break;
            case UNKNOWN:
            default:
                min.set(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN);
                max.set(BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
                break;
        }

        setBlockBounds(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(BLOCK_EDGE_MIN, 0.0F, BLOCK_EDGE_MIN, BLOCK_EDGE_MAX, BLOCK_HEIGHT, BLOCK_EDGE_MAX);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block blockID) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileElectricLight) {
            ((TileElectricLight) te).onNeighborBlockChange(blockID);
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileElectricLight) {
            ((TileElectricLight) te).onBlockRemoved();
        }
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityEnder te, ItemStack drop) {
        TileElectricLight light = (TileElectricLight) te;
        if (light == null) {
            return;
        }
        int meta = light.isInvereted() ? 1 : 0;
        if (!light.isRequiresPower()) {
            meta += 2;
        } else if (light.isWireless()) {
            meta += 4;
        }
        drop.setItemDamage(meta);
    }

    @Override
    public boolean doNormalDrops(World world, int x, int y, int z) {
        return false;
    }

    /* IRedstoneConnectable */

    @Override
    public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, ForgeDirection from) {
        return true;
    }
}
