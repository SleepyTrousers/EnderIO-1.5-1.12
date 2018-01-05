package crazypants.enderio.machines.machine.tank;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.lang.LangFluid;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockItemTank extends ItemBlock implements IAdvancedTooltipProvider {

  public BlockItemTank(Block block) {
    super(block);
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return super.getUnlocalizedName(stack) + EnumTankType.getType(stack).getSuffix();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull Item par1, @Nonnull CreativeTabs par2CreativeTabs, @Nonnull NonNullList<ItemStack> par3List) {
    NNList.of(EnumTankType.class).apply(new Callback<EnumTankType>() {
      @Override
      public void apply(@Nonnull EnumTankType e) {
        par3List.add(new ItemStack(BlockItemTank.this, 1, EnumTankType.getMeta(e)));
      }
    });
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    ((IAdvancedTooltipProvider) block).addCommonEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SmartTank tank = EnumTankType.loadTank(itemstack);
    if (!tank.isEmpty()) {
      list.add(LangFluid.MB(tank.getFluid(), tank.getCapacity()));
    }
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    ((IAdvancedTooltipProvider) block).addDetailedEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return new TankItemFluidCapability(stack);
  }

}
