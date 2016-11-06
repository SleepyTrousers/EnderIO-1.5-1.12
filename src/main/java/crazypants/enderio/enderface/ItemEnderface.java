package crazypants.enderio.enderface;

import java.util.List;

import crazypants.enderio.GuiID;
import crazypants.enderio.ModObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEnderface extends Item implements IGuiHandler {
  
  public static ItemEnderface create() {
    ItemEnderface result = new ItemEnderface();
    result.init();
    return result;
  }

  protected ItemEnderface() {
    setCreativeTab(null);
    setUnlocalizedName("enderio." + ModObject.itemEnderface.name());
    setMaxStackSize(1);
    setRegistryName(ModObject.itemEnderface.getUnlocalisedName());
  }

  protected void init() {
    GameRegistry.register(this);    
    GuiID.registerGuiHandler(GuiID.GUI_ID_ENDERFACE, this);
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return true;
  }
  
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiEnderface(player, world, x, y, z);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    if (tab != null) {
      super.getSubItems(itemIn, tab, subItems);
    }
  }

}
