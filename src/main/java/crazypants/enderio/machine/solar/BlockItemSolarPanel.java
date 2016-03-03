package crazypants.enderio.machine.solar;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class BlockItemSolarPanel extends ItemBlock implements IAdvancedTooltipProvider, IResourceTooltipProvider {

  public BlockItemSolarPanel() {
    super(EnderIO.blockSolarPanel);
    setHasSubtypes(true);
    setMaxDamage(0);    
    setCreativeTab(EnderIOTab.tabEnderIO);
  }

  public BlockItemSolarPanel(Block block) {
    super(block);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);    
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getMetadata();
    SolarType type = SolarType.getTypeFromMeta(meta);
    return super.getUnlocalizedName(par1ItemStack) + type.getUnlocalisedName();
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SpecialTooltipHandler.addCommonTooltipFromResources(list, itemstack);
  }


  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String>  list, boolean flag) {
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String>  list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
    int prod = SolarType.getTypeFromMeta(itemstack.getMetadata()).getRfperTick();
    list.add(EnderIO.lang.localize("maxSolorProduction") + " " + PowerDisplayUtil.formatPowerPerTick(prod));
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
	  return super.getUnlocalizedName(itemStack);
  }

}
