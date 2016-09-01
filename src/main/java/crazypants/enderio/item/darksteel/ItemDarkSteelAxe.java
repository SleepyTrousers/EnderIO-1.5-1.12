package crazypants.enderio.item.darksteel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.machine.farm.farmers.HarvestResult;
import crazypants.enderio.machine.farm.farmers.TreeHarvestUtil;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemDarkSteelAxe extends ItemAxe implements IEnergyContainerItem, IAdvancedTooltipProvider, IDarkSteelItem, IOverlayRenderAware {

  public static final String NAME = "darkSteel_axe";

  public static boolean isEquipped(EntityPlayer player) {
    if (player == null) {
      return false;
    }
    ItemStack equipped = player.getHeldItemMainhand();
    if (equipped == null) {
      return false;
    }
    return equipped.getItem() == DarkSteelItems.itemDarkSteelAxe;
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

  public static ItemDarkSteelAxe create() {
    ItemDarkSteelAxe res = new ItemDarkSteelAxe();
    MinecraftForge.EVENT_BUS.register(res);
    res.init();
    return res;
  }

  private int logOreId = -1;
  private final MultiHarvestComparator harvestComparator = new MultiHarvestComparator();

  protected ItemDarkSteelAxe() {
    //super(ItemDarkSteelSword.MATERIAL); //TODO: 1.9 bug in forge maybe?
    super(ToolMaterial.DIAMOND);
    toolMaterial = ItemDarkSteelSword.MATERIAL;
    damageVsEntity = 8;
    attackSpeed = -3;
    
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
  }

  @Override
  public String getItemName() {
    return NAME;
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 3;
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
    if (!player.worldObj.isRemote && player.isSneaking()) {
      IBlockState bs = player.worldObj.getBlockState(pos);
      Block block = bs.getBlock();
      if (isLog(bs)) {
        int powerStored = EnergyUpgrade.getEnergyStored(itemstack);

        TreeHarvestUtil harvester = new TreeHarvestUtil();
        HarvestResult res = new HarvestResult();
        harvester.harvest(player.worldObj, pos, res);

        List<BlockPos> sortedTargets = new ArrayList<BlockPos>(res.getHarvestedBlocks());
        harvestComparator.refPoint = pos;
        Collections.sort(sortedTargets, harvestComparator);

        int maxBlocks = powerStored / Config.darkSteelAxePowerUsePerDamagePointMultiHarvest;
        int numUsedPower = 0;
        for (int i = 0; numUsedPower < maxBlocks && i < sortedTargets.size(); i++) {
          if (doMultiHarvest(player, player.worldObj, sortedTargets.get(i), block)) {
            numUsedPower++;
          }
        }
        return numUsedPower != 0;
      }
    }
    return false;
  }

  private boolean doMultiHarvest(EntityPlayer player, World worldObj, BlockPos bc, Block refBlock) {

    IBlockState bs = worldObj.getBlockState(bc);
    Block block = bs.getBlock();
    bs = bs.getActualState(worldObj, bc);
    ItemStack held = player.getHeldItemMainhand();

    List<ItemStack> itemDrops = block.getDrops(worldObj, bc, bs, 0);
    float chance = ForgeEventFactory.fireBlockHarvesting(itemDrops, worldObj, bc, bs,
        EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, held), 1,
        EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, held) != 0, player);

    worldObj.setBlockToAir(bc);
    boolean usedPower = false;
    if (itemDrops != null) {
      for (ItemStack stack : itemDrops) {
        if (worldObj.rand.nextFloat() <= chance) {
          worldObj.spawnEntityInWorld(new EntityItem(worldObj, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, stack.copy()));
          if (block == refBlock) { // other wise leaves
            extractEnergy(player.getHeldItemMainhand(), Config.darkSteelAxePowerUsePerDamagePointMultiHarvest, false);
            usedPower = true;
          }
        }
      }
    }
    return usedPower;
  }

  @SubscribeEvent
  public void onBreakSpeedEvent(PlayerEvent.BreakSpeed evt) {
    if (evt.getEntityPlayer().isSneaking() && isEquippedAndPowered(evt.getEntityPlayer(), Config.darkSteelAxePowerUsePerDamagePointMultiHarvest) && isLog(evt.getState())) {
      evt.setNewSpeed(evt.getOriginalSpeed() / Config.darkSteelAxeSpeedPenaltyMultiHarvest);
    }
    if (isEquipped(evt.getEntityPlayer()) && evt.getState().getMaterial() == Material.LEAVES) {
      evt.setNewSpeed(6);
    }
  }

  @Override
  public EnumActionResult onItemUse(ItemStack item, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX,
      float hitY, float hitZ) {
    if (world.isRemote) {
      return ItemDarkSteelPickaxe.doRightClickItemPlace(player, world, pos, side, hand, hitX, hitY, hitZ);
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

      if (!absorbDamageWithEnergy(stack, damage * Config.darkSteelAxePowerUsePerDamagePoint)) {
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
  public float getStrVsBlock(ItemStack stack, IBlockState state) {
    if (ItemDarkSteelPickaxe.isToolEffective(state, stack)) {
      if (Config.darkSteelPickPowerUsePerDamagePoint <= 0 || getEnergyStored(stack) > 0) {
        return ItemDarkSteelSword.MATERIAL.getEfficiencyOnProperMaterial() + Config.darkSteelAxeEffeciencyBoostWhenPowered;
      }
      return ItemDarkSteelSword.MATERIAL.getEfficiencyOnProperMaterial();
    }
    return super.getStrVsBlock(stack, state);
  }

  private boolean isLog(IBlockState bs) {
    if (logOreId == -1) {
      logOreId = OreDictionary.getOreID("logWood");
    }
    int[] targetOreId = OreDictionary.getOreIDs(new ItemStack(bs.getBlock(), 1, bs.getBlock().getMetaFromState(bs)));
    for (int id : targetOreId) {
      if (logOreId == id) {
        return true;
      }
    }
    return false;
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
    return EnergyUpgrade.receiveEnergy(container, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public int getEnergyStored(ItemStack container) {
    return EnergyUpgrade.getEnergyStored(container);
  }

  @Override
  public int getMaxEnergyStored(ItemStack container) {
    return EnergyUpgrade.getMaxEnergyStored(container);
  }

  @Override
  public boolean getIsRepairable(ItemStack i1, ItemStack i2) {
    // return i2 != null && i2.getItem() == EnderIO.itemAlloy && i2.getItemDamage() == Alloy.DARK_STEEL.ordinal();
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
      list.add(EnderIO.lang.localize("item.darkSteel_axe.tooltip.multiHarvest"));
      list.add(TextFormatting.WHITE + "+" + Config.darkSteelAxeEffeciencyBoostWhenPowered + " "
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

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return slotChanged || oldStack == null || newStack == null || oldStack.getItem() != newStack.getItem();
  }

}
