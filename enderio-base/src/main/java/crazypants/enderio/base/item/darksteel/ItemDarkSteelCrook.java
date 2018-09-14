package crazypants.enderio.base.item.darksteel;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.OreDictionaryHelper;
import com.google.common.collect.ImmutableSet;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.farming.farmers.HarvestResult;
import crazypants.enderio.base.farming.harvesters.CrookHarvestingTarget;
import crazypants.enderio.base.farming.harvesters.IHarvestingTarget;
import crazypants.enderio.base.farming.harvesters.TreeHarvester;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.item.darksteel.upgrade.hoe.HoeUpgrade;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ItemDarkSteelCrook extends ItemTool implements IAdvancedTooltipProvider, IDarkSteelItem, IOverlayRenderAware {

  public static boolean isEquipped(EntityPlayer player) {
    return player != null && player.getHeldItemMainhand().getItem() instanceof ItemDarkSteelCrook;
  }

  public static boolean isPowered(EntityPlayer player, int requiredPower) {
    return getStoredPower(player) > requiredPower;
  }

  public static int getStoredPower(EntityPlayer player) {
    return EnergyUpgradeManager.getEnergyStored(player.getHeldItemMainhand());
  }

  public static @Nonnull ItemDarkSteelCrook createEndSteel(@Nonnull IModObject modObject) {
    ItemDarkSteelCrook res = new ItemDarkSteelCrook(modObject, EquipmentData.END_STEEL);
    return res;
  }

  public static @Nonnull ItemDarkSteelCrook createDarkSteel(@Nonnull IModObject modObject) {
    ItemDarkSteelCrook res = new ItemDarkSteelCrook(modObject, EquipmentData.DARK_STEEL);
    return res;
  }

  private final MultiHarvestComparator harvestComparator = new MultiHarvestComparator();
  private final @Nonnull IEquipmentData data;

  protected ItemDarkSteelCrook(@Nonnull IModObject modObject, @Nonnull IEquipmentData data) {
    super(data.getToolMaterial(), new HashSet<>());
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    this.data = data;
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 2;
  }

  @Override
  public boolean isItemForRepair(@Nonnull ItemStack right) {
    return OreDictionaryHelper.hasName(right, data.getRepairIngotOredict());
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      @Nonnull
      ItemStack is = new ItemStack(this);
      list.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(3).addToItem(is, this);
      EnergyUpgradeManager.setPowerFull(is, this);
      HoeUpgrade.INSTANCE.addToItem(is, this);
      list.add(is);
    }
  }

  @Override
  public boolean onBlockStartBreak(@Nonnull ItemStack itemstack, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    if (!player.world.isRemote && !player.isSneaking() && EnergyUpgradeManager.itemHasAnyPowerUpgrade(itemstack)) {
      IBlockState bs = player.world.getBlockState(pos);
      if (IHarvestingTarget.isDefaultLeaves(bs)) {
        int powerStored = EnergyUpgradeManager.getEnergyStored(itemstack);

        HarvestResult res = new HarvestResult();
        final IHarvestingTarget target = new CrookHarvestingTarget(pos);
        TreeHarvester.harvest(player.world, pos, res, target);

        NNList<BlockPos> sortedTargets = new NNList<BlockPos>(res.getHarvestedBlocks());
        harvestComparator.refPoint = pos;
        Collections.sort(sortedTargets, harvestComparator);

        int maxBlocks = powerStored / DarkSteelConfig.crookEnergyPerDamageMulti.get();
        int numUsedPower = 0;
        for (int i = 0; numUsedPower < maxBlocks && i < sortedTargets.size(); i++) {
          if (doMultiHarvest(player, player.world, sortedTargets.get(i))) {
            numUsedPower++;
          }
        }
        return numUsedPower != 0;
      }
    }
    return false;
  }

  private boolean doMultiHarvest(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
    IBlockState bs = world.getBlockState(bc);
    Block block = bs.getBlock();
    bs = bs.getActualState(world, bc);
    ItemStack held = player.getHeldItemMainhand();

    NNList<ItemStack> drops = new NNList<>();
    block.getDrops(drops, world, bc, bs, 0);
    float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, bc, bs, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, held), 1,
        EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, held) != 0, player);

    world.setBlockToAir(bc);
    boolean usedPower = false;
    for (ItemStack stack : drops) {
      if (world.rand.nextFloat() <= chance) {
        world.spawnEntity(new EntityItem(world, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, stack.copy()));
        EnergyUpgradeManager.extractEnergy(player.getHeldItemMainhand(), this, DarkSteelConfig.crookEnergyPerDamageMulti.get(), false);
        usedPower = true;
      }
    }
    return usedPower;
  }

  @SuppressWarnings("incomplete-switch")
  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);

    if (player.canPlayerEdit(pos, side, stack) && HoeUpgrade.INSTANCE.hasUpgrade(stack)) {
      int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(stack, player, world, pos);
      if (hook == 0) {

        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (side != EnumFacing.DOWN && world.isAirBlock(pos.up())) {
          if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
            this.setBlock(stack, player, world, pos, Blocks.FARMLAND.getDefaultState());
            return EnumActionResult.SUCCESS;
          }

          if (block == Blocks.DIRT) {
            switch (iblockstate.getValue(BlockDirt.VARIANT)) {
            case DIRT:
              this.setBlock(stack, player, world, pos, Blocks.FARMLAND.getDefaultState());
              return EnumActionResult.SUCCESS;
            case COARSE_DIRT:
              this.setBlock(stack, player, world, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
              return EnumActionResult.SUCCESS;
            }
          }
        }
      }
    }
    if (world.isRemote && DarkSteelConfig.rightClickPlaceEnabled_crook.get()) {
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

      if (!absorbDamageWithEnergy(stack, damage * DarkSteelConfig.crookEnergyPerDamage.get())) {
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
    } else {
      return false;
    }
  }

  @Override
  public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull IBlockState state) {
    return IHarvestingTarget.isDefaultLeaves(state) ? this.efficiency : 1.0F;
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
    if (!SpecialTooltipHandler.showDurability(flag)) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgradeManager.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    if (EnergyUpgradeManager.itemHasAnyPowerUpgrade(itemstack)) {
      list.add(Lang.CROOK_MULTIHARVEST.get());
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

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == EntityEquipmentSlot.MAINHAND;
  }

  protected void setBlock(ItemStack stack, @Nonnull EntityPlayer player, World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

    if (!worldIn.isRemote) {
      worldIn.setBlockState(pos, state, 11);
      stack.damageItem(1, player);
    }
  }

  @Override
  public boolean hasUpgradeCallbacks(@Nonnull IDarkSteelUpgrade upgrade) {
    return upgrade == HoeUpgrade.INSTANCE;
  }

  @Override
  public @Nonnull IEquipmentData getEquipmentData() {
    return data;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_CROOK_ENERGY_BUFFER;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_CROOK_ENERGY_INPUT;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_CROOK_ENERGY_USE;
  }

  @Override
  public @Nonnull ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_CROOK_ABSORPTION_RATIO;
  }

  @SubscribeEvent
  public static void onHarvest(@Nonnull BlockEvent.HarvestDropsEvent event) {
    final World world = event.getWorld();
    if (world == null || world.isRemote || event.getHarvester() == null || event.isSilkTouching() || !isEquipped(event.getHarvester())) {
      return;
    }
    final IBlockState state = event.getState();
    if (state == null || !IHarvestingTarget.isDefaultLeaves(state)) {
      return;
    }

    final boolean exnihilo = hasExNihilo();
    final boolean powered = isPowered(event.getHarvester(), 1);

    if (exnihilo && !powered) {
      return; // ex nihilo already adds extra drops
    }

    NNList<ItemStack> list = new NNList<>();
    final int start = exnihilo ? DarkSteelConfig.crookExtraDropsUnpowered.get() : 0;
    final int loops = Math.min(start, (powered ? DarkSteelConfig.crookExtraDropsPowered : DarkSteelConfig.crookExtraDropsUnpowered).get());
    for (int i = start; i < loops; i++) {
      state.getBlock().getDrops(list, world, event.getPos(), state, event.getFortuneLevel());
    }
    event.getDrops().addAll(list);
  }

  @Override
  public @Nonnull Set<String> getToolClasses(@Nonnull ItemStack stack) {
    return hasExNihilo() ? CROOK : super.getToolClasses(stack);
  }

  private static final @Nonnull Set<String> CROOK = ImmutableSet.of("crook");

  private static Boolean hasExNihilo = null;

  private static boolean hasExNihilo() {
    if (hasExNihilo != null) {
      return hasExNihilo;
    }
    return hasExNihilo = Loader.isModLoaded("exnihilocreatio");
  }

}
