package crazypants.enderio.item.darksteel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.OreDictionaryHelper;
import com.google.common.base.Predicate;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.material.alloy.Alloy;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDarkSteelShears extends ItemShears implements IAdvancedTooltipProvider, IDarkSteelItem, IOverlayRenderAware {

  public static final String NAME = "darkSteel_shears";

  public static boolean isEquipped(EntityPlayer player) {
    if (player == null) {
      return false;
    }
    ItemStack equipped = player.getHeldItemMainhand();
    if (equipped == null) {
      return false;
    }
    return equipped.getItem() == ModObject.itemDarkSteelShears;
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, int requiredPower) {
    return getStoredPower(player) > requiredPower;
  }

  public static int getStoredPower(EntityPlayer player) {
    if (!isEquipped(player)) {
      return 0;
    }
    return EnergyUpgrade.getEnergyStored(player.getHeldItemMainhand());
  }

  public static ItemDarkSteelShears create() {
    ItemDarkSteelShears res = new ItemDarkSteelShears();
    MinecraftForge.EVENT_BUS.register(res);
    res.init();
    return res;
  }

  private final MultiHarvestComparator harvestComparator = new MultiHarvestComparator();
  private final EntityComparator entityComparator = new EntityComparator();

  protected ItemDarkSteelShears() {
    super();
    this.setMaxDamage(this.getMaxDamage() * Config.darkSteelShearsDurabilityFactor);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
  }

  @Override
  public String getItemName() {
    return NAME;
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 2;
  }
  
  @Override
  public boolean isItemForRepair(ItemStack right) {
    return OreDictionaryHelper.hasName(right, Alloy.DARK_STEEL.getOreIngot());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    EnergyUpgrade.EMPOWERED_FOUR.writeToItem(is);
    EnergyUpgrade.setPowerFull(is);
    par3List.add(is);
  }

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    if (player.world.isRemote) {
      return false;
    }

    int powerStored = getStoredPower(player);
    if (powerStored < Config.darkSteelShearsPowerUsePerDamagePoint) {
      return super.onBlockStartBreak(itemstack, pos, player);
    }

    List<BlockPos> res = new ArrayList<BlockPos>();

    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();

    for (int dx = -Config.darkSteelShearsBlockAreaBoostWhenPowered; dx <= Config.darkSteelShearsBlockAreaBoostWhenPowered; dx++) {
      for (int dy = -Config.darkSteelShearsBlockAreaBoostWhenPowered; dy <= Config.darkSteelShearsBlockAreaBoostWhenPowered; dy++) {
        for (int dz = -Config.darkSteelShearsBlockAreaBoostWhenPowered; dz <= Config.darkSteelShearsBlockAreaBoostWhenPowered; dz++) {
          Block block2 = player.world.getBlockState(new BlockPos(x + dx, y + dy, z + dz)).getBlock();
          if (block2 instanceof IShearable && ((IShearable) block2).isShearable(itemstack, player.world, new BlockPos(x + dx, y + dy, z + dz))) {
            res.add(new BlockPos(x + dx, y + dy, z + dz));
          }
        }
      }
    }

    List<BlockPos> sortedTargets = new ArrayList<BlockPos>(res);
    harvestComparator.refPoint = pos;
    Collections.sort(sortedTargets, harvestComparator);

    int maxBlocks = Math.min(sortedTargets.size(), powerStored / Config.darkSteelShearsPowerUsePerDamagePoint);
    for (int i = 0; i < maxBlocks; i++) {
      BlockPos bc2 = sortedTargets.get(i);
      super.onBlockStartBreak(itemstack, bc2, player);
      if (bc2 != pos) {
        player.world.setBlockToAir(bc2);
      }
    }

    return false;
  }

  Predicate<Entity> selectShearable = new Predicate<Entity>() {

    @Override
    public boolean apply(@Nullable Entity entity) {
      return entity instanceof IShearable && !entity.isDead && ((IShearable) entity).isShearable(null, entity.world, entity.getPosition());
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
  public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
    if (entity.world.isRemote) {
      return false;
    }

    int powerStored = getStoredPower(player);
    if (powerStored < Config.darkSteelShearsPowerUsePerDamagePoint) {
      return super.itemInteractionForEntity(itemstack, player, entity, hand);
    }

    if (entity instanceof IShearable) {
      AxisAlignedBB bb = new AxisAlignedBB(entity.posX - Config.darkSteelShearsEntityAreaBoostWhenPowered,
          entity.posY - Config.darkSteelShearsEntityAreaBoostWhenPowered, entity.posZ - Config.darkSteelShearsEntityAreaBoostWhenPowered,
          entity.posX + Config.darkSteelShearsEntityAreaBoostWhenPowered, entity.posY + Config.darkSteelShearsEntityAreaBoostWhenPowered,
          entity.posZ + Config.darkSteelShearsEntityAreaBoostWhenPowered);

      List<Entity> sortedTargets = new ArrayList<Entity>(entity.world.getEntitiesWithinAABB(Entity.class, bb, selectShearable));
      entityComparator.refPoint = entity;
      Collections.sort(sortedTargets, entityComparator);

      boolean result = false;
      int maxSheep = Math.min(sortedTargets.size(), powerStored / Config.darkSteelShearsPowerUsePerDamagePoint);
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
    if (evt.getOriginalSpeed() > 2.0 && isEquippedAndPowered(evt.getEntityPlayer(), Config.darkSteelShearsPowerUsePerDamagePoint)) {
      evt.setNewSpeed(evt.getOriginalSpeed() * Config.darkSteelShearsEffeciencyBoostWhenPowered);
    }
  }

  @Override
  public void setDamage(ItemStack stack, int newDamage) {
    int oldDamage = getDamage(stack);
    if (newDamage <= oldDamage) {
      super.setDamage(stack, newDamage);
    }
    int damage = newDamage - oldDamage;

    EnergyUpgrade eu = EnergyUpgrade.loadFromItem(stack);
    if (eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(damage * Config.darkSteelShearsPowerUsePerDamagePoint, false);
    } else {
      super.setDamage(stack, newDamage);
    }
    if (eu != null) {
      eu.writeToItem(stack);
    }
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public boolean getIsRepairable(ItemStack i1, ItemStack i2) {
    // return i2 != null && i2.getItem() == EnderIO.itemAlloy && i2.getItemDamage() == Alloy.DARK_STEEL.ordinal();
    return false;
  }

  @Override
  public int getItemEnchantability() {
    return ItemDarkSteelSword.MATERIAL.getEnchantability();
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    DarkSteelRecipeManager.instance.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    DarkSteelRecipeManager.instance.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    if (!Config.addDurabilityTootip) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgrade.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    if (EnergyUpgrade.itemHasAnyPowerUpgrade(itemstack)) {
      list.add(EnderIO.lang.localize("item.darkSteel_shears.tooltip.multiHarvest"));
      list.add(TextFormatting.WHITE + "+" + Config.darkSteelShearsEffeciencyBoostWhenPowered + " "
          + EnderIO.lang.localize("item.darkSteel_pickaxe.tooltip.effPowered"));
    }
    DarkSteelRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
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
      double distanceSqToEntity1 = refPoint.getDistanceSqToEntity(paramT1);
      double distanceSqToEntity2 = refPoint.getDistanceSqToEntity(paramT2);
      if (distanceSqToEntity1 < distanceSqToEntity2)
        return -1;
      if (distanceSqToEntity1 > distanceSqToEntity2)
        return 1;
      // Double.compare() does something with bits now, but for distances it's clear:
      // if it's neither farther nor nearer is same.
      return 0;
    }

  }

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return slotChanged || oldStack == null || newStack == null || oldStack.getItem() != newStack.getItem();
  }

}
