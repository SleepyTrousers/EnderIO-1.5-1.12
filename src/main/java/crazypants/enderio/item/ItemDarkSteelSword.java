package crazypants.enderio.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovementInput;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.material.Alloy;
import crazypants.util.Util;

public class ItemDarkSteelSword extends ItemSword implements IEnergyContainerItem {

  static final ToolMaterial MATERIAL = EnumHelper.addToolMaterial("darkSteel", 3, 1561, 7, 2, 25);

  public static boolean isEquipped(EntityPlayer player) {
    if(player == null) {
      return false;
    }
    ItemStack equipped = player.getCurrentEquippedItem();
    if(equipped == null) {
      return false;
    }
    return equipped.getItem() == EnderIO.itemDarkSteelSword;
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, int requiredPower) {
    if(!isEquipped(player)) {
      return false;
    }
    return EnderIO.itemDarkSteelSword.getEnergyStored(player.getCurrentEquippedItem()) >= requiredPower;
  }

  public static ItemDarkSteelSword create() {
    ItemDarkSteelSword res = new ItemDarkSteelSword();
    res.init();
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  private int powerPerDamagePoint = Config.darkSteelPowerStorage / MATERIAL.getMaxUses();

  private EnergyContainer energyCont = new EnergyContainer(Config.darkSteelPowerStorage, Config.darkSteelPowerStorage / 10, Config.darkSteelPowerStorage / 10);

  public ItemDarkSteelSword() {
    super(MATERIAL);
    setCreativeTab(EnderIOTab.tabEnderIO);

    String str = "darkSteel_sword";
    setUnlocalizedName(str);
    setTextureName("enderIO:" + str);
  }

  @Override
  public boolean isDamaged(ItemStack stack) {
    return false;
  }

  @SubscribeEvent
  public void onEnderTeleport(EnderTeleportEvent evt) {
    if(evt.entityLiving.getEntityData().getBoolean("hitByDarkSteelSword")) {
      evt.setCanceled(true);
    }
  }

  @SubscribeEvent
  public void onEntityDrop(LivingDropsEvent evt) {

    if(evt.source.getEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) evt.source.getEntity();

      double skullDropChance = getSkullDropChance(player, evt);
      if(Math.random() >= skullDropChance) {
        ItemStack skull = getSkullForEntity(evt.entityLiving);
        if(skull != null && !containsDrop(evt, skull)) {
          //TODO: Shouldn't I add this to the list in the event?
          Util.dropItems(player.worldObj, skull, evt.entityLiving.posX, evt.entityLiving.posY, evt.entityLiving.posZ, true);
        }
      }
      if(isEquipped(player)) {
        if(evt.entityLiving instanceof EntityEnderman) {
          int numPearls = 0;
          if(Math.random() >= Config.darkSteelSwordEnderPearlDropChance) {
            numPearls++;
          }
          for (int i = 0; i < evt.lootingLevel; i++) {
            if(Math.random() >= Config.darkSteelSwordEnderPearlDropChancePerLooting) {
              numPearls++;
            }
          }
          //TODO: Shouldn't I add this to the list in the event?
          Util.dropItems(player.worldObj, new ItemStack(Items.ender_pearl, numPearls, 0), evt.entityLiving.posX, evt.entityLiving.posY, evt.entityLiving.posZ,
              true);
        }

      }

    }
  }

  private double getSkullDropChance(EntityPlayer player, LivingDropsEvent evt) {
    if(evt.entityLiving instanceof EntitySkeleton && ((EntitySkeleton) evt.entityLiving).getSkeletonType() == 1) {
      if(isEquippedAndPowered(player, Config.darkSteelSwordPowerUsePerHit)) {
        return Config.darkSteelSwordWitherSkullChance + (Config.darkSteelSwordWitherSkullLootingModifier * evt.lootingLevel);
      } else {
        return 0;
      }
    }
    float fromWeapon = 0;
    float fromLooting = 0;
    if(isEquippedAndPowered(player, Config.darkSteelSwordPowerUsePerHit)) {
      fromWeapon = Config.darkSteelSwordSkullChance;
      fromLooting = Config.darkSteelSwordSkullLootingModifier * evt.lootingLevel;
    } else {
      fromLooting = Config.vanillaSwordSkullLootingModifier * evt.lootingLevel;
    }
    return fromWeapon + fromLooting;
  }

  private boolean containsDrop(LivingDropsEvent evt, ItemStack skull) {
    for (EntityItem ei : evt.drops) {
      if(ei != null && ei.getEntityItem() != null && ei.getEntityItem().getItem() == skull.getItem()
          && ei.getEntityItem().getItemDamage() == skull.getItemDamage()) {
        return true;
      }
    }
    return false;
  }

  private ItemStack getSkullForEntity(EntityLivingBase entityLiving) {
    if(entityLiving instanceof EntitySkeleton) {
      int type = ((EntitySkeleton) entityLiving).getSkeletonType();
      if(type == 1) {
        return new ItemStack(Items.skull, 1, 1);
      } else {
        return new ItemStack(Items.skull, 1, 0);
      }
    } else if(entityLiving instanceof EntityZombie) {
      return new ItemStack(Items.skull, 1, 2);
    } else if(entityLiving instanceof EntityCreeper) {
      return new ItemStack(Items.skull, 1, 4);
    }

    return null;
  }

  protected void init() {
    GameRegistry.registerItem(this, getUnlocalizedName());
  }

  @Override
  public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase entity, EntityLivingBase playerEntity) {

    if(playerEntity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) playerEntity;
      ItemStack sword = player.getCurrentEquippedItem();
      boolean absorbPower = energyCont.isAbsorbDamageWithPower(sword);
      if(isEquippedAndPowered(player, powerPerDamagePoint) && absorbPower) {
        extractEnergy(player.getCurrentEquippedItem(), powerPerDamagePoint, false);
      } else {
        super.hitEntity(par1ItemStack, entity, playerEntity);
      }
      energyCont.setAbsorbDamageWithPower(sword, !absorbPower);

      if(isEquippedAndPowered(player, Config.darkSteelSwordPowerUsePerHit)) {
        extractEnergy(player.getCurrentEquippedItem(), Config.darkSteelSwordPowerUsePerHit, false);
        if(entity instanceof EntityEnderman) {
          entity.getEntityData().setBoolean("hitByDarkSteelSword", true);
        }
      }
    }
    return true;
  }

  @Override
  public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
    return energyCont.receiveEnergy(container, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
    return energyCont.extractEnergy(container, maxExtract, simulate);
  }

  @Override
  public int getEnergyStored(ItemStack container) {
    return energyCont.getEnergyStored(container);
  }

  @Override
  public int getMaxEnergyStored(ItemStack container) {
    return energyCont.getMaxEnergyStored(container);
  }

  @Override
  public boolean getIsRepairable(ItemStack i1, ItemStack i2) {
    return i2 != null && i2.getItem() == EnderIO.itemAlloy && i2.getItemDamage() == Alloy.DARK_STEEL.ordinal();
  }

  @Override
  public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    //TODO: Localize
    list.add("Durability: " + (itemstack.getMaxDamage() - itemstack.getItemDamage()) + "/" + itemstack.getMaxDamage());
    String str = "Power: " + PowerDisplayUtil.formatPower(getEnergyStored(itemstack) / 10) + "/"
        + PowerDisplayUtil.formatPower(getMaxEnergyStored(itemstack) / 10) + " " + PowerDisplayUtil.abrevation();
    list.add(str);
    MovementInput input = Minecraft.getMinecraft().thePlayer.movementInput;
    list.add("Increased skull and ender pearl drops.");
    list.add("Enderman can't teleport once hit.");
    list.add("");
    list.add(EnumChatFormatting.BLUE + "+1 Damage when Powered");

  }

  public ItemStack createItemStack() {
    return new ItemStack(this);
  }

}
