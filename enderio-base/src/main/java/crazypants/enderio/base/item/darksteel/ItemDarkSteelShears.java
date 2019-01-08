package crazypants.enderio.base.item.darksteel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.OreDictionaryHelper;
import com.google.common.base.Predicate;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.direct.DirectUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.util.Prep;
import info.loenwind.autoconfig.factory.IValue;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemDarkSteelShears extends ItemShears implements IAdvancedTooltipProvider, IDarkSteelItem, IOverlayRenderAware {

  public static boolean isEquipped(EntityPlayer player) {
    return player != null && player.getHeldItemMainhand().getItem() == ModObject.itemDarkSteelAxe.getItem();
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, int requiredPower) {
    return getStoredPower(player) > requiredPower;
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, IValue<Integer> requiredPower) {
    return getStoredPower(player) > requiredPower.get();
  }

  public static int getStoredPower(EntityPlayer player) {
    return EnergyUpgradeManager.getEnergyStored(player.getHeldItemMainhand());
  }

  public static ItemDarkSteelShears create(@Nonnull IModObject modObject) {
    ItemDarkSteelShears res = new ItemDarkSteelShears(modObject);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private final MultiHarvestComparator harvestComparator = new MultiHarvestComparator();
  private final EntityComparator entityComparator = new EntityComparator();

  protected ItemDarkSteelShears(@Nonnull IModObject modObject) {
    this.setMaxDamage(this.getMaxDamage() * DarkSteelConfig.shearsDurabilityFactor.get());
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 2;
  }

  @Override
  public boolean isItemForRepair(@Nonnull ItemStack right) {
    return OreDictionaryHelper.hasName(right, Alloy.DARK_STEEL.getOreIngot());
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack is = new ItemStack(this);
      list.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(3).addToItem(is, this);
      EnergyUpgradeManager.setPowerFull(is, this);
      DirectUpgrade.INSTANCE.addToItem(is, this);
      list.add(is);
    }
  }

  @Override
  public boolean onBlockStartBreak(@Nonnull ItemStack itemstack, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    if (player.world.isRemote) {
      return false;
    }

    int powerStored = getStoredPower(player);
    if (powerStored < DarkSteelConfig.shearsPowerUsePerDamagePoint.get()) {
      return super_onBlockStartBreak(itemstack, pos, player);
    }

    List<BlockPos> res = new ArrayList<BlockPos>();

    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();

    for (int dx = -DarkSteelConfig.shearsBlockAreaBoostWhenPowered.get(); dx <= DarkSteelConfig.shearsBlockAreaBoostWhenPowered.get(); dx++) {
      for (int dy = -DarkSteelConfig.shearsBlockAreaBoostWhenPowered.get(); dy <= DarkSteelConfig.shearsBlockAreaBoostWhenPowered.get(); dy++) {
        for (int dz = -DarkSteelConfig.shearsBlockAreaBoostWhenPowered.get(); dz <= DarkSteelConfig.shearsBlockAreaBoostWhenPowered.get(); dz++) {
          Block block2 = player.world.getBlockState(new BlockPos(x + dx, y + dy, z + dz)).getBlock();
          if (block2 instanceof IShearable && ((IShearable) block2).isShearable(itemstack, player.world, new BlockPos(x + dx, y + dy, z + dz))) {
            res.add(new BlockPos(x + dx, y + dy, z + dz));
          }
        }
      }
    }

    NNList<BlockPos> sortedTargets = new NNList<BlockPos>(res);
    harvestComparator.refPoint = pos;
    Collections.sort(sortedTargets, harvestComparator);

    int maxBlocks = DarkSteelConfig.shearsPowerUsePerDamagePoint.get() == 0 ? sortedTargets.size()
        : Math.min(sortedTargets.size(), powerStored / DarkSteelConfig.shearsPowerUsePerDamagePoint.get());
    for (int i = 0; i < maxBlocks; i++) {
      BlockPos bc2 = sortedTargets.get(i);
      super_onBlockStartBreak(itemstack, bc2, player);
      if (bc2 != pos) {
        player.world.setBlockToAir(bc2);
      }
    }

    return false;
  }

  /**
   * This is a copy of net.minecraft.item.ItemShears.onBlockStartBreak(ItemStack, BlockPos, EntityPlayer) with added code to fire a BlockHarvestEvent.
   */
  @SuppressWarnings({ "cast", "null" })
  public boolean super_onBlockStartBreak(@Nonnull ItemStack itemstack, @Nonnull BlockPos pos, @Nonnull net.minecraft.entity.player.EntityPlayer player) {
    if (player.world.isRemote || player.capabilities.isCreativeMode) {
      return false;
    }
    IBlockState blockState = player.world.getBlockState(pos); // EIO ADD
    Block block = player.world.getBlockState(pos).getBlock();
    if (block instanceof net.minecraftforge.common.IShearable) {
      net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable) block;
      if (target.isShearable(itemstack, player.world, pos)) {
        java.util.List<ItemStack> drops = target.onSheared(itemstack, player.world, pos,
            net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FORTUNE, itemstack));
        java.util.Random rand = new java.util.Random();

        drops = new NNList<>(drops); // EIO ADD
        float chance = ForgeEventFactory.fireBlockHarvesting(drops, player.world, pos, blockState,
            EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, itemstack), 1,
            EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) != 0, player); // EIO ADD

        for (ItemStack stack : drops) {
          if (player.world.rand.nextFloat() <= chance) { // EIO ADD
            float f = 0.7F;
            double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            net.minecraft.entity.item.EntityItem entityitem = new net.minecraft.entity.item.EntityItem(player.world, (double) pos.getX() + d,
                (double) pos.getY() + d1, (double) pos.getZ() + d2, stack);
            entityitem.setDefaultPickupDelay();
            player.world.spawnEntity(entityitem);
          } // EIO ADD
        }

        itemstack.damageItem(1, player);
        player.addStat(net.minecraft.stats.StatList.getBlockStats(block));
        player.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        return true;
      }
    }
    return false;
  }

  private static final @Nonnull Predicate<Entity> selectShearable = new Predicate<Entity>() {

    @Override
    public boolean apply(@Nullable Entity entity) {
      return entity instanceof IShearable && !entity.isDead && ((IShearable) entity).isShearable(Prep.getEmpty(), entity.world, entity.getPosition());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      return super.equals(obj);
    }

    @Override
    public int hashCode() {
      return super.hashCode();
    }
  };

  @Override
  public boolean itemInteractionForEntity(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer player, @Nonnull EntityLivingBase entity,
      @Nonnull EnumHand hand) {
    if (entity.world.isRemote) {
      return false;
    }

    int powerStored = getStoredPower(player);
    if (powerStored < DarkSteelConfig.shearsPowerUsePerDamagePoint.get()) {
      return super.itemInteractionForEntity(itemstack, player, entity, hand);
    }

    if (entity instanceof IShearable) {
      AxisAlignedBB bb = new AxisAlignedBB(entity.posX - DarkSteelConfig.shearsEntityAreaBoostWhenPowered.get(),
          entity.posY - DarkSteelConfig.shearsEntityAreaBoostWhenPowered.get(), entity.posZ - DarkSteelConfig.shearsEntityAreaBoostWhenPowered.get(),
          entity.posX + DarkSteelConfig.shearsEntityAreaBoostWhenPowered.get(), entity.posY + DarkSteelConfig.shearsEntityAreaBoostWhenPowered.get(),
          entity.posZ + DarkSteelConfig.shearsEntityAreaBoostWhenPowered.get());

      List<Entity> sortedTargets = new ArrayList<Entity>(entity.world.getEntitiesWithinAABB(Entity.class, bb, selectShearable));
      entityComparator.refPoint = entity;
      Collections.sort(sortedTargets, entityComparator);

      boolean result = false;
      int maxSheep = DarkSteelConfig.shearsPowerUsePerDamagePoint.get() == 0 ? sortedTargets.size()
          : Math.min(sortedTargets.size(), powerStored / DarkSteelConfig.shearsPowerUsePerDamagePoint.get());
      for (int i = 0; i < maxSheep; i++) {
        Entity entity2 = sortedTargets.get(i);
        if (entity2 instanceof EntityLivingBase && super.itemInteractionForEntity(itemstack, player, (EntityLivingBase) entity2, hand)) {
          result = true;
        }
      }
      return result;
    }
    return false;
  }

  @SubscribeEvent
  public void onBreakSpeedEvent(PlayerEvent.BreakSpeed evt) {
    if (evt.getOriginalSpeed() > 2.0 && isEquippedAndPowered(evt.getEntityPlayer(), DarkSteelConfig.shearsPowerUsePerDamagePoint)) {
      evt.setNewSpeed(evt.getOriginalSpeed() * DarkSteelConfig.shearsEfficiencyBoostWhenPowered.get());
    }
  }

  @Override
  public void setDamage(@Nonnull ItemStack stack, int newDamage) {
    int oldDamage = getDamage(stack);
    if (newDamage <= oldDamage) {
      super.setDamage(stack, newDamage);
    }
    int damage = newDamage - oldDamage;

    EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
    if (eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(damage * DarkSteelConfig.shearsPowerUsePerDamagePoint.get(), false);
    } else {
      super.setDamage(stack, newDamage);
    }
    if (eu != null) {
      eu.writeToItem();
    }
  }

  @Override
  public boolean getIsRepairable(@Nonnull ItemStack i1, @Nonnull ItemStack i2) {
    return false;
  }

  @Override
  public int getItemEnchantability() {
    return EquipmentData.DARK_STEEL.getToolMaterial().getEnchantability();
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
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
    if (EnergyUpgradeManager.itemHasAnyPowerUpgrade(itemstack)) {
      list.add(Lang.SHEARS_MULTIHARVEST.get());
      list.add(Lang.SHEARS_POWERED.get(TextFormatting.WHITE, DarkSteelConfig.shearsEfficiencyBoostWhenPowered.get()));
    }
    DarkSteelRecipeManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  public ItemStack createItemStack() {
    return new ItemStack(this);
  }

  private static class MultiHarvestComparator implements Comparator<BlockPos> {

    BlockPos refPoint;

    @Override
    public int compare(BlockPos arg0, BlockPos arg1) {
      double d1 = refPoint.distanceSq(arg0.getX(), arg0.getY(), arg0.getZ());
      double d2 = refPoint.distanceSq(arg1.getX(), arg1.getY(), arg1.getZ());
      return compare(d1, d2);
    }

    // NB: Copy of Integer.compare, which is only in Java 1.7+
    public static int compare(double x, double y) {
      return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

  }

  private static class EntityComparator implements Comparator<Entity> {

    Entity refPoint;

    @Override
    public int compare(Entity paramT1, Entity paramT2) {
      double distanceSqToEntity1 = paramT1 == null ? 0 : refPoint.getDistanceSq(paramT1);
      double distanceSqToEntity2 = paramT2 == null ? 0 : refPoint.getDistanceSq(paramT2);
      if (distanceSqToEntity1 < distanceSqToEntity2) {
        return -1;
      }
      if (distanceSqToEntity1 > distanceSqToEntity2) {
        return 1;
      }
      // Double.compare() does something with bits now, but for distances it's clear:
      // if it's neither farther nor nearer is same.
      return 0;
    }

  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
    return slotChanged || oldStack.getItem() != newStack.getItem();
  }

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == EntityEquipmentSlot.MAINHAND;
  }

  @Override
  public boolean isBlockBreakingTool() {
    return true;
  }

  @Override
  public @Nonnull IEquipmentData getEquipmentData() {
    return EquipmentData.DARK_STEEL;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SHEARS_ENERGY_BUFFER;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SHEARS_ENERGY_INPUT;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SHEARS_ENERGY_USE;
  }

  @Override
  public @Nonnull ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SHEARS_ABSORPTION_RATIO;
  }

}
