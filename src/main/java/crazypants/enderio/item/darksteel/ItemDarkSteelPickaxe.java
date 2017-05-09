package crazypants.enderio.item.darksteel;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.OreDictionaryHelper;
import com.google.common.collect.Sets;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpoonUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TravelUpgrade;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.power.PowerDisplayUtil;
import crazypants.enderio.teleport.TravelController;
import crazypants.util.Prep;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDarkSteelPickaxe extends ItemPickaxe implements IAdvancedTooltipProvider, IDarkSteelItem, IItemOfTravel, IOverlayRenderAware {

  public static final @Nonnull String NAME = "darkSteel_pickaxe";

  public static boolean isEquipped(EntityPlayer player, EnumHand hand) {
    if (player == null) {
      return false;
    }
    ItemStack equipped = player.getHeldItem(hand);
    if (Prep.isInvalid(equipped)) {
      return false;
    }
    return equipped.getItem() == DarkSteelItems.itemDarkSteelPickaxe;
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, EnumHand hand, int requiredPower) {
    if (!isEquipped(player, hand)) {
      return false;
    }
    return EnergyUpgrade.getEnergyStored(player.getHeldItem(hand)) >= requiredPower;
  }

  public static ItemDarkSteelPickaxe create() {
    ItemDarkSteelPickaxe res = new ItemDarkSteelPickaxe();
    res.init();
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private long lastBlickTick = -1;

  public ItemDarkSteelPickaxe() {
    super(ItemDarkSteelSword.MATERIAL);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
  }

  @Override
  public String getItemName() {
    return NAME;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    @Nonnull
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    EnergyUpgrade.EMPOWERED_FOUR.writeToItem(is);
    EnergyUpgrade.setPowerFull(is);
    TravelUpgrade.INSTANCE.writeToItem(is);
    SpoonUpgrade.INSTANCE.writeToItem(is);
    par3List.add(is);
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 3;
  }
  
  @Override
  public boolean isItemForRepair(ItemStack right) {
    return OreDictionaryHelper.hasName(right, Alloy.DARK_STEEL.getOreIngot());
  }

  @Override
  public boolean onBlockDestroyed(ItemStack item, World world, IBlockState bs, BlockPos pos, EntityLivingBase entityLiving) {
    if (bs.getBlockHardness(world, pos) != 0.0D) {
      if (useObsidianEffeciency(item, bs)) {
        extractInternal(item, Config.darkSteelPickPowerUseObsidian);
      }
    }
    return super.onBlockDestroyed(item, world, bs, pos, entityLiving);
  }

  @Override
  public EnumActionResult onItemUse(ItemStack item, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX,
      float hitY, float hitZ) {
    if(world.isRemote) {
      if (Config.darkSteelRightClickPlaceEnabled) {
        return doRightClickItemPlace(player, world, pos, side, hand, hitX, hitX, hitX);
      } else {
        if (doTravelAction(item, world, player, hand) != null) {
          return EnumActionResult.SUCCESS;
        }
      }
    }
    if (Math.random() < 0.001) {
      Entity cow = EntityList.createEntityByIDFromName("Pig", world); // TODO 1.11
      BlockPos p = pos.offset(side);
      cow.setLocationAndAngles(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, 0, 0);
      world.spawnEntityInWorld(cow);
    }
    return EnumActionResult.PASS;
  }

  @SideOnly(Side.CLIENT)
  static EnumActionResult doRightClickItemPlace(EntityPlayer player, World world, BlockPos pos, EnumFacing side, EnumHand hand, float par8, float par9, float par10) {
    
    if(!Config.darkSteelRightClickPlaceEnabled || hand != EnumHand.MAIN_HAND) {
      return EnumActionResult.PASS;
    }
    
    int current = player.inventory.currentItem;
    int slot = current == 0 && Config.slotZeroPlacesEight ? 8 : current + 1;
    if (slot < 9 && Prep.isValid(player.inventory.mainInventory[slot]) && !(player.inventory.mainInventory[slot].getItem() instanceof IDarkSteelItem)) {
      /*
       * this will not work with buckets unless we don't switch back to the current item (the pick); there's probably some client <-> server event thing going
       * on with buckets, so our item-switch within the same tick would be a problem.
       */
      player.inventory.currentItem = slot;
      Minecraft mc = Minecraft.getMinecraft();
       
      EnumActionResult result = mc.playerController.processRightClickBlock(mc.player, mc.theWorld, player.inventory.mainInventory[slot], pos, side, new Vec3d(par8, par9, par10), hand);
      player.inventory.currentItem = current;
      return result;
    }
    return EnumActionResult.PASS;
  }

  @Override
  public void setDamage(ItemStack stack, int newDamage) {
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

  private boolean absorbDamageWithEnergy(ItemStack stack, int amount) {
    EnergyUpgrade eu = EnergyUpgrade.loadFromItem(stack);
    if (eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(amount, false);
      eu.writeToItem(stack);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean canHarvestBlock(IBlockState block, ItemStack item) {
    if (hasSpoonUpgrade(item) && getEnergyStored(item) > 0) {
      return block.getBlock() == Blocks.SNOW_LAYER || block.getBlock() == Blocks.SNOW || super.canHarvestBlock(block, item);
    } else {
      return super.canHarvestBlock(block, item);
    }
  }

  private boolean hasSpoonUpgrade(ItemStack item) {
    return SpoonUpgrade.loadFromItem(item) != null;
  }

  @Override
  public float getStrVsBlock(ItemStack stack, IBlockState state) {
    if (state.getMaterial() == Material.GLASS) {
      return toolMaterial.getEfficiencyOnProperMaterial();
    }
    if (useObsidianEffeciency(stack, state)) {
      return toolMaterial.getEfficiencyOnProperMaterial() + Config.darkSteelPickEffeciencyBoostWhenPowered + Config.darkSteelPickEffeciencyObsidian;
    }
    if (isToolEffective(state, stack)) {
      if (Config.darkSteelPickPowerUsePerDamagePoint <= 0 ? EnergyUpgrade.itemHasAnyPowerUpgrade(stack) : EnergyUpgrade.getEnergyStored(stack) > 0) {
        return toolMaterial.getEfficiencyOnProperMaterial() + Config.darkSteelPickEffeciencyBoostWhenPowered;
      }
      return toolMaterial.getEfficiencyOnProperMaterial();
    }
    return super.getStrVsBlock(stack, state);
  }

  public static boolean isToolEffective(IBlockState state, ItemStack stack) {
    for (String type : stack.getItem().getToolClasses(stack)) {
      if (state.getBlock().isToolEffective(type, state))
        return true;
    }
    return false;
  }

  private boolean useObsidianEffeciency(ItemStack item, IBlockState blockState) {
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

  @Override
  public Set<String> getToolClasses(ItemStack stack) {
    Set<String> set = Sets.newHashSet("pickaxe");
    if (hasSpoonUpgrade(stack)) {
      set.add("shovel");
    }
    return set;
  }

  protected void init() {
    GameRegistry.register(this);
  }
  
  @Override
  public int getEnergyStored(ItemStack container) {
    return EnergyUpgrade.getEnergyStored(container);
  }
 
  @Override
  public boolean getIsRepairable(ItemStack i1, ItemStack i2) {
    return false;
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
      list.add(TextFormatting.WHITE + "+" + Config.darkSteelPickEffeciencyBoostWhenPowered + " "
          + EnderIO.lang.localize("item.darkSteel_pickaxe.tooltip.effPowered"));
      list.add(TextFormatting.WHITE + "+" + Config.darkSteelPickEffeciencyObsidian + " " + EnderIO.lang.localize("item.darkSteel_pickaxe.tooltip.effObs")
          + " ");
      list.add(TextFormatting.WHITE + "     " + "(" + EnderIO.lang.localize("item.darkSteel_pickaxe.tooltip.cost") + " "
          + PowerDisplayUtil.formatPower(Config.darkSteelPickPowerUseObsidian) + " " + PowerDisplayUtil.abrevation() + ")");
    }
    DarkSteelRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public boolean isActive(EntityPlayer ep, ItemStack equipped) {
    return isTravelUpgradeActive(ep, equipped, EnumHand.MAIN_HAND) || isTravelUpgradeActive(ep, equipped, EnumHand.OFF_HAND);
  }

  @Override
  public void extractInternal(ItemStack equipped, int power) {
    EnergyUpgrade.extractEnergy(equipped, power, false);
  }

  private boolean isTravelUpgradeActive(EntityPlayer ep, ItemStack equipped, EnumHand hand) {
    return isEquipped(ep, hand) && ep.isSneaking() && TravelUpgrade.loadFromItem(equipped) != null;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
    ActionResult<ItemStack> doTravelAction = doTravelAction(stack, world, player, hand);

    return doTravelAction != null ? doTravelAction : super.onItemRightClick(stack, world, player, hand);
  }

  protected ActionResult<ItemStack> doTravelAction(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
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
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return slotChanged || Prep.isInvalid(oldStack) || Prep.isInvalid(newStack) || oldStack.getItem() != newStack.getItem();
  }

}
