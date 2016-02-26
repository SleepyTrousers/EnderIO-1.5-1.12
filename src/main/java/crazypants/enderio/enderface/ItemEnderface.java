package crazypants.enderio.enderface;

import crazypants.enderio.ModObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemEnderface extends Item {
  
  public static ItemEnderface create() {
    ItemEnderface result = new ItemEnderface();
    result.init();
    return result;
  }

  protected ItemEnderface() {
    setCreativeTab(null);
    setUnlocalizedName("enderio." + ModObject.itemEnderface.name());
    setMaxStackSize(1);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemEnderface.unlocalisedName);    
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return true;
  }


}
