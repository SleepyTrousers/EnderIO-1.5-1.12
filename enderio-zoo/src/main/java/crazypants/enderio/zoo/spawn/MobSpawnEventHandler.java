package crazypants.enderio.zoo.spawn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crazypants.enderio.zoo.config.Config;
import crazypants.enderio.zoo.entity.EntityDireSlime;
import crazypants.enderio.zoo.entity.IEnderZooMob;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class MobSpawnEventHandler {

  private static final String APPLIED_KEY = "ezModsApp";

  private static List<EntityLivingBase> toApplyEZ = new ArrayList<EntityLivingBase>();
  private static List<EntityLivingBase> toApplyOthers = new ArrayList<EntityLivingBase>();

  private Map<EnumDifficulty, Double> ezHealthMods = new HashMap<EnumDifficulty, Double>();
  private Map<EnumDifficulty, Double> ezAttackMods = new HashMap<EnumDifficulty, Double>();

  private Map<EnumDifficulty, Double> otherHealthMods = new HashMap<EnumDifficulty, Double>();
  private Map<EnumDifficulty, Double> otherAttackMods = new HashMap<EnumDifficulty, Double>();

  public MobSpawnEventHandler() {

    ezHealthMods.put(EnumDifficulty.PEACEFUL, 1d);
    ezHealthMods.put(EnumDifficulty.EASY, Config.enderZooEasyHealthModifier);
    ezHealthMods.put(EnumDifficulty.NORMAL, Config.enderZooNormalHealthModifier);
    ezHealthMods.put(EnumDifficulty.HARD, Config.enderZooHardHealthModifier);
    ezAttackMods.put(EnumDifficulty.PEACEFUL, 1d);
    ezAttackMods.put(EnumDifficulty.EASY, Config.enderZooEasyAttackModifier);
    ezAttackMods.put(EnumDifficulty.NORMAL, Config.enderZooNormalAttackModifier);
    ezAttackMods.put(EnumDifficulty.HARD, Config.enderZooHardAttackModifier);

    otherHealthMods.put(EnumDifficulty.PEACEFUL, 1d);
    otherHealthMods.put(EnumDifficulty.EASY, Config.globalEasyHealthModifier);
    otherHealthMods.put(EnumDifficulty.NORMAL, Config.globalNormalHealthModifier);
    otherHealthMods.put(EnumDifficulty.HARD, Config.globalHardHealthModifier);
    otherAttackMods.put(EnumDifficulty.PEACEFUL, 1d);
    otherAttackMods.put(EnumDifficulty.EASY, Config.globalEasyAttackModifier);
    otherAttackMods.put(EnumDifficulty.NORMAL, Config.globalNormalAttackModifier);
    otherAttackMods.put(EnumDifficulty.HARD, Config.globalHardAttackModifier);

  }

  public void init() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void onCheckSpawn(CheckSpawn evt) {
    if (evt.getEntityLiving() == null) {
      return;
    }
    String name = EntityList.getEntityString(evt.getEntityLiving());
    if (name == null) {
      return;
    }
    for (ISpawnEntry ent : MobSpawns.instance.getEntries()) {
      if (name.equals(ent.getMobName())) {
        if (!ent.canSpawnInDimension(evt.getWorld())) {
          evt.setResult(Result.DENY);
        }
      }
    }

  }

  @SubscribeEvent
  public void onEntityJoinWorld(EntityJoinWorldEvent evt) {
    if (evt.getWorld() == null || evt.getWorld().isRemote) {
      return;
    }
    if (Config.enderZooDifficultyModifierEnabled && evt.getEntity() instanceof IEnderZooMob) {
      EntityLivingBase ent = (EntityLivingBase) evt.getEntity();
      if (!ent.getEntityData().getBoolean(APPLIED_KEY)) {
        toApplyEZ.add(ent);
      }
    } else if (Config.globalDifficultyModifierEnabled && evt.getEntity() instanceof IMob && evt.getEntity() instanceof EntityLivingBase) {
      EntityLivingBase ent = (EntityLivingBase) evt.getEntity();
      if (!ent.getEntityData().getBoolean(APPLIED_KEY)) {
        toApplyOthers.add(ent);
      }
    }
  }

  @SubscribeEvent
  public void onServerTick(ServerTickEvent evt) {
    if (evt.phase != Phase.END) {
      return;
    }
   
    for (EntityLivingBase ent : toApplyEZ) {
      if (!ent.isDead && ent.getEntityWorld() != null) {
        applyEnderZooModifiers(ent, ent.getEntityWorld());
        ent.getEntityData().setBoolean(APPLIED_KEY, true);
      }
    }
    toApplyEZ.clear();

    for (EntityLivingBase ent : toApplyOthers) {
      if (!ent.isDead && ent.getEntityWorld() != null) {
        applyGlobalModifiers(ent, ent.getEntityWorld());
        ent.getEntityData().setBoolean(APPLIED_KEY, true);
      }
    }
    toApplyOthers.clear();
  }

  private void applyGlobalModifiers(EntityLivingBase entity, World world) {
    if (world == null || world.getDifficulty() == null) {
      return;
    }
    double attackModifier = otherAttackMods.get(world.getDifficulty());
    double healthModifier = otherHealthMods.get(world.getDifficulty());
    if (attackModifier != 1) {
      adjustBaseAttack(entity, attackModifier);
    }
    if (healthModifier != 1) {
      addjustBaseHealth(entity, healthModifier);
    }
  }

  private void applyEnderZooModifiers(EntityLivingBase entity, World world) {
    if (world == null || world.getDifficulty() == null) {
      return;
    }
    double attackModifier = ezAttackMods.get(world.getDifficulty());
    double healthModifier = ezHealthMods.get(world.getDifficulty());
    if (attackModifier != 1) {
      adjustBaseAttack(entity, attackModifier);
    }
    if (healthModifier != 1) {
      addjustBaseHealth(entity, healthModifier);
    }
  }

  protected void addjustBaseHealth(EntityLivingBase ent, double healthModifier) {
    IAttributeInstance att = ent.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
    if (att == null) {
      return;
    }
    double curValue = att.getBaseValue();
    // only change in incs of 2 so we dont have 1/2 hearts
    double newValue = (curValue * healthModifier) / 2;
    if (healthModifier >= 1) {
      newValue = Math.ceil(newValue);
    } else {
      newValue = Math.floor(newValue);
    }
    newValue = Math.floor(newValue * 2.0);
    if (newValue < 2) {
      newValue = curValue;
    }
    att.setBaseValue(newValue);
    ent.setHealth((float) newValue);
    // System.out.println("MobSpawnEventHandler.addjustBaseHealth: Base health
    // changed from: " + curValue + " to " + newValue);
  }

  protected void adjustBaseAttack(EntityLivingBase ent, double attackModifier) {
    IAttributeInstance att = ent.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
    if (att == null) {
      return;
    }
    double curValue = att.getBaseValue();
    double newValue = curValue * attackModifier;
    att.setBaseValue(newValue);
    // System.out.println("MobSpawnEventHandler.adjustBaseAttack: base attack
    // changed from " + curValue + " to " + newValue);
  }

  @SubscribeEvent
  public void onBlockHarvest(HarvestDropsEvent event) {

    if (!Config.direSlimeEnabled || event.isCanceled() || event.getWorld() == null || event.getWorld().isRemote) {
      return;
    }
    if (event.getHarvester() == null || event.getHarvester().capabilities.isCreativeMode) {
      return;
    }

    if (!(event.getState().getBlock() instanceof BlockDirt || event.getState().getBlock() instanceof BlockGrass)) {
      return;
    }

    if (!isToolEffective(event.getState(), event.getHarvester().getHeldItemMainhand())) {

      if (Config.direSlimeChance < event.getWorld().rand.nextFloat()) {
        return;
      }

      EntityDireSlime direSlime = new EntityDireSlime(event.getWorld());
      direSlime.setPosition(event.getPos().getX() + 0.5, event.getPos().getY() + 0.0, event.getPos().getZ() + 0.5);
      event.getWorld().spawnEntity(direSlime);
      direSlime.playLivingSound();
      for (ItemStack drop : event.getDrops()) {
        if (drop != null && drop.getItem() != null && drop.getItem() == Item.getItemFromBlock(Blocks.DIRT)) {
          if (drop.getCount() > 1) {
            drop.shrink(1);
          } else if (event.getDrops().size() == 1) {
            event.getDrops().clear();
          } else {
            event.getDrops().remove(drop);
          }
          return;
        }
      }
    }
  }

  public static boolean isToolEffective(IBlockState state, ItemStack stack) {
    if(stack.isEmpty()) { //don't spawn them with an empty hand, helps newly spawned players
      return true;
    }
    for (String type : stack.getItem().getToolClasses(stack)) {
      // the "shovel" check is needed for modded blocks that extend dirt/grass but refuse to acknowledge any tool as effective,
      // e.g. TiC
      if (state.getBlock().isToolEffective(type, state) || "shovel".equals(type)) {
        return true;
      }
    }
    return false;
  }

}
