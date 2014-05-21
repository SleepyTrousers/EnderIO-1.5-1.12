package crazypants.enderio.machine.solar;

import java.util.List;

import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.util.Lang;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class BlockItemSolorPanel extends ItemBlockWithMetadata implements IAdvancedTooltipProvider{

  public BlockItemSolorPanel() {
    super(EnderIO.blockSolarPanel, EnderIO.blockSolarPanel);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }
  
  public BlockItemSolorPanel(Block block) {
    super(block, block);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }
  
  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int meta = par1ItemStack.getItemDamage();
    String result = super.getUnlocalizedName(par1ItemStack);   
    if(meta == 1) {
      result += ".advanced";
    }
    return result;
  }
  
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    ItemStack stack = new ItemStack(this, 1,0);
    par3List.add(stack);
    stack = new ItemStack(this, 1,1);
    par3List.add(stack);
  }
  
  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {       
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {       
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    TooltipAddera.addDetailedTooltipFromResources(list, itemstack); 
    float prod = (float)Config.maxPhotovoltaicOutput;
    if(itemstack.getItemDamage() == 1) {
      prod = (float)Config.maxPhotovoltaicAdvancedOutput;
    }
    list.add(Lang.localize("maxSolorProduction") + " " + PowerDisplayUtil.formatPowerPerTick(prod));
  }

}
