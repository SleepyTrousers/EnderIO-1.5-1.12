package crazypants.enderio.base.item.darksteel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.OreDictionaryHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.farmers.HarvestResult;
import crazypants.enderio.base.farming.farmers.TreeHarvestUtil;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDarkSteelAxe extends ItemAxe implements IAdvancedTooltipProvider, IDarkSteelItem, IOverlayRenderAware {

  public static boolean isEquipped(EntityPlayer player) {
    return player != null && player.getHeldItemMainhand().getItem() == ModObject.itemDarkSteelAxe.getItem();
  }

  public static boolean isPowered(EntityPlayer player, int requiredPower) {
    return getStoredPower(player) > requiredPower;
  }

  public static int getStoredPower(EntityPlayer player) {
    return EnergyUpgradeManager.getEnergyStored(player.getHeldItemMainhand());
  }

  public static @Nonnull ItemDarkSteelAxe create(@Nonnull IModObject modObject) {
    ItemDarkSteelAxe res = new ItemDarkSteelAxe(modObject);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private final MultiHarvestComparator harvestComparator = new MultiHarvestComparator();

  protected ItemDarkSteelAxe(@Nonnull IModObject modObject) {
    super(ItemDarkSteelSword.MATERIAL, 8, -3);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
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
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull Item item, @Nullable CreativeTabs par2CreativeTabs, @Nonnull NonNullList<ItemStack> par3List) {
    @Nonnull
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    EnergyUpgrade.EMPOWERED_FOUR.addToItem(is);
    EnergyUpgradeManager.setPowerFull(is);
    par3List.add(is);
  }

  @Override
  public boolean onBlockStartBreak(@Nonnull ItemStack itemstack, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    if (!player.world.isRemote && player.isSneaking()) {
      IBlockState bs = player.world.getBlockState(pos);
      Block block = bs.getBlock();
      if (FarmersRegistry.isLog(block)) {
        int powerStored = EnergyUpgradeManager.getEnergyStored(itemstack);

        TreeHarvestUtil harvester = new TreeHarvestUtil();
        HarvestResult res = new HarvestResult();
        harvester.harvest(player.world, pos, res);

        NNList<BlockPos> sortedTargets = new NNList<BlockPos>(res.getHarvestedBlocks());
        harvestComparator.refPoint = pos;
        Collections.sort(sortedTargets, harvestComparator);

        int maxBlocks = powerStored / Config.darkSteelAxePowerUsePerDamagePointMultiHarvest;
        int numUsedPower = 0;
        for (int i = 0; numUsedPower < maxBlocks && i < sortedTargets.size(); i++) {
          if (doMultiHarvest(player, player.world, sortedTargets.get(i), block)) {
            numUsedPower++;
          }
        }
        return numUsedPower != 0;
      }
    }
    return false;
  }

  private boolean doMultiHarvest(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc, @Nonnull Block refBlock) {

    IBlockState bs = world.getBlockState(bc);
    Block block = bs.getBlock();
    bs = bs.getActualState(world, bc);
    ItemStack held = player.getHeldItemMainhand();

    List<ItemStack> itemDrops = block.getDrops(world, bc, bs, 0);
    float chance = ForgeEventFactory.fireBlockHarvesting(itemDrops, world, bc, bs, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, held), 1,
        EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, held) != 0, player);

    world.setBlockToAir(bc);
    boolean usedPower = false;
    for (ItemStack stack : itemDrops) {
      if (world.rand.nextFloat() <= chance) {
        world.spawnEntity(new EntityItem(world, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, stack.copy()));
        if (block == refBlock) { // other wise leaves
          EnergyUpgradeManager.extractEnergy(player.getHeldItemMainhand(), Config.darkSteelAxePowerUsePerDamagePointMultiHarvest, false);
          usedPower = true;
        }
      }
    }
    return usedPower;
  }

  @SubscribeEvent
  public void onBreakSpeedEvent(PlayerEvent.BreakSpeed evt) {
    if (isEquipped(evt.getEntityPlayer())) {
      if (evt.getEntityPlayer().isSneaking() && isPowered(evt.getEntityPlayer(), Config.darkSteelAxePowerUsePerDamagePointMultiHarvest)
          && FarmersRegistry.isLog(evt.getState().getBlock())) {
        evt.setNewSpeed(evt.getOriginalSpeed() / Config.darkSteelAxeSpeedPenaltyMultiHarvest);
      } else if (evt.getState().getMaterial() == Material.LEAVES) {
        evt.setNewSpeed(6);
      }
    }
  }

  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (world.isRemote) {
      return ItemDarkSteelPickaxe.doRightClickItemPlace(player, world, pos, side, hand, hitX, hitY, hitZ);
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

      if (!absorbDamageWithEnergy(stack, damage * Config.darkSteelAxePowerUsePerDamagePoint)) {
        super.setDamage(stack, newDamage);
      }
    }
  }

  private boolean absorbDamageWithEnergy(@Nonnull ItemStack stack, int amount) {
    EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
    if (eu != null && eu.getUpgrade().isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(amount, false);
      eu.writeToItem(stack);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public float getStrVsBlock(@Nonnull ItemStack stack, @Nonnull IBlockState state) {
    if (ItemDarkSteelPickaxe.isToolEffective(state, stack)) {
      if (Config.darkSteelAxePowerUsePerDamagePoint <= 0 ? EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)
          : EnergyUpgradeManager.getEnergyStored(stack) > 0) {
        return toolMaterial.getEfficiencyOnProperMaterial() + Config.darkSteelAxeEffeciencyBoostWhenPowered;
      }
      return toolMaterial.getEfficiencyOnProperMaterial();
    }
    return super.getStrVsBlock(stack, state);
  }

  @Override
  public boolean getIsRepairable(@Nonnull ItemStack i1, @Nonnull ItemStack i2) {
    return false;
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
    if (!Config.addDurabilityTootip) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgradeManager.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    if (EnergyUpgradeManager.itemHasAnyPowerUpgrade(itemstack)) {
      list.add(Lang.AXE_MULTIHARVEST.get());
      list.add(Lang.AXE_POWERED.get(TextFormatting.WHITE, Config.darkSteelAxeEffeciencyBoostWhenPowered));
    }
    DarkSteelRecipeManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
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

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
    return slotChanged || Prep.isInvalid(oldStack) || Prep.isInvalid(newStack) || oldStack.getItem() != newStack.getItem();
  }

}
