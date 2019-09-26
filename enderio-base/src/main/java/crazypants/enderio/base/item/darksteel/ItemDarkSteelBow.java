package crazypants.enderio.base.item.darksteel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.interfaces.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.OreDictionaryHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.DarkSteelTooltipManager;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.util.Prep;
import info.loenwind.autoconfig.factory.IValue;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDarkSteelBow extends ItemBow implements IDarkSteelItem, IAdvancedTooltipProvider, IOverlayRenderAware {

  private final @Nonnull NNList<IValue<Double>> damageBonus;
  private final @Nonnull NNList<IValue<Float>> fovMultipliers;
  private final @Nonnull NNList<IValue<Float>> forceMultipliers;
  private final @Nonnull NNList<IValue<Integer>> drawSpeeds;
  private final @Nonnull IEquipmentData data;

  public static ItemDarkSteelBow createEndSteel(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemDarkSteelBow res = new ItemDarkSteelBow(modObject, EquipmentData.END_STEEL, DarkSteelConfig.endBowDrawSpeed, DarkSteelConfig.endBowForceMultipliers,
        DarkSteelConfig.endBowFOVMultipliers, DarkSteelConfig.endBowDamageBonus);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  public static ItemDarkSteelBow createDarkSteel(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemDarkSteelBow res = new ItemDarkSteelBow(modObject, EquipmentData.DARK_STEEL, DarkSteelConfig.darkBowDrawSpeed, DarkSteelConfig.darkBowForceMultipliers,
        DarkSteelConfig.darkBowFOVMultipliers, DarkSteelConfig.darkBowDamageBonus);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  protected ItemDarkSteelBow(@Nonnull IModObject modObject, @Nonnull IEquipmentData data, @Nonnull NNList<IValue<Integer>> drawSpeeds,
      @Nonnull NNList<IValue<Float>> forceMultipliers, @Nonnull NNList<IValue<Float>> fovMultipliers, @Nonnull NNList<IValue<Double>> damageBonus) {
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setMaxDamage(300);
    setHasSubtypes(false);
    this.data = data;
    this.drawSpeeds = drawSpeeds;
    this.forceMultipliers = forceMultipliers;
    this.fovMultipliers = fovMultipliers;
    this.damageBonus = damageBonus;

    addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
      @Override
      @SideOnly(Side.CLIENT)
      public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
        return updatePullProperty(stack, worldIn, entityIn);
      }
    });
    addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
      @Override
      @SideOnly(Side.CLIENT)
      public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
        return updatePullingProperty(stack, entityIn);
      }
    });
  }

  private float updatePullProperty(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
    if (stack.getItem() != this || entityIn == null) {
      return 0;
    }
    float res = (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / (float) getDrawTime(stack);
    return res;
  }

  private float updatePullingProperty(@Nonnull ItemStack stack, @Nullable EntityLivingBase entityIn) {
    float res = entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
    return res;
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack is = new ItemStack(this);
      list.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(3).addToItem(is, this);
      if (EnergyUpgrade.UPGRADES.get(4).canAddToItem(is, this)) {
        EnergyUpgrade.UPGRADES.get(4).addToItem(is, this);
      }
      EnergyUpgradeManager.setPowerFull(is, this);
      list.add(is);
    }
  }

  @Override
  public void onPlayerStoppedUsing(@Nonnull ItemStack theBow, @Nonnull World worldIn, @Nonnull EntityLivingBase theShootingEntity, int timeLeft) {

    if (!(theShootingEntity instanceof EntityPlayer)) {
      return;
    }
    final EntityPlayer theShooter = (EntityPlayer) theShootingEntity;
    final boolean isCreativeMode = theShooter.capabilities.isCreativeMode;
    final ItemStack theArrows = getArrowsToShoot(theShooter, isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, theBow) > 0);
    if (Prep.isInvalid(theArrows)) {
      return;
    }
    final int drawTime = ForgeEventFactory.onArrowLoose(theBow, worldIn, (EntityPlayer) theShootingEntity, getMaxItemUseDuration(theBow) - timeLeft, true);
    if (drawTime < 0) {
      return;
    }
    final float drawRatio = getCustomArrowVelocity(theBow, drawTime);
    if (drawRatio < 0.1) {
      return;
    }
    final ItemArrow theArrow = (ItemArrow) (theArrows.getItem() instanceof ItemArrow ? theArrows.getItem() : Items.ARROW);
    final boolean arrowIsInfinite = isCreativeMode || theArrow.isInfinite(theArrows, theBow, theShooter);

    if (!worldIn.isRemote) {
      final EnergyUpgradeHolder upgrade = EnergyUpgradeManager.loadFromItem(theBow);

      final EntityArrow entityArrow = theArrow.createArrow(worldIn, theArrows, theShooter);
      // ATTENTION: Doesn't actually shoot the arrow!
      entityArrow.shoot(theShooter, theShooter.rotationPitch, theShooter.rotationYaw, 0.0F, drawRatio * 3.0F * getForceMultiplier(upgrade), 0.25F);

      if (drawRatio == 1.0F) {
        entityArrow.setIsCritical(true);
      }
      final int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, theBow);
      if (powerLevel > 0) {
        entityArrow.setDamage(entityArrow.getDamage() + powerLevel * 0.5D + 0.5D);
      }
      final int knockBack = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, theBow);
      if (knockBack > 0) {
        entityArrow.setKnockbackStrength(knockBack);
      }
      if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, theBow) > 0) {
        entityArrow.setFire(100);
      }

      theBow.damageItem(1, theShooter);

      if (arrowIsInfinite) {
        entityArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
      }

      entityArrow.setDamage(entityArrow.getDamage() + getDamageBonus(upgrade));

      worldIn.spawnEntity(entityArrow);

      final int usedEnergy = getRequiredPower(drawTime, upgrade);
      if (usedEnergy > 0) {
        upgrade.setEnergy(upgrade.getEnergy() - usedEnergy);
        upgrade.writeToItem();
      }

    }

    worldIn.playSound((EntityPlayer) null, theShooter.posX, theShooter.posY, theShooter.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F,
        1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + drawRatio * 0.5F);

    if (!arrowIsInfinite) {
      theArrows.shrink(1);
    }
    supressed(theShooter);

  }

  @SuppressWarnings("null")
  private void supressed(EntityPlayer entityplayer) {
    entityplayer.addStat(StatList.getObjectUseStats(this));
  }

  private int getRequiredPower(int drawDuration, EnergyUpgradeHolder upgrade) {
    if (upgrade == null || drawDuration <= 0) {
      return 0;
    }
    int drawTime = getDrawTime(upgrade);
    float ratio = Math.min(1, drawDuration / (float) drawTime);
    int powerRequired = (int) Math.ceil(DarkSteelConfig.bowPowerUsePerDraw.get() * ratio);
    if (drawDuration > drawTime) {
      powerRequired += (drawDuration - drawTime) * DarkSteelConfig.bowPowerUsePerHoldTick.get();
    }
    return powerRequired;
  }

  public int getRequiredPower(EntityLivingBase entity, EnergyUpgradeManager upgrade, @Nonnull ItemStack stack, int drawTime) {
    int powerRequired = 0;
    if (upgrade == null) {
      return powerRequired;
    }
    int drawDuration = getMaxItemUseDuration(stack) - entity.getItemInUseCount();
    // int drawTime = getDrawTime(entity, upgrade, stack);
    float ratio = Math.min(1, drawDuration / (float) drawTime);
    powerRequired = (int) Math.ceil(DarkSteelConfig.bowPowerUsePerDraw.get() * ratio);
    if (drawDuration > drawTime) {
      powerRequired += (drawDuration - drawTime) * DarkSteelConfig.bowPowerUsePerHoldTick.get();
    }
    return powerRequired;
  }

  public float getCustomArrowVelocity(@Nonnull ItemStack stack, int charge) {
    float f = charge / (float) getDrawTime(stack);
    f = (f * f + f * 2.0F) / 3.0F;
    if (f > 1.0F) {
      f = 1.0F;
    }

    return f;
  }

  private @Nonnull ItemStack getArrowsToShoot(@Nonnull EntityPlayer player, boolean hasInfiniteArrows) {
    ItemStack result = findAmmo(player);
    return Prep.isValid(result) ? result : hasInfiniteArrows ? new ItemStack(Items.ARROW) : Prep.getEmpty();
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onFovUpdateEvent(FOVUpdateEvent fovEvt) {
    ItemStack currentItem = fovEvt.getEntity().getHeldItemMainhand();
    if (currentItem.getItem() != this || fovEvt.getEntity().getItemInUseCount() <= 0) {
      return;
    }

    int drawDuration = getMaxItemUseDuration(currentItem) - fovEvt.getEntity().getItemInUseCount();
    float ratio = drawDuration / (float) getDrawTime(currentItem);

    if (ratio > 1.0F) {
      ratio = 1.0F;
    } else {
      ratio *= ratio;
    }

    float mult = getFOVMultiplier(EnergyUpgradeManager.loadFromItem(currentItem));
    fovEvt.setNewfov((1.0F - ratio * mult));
  }

  public int getDrawTime(@Nonnull ItemStack stack) {
    return getDrawTime(EnergyUpgradeManager.loadFromItem(stack));
  }

  public int getDrawTime(EnergyUpgradeHolder upgrade) {
    if (upgrade != null && upgrade.getEnergy() >= DarkSteelConfig.bowPowerUsePerDraw.get()) {
      return drawSpeeds.get(upgrade.getUpgrade().getLevel() + 1).get();
    }
    return drawSpeeds.get(0).get();
  }

  private float getForceMultiplier(EnergyUpgradeHolder upgrade) {
    if (upgrade != null && upgrade.getEnergy() >= DarkSteelConfig.bowPowerUsePerDraw.get()) {
      return forceMultipliers.get(upgrade.getUpgrade().getLevel() + 1).get();
    }
    return forceMultipliers.get(0).get();
  }

  private float getFOVMultiplier(EnergyUpgradeHolder upgrade) {
    if (upgrade != null && upgrade.getEnergy() >= DarkSteelConfig.bowPowerUsePerDraw.get()) {
      return fovMultipliers.get(upgrade.getUpgrade().getLevel() + 1).get();
    }
    return fovMultipliers.get(0).get();
  }

  private double getDamageBonus(EnergyUpgradeHolder upgrade) {
    if (upgrade != null && upgrade.getEnergy() >= DarkSteelConfig.bowPowerUsePerDraw.get()) {
      return damageBonus.get(upgrade.getUpgrade().getLevel() + 1).get();
    }
    return damageBonus.get(0).get();
  }

  @Override
  public void setDamage(@Nonnull ItemStack stack, int newDamage) {
    int oldDamage = getDamage(stack);
    if (newDamage <= oldDamage) {
      super.setDamage(stack, newDamage);
    } else {
      int damage = newDamage - oldDamage;
      if (!absorbDamageWithEnergy(stack, damage * DarkSteelConfig.bowPowerUsePerDamagePoint.get())) {
        super.setDamage(stack, newDamage);
      }
    }
  }

  private boolean absorbDamageWithEnergy(@Nonnull ItemStack stack, int amount) {
    EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
    if (eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(amount, false);
      eu.writeToItem();
      return true;
    }
    return false;
  }

  @Override
  public int getMaxItemUseDuration(@Nonnull ItemStack p_77626_1_) {
    return 72000;
  }

  @Override
  public @Nonnull EnumAction getItemUseAction(@Nonnull ItemStack p_77661_1_) {
    return EnumAction.BOW;
  }

  @Override
  public int getItemEnchantability() {
    return 1;
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 2;
  }

  @Override
  public boolean isItemForRepair(@Nonnull ItemStack right) {
    return OreDictionaryHelper.hasName(right, data.getBowRepairIngotOredict());
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  // This will break the animation
  // @Override
  // public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
  // return slotChanged || oldStack == null || newStack == null || oldStack.getItem() != newStack.getItem();
  // }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelTooltipManager.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelTooltipManager.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    if (!SpecialTooltipHandler.showDurability(flag)) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgradeManager.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    DarkSteelTooltipManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == EntityEquipmentSlot.MAINHAND;
  }

  @Override
  public @Nonnull IEquipmentData getEquipmentData() {
    return data;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_BOW_ENERGY_BUFFER;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_BOW_ENERGY_INPUT;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_BOW_ENERGY_USE;
  }

  @Override
  public @Nonnull ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_BOW_ABSORPTION_RATIO;
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand handIn) {
    if (playerIn.isSneaking()) {
      if (!worldIn.isRemote) {
        openUpgradeGui(playerIn, handIn);
      }
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
    return super.onItemRightClick(worldIn, playerIn, handIn);
  }

}
