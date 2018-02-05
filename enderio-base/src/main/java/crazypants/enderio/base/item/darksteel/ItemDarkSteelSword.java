package crazypants.enderio.base.item.darksteel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.OreDictionaryHelper;
import com.enderio.core.common.util.Util;
import com.google.common.collect.Multimap;

import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.integration.tic.TicUtil;
import crazypants.enderio.base.item.darksteel.attributes.DarkSteelAttributeModifiers;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.item.darksteel.upgrade.travel.TravelUpgrade;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.base.teleport.TravelController;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.init.ModObject.blockEndermanSkull;

public class ItemDarkSteelSword extends ItemSword implements IAdvancedTooltipProvider, IDarkSteelItem, IItemOfTravel, IOverlayRenderAware {

  private static final @Nonnull String HIT_BY_DARK_STEEL_SWORD = "hitByDarkSteelSword";

  private static final @Nonnull ResourceLocation ENDERZOO_ENDERMINY = new ResourceLocation("enderzoo", "enderminy");

  static final @Nonnull ToolMaterial MATERIAL = NullHelper
      .notnull(EnumHelper.addToolMaterial("darkSteel", Config.darkSteelPickMinesTiCArdite ? 5 : 3, 2000, 8, 3.0001f, 25), "failed to add tool material");
  // 3.0001f = more desirable for mobs (i.e. they'll pick it up even if they already have diamond)

  public static boolean isEquipped(EntityPlayer player) {
    return player != null && player.getHeldItemMainhand().getItem() == ModObject.itemDarkSteelSword.getItem();
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, int requiredPower) {
    return getStoredPower(player) > requiredPower;
  }

  public static int getStoredPower(EntityPlayer player) {
    return EnergyUpgradeManager.getEnergyStored(player.getHeldItemMainhand());
  }

  public static ItemDarkSteelSword create(@Nonnull IModObject modObject) {
    ItemDarkSteelSword res = new ItemDarkSteelSword(modObject);
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private final int powerPerDamagePoint = Config.darkSteelPowerStorageBase / MATERIAL.getMaxUses();
  private long lastBlickTick = -1;

  public ItemDarkSteelSword(@Nonnull IModObject modObject) {
    super(MATERIAL);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nullable CreativeTabs par2CreativeTabs, @Nonnull NonNullList<ItemStack> par3List) {
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    EnergyUpgrade.EMPOWERED_FOUR.addToItem(is);
    EnergyUpgradeManager.setPowerFull(is);
    TravelUpgrade.INSTANCE.addToItem(is);
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

  @SubscribeEvent
  public void onEnderTeleport(EnderTeleportEvent evt) {
    if (evt.getEntityLiving().getEntityData().getBoolean(HIT_BY_DARK_STEEL_SWORD)) {
      evt.setCanceled(true);
    }
  }

  // Set priority to lowest in the hope any other mod adding head drops will have already added them
  // by the time this is called to prevent multiple head drops
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onEntityDrop(LivingDropsEvent evt) {

    final Entity entity = evt.getSource().getTrueSource();
    final EntityLivingBase entityLiving = evt.getEntityLiving();
    if (!(entity instanceof EntityPlayer) || entityLiving == null) {
      return;
    }

    EntityPlayer player = (EntityPlayer) entity;

    // Handle TiC weapons with beheading differently
    if (handleBeheadingWeapons(player, evt)) {
      return;
    }

    double skullDropChance = getSkullDropChance(player, evt);
    if (player instanceof FakePlayer) {
      skullDropChance *= Config.fakePlayerSkullChance;
    }
    if (Math.random() <= skullDropChance) {
      dropSkull(evt, player);
    }

    // Special handling for ender pearl drops
    if (isEquipped(player)) {
      ResourceLocation name = EntityList.getKey(entityLiving);
      if (entityLiving instanceof EntityEnderman || ENDERZOO_ENDERMINY.equals(name)) {
        int numPearls = 0;
        double chance = Config.darkSteelSwordEnderPearlDropChance;
        while (chance >= 1) {
          numPearls++;
          chance--;
        }
        if (chance > 0 && Math.random() <= chance) {
          numPearls++;
        }
        for (int i = 0; i < evt.getLootingLevel(); i++) {
          chance = Config.darkSteelSwordEnderPearlDropChancePerLooting;
          while (chance >= 1) {
            numPearls++;
            chance--;
          }
          if (chance > 0 && Math.random() <= chance) {
            numPearls++;
          }
        }

        int existing = 0;
        for (EntityItem stack : evt.getDrops()) {
          if (stack.getItem().getItem() == Items.ENDER_PEARL) {
            existing += stack.getItem().getCount();
          }
        }
        int toDrop = numPearls - existing;
        if (toDrop > 0) {
          evt.getDrops()
              .add(Util.createDrop(player.world, new ItemStack(Items.ENDER_PEARL, toDrop, 0), entityLiving.posX, entityLiving.posY, entityLiving.posZ, false));
        }

      }
    }

  }

  protected void dropSkull(LivingDropsEvent evt, EntityPlayer player) {
    ItemStack skull = getSkullForEntity(evt.getEntityLiving());
    if (skull != null && !containsDrop(evt, skull)) {
      evt.getDrops().add(Util.createEntityItem(player.world, skull, evt.getEntityLiving().posX, evt.getEntityLiving().posY, evt.getEntityLiving().posZ));
    }
  }

  private boolean handleBeheadingWeapons(EntityPlayer player, LivingDropsEvent evt) {
    if (player == null) {
      return false;
    }
    ItemStack equipped = player.getHeldItemMainhand();
    NBTTagCompound tagCompound = equipped.getTagCompound();
    if (tagCompound == null) {
      return false;
    }

    int beheading = TicUtil.getModifier(tagCompound, TicUtil.BEHEADING);
    int cleaver = TicUtil.getModifier(tagCompound, TicUtil.CLEAVER);

    if (beheading == 0 && cleaver == 0) {
      // Use default behavior if it is not a cleaver and doesn't have beheading
      return false;
    }

    if (!(evt.getEntityLiving() instanceof EntityEnderman)) {
      // If its not an enderman just let TiC do its thing
      // We wont modify head drops at all
      return true;
    }

    float chance = Math.max(Config.vanillaSwordSkullChance, cleaver * Config.ticCleaverSkullDropChance) + (Config.ticBeheadingSkullModifier * beheading);
    if (player instanceof FakePlayer) {
      chance *= Config.fakePlayerSkullChance;
    }
    while (chance >= 1) {
      dropSkull(evt, player);
      chance--;
    }
    if (chance > 0 && Math.random() <= chance) {
      dropSkull(evt, player);
    }
    return true;
  }

  private double getSkullDropChance(@Nonnull EntityPlayer player, LivingDropsEvent evt) {
    if (isWitherSkeleton(evt)) {
      if (isEquippedAndPowered(player, Config.darkSteelSwordPowerUsePerHit)) {
        return Config.darkSteelSwordWitherSkullChance + (Config.darkSteelSwordWitherSkullLootingModifier * evt.getLootingLevel());
      } else {
        return 0.01;
      }
    }
    float fromWeapon;
    float fromLooting;
    if (isEquippedAndPowered(player, Config.darkSteelSwordPowerUsePerHit)) {
      fromWeapon = Config.darkSteelSwordSkullChance;
      fromLooting = Config.darkSteelSwordSkullLootingModifier * evt.getLootingLevel();
    } else {
      fromWeapon = Config.vanillaSwordSkullChance;
      fromLooting = Config.vanillaSwordSkullLootingModifier * evt.getLootingLevel();
    }
    return fromWeapon + fromLooting;
  }

  protected boolean isWitherSkeleton(LivingDropsEvent evt) {
    return evt.getEntityLiving() instanceof EntityWitherSkeleton;
  }

  private boolean containsDrop(LivingDropsEvent evt, @Nonnull ItemStack skull) {
    for (EntityItem ei : evt.getDrops()) {
      if (ei != null && ei.getItem().getItem() == skull.getItem() && ei.getItem().getItemDamage() == skull.getItemDamage()) {
        return true;
      }
    }
    return false;
  }

  private ItemStack getSkullForEntity(EntityLivingBase entityLiving) {
    // ItemSkull: {"skeleton", "wither", "zombie", "char", "creeper", "dragon"}
    if (entityLiving instanceof EntitySkeleton) {
      return new ItemStack(Items.SKULL, 1, 0);
    } else if (entityLiving instanceof EntityWitherSkeleton) {
      return new ItemStack(Items.SKULL, 1, 1);
    } else if (entityLiving.getClass() == EntityZombie.class) { // sic! not PigZombie, ZombieVillager or Husk
      return new ItemStack(Items.SKULL, 1, 2);
    } else if (entityLiving instanceof EntityCreeper) {
      return new ItemStack(Items.SKULL, 1, 4);
    } else if (entityLiving instanceof EntityEnderman) {
      return new ItemStack(blockEndermanSkull.getBlockNN());
    }

    return null;
  }

  @Override
  public @Nonnull Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot equipmentSlot, @Nonnull ItemStack stack) {
    Multimap<String, AttributeModifier> res = super.getItemAttributeModifiers(equipmentSlot);
    if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
      if (Config.darkSteelSwordPowerUsePerHit <= 0 || EnergyUpgradeManager.getEnergyStored(stack) >= Config.darkSteelSwordPowerUsePerHit) {
        EnergyUpgrade energyUpgrade = EnergyUpgrade.loadAnyFromItem(stack);
        int level = energyUpgrade.getLevel();
        res.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), DarkSteelAttributeModifiers.getAttackDamage(level));
        res.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), DarkSteelAttributeModifiers.getAttackSpeed(level));
      }
    }
    return res;
  }

  @Override
  public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull EntityLivingBase entity, @Nonnull EntityLivingBase playerEntity) {

    if (playerEntity instanceof EntityPlayer) {

      EntityPlayer player = (EntityPlayer) playerEntity;
      ItemStack sword = player.getHeldItemMainhand();

      // Durability damage
      EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
      if (eu != null && eu.getUpgrade().isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
        eu.extractEnergy(powerPerDamagePoint, false);

      } else {
        super.hitEntity(stack, entity, playerEntity);
      }

      // sword hit
      if (eu != null) {
        eu.writeToItem(sword);

        if (eu.getEnergy() > Config.darkSteelSwordPowerUsePerHit) {
          extractInternal(player.getHeldItemMainhand(), Config.darkSteelSwordPowerUsePerHit);
          entity.getEntityData().setBoolean(HIT_BY_DARK_STEEL_SWORD, true);
        }

      }

    }
    return true;
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
    if (!Config.addDurabilityTootip) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgradeManager.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    DarkSteelRecipeManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  public ItemStack createItemStack() {
    return new ItemStack(this);
  }

  @Override
  public boolean isActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped) {
    return isTravelUpgradeActive(ep, equipped);
  }

  @Override
  public void extractInternal(@Nonnull ItemStack equipped, int power) {
    EnergyUpgradeManager.extractEnergy(equipped, power, false);
  }

  private boolean isTravelUpgradeActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped) {
    return isEquipped(ep) && ep.isSneaking() && TravelUpgrade.INSTANCE.hasUpgrade(equipped);
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    if (hand == EnumHand.MAIN_HAND) {
      ItemStack stack = player.getHeldItem(hand);
      if (isTravelUpgradeActive(player, stack)) {
        if (world.isRemote) {
          if (TravelController.instance.activateTravelAccessable(stack, hand, world, player, TravelSource.STAFF)) {
            player.swingArm(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
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
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
      }
    }

    return super.onItemRightClick(world, player, hand);
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
    return slotChanged || oldStack.getItem() != newStack.getItem();
  }

}
