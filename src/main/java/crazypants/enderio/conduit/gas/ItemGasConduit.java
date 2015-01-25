package crazypants.enderio.conduit.gas;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.util.Lang;

public class ItemGasConduit extends AbstractItemConduit implements IAdvancedTooltipProvider {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemGasConduit.name(), "enderio:itemGasConduit"),
  };

  public static ItemGasConduit create() {
    ItemGasConduit result = new ItemGasConduit();
    result.init(subtypes);
    return result;
  }

  protected ItemGasConduit() {
    super(ModObject.itemGasConduit);
    if(!GasUtil.isGasConduitEnabled()) {
      setCreativeTab(null);
    }
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IGasConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack stack, EntityPlayer player) {
    return new GasConduit();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {

  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    String gpt = " " + Lang.localize("gas.gasTick");
    int extractRate = Config.gasConduitExtractRate;
    int maxIo = Config.gasConduitMaxIoRate;
    list.add(Lang.localize("itemGasConduit.tooltip.maxExtract") + " " + extractRate + gpt);
    list.add(Lang.localize("itemGasConduit.tooltip.maxIo") + " " + maxIo + gpt);
    TooltipAddera.addDetailedTooltipFromResources(list, "enderio.itemGasConduit");
  }
  
  @Override
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    return true;
  }

}
