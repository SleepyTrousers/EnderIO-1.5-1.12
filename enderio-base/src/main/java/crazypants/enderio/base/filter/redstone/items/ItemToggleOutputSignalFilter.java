package crazypants.enderio.base.filter.redstone.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.filter.redstone.IOutputSignalFilter;
import crazypants.enderio.base.filter.redstone.ToggleOutputSignalFilter;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.util.NbtValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemToggleOutputSignalFilter extends Item implements IItemOutputSignalFilterUpgrade {

  public static ItemToggleOutputSignalFilter create(@Nonnull IModObject modObject) {
    return new ItemToggleOutputSignalFilter(modObject);
  }

  public ItemToggleOutputSignalFilter(@Nonnull IModObject modObject) {
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public IOutputSignalFilter createFilterFromStack(@Nonnull ItemStack stack) {
    ToggleOutputSignalFilter filter = new ToggleOutputSignalFilter();
    if (NbtValue.FILTER.hasTag(stack)) {
      filter.readFromNBT(NbtValue.FILTER.getTag(stack));
    }
    return filter;
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    return null;
  }

}
