package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.Util;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TravelUpgrade;
import crazypants.enderio.teleport.TravelController;

public class ItemDarkSteelSword extends ItemSword implements IAdvancedTooltipProvider, IDarkSteelItem, IItemOfTravel, IOverlayRenderAware {

  public static final String NAME = "darkSteel_sword";

  private static final String ENDERZOO_ENDERMINY = "enderzoo.Enderminy";

  static final ToolMaterial MATERIAL = EnumHelper.addToolMaterial("darkSteel", Config.darkSteelPickMinesTiCArdite ? 5 : 3, 1561, 7, 2, 25);

  public static boolean isEquipped(EntityPlayer player, EnumHand hand) {
    if (player == null) {
      return false;
    }
    ItemStack equipped = player.getHeldItem(hand);
    if (equipped == null) {
      return false;
    }
    return equipped.getItem() == DarkSteelItems.itemDarkSteelSword;
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, EnumHand hand, int requiredPower) {
    if (!isEquipped(player, hand)) {
      return false;
    }
    return EnergyUpgrade.getEnergyStored(player.getHeldItem(hand)) >= requiredPower;
  }

  public static ItemDarkSteelSword create() {
    ItemDarkSteelSword res = new ItemDarkSteelSword();
    res.init();
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private final int powerPerDamagePoint = Config.darkSteelPowerStorageBase / MATERIAL.getMaxUses();
  private long lastBlickTick = -1;

  public ItemDarkSteelSword() {
    super(MATERIAL);
    setCreativeTab(EnderIOTab.tabEnderIO);
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
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    EnergyUpgrade.EMPOWERED_FOUR.writeToItem(is);
    EnergyUpgrade.setPowerFull(is);
    TravelUpgrade.INSTANCE.writeToItem(is);
    par3List.add(is);
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 3;
  }

  @SubscribeEvent
  public void onEnderTeleport(EnderTeleportEvent evt) {
    if (evt.getEntityLiving().getEntityData().getBoolean("hitByDarkSteelSword")) {
      evt.setCanceled(true);
    }
  }

  // Set priority to lowest in the hope any other mod adding head drops will have already added them
  // by the time this is called to prevent multiple head drops
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onEntityDrop(LivingDropsEvent evt) {

    if (!(evt.getSource().getEntity() instanceof EntityPlayer)) {
      return;
    }

    EntityPlayer player = (EntityPlayer) evt.getSource().getEntity();
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
    if (isEquipped(player, EnumHand.MAIN_HAND)) {
      String name = EntityList.getEntityString(evt.getEntityLiving());
      if (evt.getEntityLiving() instanceof EntityEnderman || ENDERZOO_ENDERMINY.equals(name)) {
        int numPearls = 0;
        if (Math.random() <= Config.darkSteelSwordEnderPearlDropChance) {
          numPearls++;
        }
        for (int i = 0; i < evt.getLootingLevel(); i++) {
          if (Math.random() <= Config.darkSteelSwordEnderPearlDropChancePerLooting) {
            numPearls++;
          }
        }

        int existing = 0;
        for (EntityItem stack : evt.getDrops()) {
          if (stack.getEntityItem() != null && stack.getEntityItem().getItem() == Items.ender_pearl) {
            existing += stack.getEntityItem().stackSize;
          }
        }
        int toDrop = numPearls - existing;
        if (toDrop > 0) {
          evt.getDrops().add(Util.createDrop(player.worldObj, new ItemStack(Items.ender_pearl, toDrop, 0), evt.getEntityLiving().posX, evt.getEntityLiving().posY,
              evt.getEntityLiving().posZ, false));
        }

      }
    }

  }

  protected void dropSkull(LivingDropsEvent evt, EntityPlayer player) {
    ItemStack skull = getSkullForEntity(evt.getEntityLiving());
    if (skull != null && !containsDrop(evt, skull)) {
      evt.getDrops().add(Util.createEntityItem(player.worldObj, skull, evt.getEntityLiving().posX, evt.getEntityLiving().posY, evt.getEntityLiving().posZ));
    }
  }

  private boolean handleBeheadingWeapons(EntityPlayer player, LivingDropsEvent evt) {
    ItemStack equipped = player.getHeldItemMainhand();
    if (equipped == null || equipped.getTagCompound() == null) {
      return false;
    }
    NBTTagCompound infiToolRoot = equipped.getTagCompound().getCompoundTag("InfiTool");
    if (infiToolRoot == null) {
      return false;
    }

    boolean isCleaver = "tconstruct.items.tools.Cleaver".equals(equipped.getItem().getClass().getName());
    boolean hasBeheading = infiToolRoot.hasKey("Beheading");
    if (!isCleaver && !hasBeheading) {
      // Use default behavior if it is not a cleaver and doesn't have beheading
      return false;
    }

    if (!(evt.getEntityLiving() instanceof EntityEnderman)) {
      // If its not an enderman just let TiC do its thing
      // We wont modify head drops at all
      return true;
    }

    float fromWeapon;
    if (isCleaver) {
      fromWeapon = Config.ticCleaverSkullDropChance;
    } else {
      fromWeapon = Config.vanillaSwordSkullChance;
    }
    float fromLooting = 0;
    if (hasBeheading) {
      fromLooting = Config.ticBeheadingSkullModifier * infiToolRoot.getInteger("Beheading");
    }
    float skullDropChance = fromWeapon + fromLooting;
    if (Math.random() <= skullDropChance) {
      dropSkull(evt, player);
    }
    return true;
  }

  private double getSkullDropChance(EntityPlayer player, LivingDropsEvent evt) {
    if (isWitherSkeleton(evt)) {
      if (isEquippedAndPowered(player, EnumHand.MAIN_HAND, Config.darkSteelSwordPowerUsePerHit)) {
        return Config.darkSteelSwordWitherSkullChance + (Config.darkSteelSwordWitherSkullLootingModifier * evt.getLootingLevel());
      } else {
        return 0.01;
      }
    }
    float fromWeapon;
    float fromLooting;
    if (isEquippedAndPowered(player, EnumHand.MAIN_HAND, Config.darkSteelSwordPowerUsePerHit)) {
      fromWeapon = Config.darkSteelSwordSkullChance;
      fromLooting = Config.darkSteelSwordSkullLootingModifier * evt.getLootingLevel();
    } else {
      fromWeapon = Config.vanillaSwordSkullChance;
      fromLooting = Config.vanillaSwordSkullLootingModifier * evt.getLootingLevel();
    }
    return fromWeapon + fromLooting;
  }

  protected boolean isWitherSkeleton(LivingDropsEvent evt) {
    return evt.getEntityLiving() instanceof EntitySkeleton && ((EntitySkeleton) evt.getEntityLiving()).getSkeletonType() == 1;
  }

  private boolean containsDrop(LivingDropsEvent evt, ItemStack skull) {
    for (EntityItem ei : evt.getDrops()) {
      if (ei != null && ei.getEntityItem() != null && ei.getEntityItem().getItem() == skull.getItem()
          && ei.getEntityItem().getItemDamage() == skull.getItemDamage()) {
        return true;
      }
    }
    return false;
  }

  private ItemStack getSkullForEntity(EntityLivingBase entityLiving) {
    if (entityLiving instanceof EntitySkeleton) {
      int type = ((EntitySkeleton) entityLiving).getSkeletonType();
      if (type == 1) {
        return new ItemStack(Items.skull, 1, 1);
      } else {
        return new ItemStack(Items.skull, 1, 0);
      }
    } else if (entityLiving instanceof EntityZombie) {
      return new ItemStack(Items.skull, 1, 2);
    } else if (entityLiving instanceof EntityCreeper) {
      return new ItemStack(Items.skull, 1, 4);
    } else if (entityLiving instanceof EntityEnderman) {
      return new ItemStack(EnderIO.blockEndermanSkull);
    }

    return null;
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase playerEntity) {

    if (playerEntity instanceof EntityPlayer) {

      EntityPlayer player = (EntityPlayer) playerEntity;
      ItemStack sword = player.getHeldItemMainhand();

      // Durability damage
      EnergyUpgrade eu = EnergyUpgrade.loadFromItem(stack);
      if (eu != null && eu.isAbsorbDamageWithPower(stack) && eu.getEnergy() > 0) {
        eu.extractEnergy(powerPerDamagePoint, false);

      } else {
        super.hitEntity(stack, entity, playerEntity);
      }

      // sword hit
      if (eu != null) {
        eu.writeToItem(sword);

        if (eu.getEnergy() > Config.darkSteelSwordPowerUsePerHit) {
          extractEnergy(player.getHeldItemMainhand(), Config.darkSteelSwordPowerUsePerHit, false);
          String name = EntityList.getEntityString(entity);
          if (entity instanceof EntityEnderman || ENDERZOO_ENDERMINY.equals(name)) {
            entity.getEntityData().setBoolean("hitByDarkSteelSword", true);
          }
        }

      }

    }
    return true;
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
    list.add(TextFormatting.WHITE + EnderIO.lang.localize("item.darkSteel_sword.tooltip.line1"));
    if (EnergyUpgrade.itemHasAnyPowerUpgrade(itemstack)) {
      list.add(TextFormatting.WHITE + EnderIO.lang.localize("item.darkSteel_sword.tooltip.line2"));
      list.add(TextFormatting.WHITE + EnderIO.lang.localize("item.darkSteel_sword.tooltip.line3"));
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
    
    return (isEquipped(ep, EnumHand.MAIN_HAND) || isEquipped(ep, EnumHand.OFF_HAND)) && ep.isSneaking() && TravelUpgrade.loadFromItem(equipped) != null;
  }
  
  private boolean isTravelUpgradeActive(EntityPlayer player, ItemStack stack, EnumHand hand) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {    
    if (isTravelUpgradeActive(player, stack, hand)) {
      if (world.isRemote) {
        if (TravelController.instance.activateTravelAccessable(stack, world, player, TravelSource.STAFF)) {
          player.swingArm(hand);
          return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
        }
      }

      long ticksSinceBlink = EnderIO.proxy.getTickCount() - lastBlickTick;
      if (ticksSinceBlink < 0) {
        lastBlickTick = -1;
      }
      if (Config.travelStaffBlinkEnabled && world.isRemote && ticksSinceBlink >= Config.travelStaffBlinkPauseTicks) {
        if (TravelController.instance.doBlink(stack, player)) {
          player.swingArm(hand);
          lastBlickTick = EnderIO.proxy.getTickCount();
        }
      }
      return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

    return super.onItemRightClick(stack, world, player, hand);
  }

  

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

}
