package crazypants.enderio.base.material.food;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.teleport.RandomTeleportUtil;
import crazypants.enderio.util.Prep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      for (EnderFood f : EnderFood.VALUES) {
        list.add(f.getStack());
      }
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
    return EnderFood.get(stack).getHunger();
  }

  @Override
  public float getSaturationModifier(@Nonnull ItemStack stack) {
    return EnderFood.get(stack).getSaturation();
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
    if (!worldIn.isRemote && EnderFood.get(stack).doesTeleport() && worldIn.rand.nextFloat() < Config.teleportEffectProbability) {
      RandomTeleportUtil.teleportEntity(worldIn, player, true, false, 16);
    }
  }

  @Override
  public boolean hasContainerItem(@Nonnull ItemStack stack) {
    return Prep.isValid(EnderFood.get(stack).getContainerItem());
  }

  @Override
  public @Nonnull ItemStack getContainerItem(@Nonnull ItemStack stack) {
    return EnderFood.get(stack).getContainerItem();
  }

  @Override
  public @Nonnull ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World worldIn, @Nonnull EntityLivingBase entityLiving) {
    ItemStack remaining = super.onItemUseFinish(stack, worldIn, entityLiving);
    final ItemStack containerItem = EnderFood.get(stack).getContainerItem();
    if (Prep.isInvalid(containerItem)) { // no container
      return remaining;
    }
    if (Prep.isInvalid(remaining)) { // not stackable
      return containerItem;
    }
    if (entityLiving instanceof EntityPlayer) { // stackable with container item...
      ((EntityPlayer) entityLiving).addItemStackToInventory(containerItem);
    } else {
      entityLiving.entityDropItem(containerItem, 0);
    }
    return remaining;
  }

}
