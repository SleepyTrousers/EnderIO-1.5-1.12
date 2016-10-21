package crazypants.enderio.machine.spawner;

import java.util.List;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.util.CapturedMob;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBrokenSpawner extends Item {

  private static final String[] CREATIVE_TYPES = new String[] { "Skeleton", "Zombie", "Spider", "CaveSpider", "Blaze", "Enderman", "Chicken" };

  public static ItemBrokenSpawner create() {
    ItemBrokenSpawner result = new ItemBrokenSpawner();
    result.init();
    return result;
  }

  protected ItemBrokenSpawner() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemBrokenSpawner.getUnlocalisedName());
    setRegistryName(ModObject.itemBrokenSpawner.getUnlocalisedName());
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public boolean isDamageable() {
    return false;
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @SuppressWarnings("null")
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (String mobType : CREATIVE_TYPES) {
      if (mobType.equals(CapturedMob.SKELETON_ENTITY_NAME)) {
        for (SkeletonType type : SkeletonType.values()) {
          par3List.add(CapturedMob.create(mobType, type).toStack(par1, 0, 1));
        }
      } else {
        par3List.add(CapturedMob.create(mobType, null).toStack(par1, 0, 1));
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    CapturedMob mob = CapturedMob.create(par1ItemStack);
    if (mob != null) {
      par3List.add(mob.getDisplayName());
    }
    if (!SpecialTooltipHandler.showAdvancedTooltips()) {
      SpecialTooltipHandler.addShowDetailsTooltip(par3List);
    } else {
      SpecialTooltipHandler.addDetailedTooltipFromResources(par3List, par1ItemStack);
    }
  }

}
