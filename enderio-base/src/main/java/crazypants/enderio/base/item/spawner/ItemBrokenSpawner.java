package crazypants.enderio.base.item.spawner;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.recipe.spawner.PoweredSpawnerRecipeRegistry;
import crazypants.enderio.base.render.itemoverlay.MobNameOverlayRenderHelper;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBrokenSpawner extends Item implements IOverlayRenderAware {

  public static ItemBrokenSpawner create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemBrokenSpawner(modObject);
  }

  protected ItemBrokenSpawner(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public boolean isDamageable() {
    return false;
  }

  @SuppressWarnings("null")
  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (tab == getCreativeTab()) {
      list.add(CapturedMob.create(new ResourceLocation("minecraft", "chicken")).toStack(this, 0, 1));
    } else if (tab == EnderIOTab.tabEnderIOMobs) {
      for (CapturedMob capturedMob : CapturedMob.getAllSouls()) {
        if (!PoweredSpawnerRecipeRegistry.getInstance().isBlackListed(capturedMob.getEntityName())) {
          list.add(capturedMob.toStack(this, 0, 1));
        }
      }
    }
  }

  @Override
  public @Nonnull CreativeTabs[] getCreativeTabs() {
    return new CreativeTabs[] { getCreativeTab(), EnderIOTab.tabEnderIOMobs };
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    CapturedMob mob = CapturedMob.create(stack);
    if (mob != null) {
      tooltip.add(mob.getDisplayName());
    }
    if (!SpecialTooltipHandler.showAdvancedTooltips()) {
      SpecialTooltipHandler.addShowDetailsTooltip(tooltip);
    } else {
      SpecialTooltipHandler.addDetailedTooltipFromResources(tooltip, stack);
    }
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    MobNameOverlayRenderHelper.doItemOverlayIntoGUI(stack, xPosition, yPosition);
  }

}
