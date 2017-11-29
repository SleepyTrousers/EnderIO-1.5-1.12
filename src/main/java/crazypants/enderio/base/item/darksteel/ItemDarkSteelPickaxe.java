package crazypants.enderio.base.item.darksteel;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.OreDictionaryHelper;

import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.Lang;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.handler.darksteel.IDarkSteelItem;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.item.darksteel.upgrade.spoon.SpoonUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.travel.TravelUpgrade;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.power.PowerDisplayUtil;
import crazypants.enderio.base.render.util.PowerBarOverlayRenderHelper;
import crazypants.enderio.base.teleport.TravelController;
import crazypants.enderio.util.Prep;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDarkSteelPickaxe extends ItemPickaxe implements IAdvancedTooltipProvider, IDarkSteelItem, IItemOfTravel, IOverlayRenderAware {

  public static boolean isEquipped(EntityPlayer player, @Nonnull EnumHand hand) {
    return player != null && player.getHeldItem(hand).getItem() == ModObject.itemDarkSteelPickaxe.getItem();
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, EnumHand hand, int requiredPower) {
    return getStoredPower(player) > requiredPower;
  }

  public static int getStoredPower(EntityPlayer player) {
    return EnergyUpgradeManager.getEnergyStored(player.getHeldItemMainhand());
  }

  public static ItemDarkSteelPickaxe create(@Nonnull IModObject modObject) {
    ItemDarkSteelPickaxe res = new ItemDarkSteelPickaxe(modObject);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private long lastBlickTick = -1;

  public ItemDarkSteelPickaxe(@Nonnull IModObject modObject) {
    super(ItemDarkSteelSword.MATERIAL);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull Item item, @Nullable CreativeTabs par2CreativeTabs, @Nonnull NonNullList<ItemStack> par3List) {
    @Nonnull
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    EnergyUpgrade.EMPOWERED_FOUR.writeToItem(is);
    EnergyUpgradeManager.setPowerFull(is);
    TravelUpgrade.INSTANCE.writeToItem(is);
    SpoonUpgrade.INSTANCE.writeToItem(is);
    par3List.add(is);
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 3;
  }

  @Override
  public boolean isItemForRepair(@Nonnull ItemStack right) {
    return OreDictionaryHelper.hasName(right, Alloy.DARK_STEEL.getOreIngot());
  }

  @Override
  public boolean onBlockDestroyed(@Nonnull ItemStack item, @Nonnull World world, @Nonnull IBlockState bs, @Nonnull BlockPos pos,
      @Nonnull EntityLivingBase entityLiving) {
    if (bs.getBlockHardness(world, pos) != 0.0D) {
      if (useObsidianEffeciency(item, bs)) {
        extractInternal(item, Config.darkSteelPickPowerUseObsidian);
      }
    }
    return super.onBlockDestroyed(item, world, bs, pos, entityLiving);
  }

  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (world.isRemote) {
      if (Config.darkSteelRightClickPlaceEnabled) {
        return doRightClickItemPlace(player, world, pos, side, hand, hitX, hitX, hitX);
      } else {
        if (doTravelAction(player.getHeldItem(hand), world, player, hand) != null) {
          return EnumActionResult.SUCCESS;
        }
      }
    }
    return EnumActionResult.PASS;
  }

  @SideOnly(Side.CLIENT)
  static @Nonnull EnumActionResult doRightClickItemPlace(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side,
      @Nonnull EnumHand hand, float par8, float par9, float par10) {

    if (!Config.darkSteelRightClickPlaceEnabled || hand != EnumHand.MAIN_HAND) {
      return EnumActionResult.PASS;
    }

    int current = player.inventory.currentItem;
    int slot = current == 0 && Config.slotZeroPlacesEight ? 8 : current + 1;
    if (slot < InventoryPlayer.getHotbarSize() && !(player.inventory.mainInventory.get(slot).getItem() instanceof IDarkSteelItem)) {
      /*
       * this will not work with buckets unless we don't switch back to the current item (the pick); there's probably some client <-> server event thing going
       * on with buckets, so our item-switch within the same tick would be a problem.
       */
      player.inventory.currentItem = slot;
      Minecraft mc = Minecraft.getMinecraft();

      EnumActionResult result = mc.playerController.processRightClickBlock(mc.player, mc.world, pos, side, new Vec3d(par8, par9, par10), hand);
      player.inventory.currentItem = current;
      return result;
    }
    return EnumActionResult.PASS;
  }

  @Override
  public void setDamage(@Nonnull ItemStack stack, int newDamage) {
    int oldDamage = getDamage(stack);
    if (newDamage <= oldDamage) {
      super.setDamage(stack, newDamage);
    } else {
      int damage = newDamage - oldDamage;

      if (!absorbDamageWithEnergy(stack, damage * Config.darkSteelPickPowerUsePerDamagePoint)) {
        super.setDamage(stack, newDamage);
      }
    }
  }

  private boolean absorbDamageWithEnergy(@Nonnull ItemStack stack, int amount) {
    EnergyUpgrade eu = EnergyUpgradeManager.loadFromItem(stack);
    if (eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(amount, false);
      eu.writeToItem(stack);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean canHarvestBlock(@Nonnull IBlockState block, @Nonnull ItemStack item) {
    if (hasSpoonUpgrade(item) && getEnergyStored(item) > 0) {
      return block.getBlock() == Blocks.SNOW_LAYER || block.getBlock() == Blocks.SNOW || super.canHarvestBlock(block, item);
    } else {
      return super.canHarvestBlock(block, item);
    }
  }

  private boolean hasSpoonUpgrade(@Nonnull ItemStack item) {
    return SpoonUpgrade.loadFromItem(item) != null;
  }

  @Override
  public float getStrVsBlock(@Nonnull ItemStack stack, @Nonnull IBlockState state) {
    if (state.getMaterial() == Material.GLASS) {
      return toolMaterial.getEfficiencyOnProperMaterial();
    }
    if (useObsidianEffeciency(stack, state)) {
      return toolMaterial.getEfficiencyOnProperMaterial() + Config.darkSteelPickEffeciencyBoostWhenPowered + Config.darkSteelPickEffeciencyObsidian;
    }
    if (isToolEffective(state, stack)) {
      if (Config.darkSteelPickPowerUsePerDamagePoint <= 0 ? EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)
          : EnergyUpgradeManager.getEnergyStored(stack) > 0) {
        return toolMaterial.getEfficiencyOnProperMaterial() + Config.darkSteelPickEffeciencyBoostWhenPowered;
      }
      return toolMaterial.getEfficiencyOnProperMaterial();
    }
    return super.getStrVsBlock(stack, state);
  }

  public static boolean isToolEffective(@Nonnull IBlockState state, @Nonnull ItemStack stack) {
    for (String type : stack.getItem().getToolClasses(stack)) {
      if (state.getBlock().isToolEffective(NullHelper.notnull(type, "getToolClasses() derped"), state))
        return true;
    }
    return false;
  }

  @SuppressWarnings("null")
  private boolean useObsidianEffeciency(@Nonnull ItemStack item, @Nonnull IBlockState blockState) {
    boolean useObsidianSpeed = false;
    int energy = getEnergyStored(item);
    if (energy > Config.darkSteelPickPowerUseObsidian) {
      useObsidianSpeed = blockState.getBlock() == Blocks.OBSIDIAN;
      if (!useObsidianSpeed && Config.darkSteelPickApplyObsidianEffeciencyAtHardess > 0) {
        try {
          useObsidianSpeed = blockState.getBlockHardness(null, new BlockPos(-1, -1, -1)) >= Config.darkSteelPickApplyObsidianEffeciencyAtHardess;
        } catch (Exception e) {
          // given we are passing in a null world to getBlockHardness it is
          // possible this could cause an NPE, so just ignore it
        }
      }
    }
    return useObsidianSpeed;
  }

  private static final @Nonnull Set<String> TOOL_CLASS_PICK = Collections.singleton("pickaxe");
  private static final @Nonnull Set<String> TOOL_CLASS_INTERNAL = new HashSet<>(TOOL_CLASS_PICK);
  static {
    TOOL_CLASS_INTERNAL.add("shovel");
  }
  private static final @Nonnull Set<String> TOOL_CLASS_SPOON = Collections.unmodifiableSet(TOOL_CLASS_INTERNAL);

  @Override
  public @Nonnull Set<String> getToolClasses(@Nonnull ItemStack stack) {
    return hasSpoonUpgrade(stack) ? TOOL_CLASS_SPOON : TOOL_CLASS_PICK;
  }

  @Override
  public int getEnergyStored(@Nonnull ItemStack container) {
    return EnergyUpgradeManager.getEnergyStored(container);
  }

  @Override
  public boolean getIsRepairable(@Nonnull ItemStack i1, @Nonnull ItemStack i2) {
    return false;
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.instance.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.instance.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    if (!Config.addDurabilityTootip) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgradeManager.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    if (EnergyUpgradeManager.itemHasAnyPowerUpgrade(itemstack)) {
      list.add(Lang.PICK_POWERED.get(TextFormatting.WHITE, Config.darkSteelPickEffeciencyBoostWhenPowered));
      list.add(Lang.PICK_OBSIDIAN.get(TextFormatting.WHITE, Config.darkSteelPickEffeciencyObsidian));
      list.add(
          Lang.PICK_OBSIDIAN_COST.get(TextFormatting.WHITE, PowerDisplayUtil.formatPower(Config.darkSteelPickPowerUseObsidian), PowerDisplayUtil.abrevation()));
    }
    DarkSteelRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public boolean isActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped) {
    return isTravelUpgradeActive(ep, equipped, EnumHand.MAIN_HAND) || isTravelUpgradeActive(ep, equipped, EnumHand.OFF_HAND);
  }

  @Override
  public void extractInternal(@Nonnull ItemStack equipped, int power) {
    EnergyUpgradeManager.extractEnergy(equipped, power, false);
  }

  private boolean isTravelUpgradeActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped, @Nonnull EnumHand hand) {
    return isEquipped(ep, hand) && ep.isSneaking() && TravelUpgrade.loadFromItem(equipped) != null;
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ActionResult<ItemStack> doTravelAction = doTravelAction(player.getHeldItem(hand), world, player, hand);

    return doTravelAction != null ? doTravelAction : super.onItemRightClick(world, player, hand);
  }

  protected ActionResult<ItemStack> doTravelAction(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    if (isTravelUpgradeActive(player, stack, hand)) {
      if (world.isRemote) {
        if (TravelController.instance.activateTravelAccessable(stack, hand, world, player, TravelSource.STAFF)) {
          player.swingArm(hand);
          return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
        }
      }

      long ticksSinceBlink = EnderIO.proxy.getTickCount() - lastBlickTick;
      if (ticksSinceBlink < 0) {
        lastBlickTick = -1;
      }
      if (Config.travelStaffBlinkEnabled && world.isRemote && ticksSinceBlink >= Config.travelStaffBlinkPauseTicks) {
        if (TravelController.instance.doBlink(stack, hand, player)) {
          player.swingArm(hand);
          lastBlickTick = EnderIO.proxy.getTickCount();
        }
      }
      return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }
    return null;
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
    return slotChanged || Prep.isInvalid(oldStack) || Prep.isInvalid(newStack) || oldStack.getItem() != newStack.getItem();
  }

}
