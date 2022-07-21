package crazypants.enderio.machine.power;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.TileEntityEnder;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ChatUtil;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.tool.ToolUtil;
import crazypants.enderio.waila.IWailaInfoProvider;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCapacitorBank extends BlockEio implements IGuiHandler, IAdvancedTooltipProvider, IWailaInfoProvider {

    public static int renderId = -1;

    public static BlockCapacitorBank create() {
        PacketHandler.INSTANCE.registerMessage(
                PacketClientState.class, PacketClientState.class, PacketHandler.nextID(), Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(
                PacketPowerStorage.class, PacketPowerStorage.class, PacketHandler.nextID(), Side.CLIENT);

        BlockCapacitorBank res = new BlockCapacitorBank();
        res.init();
        return res;
    }

    IIcon overlayIcon;
    IIcon fillBarIcon;

    private IIcon blockIconInput;
    private IIcon blockIconOutput;
    private IIcon blockIconLocked;

    protected BlockCapacitorBank() {
        super(ModObject.blockCapacitorBank.unlocalisedName, TileCapacitorBank.class);
        setHardness(2.0F);
        setCreativeTab(null);
    }

    @Override
    protected void init() {

        GameRegistry.registerBlock(this, BlockItemCapacitorBank.class, name);
        if (teClass != null) {
            GameRegistry.registerTileEntity(teClass, name + "TileEntity");
        }

        EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_CAPACITOR_BANK, this);
        setLightOpacity(255);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list) {}

    @Override
    public int damageDropped(int par1) {
        return par1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        list.add(PowerDisplayUtil.formatStoredPower(
                PowerHandlerUtil.getStoredEnergyForItem(itemstack), TileCapacitorBank.BASE_CAP.getMaxEnergyStored()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
    }

    @Override
    public boolean onBlockActivated(
            World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileCapacitorBank)) {
            return false;
        }

        if (ToolUtil.isToolEquipped(entityPlayer)) {

            ForgeDirection faceHit = ForgeDirection.getOrientation(side);
            TileCapacitorBank tcb = (TileCapacitorBank) te;
            tcb.toggleIoModeForFace(faceHit);
            if (world.isRemote) {
                world.markBlockForUpdate(x, y, z);
            } else {
                world.notifyBlocksOfNeighborChange(x, y, z, EnderIO.blockCapacitorBank);
                world.markBlockForUpdate(x, y, z);
            }

            return true;
        }

        return super.onBlockActivated(world, x, y, z, entityPlayer, side, par7, par8, par9);
    }

    @Override
    protected boolean openGui(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
        if (!world.isRemote) {
            entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_CAPACITOR_BANK, world, x, y, z);
        }
        return true;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCapacitorBank) {
            return new ContainerCapacitorBank(player, player.inventory, ((TileCapacitorBank) te).getController());
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCapacitorBank) {
            return new GuiCapacitorBank(player, player.inventory, ((TileCapacitorBank) te).getController());
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister IIconRegister) {
        blockIcon = IIconRegister.registerIcon("enderio:capacitorBank");
        blockIconInput = IIconRegister.registerIcon("enderio:capacitorBankInput");
        blockIconOutput = IIconRegister.registerIcon("enderio:capacitorBankOutput");
        blockIconLocked = IIconRegister.registerIcon("enderio:capacitorBankLocked");
        overlayIcon = IIconRegister.registerIcon("enderio:capacitorBankOverlays");
        fillBarIcon = IIconRegister.registerIcon("enderio:capacitorBankFillBar");
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
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
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        Block i1 = par1IBlockAccess.getBlock(par2, par3, par4);
        return i1 == this ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess ba, int x, int y, int z, int side) {
        TileEntity te = ba.getTileEntity(x, y, z);
        if (!(te instanceof TileCapacitorBank)) {
            return blockIcon;
        }
        TileCapacitorBank cb = (TileCapacitorBank) te;
        IoMode mode = cb.getIoMode(ForgeDirection.values()[side]);
        if (mode == null || mode == IoMode.NONE) {
            return blockIcon;
        }
        if (mode == IoMode.PULL) {
            return blockIconInput;
        }
        if (mode == IoMode.PUSH) {
            return blockIconOutput;
        }
        return blockIconLocked;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (world.isRemote) {
            return;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCapacitorBank) {
            TileCapacitorBank tr = (TileCapacitorBank) te;
            int meta = world.getBlockMetadata(x, y, z);
            if (meta == 1) {
                tr.setCreativeMode();
            }
            tr.onBlockAdded();
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId) {
        if (world.isRemote) {
            return;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileCapacitorBank) {
            TileCapacitorBank te = (TileCapacitorBank) tile;
            te.onNeighborBlockChange(blockId);
        }
    }

    @Override
    public boolean doNormalDrops(World world, int x, int y, int z) {
        return false;
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityEnder te, ItemStack drop) {
        PowerHandlerUtil.setStoredEnergyForItem(drop, ((TileCapacitorBank) te).doGetEnergyStored());
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        if (world.isRemote) {
            return;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCapacitorBank) {
            TileCapacitorBank cb = (TileCapacitorBank) te;
            cb.addEnergy(PowerHandlerUtil.getStoredEnergyForItem(stack));
            if (player instanceof EntityPlayerMP) {
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    BlockCoord bc = new BlockCoord(x, y, z);
                    bc = bc.getLocation(dir);
                    te = world.getTileEntity(bc.x, bc.y, bc.z);
                    if (te instanceof TileCapacitorBank) {
                        if (((TileCapacitorBank) te).isMaxSize()) {
                            ChatUtil.sendNoSpam((EntityPlayerMP) player, "Capacitor bank is at maximum size");
                        }
                    }
                }
            }
        }
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public int quantityDropped(Random r) {
        return 0;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (!(te instanceof TileCapacitorBank)) {
                super.breakBlock(world, x, y, z, par5, par6);
                return;
            }
            TileCapacitorBank cb = (TileCapacitorBank) te;
            Util.dropItems(world, cb, x, y, z, true);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileCapacitorBank)) {
            return super.getSelectedBoundingBoxFromPool(world, x, y, z);
        }
        TileCapacitorBank tr = (TileCapacitorBank) te;
        if (!tr.isMultiblock()) {
            return super.getSelectedBoundingBoxFromPool(world, x, y, z);
        }

        Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
        for (BlockCoord bc : tr.multiblock) {
            min.x = Math.min(min.x, bc.x);
            max.x = Math.max(max.x, bc.x + 1);
            min.y = Math.min(min.y, bc.y);
            max.y = Math.max(max.y, bc.y + 1);
            min.z = Math.min(min.z, bc.z);
            max.z = Math.max(max.z, bc.z + 1);
        }
        return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World w, int x, int y, int z, int side) {
        TileEntity te = w.getTileEntity(x, y, z);
        if (te instanceof TileCapacitorBank) {
            return ((TileCapacitorBank) te).getComparatorOutput();
        }
        return 0;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCapacitorBank) {
            TileCapacitorBank cap = (TileCapacitorBank) te;
            String format = Util.TAB + Util.ALIGNRIGHT + EnumChatFormatting.WHITE;

            tooltip.add(String.format(
                    "%s : %s%s%sRF/t ",
                    EnderIO.lang.localize("capbank.maxIO"),
                    format,
                    PowerDisplayUtil.formatPower(cap.getMaxIO()),
                    Util.TAB + Util.ALIGNRIGHT));
            tooltip.add(String.format(
                    "%s : %s%s%sRF/t ",
                    EnderIO.lang.localize("capbank.maxIn"),
                    format,
                    PowerDisplayUtil.formatPower(cap.getMaxInput()),
                    Util.TAB + Util.ALIGNRIGHT));
            tooltip.add(String.format(
                    "%s : %s%s%sRF/t ",
                    EnderIO.lang.localize("capbank.maxOut"),
                    format,
                    PowerDisplayUtil.formatPower(cap.getMaxOutput()),
                    Util.TAB + Util.ALIGNRIGHT));
            tooltip.add("Deprecated. Convert by placing in crafting grid");
        }
    }

    @Override
    public int getDefaultDisplayMask(World world, int x, int y, int z) {
        return IWailaInfoProvider.BIT_DETAILED;
    }
}
