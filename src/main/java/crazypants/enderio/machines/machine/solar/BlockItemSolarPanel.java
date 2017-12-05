package crazypants.enderio.machines.machine.solar;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.lang.LangPower;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockItemSolarPanel extends ItemBlock implements IAdvancedTooltipProvider, IResourceTooltipProvider {

  public BlockItemSolarPanel(BlockSolarPanel blockSolarPanel) {
    super(blockSolarPanel);
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack par1ItemStack) {
    int meta = par1ItemStack.getMetadata();
    SolarType type = SolarType.getTypeFromMeta(meta);
    return super.getUnlocalizedName(par1ItemStack) + type.getUnlocalisedName();
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addCommonTooltipFromResources(list, itemstack);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
    int prod = SolarType.getTypeFromMeta(itemstack.getMetadata()).getRfperTick();
    list.add(EnderIO.lang.localize("maxSolorProduction") + " " + LangPower.RFt(prod));
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return super.getUnlocalizedName(itemStack);
  }

}
