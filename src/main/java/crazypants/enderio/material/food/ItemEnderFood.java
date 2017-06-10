package crazypants.enderio.material.food;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEnderFood extends ItemFood implements IResourceTooltipProvider, IHaveRenderers {

  public static ItemEnderFood create(@Nonnull IModObject modObject) {
    ItemEnderFood ret = new ItemEnderFood(modObject);
    GameRegistry.register(ret);
    return ret;
  }

  @SideOnly(Side.CLIENT)
  private SpecialFont fr;

  public ItemEnderFood(@Nonnull IModObject modObject) {
    super(0, false);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setMaxStackSize(1);
    setHasSubtypes(true);
    modObject.apply(this);
  }

  @Override
  public void getSubItems(@Nonnull Item item, @Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    for (EnderFood f : EnderFood.VALUES) {
      list.add(f.getStack());
    }
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName() + "_" + EnderFood.get(itemStack).unlocalisedName;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    NNList<ResourceLocation> names = EnderFood.resources();
    ModelBakery.registerItemVariants(this, names.toArray(new ResourceLocation[0]));
    for (EnderFood c : EnderFood.values()) {
      ClientUtil.regRenderer(this, c.ordinal(), c.unlocalisedName);
    }
  }

  @Override
  public int getHealAmount(@Nonnull ItemStack stack) {
    return EnderFood.get(stack).hunger;
  }

  @Override
  public float getSaturationModifier(@Nonnull ItemStack stack) {
    return EnderFood.get(stack).saturation;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public FontRenderer getFontRenderer(@Nonnull ItemStack stack) {
    if (fr == null) {
      fr = new SpecialFont(Minecraft.getMinecraft().fontRenderer);
    }
    return fr;
  }
}
