package crazypants.enderio.material;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.*;
import crazypants.enderio.*;
import crazypants.enderio.power.*;

public class ItemCapacitor extends Item implements ICapacitorItem {
  
  private static final BasicCapacitor CAP = new BasicCapacitor();
  
  public static ItemCapacitor create() {
    ItemCapacitor result = new ItemCapacitor();
    result.init();
    return result;
  }

  protected ItemCapacitor() {
    super(ModObject.itemBasicCapacitor.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemBasicCapacitor.unlocalisedName);
    setMaxStackSize(64);
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.itemBasicCapacitor.name);        
    GameRegistry.registerItem(this, ModObject.itemBasicCapacitor.unlocalisedName);    
  }
    
  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {   
    itemIcon = iconRegister.registerIcon("enderio:basicCapacitor");    
  }

  @Override
  public ICapacitor getCapacitor() {
    return CAP;
  }
  
}
