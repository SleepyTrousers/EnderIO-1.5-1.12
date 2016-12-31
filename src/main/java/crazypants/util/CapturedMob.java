package crazypants.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.EntityUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import info.loenwind.scheduler.Celeb;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.monster.ZombieType;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import static crazypants.enderio.ModObject.itemSoulVessel;

public class CapturedMob {

  public static final String SKELETON_ENTITY_NAME = "Skeleton";
  public static final String ZOMBIE_ENTITY_NAME = "Zombie";
  public static final String ENTITY_KEY = "entity";
  public static final String ENTITY_ID_KEY = "entityId";
  public static final String CUSTOM_NAME_KEY = "customName";
  public static final String IS_STUB_KEY = "isStub";
  public static final String VARIANT_KEY = "isVariant";

  private final static List<String> blacklist = new ArrayList<String>();
  private final static List<String> unspawnablelist = new ArrayList<String>();

  private final NBTTagCompound entityNbt;
  private final String entityId;
  private final String customName;
  private final boolean isStub;
  private Enum<?> variant;

  private CapturedMob(@Nonnull EntityLivingBase entity) {

    entityId = EntityList.getEntityString(entity);

    entityNbt = entity.serializeNBT();

    String name = null;
    if (entity instanceof EntityLiving) {
      EntityLiving entLiv = (EntityLiving) entity;
      if (entLiv.hasCustomName()) {
        name = entLiv.getCustomNameTag();
      }
    }
    if (name != null && name.length() > 0) {
      customName = name;
    } else {
      customName = null;
    }
    if (entity instanceof EntitySkeleton) {
      variant = ((EntitySkeleton) entity).func_189771_df();
    } else if (entity instanceof EntityZombie) {
      variant = ((EntityZombie) entity).func_189777_di();
    } else {
      variant = null;
    }

    isStub = false;
  }

  private CapturedMob(NBTTagCompound nbt) {
    if (nbt.hasKey(ENTITY_KEY)) {
      entityNbt = nbt.getCompoundTag(ENTITY_KEY).copy();
    } else {
      entityNbt = null;
    }
    if (nbt.hasKey(ENTITY_ID_KEY)) {
      entityId = nbt.getString(ENTITY_ID_KEY);
    } else {
      entityId = null;
    }
    if (nbt.hasKey(CUSTOM_NAME_KEY)) {
      customName = nbt.getString(CUSTOM_NAME_KEY);
    } else {
      customName = null;
    }
    isStub = nbt.getBoolean(IS_STUB_KEY);
    if(nbt.hasKey(VARIANT_KEY)) {
      short ord = nbt.getShort(VARIANT_KEY);
      variant = mkEnumForType(ord);
    } else {
      variant = null;
    }
  }

  private CapturedMob(String entityId, Enum<?> variant) {
    this.entityNbt = null;
    this.entityId = entityId;
    this.customName = null;
    this.isStub = true;
    this.variant = variant;
  }

  public static @Nullable CapturedMob create(@Nullable Entity entity) {
    if (!(entity instanceof EntityLivingBase) || entity.worldObj == null || entity.worldObj.isRemote || entity instanceof EntityPlayer || isBlacklisted(entity)) {
      return null;
    }
    return new CapturedMob((EntityLivingBase) entity);
  }

  public static @Nullable CapturedMob create(@Nullable String entityId, Enum<?> variant) {
    if (entityId == null || !EntityList.isStringValidEntityName(entityId)) {
      return null;
    }
    return new CapturedMob(entityId, variant);
  }

  public @Nonnull ItemStack toStack(Item item, int meta, int amount) {
    ItemStack stack = new ItemStack(item, amount, meta);
    stack.setTagCompound(toNbt(null));
    if (item == itemSoulVessel.getItem() && customName == null && "Pig".equals(entityId) && Math.random() < 0.01) {
      stack.getTagCompound().setString(CUSTOM_NAME_KEY, EnderIO.lang.localize("easteregg.piginabottle"));
    }
    return stack;
  }

  public @Nonnull ItemStack toGenericStack(Item item, int meta, int amount) {
    NBTTagCompound data = new NBTTagCompound();
    String id;
    if (entityId != null) {
      id = entityId;
    } else if (entityNbt != null && entityNbt.hasKey("id")) {
      id = entityNbt.getString("id");
    } else {
      id = "Pig";
    }
    if (isUnspawnable(id)) {
      return toStack(item, meta, amount);
    }
    data.setString(ENTITY_ID_KEY, id);
    data.setBoolean(IS_STUB_KEY, true);
    if (variant != null) {
      data.setShort(VARIANT_KEY, (short) variant.ordinal());
    }
    if (item == itemSoulVessel.getItem() && customName == null && "Pig".equals(entityId) && Math.random() < 0.01) {
      data.setString(CUSTOM_NAME_KEY, EnderIO.lang.localize("easteregg.piginabottle"));
    }
    ItemStack stack = new ItemStack(item, amount, meta);
    stack.setTagCompound(data);
    return stack;
  }

  public @Nonnull NBTTagCompound toNbt(@Nullable NBTTagCompound nbt) {
    NBTTagCompound data = nbt != null ? nbt : new NBTTagCompound();
    if (entityNbt != null) {
      data.setTag(ENTITY_KEY, entityNbt.copy());
    }
    if (entityId != null) {
      data.setString(ENTITY_ID_KEY, entityId);
    }
    if (customName != null) {
      data.setString(CUSTOM_NAME_KEY, customName);
    }
    if (isStub) {
      data.setBoolean(IS_STUB_KEY, isStub);
    }
    if (variant != null) {
      data.setShort(VARIANT_KEY, (short)variant.ordinal());
    }
    return data;
  }

  public static boolean containsSoul(@Nullable NBTTagCompound nbt) {
    return nbt != null && (nbt.hasKey(ENTITY_KEY) || (nbt.hasKey(ENTITY_ID_KEY) && nbt.hasKey(IS_STUB_KEY)));
  }

  public static boolean containsSoul(@Nullable ItemStack stack) {
    return stack != null && stack.hasTagCompound() && containsSoul(stack.getTagCompound());
  }

  public static @Nullable CapturedMob create(@Nullable ItemStack stack) {
    if (containsSoul(stack)) {
      return new CapturedMob(stack.getTagCompound());
    } else {
      return null;
    }
  }

  public static @Nullable CapturedMob create(@Nullable NBTTagCompound nbt) {
    if (containsSoul(nbt)) {
      return new CapturedMob(nbt);
    } else {
      return null;
    }
  }

  public static boolean isBlacklisted(@Nonnull Entity entity) {
    String entityId = EntityList.getEntityString(entity);
    if (entityId == null || entityId.trim().isEmpty() || (!Config.soulVesselCapturesBosses && !entity.isNonBoss())) {
      return true;
    }
    return Config.soulVesselBlackList.contains(entityId) || blacklist.contains(entityId);
  }

  public boolean spawn(@Nullable World world, @Nullable BlockPos pos, @Nullable EnumFacing side, boolean clone) {
    if (world == null || pos == null) {
      return false;
    }
    @Nonnull
    EnumFacing theSide = side != null ? side : EnumFacing.UP;
    Entity entity = getEntity(world, pos, null, clone);
    if (entity == null) {
      return false;
    }

    Block blk = world.getBlockState(pos).getBlock();
    double spawnX = pos.getX() + theSide.getFrontOffsetX() + 0.5;
    double spawnY = pos.getY() + theSide.getFrontOffsetY();
    double spawnZ = pos.getZ() + theSide.getFrontOffsetZ() + 0.5;
    if (theSide == EnumFacing.UP && (blk instanceof BlockFence || blk instanceof BlockWall || blk instanceof BlockFenceGate)) {
      spawnY += 0.5;
    }
    entity.setLocationAndAngles(spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0);

    if (!world.checkNoEntityCollision(entity.getEntityBoundingBox()) || !world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()) {
      return false;
    }

    if (customName != null && entity instanceof EntityLiving) {
      ((EntityLiving) entity).setCustomNameTag(customName);
    }

    if (!world.spawnEntityInWorld(entity)) {
      entity.setUniqueId(MathHelper.getRandomUuid(world.rand));
      if (!world.spawnEntityInWorld(entity)) {
        return false;
      }
    }

    if (entity instanceof EntityLiving) {
      ((EntityLiving) entity).playLivingSound();
    }

    //TODO: 1.10, need to figure out how this works now
//    Entity riddenByEntity = entity.riddenByEntity;
//    while (riddenByEntity != null) {
//      riddenByEntity.setLocationAndAngles(spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0.0F);
//      if (world.spawnEntityInWorld(riddenByEntity)) {
//        if (riddenByEntity instanceof EntityLiving) {
//          ((EntityLiving) riddenByEntity).playLivingSound();
//        }
//        riddenByEntity = riddenByEntity.riddenByEntity;
//      } else {
//        riddenByEntity = null;
//      }
//    }

    return true;
  }

  public @Nullable Entity getEntity(@Nullable World world, boolean clone) {
    return getEntity(world, null, null, clone);
  }

  @SuppressWarnings("null")
  public @Nullable Entity getEntity(@Nullable World world, @Nullable BlockPos pos, @Nullable DifficultyInstance difficulty, boolean clone) {
    Entity entity = null;
    if (world != null) {
      if (entityId != null && (isStub || !clone) && (!isUnspawnable(entityId) || entityNbt == null)) {
        entity = EntityList.createEntityByName(entityId, world);
      } else if (entityNbt != null) {
        if (clone || isUnspawnable(entityId)) {
          entity = EntityList.createEntityFromNBT(entityNbt, world);
          return entity;
        } else {
          entity = EntityList.createEntityByName(entityNbt.getString("id"), world);
        }
      }
      if (pos != null && entity != null) {
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
      }
      if (entity instanceof EntityLiving) {
        if (pos != null && difficulty == null) {
          difficulty = world.getDifficultyForLocation(pos);
        }
        if (difficulty != null) {
          IEntityLivingData livingData = null;
          if (variant != null && entity instanceof EntityZombie) {
            livingData = new IEntityLivingData() {
            };
          }
          ((EntityLiving) entity).onInitialSpawn(difficulty, livingData);
        }
        }
      if (variant != null) {
        if (entity instanceof EntitySkeleton) {
          EntitySkeleton skel = (EntitySkeleton) entity;
          skel.func_189768_a((SkeletonType) variant);
          if (variant == SkeletonType.WITHER) {
            skel.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
            skel.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, null);
            skel.setItemStackToSlot(EntityEquipmentSlot.CHEST, null);
            skel.setItemStackToSlot(EntityEquipmentSlot.FEET, null);
            skel.setItemStackToSlot(EntityEquipmentSlot.HEAD, null);
            skel.setItemStackToSlot(EntityEquipmentSlot.LEGS, null);

            skel.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
            skel.setCombatTask();

            if (Celeb.H31.isOn() && Math.random() < 0.25) {
              skel.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Math.random() < 0.1 ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
              skel.setDropChance(EntityEquipmentSlot.HEAD, 0.0F);
            } else if (Celeb.C06.isOn() && Math.random() < 0.25) {
              skel.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Math.random() < 0.25 ? Items.LEATHER_BOOTS : Items.STICK));
            } else if (Math.random() < 0.1) {
              skel.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(DarkSteelItems.itemDarkSteelSword));
              skel.setDropChance(EntityEquipmentSlot.MAINHAND, 0.00001F);
            }
          }
        } else if (entity instanceof EntityZombie) {
          boolean isChild = world.rand.nextFloat() < net.minecraftforge.common.ForgeModContainer.zombieBabyChance;
          ((EntityZombie)entity).func_189778_a((ZombieType)variant);
          if (((ZombieType) variant).func_190154_b()) {
            do {
              net.minecraftforge.fml.common.registry.VillagerRegistry.setRandomProfession((EntityZombie) entity, world.rand);
            } while (((EntityZombie) entity).getVillagerTypeForge() == null && !((EntityZombie) entity).func_189777_di().func_190154_b());
          }
          if (isChild) {
            ((EntityZombie) entity).setChild(true);
            if (world.rand.nextFloat() < 0.05D) {
              EntityChicken entitychicken1 = new EntityChicken(world);
              entitychicken1.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0F);
              entitychicken1.onInitialSpawn(difficulty, (IEntityLivingData) null);
              entitychicken1.setChickenJockey(true);
              world.spawnEntityInWorld(entitychicken1);
              entity.startRiding(entitychicken1);
            }
          }
        }
      }
    }
    return entity;
  }

  public @Nonnull String getDisplayName() {
    String baseName = null;
    if (variant != null && SKELETON_ENTITY_NAME.equals(entityId)) {
      // The value is in the enum but not exposed, need to fix this
      String typeName = variant == SkeletonType.NORMAL ? entityId : variant == SkeletonType.WITHER ? "WitherSkeleton" : "Stray";
      baseName = EntityUtil.getDisplayNameForEntity(typeName);
    } else if (variant != null && ZOMBIE_ENTITY_NAME.equals(entityId)) {
      if (((ZombieType) variant).func_190154_b()) {
        baseName = EntityUtil.getDisplayNameForEntity("ZombieVillager");
      } else {
        baseName = ((ZombieType) variant).func_190145_d().getUnformattedText();
      }
    } else if (entityId != null) {
      baseName = EntityUtil.getDisplayNameForEntity(entityId);
    } else if (entityNbt != null) {
      baseName = EntityUtil.getDisplayNameForEntity(entityNbt.getString("id"));
    }
    if (baseName == null || baseName.trim().isEmpty()) {
      if (customName != null && !customName.trim().isEmpty()) {
        return customName + "";
      } else {
        return "???";
      }
    } else {
      if (customName != null && !customName.trim().isEmpty()) {
        return customName + " (" + baseName + ")";
      } else {
        return baseName;
      }
    }
  }

  public float getHealth() {
    if (entityNbt != null && entityNbt.hasKey("HealF")) {
      return entityNbt.getFloat("HealF");
    } else {
      return Float.NaN;
    }
  }

  public float getMaxHealth() {
    NBTTagCompound maxHealthAttrib = getAttribute("generic.maxHealth");
    if (maxHealthAttrib != null && maxHealthAttrib.hasKey("Base")) {
      return maxHealthAttrib.getFloat("Base");
    }
    return Float.NaN;
  }

  public @Nullable NBTTagCompound getAttribute(@Nullable String name) {
    if (name != null && entityNbt != null && entityNbt.hasKey("Attributes")) {
      NBTBase tag = entityNbt.getTag("Attributes");
      if (tag instanceof NBTTagList) {
        NBTTagList attributes = (NBTTagList) tag;
        for (int i = 0; i < attributes.tagCount(); i++) {
          NBTTagCompound attrib = attributes.getCompoundTagAt(i);
          if (attrib.hasKey("Name") && name.equals(attrib.getString("Name"))) {
            return attrib;
          }
        }
      }
    }
    return null;
  }

  public @Nullable DyeColor getColor() {
    if (entityNbt != null && entityNbt.hasKey("Color")) {
      int colorIdx = entityNbt.getInteger("Color");
      if (colorIdx >= 0 && colorIdx <= 15) {
        return DyeColor.values()[15 - colorIdx];
      }
    }
    return null;
  }

  public @Nullable String getFluidName() {
    if (entityNbt != null && entityNbt.hasKey("FluidName")) {
      return entityNbt.getString("FluidName");
    }
    return null;
  }

  public static void addToBlackList(String entityName) {
    blacklist.add(entityName);
  }

  public static void addToUnspawnableList(String entityName) {
    unspawnablelist.add(entityName);
  }

  private boolean isUnspawnable(String entityName) {
    return Config.soulVesselUnspawnableList.contains(entityId) || unspawnablelist.contains(entityName);
  }

  public @Nullable String getEntityName() {
    return entityId != null ? entityId : entityNbt != null ? entityNbt.getString("id") : null;
  }

  public boolean isSameType(Entity entity) {
    return entity != null && EntityList.getEntityString(entity) != null && EntityList.getEntityString(entity).equals(getEntityName())
        && (!(entity instanceof EntitySkeleton) || isSameSkeleton(((EntitySkeleton) entity).func_189771_df(), variant))
        && (!(entity instanceof EntityZombie) || isSameZombie(((EntityZombie) entity).func_189777_di(), variant));
  }

  @Override
  public String toString() {
    return "CapturedMob [" + (entityId != null ? "entityId=" + entityId + ", " : "") + (customName != null ? "customName=" + customName + ", " : "")
        + "isStub=" + isStub + ", variant=" + variant + ", " + (entityNbt != null ? "entityNbt=" + entityNbt + ", " : "") + "getDisplayName()="
        + getDisplayName() + ", getHealth()=" + getHealth() + ", getMaxHealth()=" + getMaxHealth() + ", "
        + (getColor() != null ? "getColor()=" + getColor() + ", " : "") + (getFluidName() != null ? "getFluidName()=" + getFluidName() : "") + "]";
  }

  // we treat normal zombies and all villager zombies as the same kind of zombie for the pressure plate and the obelisks
  private static boolean isSameZombie(ZombieType a, Enum<?> b) {
    return a == b || (a != ZombieType.HUSK && b != ZombieType.HUSK) || b == null;
  }

  private static boolean isSameSkeleton(SkeletonType a, Enum<?> b) {
    return a == b || b == null;
  }

  /*
   * Note: The Ender Dragon cannot be spawned as expected. All of its logic (moving, fighting, being hit, ...) is a special manager class, which is very
   * hardcoded to the specifics of the vanilla dragon fight.
   */
  public static @Nonnull List<CapturedMob> getSouls(List<String> mobs) {
    List<CapturedMob> result = new ArrayList<CapturedMob>(mobs.size());
    for (String mobName : mobs) {
      CapturedMob soul = create(mobName, null);
      if (soul != null && !"EnderDragon".equals(mobName)) {
        if (SKELETON_ENTITY_NAME.equals(mobName)) {
          for (SkeletonType type : SkeletonType.values()) {
            result.add(create(mobName, type));
          }
        } else if (ZOMBIE_ENTITY_NAME.equals(mobName)) {
          result.add(create(mobName, ZombieType.NORMAL));
          result.add(create(mobName, ZombieType.VILLAGER_BUTCHER)); // Forge will randomize these
          result.add(create(mobName, ZombieType.HUSK));
        } else {
          result.add(soul);
        }
      }
    }
    return result;
  }

  public static @Nonnull List<CapturedMob> getAllSouls() {
    return getSouls(EntityUtil.getAllRegisteredMobNames());
  }

  private Enum<?> mkEnumForType(int ordinal) {
    String type = entityId;
    if (entityId == null && entityNbt != null) {
      type = entityNbt.getString("id");
    }
    if (entityId == null) {
      return null;
    }
    if (SKELETON_ENTITY_NAME.equals(type)) {
      return SkeletonType.values()[ordinal];
    }
    if (ZOMBIE_ENTITY_NAME.equals(type)) {
      return ZombieType.values()[ordinal];
  }
    return null;
  }
}
