package crazypants.enderio.item.darksteel;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import cofh.api.energy.IEnergyContainerItem;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.util.ItemUtil;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.SpoonUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TravelUpgrade;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.teleport.TravelController;

public class ItemDarkSteelPickaxe extends ItemPickaxe implements IEnergyContainerItem, IAdvancedTooltipProvider, IDarkSteelItem, IItemOfTravel {

  public static boolean isEquipped(EntityPlayer player) {
    if(player == null) {
      return false;
    }
    ItemStack equipped = player.getCurrentEquippedItem();
    if(equipped == null) {
      return false;
    }
    return equipped.getItem() == DarkSteelItems.itemDarkSteelPickaxe;
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, int requiredPower) {
    if(!isEquipped(player)) {
      return false;
    }
    return EnergyUpgrade.getEnergyStored(player.getCurrentEquippedItem()) >= requiredPower;
  }

  public static ItemDarkSteelPickaxe create() {
    ItemDarkSteelPickaxe res = new ItemDarkSteelPickaxe();
    res.init();
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private long lastBlickTick = -1;
  protected String name;

  public ItemDarkSteelPickaxe(String name, ToolMaterial mat) {
    super(mat);
    this.name = name;
    setCreativeTab(EnderIOTab.tabEnderIO);
    String str = name+"_pickaxe";
    setUnlocalizedName(str);
    setTextureName(EnderIO.DOMAIN + ":" + str);
  }

  public ItemDarkSteelPickaxe(){
	  this("darkSteel",ItemDarkSteelSword.MATERIAL);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
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
  public boolean isDamaged(ItemStack stack) {
    return false;
  }

  @Override
  public boolean onBlockDestroyed(ItemStack item, World world, Block block, int x, int y, int z, EntityLivingBase entLiving) {
    if(block.getBlockHardness(world, x, y, z) != 0.0D) {
      if(useObsidianEffeciency(item, block)) {
        extractEnergy(item, Config.darkSteelPickPowerUseObsidian, false);
      }
    }
    return super.onBlockDestroyed(item, world, block, x, y, z, entLiving);
  }

    @Override
    public boolean onItemUse(ItemStack item, EntityPlayer player, World world,
	    int x, int y, int z, int side, float par8, float par9, float par10) {
	if (!isTravelUpgradeActive(player, item) && world.isRemote) {
	    return doRightClickItemPlace(player, world, x, y, z, side, par8,
		    par9, par10);
	}
	return false;
    }

    @SideOnly(Side.CLIENT)
    static boolean doRightClickItemPlace(EntityPlayer player, World world,
	    int x, int y, int z, int side, float par8, float par9, float par10) {
	int current = player.inventory.currentItem;
	int slot = current == 0 && Config.slotZeroPlacesEight ? 8 : current + 1;
	if (slot < 9
		&& player.inventory.mainInventory[slot] != null
		&& !(player.inventory.mainInventory[slot].getItem() instanceof IDarkSteelItem)) {
	    /*
	     * this will not work with buckets unless we don't switch back to
	     * the current item (the pick); there's probably some client <->
	     * server event thing going on with buckets, so our item-switch
	     * within the same tick would be a problem.
	     */
	    player.inventory.currentItem = slot;
	    Minecraft mc = Minecraft.getMinecraft();
	    boolean result = mc.playerController.onPlayerRightClick(
		    mc.thePlayer, mc.theWorld,
		    player.inventory.mainInventory[slot],
		    mc.objectMouseOver.blockX, mc.objectMouseOver.blockY,
		    mc.objectMouseOver.blockZ, mc.objectMouseOver.sideHit,
		    mc.objectMouseOver.hitVec);
	    player.inventory.currentItem = current;
	    return (result);
	}
	return false;
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
    if(eu != null && eu.isAbsorbDamageWithPower(stack) && eu.getEnergy() > 0) {
      eu.extractEnergy(amount, false);
      eu.writeToItem(stack);
      return true;
    } else {
      return false;
    }
  }


  @Override
  public boolean canHarvestBlock(Block block, ItemStack item) {
    if(hasSpoonUpgrade(item) && getEnergyStored(item) > 0) {
      return block == Blocks.snow_layer ? true : block == Blocks.snow || super.canHarvestBlock(block, item);
    } else {
      return super.canHarvestBlock(block, item);
    }
  }

  private boolean hasSpoonUpgrade(ItemStack item) {
    return SpoonUpgrade.loadFromItem(item) != null;
  }

  @Override
  public float getDigSpeed(ItemStack stack, Block block, int meta) {
    if(useObsidianEffeciency(stack, block)) {
      return ItemDarkSteelSword.MATERIAL.getEfficiencyOnProperMaterial() + Config.darkSteelPickEffeciencyBoostWhenPowered
          + Config.darkSteelPickEffeciencyObsidian;
    }
    if(ForgeHooks.isToolEffective(stack, block, meta)) {
      if(Config.darkSteelPickPowerUsePerDamagePoint <= 0 || getEnergyStored(stack) > 0) {
        return ItemDarkSteelSword.MATERIAL.getEfficiencyOnProperMaterial() + Config.darkSteelPickEffeciencyBoostWhenPowered;
      }
      return ItemDarkSteelSword.MATERIAL.getEfficiencyOnProperMaterial();
    }
    return super.getDigSpeed(stack, block, meta);
  }

  @Override
  public float func_150893_a(ItemStack item, Block block) {
    if(block.getMaterial() == Material.glass) {
      return efficiencyOnProperMaterial;
    }
    return super.func_150893_a(item, block);
  }

  private boolean useObsidianEffeciency(ItemStack item, Block block) {
    boolean useObsidianSpeed = false;
    int energy = getEnergyStored(item);
    if(energy > Config.darkSteelPickPowerUseObsidian) {
      useObsidianSpeed = block == Blocks.obsidian;
      if(!useObsidianSpeed && Config.darkSteelPickApplyObsidianEffeciencyAtHardess > 0) {
        try {
          useObsidianSpeed = (block != null && block.getBlockHardness(null, -1, -1, -1) >= Config.darkSteelPickApplyObsidianEffeciencyAtHardess);
        } catch (Exception e) {
          //given we are passing in a null world to getBlockHardness it is possible this could cause an NPE, so just ignore it
        }
      }
    }
    return useObsidianSpeed;
  }

  @Override
  public Set<String> getToolClasses(ItemStack stack) {
    Set<String> set = Sets.newHashSet("pickaxe");
    if(hasSpoonUpgrade(stack)) {
      set.add("shovel");
    }
    return set;
  }

  protected void init() {
    GameRegistry.registerItem(this, getUnlocalizedName());
  }

  @Override
  public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
    return EnergyUpgrade.receiveEnergy(container, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
    return EnergyUpgrade.extractEnergy(container, maxExtract, simulate);
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
    return false;
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    DarkSteelRecipeManager.instance.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    DarkSteelRecipeManager.instance.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    if(!Config.addDurabilityTootip) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgrade.getStoredEnergyString(itemstack);
    if(str != null) {
      list.add(str);
    }
    if(EnergyUpgrade.itemHasAnyPowerUpgrade(itemstack)) {
      list.add(EnumChatFormatting.WHITE + "+" + Config.darkSteelPickEffeciencyBoostWhenPowered + " "
          + EnderIO.lang.localize("item."+name+"_pickaxe.tooltip.effPowered"));
      list.add(EnumChatFormatting.WHITE + "+" + Config.darkSteelPickEffeciencyObsidian + " "
          + EnderIO.lang.localize("item."+name+"_pickaxe.tooltip.effObs") + " ");
      list.add(EnumChatFormatting.WHITE + "     " + "(" + EnderIO.lang.localize("item."+name+"_pickaxe.tooltip.cost") + " "
          + PowerDisplayUtil.formatPower(Config.darkSteelPickPowerUseObsidian) + " "
          + PowerDisplayUtil.abrevation() + ")");
    }
    DarkSteelRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  public ItemStack createItemStack() {
    return new ItemStack(this);
  }

  @Override
  public boolean isActive(EntityPlayer ep, ItemStack equipped) {
    return isTravelUpgradeActive(ep, equipped);
  }

  @Override
  public void extractInternal(ItemStack equipped, int power) {
    extractEnergy(equipped, power, false);
  }

  private boolean isTravelUpgradeActive(EntityPlayer ep, ItemStack equipped) {
    return isEquipped(ep) && ep.isSneaking() && TravelUpgrade.loadFromItem(equipped) != null;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    if(isTravelUpgradeActive(player, stack)) {
      if(world.isRemote) {
        if(TravelController.instance.activateTravelAccessable(stack, world, player, TravelSource.STAFF)) {
          player.swingItem();
          return stack;
        }
      }

      long ticksSinceBlink = EnderIO.proxy.getTickCount() - lastBlickTick;
      if(ticksSinceBlink < 0) {
        lastBlickTick = -1;
      }
      if(Config.travelStaffBlinkEnabled && world.isRemote && ticksSinceBlink >= Config.travelStaffBlinkPauseTicks) {
        if(TravelController.instance.doBlink(stack, player)) {
          player.swingItem();
          lastBlickTick = EnderIO.proxy.getTickCount();
        }
      }
      return stack;
    }

    return super.onItemRightClick(stack, world, player);
  }

}
