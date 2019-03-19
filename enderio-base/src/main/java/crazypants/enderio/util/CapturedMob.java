package crazypants.enderio.util;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.scheduler.Celeb;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitherSkeleton;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import static crazypants.enderio.base.init.ModObject.itemSoulVial;

public final class CapturedMob {

  private static final @Nonnull ResourceLocation PIG = new ResourceLocation("pig");
  private static final @Nonnull ResourceLocation DRAGON = new ResourceLocation("ender_dragon");

  public static final @Nonnull String ENTITY_KEY = "entity";
  public static final @Nonnull String ENTITY_ID_KEY = "entityId";
  public static final @Nonnull String ENTITY_TAG_KEY = "EntityTag";
  public static final @Nonnull String CUSTOM_NAME_KEY = "customName";

  private final static @Nonnull NNList<Predicate<ResourceLocation>> blacklist = new NNList<>(in -> DRAGON.equals(in));
  private final static @Nonnull NNList<Predicate<ResourceLocation>> unspawnablelist = new NNList<>();
  private static boolean bossesBlacklisted = true;

  private final @Nullable NBTTagCompound entityNbt;
  private final @Nonnull ResourceLocation entityId;
  private final @Nullable String customName;

  private CapturedMob(@Nonnull EntityLivingBase entity) {

    ResourceLocation id = EntityList.getKey(entity);

    entityId = id == null ? PIG : id; // nbt will have nothing else

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

  }

  private CapturedMob(@Nonnull NBTTagCompound nbt) {
    if (nbt.hasKey(ENTITY_KEY)) {
      entityNbt = nbt.getCompoundTag(ENTITY_KEY).copy();
    } else if (nbt.hasKey(ENTITY_TAG_KEY)) {
      entityNbt = nbt.getCompoundTag(ENTITY_TAG_KEY).copy();
    } else {
      entityNbt = null;
    }
    String id = null;
    if (nbt.hasKey(ENTITY_ID_KEY)) {
      id = nbt.getString(ENTITY_ID_KEY);
    } else if (entityNbt != null && entityNbt.hasKey("id")) {
      id = NullHelper.notnullJ(entityNbt, "private final field changed its value").getString("id");
    }
    entityId = id == null || id.isEmpty() ? PIG : new ResourceLocation(id);
    if (nbt.hasKey(CUSTOM_NAME_KEY)) {
      customName = nbt.getString(CUSTOM_NAME_KEY);
    } else {
      customName = null;
    }
  }

  private CapturedMob(@Nonnull ResourceLocation entityId) {
    this.entityNbt = null;
    this.entityId = entityId;
    this.customName = null;
  }

  public static @Nullable CapturedMob create(@Nullable Entity entity) {
    if (!(entity instanceof EntityLivingBase) || !entity.isEntityAlive() || entity.world.isRemote || entity instanceof EntityPlayer || isBlacklisted(entity)) {
      return null;
    }
    return new CapturedMob((EntityLivingBase) entity);
  }

  public static @Nullable CapturedMob create(@Nullable ResourceLocation entityId) {
    if (entityId == null || !EntityList.isRegistered(entityId) || !EntityUtil.isRegisteredMob(entityId)) {
      return null;
    }
    return new CapturedMob(entityId);
  }

  public @Nonnull ItemStack toStack(@Nonnull Item item, int meta, int amount) {
    ItemStack stack = new ItemStack(item, amount, meta);
    stack.setTagCompound(toNbt(null));
    if (item == itemSoulVial.getItem() && customName == null && PIG.equals(entityId) && Math.random() < 0.01) {
      NullHelper.notnullM(stack.getTagCompound(), "getTagCompound() doesn't produce value that was set with setTagCompound()").setString(CUSTOM_NAME_KEY,
          Lang.EASTER_PIGGY.get());
    }
    return stack;
  }

  public @Nonnull ItemStack toStack(@Nonnull Block block, int meta, int amount) {
    ItemStack stack = new ItemStack(block, amount, meta);
    stack.setTagCompound(toNbt(null));
    return stack;
  }

  public @Nonnull ItemStack toGenericStack(@Nonnull Item item, int meta, int amount) {
    NBTTagCompound data = new NBTTagCompound();
    if (isUnspawnable(entityId)) {
      return toStack(item, meta, amount);
    }
    data.setString(ENTITY_ID_KEY, entityId.toString());
    if (item == itemSoulVial.getItem() && customName == null && PIG.equals(entityId) && Math.random() < 0.01) {
      data.setString(CUSTOM_NAME_KEY, Lang.EASTER_PIGGY.get());
    }
    ItemStack stack = new ItemStack(item, amount, meta);
    stack.setTagCompound(data);
    return stack;
  }

  public @Nonnull NBTTagCompound toNbt(@Nullable NBTTagCompound nbt) {
    NBTTagCompound data = nbt != null ? nbt : new NBTTagCompound();
    data.setString(ENTITY_ID_KEY, entityId.toString());
    if (entityNbt != null) {
      data.setTag(ENTITY_KEY, entityNbt.copy());
    }
    if (customName != null) {
      data.setString(CUSTOM_NAME_KEY, customName);
    }
    return data;
  }

  public static boolean containsSoul(@Nullable NBTTagCompound nbt) {
    return nbt != null && (nbt.hasKey(ENTITY_KEY) || nbt.hasKey(ENTITY_ID_KEY) || nbt.hasKey(ENTITY_TAG_KEY));
  }

  @SuppressWarnings("null")
  public static boolean containsSoul(@Nonnull ItemStack stack) {
    return Prep.isValid(stack) && stack.hasTagCompound() && containsSoul(stack.getTagCompound());
  }

  @SuppressWarnings("null")
  public static @Nullable CapturedMob create(@Nonnull ItemStack stack) {
    if (containsSoul(stack)) {
      return new CapturedMob(stack.getTagCompound());
    } else {
      return null;
    }
  }

  public static @Nullable CapturedMob create(@Nullable NBTTagCompound nbt) {
    if (nbt != null && containsSoul(nbt)) {
      return new CapturedMob(nbt);
    } else {
      return null;
    }
  }

  public static boolean isBlacklisted(@Nonnull Entity entity) {
    ResourceLocation entityId = EntityList.getKey(entity);
    return entityId == null || isBlacklistedBoss(entityId, entity) || blacklist.stream().anyMatch(elem -> elem.test(entityId));
  }

  private static boolean isBlacklistedBoss(ResourceLocation entityId, Entity entity) {
    return !bossesBlacklisted && !entity.isNonBoss() && !"minecraft".equals(entityId.getResourceDomain());
  }

  public boolean spawn(@Nullable World world, @Nullable BlockPos pos, @Nullable EnumFacing side, boolean clone) {
    return doSpawn(world, pos, side, clone) != null;
  }

  public @Nullable Entity doSpawn(@Nullable World world, @Nullable BlockPos pos, @Nullable EnumFacing side, boolean clone) {
    if (world == null || pos == null) {
      return null;
    }
    @Nonnull
    EnumFacing theSide = side != null ? side : EnumFacing.UP;
    Entity entity = getEntity(world, pos, null, clone);
    if (entity == null) {
      return null;
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
      return null;
    }

    if (customName != null && entity instanceof EntityLiving) {
      ((EntityLiving) entity).setCustomNameTag(customName);
    }

    if (!world.spawnEntity(entity)) {
      entity.setUniqueId(MathHelper.getRandomUUID(world.rand));
      if (!world.spawnEntity(entity)) {
        return null;
      }
    }

    if (entity instanceof EntityLiving) {
      ((EntityLiving) entity).playLivingSound();
    }

    return entity;
  }

  public @Nullable Entity getEntity(@Nullable World world, boolean clone) {
    return getEntity(world, null, null, clone);
  }

  public @Nullable Entity getEntity(@Nullable World world, @Nullable BlockPos pos, @Nullable DifficultyInstance difficulty, boolean clone) {
    if (world == null) {
      return null;
    }

    final NBTTagCompound entityNbt_nullchecked = entityNbt;
    if (entityNbt_nullchecked != null && (clone || isUnspawnable(entityId))) {
      final Entity entity = EntityList.createEntityFromNBT(entityNbt_nullchecked, world);
      if (!clone && entity != null) {
        // The caller doesn't expect a clone, but we return one. Give it a unique/new ID to avoid problems with duplicate entities.
        entity.setUniqueId(MathHelper.getRandomUUID(world.rand));
      }
      return entity;
    }

    Entity entity = EntityList.createEntityByIDFromName(entityId, world);
    if (entity == null) {
      return null;
    }

    if (pos != null) {
      entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    if (entity instanceof EntityLiving) {
      if (pos != null && difficulty == null) {
        difficulty = world.getDifficultyForLocation(pos);
      }
      if (difficulty != null) {
        if (pos == null || !ForgeEventFactory.doSpecialSpawn((EntityLiving) entity, world, pos.getX(), pos.getY(), pos.getZ(), null)) {
          ((EntityLiving) entity).onInitialSpawn(difficulty, null);
        }
      }
    }

    if (entity instanceof EntityWitherSkeleton) {
      if (Celeb.H31.isOn() && Math.random() < 0.25 && PersonalConfig.celebrateReformation.get()) {
        entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Math.random() < 0.1 ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
        ((EntityWitherSkeleton) entity).setDropChance(EntityEquipmentSlot.HEAD, 0.0F);
      } else if (Celeb.C06.isOn() && Math.random() < 0.25 && PersonalConfig.celebrateNicholas.get()) {
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Math.random() < 0.25 ? Items.LEATHER_BOOTS : Items.STICK));
      } else if (Math.random() < 0.1) {
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ModObject.itemDarkSteelSword.getItemNN()));
        ((EntityWitherSkeleton) entity).setDropChance(EntityEquipmentSlot.MAINHAND, 0.00001F);
      }
    }

    return entity;
  }

  public @Nonnull String getDisplayName() {
    String baseName = EnderIO.lang.localizeExact("entity." + getTranslationName() + ".name");
    if (baseName.trim().isEmpty()) {
      if (customName != null && !customName.trim().isEmpty()) {
        return NullHelper.notnullJ(customName, "private final field changed its value");
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

  public String getTranslationName() {
    return EntityList.getTranslationName(entityId);
  }

  public float getHealth() {
    if (entityNbt != null && entityNbt.hasKey("HealF")) {
      return NullHelper.notnullJ(entityNbt, "private final field changed its value").getFloat("HealF");
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
      NBTBase tag = NullHelper.notnullJ(entityNbt, "private final field changed its value").getTag("Attributes");
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
      int colorIdx = NullHelper.notnullJ(entityNbt, "private final field changed its value").getInteger("Color");
      if (colorIdx >= 0 && colorIdx <= 15) {
        return DyeColor.values()[15 - colorIdx];
      }
    }
    return null;
  }

  public @Nullable String getFluidName() {
    if (entityNbt != null && entityNbt.hasKey("FluidName")) {
      return NullHelper.notnullJ(entityNbt, "private final field changed its value").getString("FluidName");
    }
    return null;
  }

  public static void addToBlackList(ResourceLocation entityName) {
    blacklist.add(in -> entityName.equals(in));
  }

  public static void addToBlackList(Predicate<ResourceLocation> entityFilter) {
    blacklist.add(entityFilter);
  }

  public static void addToUnspawnableList(ResourceLocation entityName) {
    unspawnablelist.add(in -> entityName.equals(in));
  }

  public static void addToUnspawnableList(Predicate<ResourceLocation> entityFilter) {
    unspawnablelist.add(entityFilter);
  }

  private boolean isUnspawnable(ResourceLocation entityName) {
    return unspawnablelist.stream().anyMatch(elem -> elem.test(entityName));
  }

  public @Nonnull ResourceLocation getEntityName() {
    return entityId;
  }

  public boolean isSameType(Entity entity) {
    return entity != null && getEntityName().equals(EntityList.getKey(entity));
  }

  public boolean isSameType(CapturedMob other) {
    return other != null && getEntityName().equals(other.getEntityName());
  }

  @Override
  public String toString() {
    return "CapturedMob [" + "entityId=" + entityId + ", " + (customName != null ? "customName=" + customName + ", " : "")
        + (entityNbt != null ? "entityNbt=" + entityNbt + ", " : "") + "getDisplayName()=" + getDisplayName() + ", getHealth()=" + getHealth()
        + ", getMaxHealth()=" + getMaxHealth() + ", " + (getColor() != null ? "getColor()=" + getColor() + ", " : "")
        + (getFluidName() != null ? "getFluidName()=" + getFluidName() : "") + "]";
  }

  /*
   * Note: The Ender Dragon cannot be spawned as expected. All of its logic (moving, fighting, being hit, ...) is a special manager class, which is very
   * hardcoded to the specifics of the vanilla dragon fight.
   */
  public static @Nonnull NNList<CapturedMob> getSouls(List<ResourceLocation> list) {
    NNList<CapturedMob> result = new NNList<CapturedMob>();
    for (ResourceLocation mobName : list) {
      CapturedMob soul = create(mobName);
      if (soul != null && !DRAGON.equals(mobName)) {
        result.add(soul);
      }
    }
    return result;
  }

  public static @Nonnull NNList<CapturedMob> getAllSouls() {
    return getSouls(EntityUtil.getAllRegisteredMobNames());
  }

  public static void setBossesBlacklisted(boolean b) {
    bossesBlacklisted = b;
  }

}
