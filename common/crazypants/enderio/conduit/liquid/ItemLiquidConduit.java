package crazypants.enderio.conduit.liquid;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;

public class ItemLiquidConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
    new ItemConduitSubtype(ModObject.itemLiquidConduit.unlocalisedName, ModObject.itemLiquidConduit.name, "enderio:itemLiquidConduit"),        
  };
  
  public static ItemLiquidConduit create() {
    ItemLiquidConduit result = new ItemLiquidConduit();
    result.init(subtypes);
    return result;
  }

  protected ItemLiquidConduit() {
    super(ModObject.itemLiquidConduit);    
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack stack) {
    return new LiquidConduit();
  }

}
