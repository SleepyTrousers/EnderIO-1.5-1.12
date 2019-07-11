package crazypants.enderio.base.filter.redstone.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.filter.redstone.ComparatorInputSignalFilter;
import crazypants.enderio.base.filter.redstone.IInputSignalFilter;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemComparatorInputSignalFilter extends Item implements IItemInputSignalFilterUpgrade {

  public static ItemComparatorInputSignalFilter create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemComparatorInputSignalFilter(modObject);
  }

  public ItemComparatorInputSignalFilter(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public IInputSignalFilter createFilterFromStack(@Nonnull ItemStack stack) {
    return new ComparatorInputSignalFilter();
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    return null;
  }

}
