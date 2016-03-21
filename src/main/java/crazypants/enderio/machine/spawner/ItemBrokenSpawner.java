package crazypants.enderio.machine.spawner;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

import static crazypants.util.NbtValue.MOBTYPE;
import static crazypants.util.NbtValue.WITHER_SKELETON;

public class ItemBrokenSpawner extends Item {

  private static final String[] CREATIVE_TYPES = new String[] { "Skeleton", "Zombie", "Spider", "CaveSpider", "Blaze", "Enderman", "Chicken" };

  public static String getMobTypeFromStack(ItemStack stack) {
    return MOBTYPE.getString(stack, null);
  }

  public static boolean isWitherSkeleton(ItemStack item) {
    return WITHER_SKELETON.hasTag(item);
  }

  public static ItemStack createStackForMobType(String mobType, boolean isWitherSkeleton) {
    if (mobType == null) {
      return null;
    }
    ItemStack res = new ItemStack(EnderIO.itemBrokenSpawner);
    if (isWitherSkeleton) {
      WITHER_SKELETON.setInt(res, 1);
    }
    return MOBTYPE.setString(res, mobType);
  }

  public static ItemBrokenSpawner create() {
    ItemBrokenSpawner result = new ItemBrokenSpawner();
    result.init();
    return result;
  }

  protected ItemBrokenSpawner() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemBrokenSpawner.unlocalisedName);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public boolean isDamageable() {
    return false;
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemBrokenSpawner.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (String mobType : CREATIVE_TYPES) {
      par3List.add(createStackForMobType(mobType, false));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    if (par1ItemStack != null && par1ItemStack.getTagCompound() != null) {
      String mobName = getMobTypeFromStack(par1ItemStack);
      if (mobName != null) {
        if (isWitherSkeleton(par1ItemStack)) {
          par3List.add(StatCollector.translateToLocal("entity.witherSkeleton.name"));
        } else {
          par3List.add(StatCollector.translateToLocal("entity." + mobName + ".name"));
        }
      }
      if (!SpecialTooltipHandler.showAdvancedTooltips()) {
        SpecialTooltipHandler.addShowDetailsTooltip(par3List);
      } else {
        SpecialTooltipHandler.addDetailedTooltipFromResources(par3List, par1ItemStack);
      }
    }

  }

}
