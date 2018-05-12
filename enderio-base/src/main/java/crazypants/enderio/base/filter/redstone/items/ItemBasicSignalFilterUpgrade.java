package crazypants.enderio.base.filter.redstone.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.filter.redstone.IOutputSignalFilter;
import crazypants.enderio.base.filter.redstone.InvertingOutputSignalFilter;
import crazypants.enderio.base.init.IModObject;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBasicSignalFilterUpgrade extends Item implements IItemOutputSignalFilterUpgrade {

  public static ItemBasicSignalFilterUpgrade create(@Nonnull IModObject modObject) {
    return new ItemBasicSignalFilterUpgrade(modObject);
  }

  public ItemBasicSignalFilterUpgrade(@Nonnull IModObject modObject) {
    super();
    modObject.apply(this);
  }

  @Override
  public IOutputSignalFilter createFilterFromStack(@Nonnull ItemStack stack) {
    return new InvertingOutputSignalFilter();
  }

  @Override
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    return null;
  }

}
