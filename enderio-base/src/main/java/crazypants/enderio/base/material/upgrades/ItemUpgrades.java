package crazypants.enderio.base.material.upgrades;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.util.Strings;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.util.NbtValue;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ItemUpgrades extends Item implements IHaveRenderers, IAdvancedTooltipProvider {

  private static final @Nonnull String INVENTORY = "inventory";

  public static ItemUpgrades create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemUpgrades(modObject);
  }

  private ItemUpgrades(@Nonnull IModObject modObject) {
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(1);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
  }

  @Override
  public int getItemStackLimit(@Nonnull ItemStack stack) {
    return stack.getItemDamage() > 0 ? 1 : 64;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(final @Nonnull IModObject modObject) {
    ModelBakery.registerItemVariants(this, new ModelResourceLocation(modObject.getRegistryName(), INVENTORY));
    UpgradeRegistry.getUpgrades().apply(upgrade -> {
      final ResourceLocation registryName = upgrade.getRegistryName();
      if (registryName != null) {
        ModelBakery.registerItemVariants(this, getMRL(modObject.getRegistryName(), registryName));
      }
    });

    ModelLoader.setCustomMeshDefinition(this, this::getMRL);
  }

  @SideOnly(Side.CLIENT)
  protected @Nonnull ModelResourceLocation getMRL(@Nonnull ItemStack stack) {
    IDarkSteelUpgrade upgrade = getUpgrade(stack);
    if (upgrade != null) {
      final ResourceLocation registryName = upgrade.getRegistryName();
      if (registryName != null) {
        return getMRL(getRegistryName(), registryName);
      }
    }
    return new ModelResourceLocation(NullHelper.first(getRegistryName()), INVENTORY);
  }

  @SideOnly(Side.CLIENT)
  protected @Nonnull ModelResourceLocation getMRL(final ResourceLocation base, final @Nonnull ResourceLocation registryName) {
    return new ModelResourceLocation(new ResourceLocation(registryName.getResourceDomain(), base.getResourcePath()),
        "upgrade=" + registryName.getResourcePath());
  }

  public static IDarkSteelUpgrade getUpgrade(@Nonnull ItemStack stack) {
    if (stack.getItemDamage() == 1) { // TODO 1.14: just drop the meta
      // Note: This is just so vanilla's and other mods' item/recipe handling don't see a blank plate and a upgrade one as the same item
      String string = NbtValue.DSU.getString(stack);
      if (!Strings.isBlank(string)) {
        return UpgradeRegistry.getUpgrade(new ResourceLocation(string));
      }
    }
    return null;
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    IDarkSteelUpgrade upgrade = getUpgrade(stack);
    if (upgrade != null) {
      return upgrade.getUnlocalizedName();
    }
    return getUnlocalizedName();
  }

  @Override
  public @Nonnull String getItemStackDisplayName(@Nonnull ItemStack stack) {
    IDarkSteelUpgrade upgrade = getUpgrade(stack);
    if (upgrade != null) {
      return I18n.translateToLocalFormatted(getUnlocalizedName() + ".with", super.getItemStackDisplayName(stack)).trim();
    } else {
      return super.getItemStackDisplayName(stack);
    }
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull final NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      list.add(new ItemStack(this));
      UpgradeRegistry.getUpgrades().apply(upgrade -> {
        list.add(withUpgrade(upgrade));
        list.add(setEnabled(withUpgrade(upgrade), true));
      });
    }
  }

  public @Nonnull ItemStack withUpgrade(@Nonnull IDarkSteelUpgrade upgrade) {
    final ResourceLocation registryName = upgrade.getRegistryName();
    if (registryName != null) {
      return NbtValue.DSU.setString(new ItemStack(this, 1, 1), registryName.toString());
    }
    return new ItemStack(this);
  }

  public static boolean isEnabled(@Nonnull ItemStack stack) {
    return NbtValue.ENABLED.getBoolean(stack);
  }

  public static @Nonnull ItemStack setEnabled(@Nonnull ItemStack stack, boolean value) {
    return NbtValue.ENABLED.setBoolean(stack, value);
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return isEnabled(stack) || super.hasEffect(stack);
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addCommonTooltipFromResources(list, itemstack.getUnlocalizedName());
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    SpecialTooltipHandler.addBasicTooltipFromResources(list, itemstack.getUnlocalizedName());
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    IDarkSteelUpgrade upgrade = getUpgrade(itemstack);
    if (upgrade != null) {
      list.add(Lang.DSU_TOOLTIP_MAIN.get());
      NNList<String> sublist = new NNList<>();
      // This would be nice, but the code in there adds the upgrade name and has placeholder support that depends on a live itemstack
      // if (upgrade instanceof IAdvancedTooltipProvider)
      // ((IAdvancedTooltipProvider) upgrade).addDetailedEntries(itemstack, entityplayer, sublist, flag);
      SpecialTooltipHandler.addDetailedTooltipFromResources(sublist, upgrade.getUnlocalizedName());
      sublist.apply(line -> {
        if (!line.contains("$")) { // filter out placeholders
          list.add(Lang.DSU_TOOLTIP_LINE.get(line));
        }
      });

      List<IDarkSteelUpgrade> dependencies = upgrade.getRules().stream().filter(rule -> rule instanceof IRule.Prerequisite)
          .map(rule -> ((IRule.Prerequisite) rule).getPrerequisite()).collect(Collectors.toList());
      if (!dependencies.isEmpty()) {
        list.add(Lang.DSU_TOOLTIP_DEPS.get());
        dependencies.forEach(dependency -> list.add(Lang.DSU_TOOLTIP_LINE.get(dependency.getDisplayName())));
      }

      List<String> classes = upgrade.getRules().stream().filter(rule -> rule instanceof IRule.ItemType)
          .map(rule -> ((IRule.ItemType) rule).getTooltip().getFormattedText()).collect(Collectors.toList());
      list.add(Lang.DSU_TOOLTIP_CLAS.get());
      if (classes.isEmpty()) {
        list.add(Lang.DSU_TOOLTIP_LINE.get(Lang.DSU_CLASS_EVERYTHING.get()));
      } else {
        classes.forEach(itemclass -> list.add(Lang.DSU_TOOLTIP_LINE.get(itemclass)));
      }
      if (!isEnabled(itemstack)) {
        list.add(TextFormatting.LIGHT_PURPLE + Lang.DSU_TOOLTIP_LEVELS_1.get(upgrade.getLevelCost()));
        list.add(Lang.DSU_TOOLTIP_LEVELS_2.get());
      } else {
        list.add(TextFormatting.LIGHT_PURPLE + Lang.DSU_TOOLTIP_APPLY_1.get());
        list.add(Lang.DSU_TOOLTIP_APPLY_2.get());
      }
    } else {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, getUnlocalizedName());
    }
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand handIn) {
    final ItemStack stack = playerIn.getHeldItem(handIn);

    if (playerIn.isSneaking()) {
      if (!worldIn.isRemote) {
        ModObject.blockDarkSteelAnvil.openGui(worldIn, new BlockPos(0, -1, 0), playerIn, null, -1);
      }
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    if (!isEnabled(stack) && stack.getCount() == 1) {
      IDarkSteelUpgrade upgrade = getUpgrade(stack);
      if (upgrade != null) {
        int levelCost = upgrade.getLevelCost();
        int levels = playerIn.experienceLevel;
        if (levels >= levelCost || playerIn.capabilities.isCreativeMode) {
          if (!worldIn.isRemote) {
            if (!playerIn.capabilities.isCreativeMode) {
              // Note: This is much more expensive than using a tank recipe. It takes the number of levels off the top of the player's levels, whereas the tank
              // recipe calculates the levels from 0. This is on purpose. The easy way is expensive, the hard one is cheap.
              int drainXP = XpUtil.getExperienceForLevel(levels) - XpUtil.getExperienceForLevel(levels - levelCost);
              XpUtil.addPlayerXP(playerIn, -drainXP);
            }
            setEnabled(stack, true);
            playerIn.sendStatusMessage(Lang.DSU_GUI_ACTIVATED.toChatServer(), true);
          }
        } else if (!worldIn.isRemote) {
          playerIn.sendStatusMessage(Lang.DSU_GUI_NOT_ENOUGH_LEVELS.toChatServer(levelCost), true);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
      }
    }
    return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
  }

}
