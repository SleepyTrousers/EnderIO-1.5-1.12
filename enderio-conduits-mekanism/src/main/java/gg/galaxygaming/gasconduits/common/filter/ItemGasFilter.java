package gg.galaxygaming.gasconduits.common.filter;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.TileEntityBase;
import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.IFilterContainer;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.EnumReader;
import crazypants.enderio.util.NbtValue;
import gg.galaxygaming.gasconduits.client.GasFilterGui;
import gg.galaxygaming.gasconduits.common.conduit.GasConduitObject;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGasFilter extends Item implements IItemFilterGasUpgrade, IResourceTooltipProvider {

    public static ItemGasFilter create(@Nonnull IModObject modObject, @Nullable Block block) {
        return new ItemGasFilter(modObject);
    }

    protected ItemGasFilter(@Nonnull IModObject modObject) {
        setCreativeTab(EnderIOTab.tabEnderIOItems);
        modObject.apply(this);
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(64);
    }

    @Override
    public IGasFilter createFilterFromStack(@Nonnull ItemStack stack) {
        IGasFilter filter = new GasFilter();
        if (NbtValue.FILTER.hasTag(stack)) {
            filter.readFromNBT(NbtValue.FILTER.getTag(stack));
        }
        return filter;
    }


    @Nonnull
    @Override
    public String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
        return getUnlocalizedName(stack);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (!world.isRemote && player.isSneaking()) {
            GasConduitObject.itemGasFilter.openGui(world, player.getPosition(), player, null, hand.ordinal());
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (FilterRegistry.isFilterSet(stack) && SpecialTooltipHandler.showAdvancedTooltips()) {
            tooltip.add(Lang.ITEM_FILTER_CONFIGURED.get(TextFormatting.ITALIC));
            tooltip.add(Lang.ITEM_FILTER_CLEAR.get(TextFormatting.ITALIC));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    @SideOnly(Side.CLIENT)
    public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
        Container container = player.openContainer;
        if (container instanceof IFilterContainer) {
            return new GasFilterGui(player.inventory, new ContainerFilter(player, (TileEntityBase) world.getTileEntity(pos), facing, param1), world.getTileEntity(pos),
                  ((IFilterContainer<IGasFilter>) container).getFilter(param1));
        }
        IFilter filter = FilterRegistry.getFilterForUpgrade(player.getHeldItem(EnumReader.get(EnumHand.class, param1)));
        if (filter instanceof IGasFilter) {
            //Should always be true, mainly double checked to avoid null warning
            return new GasFilterGui(player.inventory, new ContainerFilter(player, null, facing, param1), null, (IGasFilter) filter);
        }
        return null;
    }
}