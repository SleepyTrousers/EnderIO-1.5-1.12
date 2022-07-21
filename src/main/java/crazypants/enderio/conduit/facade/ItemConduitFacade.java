package crazypants.enderio.conduit.facade;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.Util;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.IPaintedBlock;
import crazypants.enderio.machine.painter.PaintSourceValidator;
import crazypants.enderio.machine.painter.PainterUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemConduitFacade extends Item implements IAdvancedTooltipProvider, IResourceTooltipProvider {

    public static enum FacadeType {
        BASIC,
        HARDENED;

        public String getUnlocName(Item me) {
            return this == BASIC ? me.getUnlocalizedName() : me.getUnlocalizedName() + ".hardened";
        }
    }

    private IIcon[] icons;

    public static ItemConduitFacade create() {
        ItemConduitFacade result = new ItemConduitFacade();
        result.init();
        return result;
    }

    protected IIcon overlayIcon;

    protected ItemConduitFacade() {
        setCreativeTab(EnderIOTab.tabEnderIO);
        setMaxStackSize(64);
        setHasSubtypes(true);
    }

    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemConduitFacade.unlocalisedName);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
        for (FacadeType t : FacadeType.values()) {
            list.add(new ItemStack(item, 1, t.ordinal()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return FacadeType.values()[stack.getItemDamage()].getUnlocName(this);
    }

    @Override
    public String getUnlocalizedName() {
        return "item.enderio." + ModObject.itemConduitFacade.name();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        icons = new IIcon[FacadeType.values().length];
        icons[0] = itemIcon = IIconRegister.registerIcon("enderio:conduitFacade");
        icons[1] = IIconRegister.registerIcon("enderio:conduitFacadeHardened");
        overlayIcon = IIconRegister.registerIcon("enderio:conduitFacadeOverlay");
    }

    public IIcon getOverlayIcon() {
        return overlayIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return icons[damage % icons.length];
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        return getIconFromDamage(stack.getItemDamage());
    }

    @Override
    public boolean onItemUse(
            ItemStack itemStack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float par8,
            float par9,
            float par10) {

        if (world.isRemote) {
            return true;
        }

        ForgeDirection dir = ForgeDirection.values()[side];
        int placeX = x + dir.offsetX;
        int placeY = y + dir.offsetY;
        int placeZ = z + dir.offsetZ;

        if (player.canPlayerEdit(placeX, placeY, placeZ, side, itemStack)
                && PainterUtil.getSourceBlock(itemStack) != null) {
            if (world.isAirBlock(placeX, placeY, placeZ)) {
                world.setBlock(placeX, placeY, placeZ, EnderIO.blockConduitBundle);
                IConduitBundle bundle = (IConduitBundle) world.getTileEntity(placeX, placeY, placeZ);
                Block facadeID = PainterUtil.getSourceBlock(itemStack);
                int facadeMeta = PainterUtil.getSourceBlockMetadata(itemStack);
                facadeMeta = PainterUtil.adjustFacadeMetadata(facadeID, facadeMeta, side);
                bundle.setFacadeId(facadeID);
                bundle.setFacadeMetadata(facadeMeta);
                bundle.setFacadeType(FacadeType.values()[itemStack.getItemDamage()]);
                ConduitUtil.playPlaceSound(facadeID.stepSound, world, x, y, z);
                if (!player.capabilities.isCreativeMode) {
                    itemStack.stackSize--;
                }
                return true;
            } else {
                Block block = world.getBlock(placeX, placeY, placeZ);
                if (block == EnderIO.blockConduitBundle) {
                    ((BlockConduitBundle) block)
                            .handleFacadeClick(
                                    world,
                                    placeX,
                                    placeY,
                                    placeZ,
                                    player,
                                    dir.getOpposite().ordinal(),
                                    (IConduitBundle) world.getTileEntity(placeX, placeY, placeZ),
                                    itemStack);
                }
            }
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    public ItemStack createItemStackForSourceBlock(Block id, int itemDamage) {
        if (id == null) {
            id = EnderIO.blockConduitFacade;
        }
        ItemStack result = new ItemStack(id, 1, 0);
        PainterUtil.setSourceBlock(result, id, itemDamage);
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        super.addInformation(item, par2EntityPlayer, list, par4);
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return getUnlocalizedName();
    }

    @SuppressWarnings("rawtypes")
    @Override
    @SideOnly(Side.CLIENT)
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        if (PainterUtil.getSourceBlock(itemstack) == null) {
            list.add(EnderIO.lang.localize("item.itemConduitFacade.tooltip.notpainted"));
        } else {
            list.add(PainterUtil.getTooltTipText(itemstack));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
        if (itemstack.getItemDamage() == FacadeType.HARDENED.ordinal()) {
            list.add("");
            list.add(EnderIO.lang.localizeExact(getUnlocalizedName(itemstack) + ".tooltip"));
        }
    }

    public final class FacadePainterRecipe extends BasicPainterTemplate {

        @Override
        public boolean isValidPaintSource(ItemStack paintSource) {
            if (paintSource == null) {
                return false;
            }
            if (paintSource.getItem() == ItemConduitFacade.this) {
                return true;
            }
            Block block = Util.getBlockFromItemId(paintSource);
            if (block == null || block instanceof IPaintedBlock) {
                return false;
            }
            if (PaintSourceValidator.instance.isBlacklisted(paintSource)) {
                return false;
            }
            if (PaintSourceValidator.instance.isWhitelisted(paintSource)) {
                return true;
            }
            if (!Config.allowTileEntitiesAsPaintSource && block.hasTileEntity(paintSource.getItemDamage())) {
                return false;
            }
            if (block == EnderIO.blockFusedQuartz && paintSource.getItemDamage() < 2) {
                return true;
            }
            return block.getRenderType() == 0 || block.isOpaqueCube() || block.isNormalCube();
        }

        @Override
        public boolean isValidTarget(ItemStack target) {
            return target != null && target.getItem() == ItemConduitFacade.this;
        }

        @Override
        public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
            ResultStack[] res = super.getCompletedResult(chance, inputs);
            return res;
        }
    }
}
