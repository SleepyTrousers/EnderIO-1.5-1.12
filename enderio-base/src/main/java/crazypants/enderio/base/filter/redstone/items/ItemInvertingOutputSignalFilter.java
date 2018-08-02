package crazypants.enderio.base.filter.redstone.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.filter.redstone.IOutputSignalFilter;
import crazypants.enderio.base.filter.redstone.InvertingOutputSignalFilter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemInvertingOutputSignalFilter extends Item implements IItemOutputSignalFilterUpgrade {

  public static ItemInvertingOutputSignalFilter create(@Nonnull IModObject modObject) {
    return new ItemInvertingOutputSignalFilter(modObject);
  }

  public ItemInvertingOutputSignalFilter(@Nonnull IModObject modObject) {
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public IOutputSignalFilter createFilterFromStack(@Nonnull ItemStack stack) {
    return new InvertingOutputSignalFilter();
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    return null;
  }

}
