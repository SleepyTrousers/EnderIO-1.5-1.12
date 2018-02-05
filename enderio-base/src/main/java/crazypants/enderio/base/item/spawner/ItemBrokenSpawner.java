package crazypants.enderio.base.item.spawner;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.itemoverlay.MobNameOverlayRenderHelper;
import crazypants.enderio.util.CapturedMob;
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

  private static final ResourceLocation[] CREATIVE_TYPES = new ResourceLocation[] { new ResourceLocation("minecraft", "chicken"),
      new ResourceLocation("minecraft", "llama"), new ResourceLocation("minecraft", "vex"), new ResourceLocation("minecraft", "zombie"),
      new ResourceLocation("minecraft", "husk"), new ResourceLocation("minecraft", "skeleton"), new ResourceLocation("minecraft", "wither_skeleton"),
      new ResourceLocation("minecraft", "stray"), new ResourceLocation("minecraft", "spider"), new ResourceLocation("minecraft", "cave_spider"),
      new ResourceLocation("minecraft", "enderman"), new ResourceLocation("minecraft", "endermite"), new ResourceLocation("minecraft", "witch"),
      new ResourceLocation("minecraft", "shulker") };

  public static ItemBrokenSpawner create(@Nonnull IModObject modObject) {
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
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull CreativeTabs par2CreativeTabs, @Nonnull NonNullList<ItemStack> par3List) {
    for (ResourceLocation mobType : CREATIVE_TYPES) {
      par3List.add(CapturedMob.create(mobType).toStack(this, 0, 1));
    }
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
