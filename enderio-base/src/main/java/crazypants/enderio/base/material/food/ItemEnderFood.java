package crazypants.enderio.base.material.food;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.teleport.RandomTeleportUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEnderFood extends ItemFood implements IResourceTooltipProvider, IHaveRenderers {

  public static ItemEnderFood create(@Nonnull IModObject modObject) {
    return new ItemEnderFood(modObject);
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
    return getUnlocalizedName() + "." + EnderFood.get(itemStack).getUnlocalisedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(final @Nonnull IModObject modObject) {
    NNList.of(EnderFood.class).apply(new Callback<EnderFood>() {
      @Override
      public void apply(@Nonnull EnderFood alloy) {
        ModelLoader.setCustomModelResourceLocation(ItemEnderFood.this, alloy.ordinal(),
            new ModelResourceLocation(modObject.getRegistryName(), "variant=" + alloy.getUnlocalisedName()));
      }
    });
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

  @Override
  protected void onFoodEaten(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull EntityPlayer player) {
    super.onFoodEaten(stack, worldIn, player);
    if (!worldIn.isRemote && EnderFood.get(stack).doesTeleport && worldIn.rand.nextFloat() < Config.teleportEffectProbability) {
      RandomTeleportUtil.teleportEntity(worldIn, player, true);
    }
  }

}
