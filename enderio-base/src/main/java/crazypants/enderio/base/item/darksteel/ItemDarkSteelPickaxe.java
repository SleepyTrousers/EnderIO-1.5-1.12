package crazypants.enderio.base.item.darksteel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.OreDictionaryHelper;
import com.enderio.core.common.util.stackable.Things;
import com.google.common.collect.Multimap;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.handler.darksteel.PlayerAOEAttributeHandler;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.item.darksteel.upgrade.explosive.ExplosiveCarpetUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.explosive.ExplosiveDepthUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.explosive.ExplosiveUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.spoon.SpoonUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.travel.TravelUpgrade;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.network.PacketSpawnParticles;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.base.teleport.TravelController;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDarkSteelPickaxe extends ItemPickaxe implements IAdvancedTooltipProvider, IDarkSteelItem, IItemOfTravel, IOverlayRenderAware {

  public static int getStoredPower(EntityPlayer player) {
    return EnergyUpgradeManager.getEnergyStored(player.getHeldItemMainhand());
  }

  public static ItemDarkSteelPickaxe createEndSteel(@Nonnull IModObject modObject) {
    ItemDarkSteelPickaxe res = new ItemDarkSteelPickaxe(modObject, EquipmentData.END_STEEL);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  public static ItemDarkSteelPickaxe createDarkSteel(@Nonnull IModObject modObject) {
    ItemDarkSteelPickaxe res = new ItemDarkSteelPickaxe(modObject, EquipmentData.DARK_STEEL);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  public static ItemDarkSteelPickaxe createStellarAlloy(@Nonnull IModObject modObject) {
    ItemDarkSteelPickaxe res = new ItemDarkSteelPickaxe(modObject, EquipmentData.STELLAR_ALLOY);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private long lastBlickTick = -1;
  private final @Nonnull IEquipmentData data;

  public ItemDarkSteelPickaxe(@Nonnull IModObject modObject, @Nonnull IEquipmentData data) {
    super(data.getToolMaterial());
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    this.data = data;
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
      TravelUpgrade.INSTANCE.addToItem(is, this);
      SpoonUpgrade.INSTANCE.addToItem(is, this);
      ExplosiveUpgrade.INSTANCE.addToItem(is, this);
      list.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(3).addToItem(is, this);
      EnergyUpgradeManager.setPowerFull(is, this);
      TravelUpgrade.INSTANCE.addToItem(is, this);
      SpoonUpgrade.INSTANCE.addToItem(is, this);
      ExplosiveUpgrade.INSTANCE5.addToItem(is, this);
      ExplosiveDepthUpgrade.INSTANCE.addToItem(is, this);
      ExplosiveCarpetUpgrade.INSTANCE.addToItem(is, this);
      list.add(is);
    }
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 3;
  }

  @Override
  public boolean isItemForRepair(@Nonnull ItemStack right) {
    return OreDictionaryHelper.hasName(right, data.getRepairIngotOredict());
  }

  @Override
  public boolean onBlockDestroyed(@Nonnull ItemStack item, @Nonnull World world, @Nonnull IBlockState bs, @Nonnull BlockPos pos,
      @Nonnull EntityLivingBase entityLiving) {
    if (bs.getBlockHardness(world, pos) != 0.0D) {// TODO
      if (useObsidianEffeciency(item, bs)) {
        extractInternal(item, Config.darkSteelPickPowerUseObsidian);
      }
    }
    if (!entityLiving.isSneaking() && entityLiving instanceof EntityPlayerMP && PlayerAOEAttributeHandler.hasAOE((EntityPlayerMP) entityLiving)) {
      doExplosiveAction(item, world, pos, (EntityPlayerMP) entityLiving);
    }
    return super.onBlockDestroyed(item, world, bs, pos, entityLiving);
  }

  // exposed for tool methods. This should actually be a static method...
  @SuppressWarnings("null") // wrong nonnull annotation in super class
  @Override
  public @Nullable RayTraceResult rayTrace(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, boolean useLiquids) {
    return super.rayTrace(worldIn, playerIn, useLiquids);
  }

  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (world.isRemote) {
      if (DarkSteelConfig.rightClickPlaceEnabled_pick.get()) {
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

    if (hand != EnumHand.MAIN_HAND) {
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
  public boolean canHarvestBlock(@Nonnull IBlockState block, @Nonnull ItemStack item) {
    if (hasSpoonUpgrade(item) && getEnergyStored(item) > 0) {
      return block.getBlock() == Blocks.SNOW_LAYER || block.getBlock() == Blocks.SNOW || super.canHarvestBlock(block, item);
    } else {
      return super.canHarvestBlock(block, item);
    }
  }

  private boolean hasSpoonUpgrade(@Nonnull ItemStack item) {
    return SpoonUpgrade.INSTANCE.hasUpgrade(item);
  }

  @Override
  public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull IBlockState state) {
    if (state.getMaterial() == Material.GLASS) {
      return toolMaterial.getEfficiency();
    }
    if (useObsidianEffeciency(stack, state)) {
      return toolMaterial.getEfficiency() + Config.darkSteelPickEffeciencyBoostWhenPowered + Config.darkSteelPickEffeciencyObsidian;
    }
    if (isToolEffective(state, stack)) {
      if (Config.darkSteelPickPowerUsePerDamagePoint <= 0 ? EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)
          : EnergyUpgradeManager.getEnergyStored(stack) > 0) {
        return toolMaterial.getEfficiency() + Config.darkSteelPickEffeciencyBoostWhenPowered;
      }
      return toolMaterial.getEfficiency();
    }
    return super.getDestroySpeed(stack, state);
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
      list.add(Lang.PICK_POWERED.get(TextFormatting.WHITE, Config.darkSteelPickEffeciencyBoostWhenPowered));
      list.add(Lang.PICK_OBSIDIAN.get(TextFormatting.WHITE, Config.darkSteelPickEffeciencyObsidian));
      list.add(Lang.PICK_OBSIDIAN_COST.get(TextFormatting.WHITE, LangPower.RF(Config.darkSteelPickPowerUseObsidian)));
    }
    DarkSteelRecipeManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public boolean isActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped) {
    return isTravelUpgradeActive(ep, equipped, EnumHand.MAIN_HAND) || isTravelUpgradeActive(ep, equipped, EnumHand.OFF_HAND);
  }

  @Override
  public void extractInternal(@Nonnull ItemStack equipped, int power) {
    EnergyUpgradeManager.extractEnergy(equipped, this, power, false);
  }

  private boolean isTravelUpgradeActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped, @Nonnull EnumHand hand) {
    return ep.isSneaking() && TravelUpgrade.INSTANCE.hasUpgrade(equipped);
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

  private static final Things STONES = new Things().add(Blocks.STONE).add(Blocks.COBBLESTONE).add(Blocks.NETHERRACK).add(Blocks.SANDSTONE)
      .add(Blocks.BRICK_BLOCK).add(Blocks.BRICK_STAIRS).add(Blocks.COBBLESTONE_WALL).add(Blocks.END_BRICKS).add(Blocks.END_STONE).add(Blocks.MOSSY_COBBLESTONE)
      .add(Blocks.MONSTER_EGG).add(Blocks.HARDENED_CLAY).add(Blocks.NETHER_BRICK).add(Blocks.NETHER_BRICK_FENCE).add(Blocks.NETHER_BRICK_STAIRS)
      .add(Blocks.SANDSTONE_STAIRS).add(Blocks.STAINED_HARDENED_CLAY).add(Blocks.STONE_BRICK_STAIRS).add(Blocks.STONE_STAIRS).add(Blocks.STONEBRICK)
      .add(Blocks.STONE_SLAB).add(Blocks.STONE_SLAB2).add(Blocks.DOUBLE_STONE_SLAB).add(Blocks.DOUBLE_STONE_SLAB2);
  private static final Things DIRTS = new Things().add(Blocks.DIRT).add(Blocks.GRAVEL).add(Blocks.GRASS).add(Blocks.SOUL_SAND).add(Blocks.MYCELIUM)
      .add(Blocks.GRASS_PATH).add(Blocks.FARMLAND).add(Blocks.CLAY).add(Blocks.SAND);

  private static final float notBedrock(float i) {
    return i >= 0 ? i : Float.MAX_VALUE;
  }

  private void doExplosiveAction(@Nonnull ItemStack item, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayerMP player) {
    boolean hasDoneSomething = false;
    float referenceHardness = world.getBlockState(pos).getBlockHardness(world, pos);
    final boolean withSpoon = hasSpoonUpgrade(item);
    GameType gameType = player.interactionManager.getGameType();
    int cost = DarkSteelConfig.explosiveUpgradeEnergyPerBlock.get();
    for (Iterator<BlockPos> itr = PlayerAOEAttributeHandler.getAOE(pos, player); getEnergyStored(item) >= cost && itr.hasNext();) {
      final BlockPos target = itr.next();
      if (target != null) {
        final IBlockState blockstate = world.getBlockState(target);
        final Block block = blockstate.getBlock();
        if ((DarkSteelConfig.explosiveUpgradeUnlimitedTargets.get() || STONES.contains(block) || (withSpoon && DIRTS.contains(block)))
            && referenceHardness >= notBedrock(blockstate.getBlockHardness(world, target))
            && (isToolEffective(blockstate, item) || ForgeHooks.canHarvestBlock(block, player, world, target))) {
          final int exp = ForgeHooks.onBlockBreakEvent(world, gameType, player, target);
          if (exp != -1 && block.canHarvestBlock(world, target, player)) {
            if (block.removedByPlayer(blockstate, world, target, player, true)) {
              block.onBlockDestroyedByPlayer(world, target, blockstate);
              block.harvestBlock(world, player, target, blockstate, null, item);
              if (!gameType.isCreative() && exp > 0) {
                block.dropXpOnBlockBreak(world, target, exp);
              }
              extractInternal(item, cost);
              if (itemRand.nextFloat() < DarkSteelConfig.explosiveUpgradeDurabilityChance.get()) {
                // damage the item
                super.onBlockDestroyed(item, world, blockstate, target, player);
              }
              hasDoneSomething = true;
              if (itemRand.nextFloat() < .3f) {
                PacketSpawnParticles.create(world, target, 1, EnumParticleTypes.EXPLOSION_NORMAL, EnumParticleTypes.SMOKE_NORMAL);
              } else if (itemRand.nextFloat() < .5f) {
                PacketSpawnParticles.create(world, target, 1, EnumParticleTypes.SMOKE_NORMAL);
              }
            }
          }
        }
      }
    }
    if (hasDoneSomething) {
      PacketSpawnParticles.create(world, pos, 1, EnumParticleTypes.EXPLOSION_LARGE);
    }
  }

  @Override
  public boolean shouldCauseBlockBreakReset(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack) {
    // This is the offending method, there are a few things going on here to make sure we don't break anything.
    // The vanilla behavior is to reset the "block break" on item change and/or metadata change and/or NBT change
    // since we use NBT to store the energy value, when a wireless charger updates the NBT the default behavior
    // will reset progress.
    // So first thing first, if by vanilla standards it's ok to keep going, keep going
    if (!super.shouldCauseBlockBreakReset(oldStack, newStack)) {
      return false;
    }

    // Make sure the only difference is in NBT
    if (oldStack.isEmpty() != newStack.isEmpty() || oldStack.hasTagCompound() != newStack.hasTagCompound() || newStack.getItem() != oldStack.getItem()
        || newStack.isItemStackDamageable() && newStack.getMetadata() != oldStack.getMetadata()) {
      return true;
    }

    // Here comes the tricky part, in theory we could totally ignore NBT but that could cause problems
    // and honestly will be an ugly hack. Instead we'll use a deep comparer that ignores the energy
    // tag.
    if (!oldStack.hasTagCompound() && !newStack.hasTagCompound()) {
      return false;
    }

    return !EnergyUpgradeManager.compareNbt(oldStack.getTagCompound(), newStack.getTagCompound());
  }

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == EntityEquipmentSlot.MAINHAND;
  }

  @Override
  public boolean isPickaxe() {
    return true;
  }

  @Override
  public boolean hasUpgradeCallbacks(@Nonnull IDarkSteelUpgrade upgrade) {
    return upgrade == ExplosiveUpgrade.INSTANCE || upgrade == SpoonUpgrade.INSTANCE || upgrade == TravelUpgrade.INSTANCE;
  }

  @Override
  public @Nonnull IEquipmentData getEquipmentData() {
    return data;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_PICKAXE_ENERGY_BUFFER;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_PICKAXE_ENERGY_INPUT;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_PICKAXE_ENERGY_USE;
  }

  @Override
  public @Nonnull ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_PICKAXE_ABSORPTION_RATIO;
  }

  @Override
  public @Nonnull Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, @Nonnull ItemStack stack) {
    return addAttributeModifiers(slot, stack, super.getAttributeModifiers(slot, stack));
  }

}
