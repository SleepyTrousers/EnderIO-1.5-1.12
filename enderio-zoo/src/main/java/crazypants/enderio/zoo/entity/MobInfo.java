package crazypants.enderio.zoo.entity;

import crazypants.enderio.zoo.config.Config;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public enum MobInfo {
  
  ENDERMINY(EntityEnderminy.class, EntityEnderminy.NAME, EntityEnderminy.EGG_BG_COL, EntityEnderminy.EGG_FG_COL, Config.enderminyEnabled,
      Config.enderminyHealth, Config.enderminyAttackDamage, Config.enderminyId),
  CONCUSSION_CREEPER(EntityConcussionCreeper.class, EntityConcussionCreeper.NAME, EntityConcussionCreeper.EGG_BG_COL, EntityConcussionCreeper.EGG_FG_COL,
      Config.concussionCreeperEnabled, Config.concussionCreeperHealth, 4, Config.concussionCreeperId),
  FALLEN_KNIGHT(EntityFallenKnight.class, EntityFallenKnight.NAME, EntityFallenKnight.EGG_BG_COL, EntityFallenKnight.EGG_FG_COL, Config.fallenKnightEnabled,
      Config.fallenKnightHealth, Config.fallenKnightBaseDamage,Config.fallenKnightId),
  FALLEN_MOUNT(EntityFallenMount.class, EntityFallenMount.NAME, EntityFallenMount.EGG_BG_COL, EntityFallenMount.EGG_FG_COL, Config.fallenMountEnabled,
      Config.fallenMountHealth, Config.fallenMountBaseAttackDamage, Config.fallenMountId),
  WITHER_WITCH(EntityWitherWitch.class, EntityWitherWitch.NAME, EntityWitherWitch.EGG_BG_COL, EntityWitherWitch.EGG_FG_COL, Config.witherWitchEnabled,
      Config.witherWitchHealth, 4, Config.witherWitchId),
  WITHER_CAT(EntityWitherCat.class, EntityWitherCat.NAME, EntityWitherCat.EGG_BG_COL, EntityWitherCat.EGG_FG_COL, Config.witherCatEnabled,
      Config.witherCatHealth, Config.witherCatAttackDamage, Config.witherCatId),
  DIRE_WOLF(EntityDireWolf.class, EntityDireWolf.NAME, EntityDireWolf.EGG_BG_COL, EntityDireWolf.EGG_FG_COL, Config.direWolfEnabled, Config.direWolfHealth,
      Config.direWolfAttackDamage, Config.direWolfId),
  DIRE_SLIME(EntityDireSlime.class, EntityDireSlime.NAME, EntityDireSlime.EGG_BG_COL, EntityDireSlime.EGG_FG_COL, Config.direSlimeEnabled,
      Config.direSlimeHealth, Config.direSlimeAttackDamage, Config.direSlimeId),
  OWL(EntityOwl.class, EntityOwl.NAME, EntityOwl.EGG_BG_COL, EntityOwl.EGG_FG_COL, Config.owlEnabled,
      Config.owlHealth, Config.owlAttachDamage, Config.owlId);

  
  public static boolean isDisabled(Class<? extends EntityLiving> clz) {
    if(clz == null) {
      return false;
    }    
    for(MobInfo info : values()) {
      if(clz == info.getClz() && !info.isEnabled()) {
        return true;
      }
    }    
    return false;
  }
  
  final Class<? extends EntityLiving> clz;
  final String name;
  final int bgCol;
  final int fgCol;
  final boolean enabled;
  final double maxHealth;
  final double attackDamage;
  final int entityId;

  private MobInfo(Class<? extends EntityLiving> clz, String name, int bgCol, int fgCol, boolean enabled, double baseHealth, double baseAttack, int entityId) {
    this.clz = clz;
    this.name = name;
    this.bgCol = bgCol;
    this.fgCol = fgCol;
    this.enabled = enabled;
    this.entityId = entityId;
    maxHealth = baseHealth;
    attackDamage = baseAttack;
  }

  public Class<? extends EntityLiving> getClz() {
    return clz;
  }

  public String getName() {
    return name;
  }

  public int getEggBackgroundColor() {
    return bgCol;
  }

  public int getEggForegroundColor() {
    return fgCol;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void applyAttributes(EntityLivingBase entity) {
    entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
    IAttributeInstance ai = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
    if(ai == null) {
      entity.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      ai = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
    }
    ai.setBaseValue(attackDamage);
  }

  public int getEntityId() {
    return entityId;
  }

}
