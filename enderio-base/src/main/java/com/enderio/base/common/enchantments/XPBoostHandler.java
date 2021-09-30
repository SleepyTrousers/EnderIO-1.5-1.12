package com.enderio.base.common.enchantments;

import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.base.Throwables;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

@EventBusSubscriber
public class XPBoostHandler {
    private static final Method getExperiencePoints = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_6552_", Player.class);
    private static final @Nonnull String NBT_KEY = "enderio:xpboost";

    @SubscribeEvent
    public static void handleEntityKill(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Entity killer = event
            .getSource()
            .getDirectEntity();

        if (!entity.level.isClientSide && killer != null) {
            if (killer instanceof Player player) {
                scheduleXP(entity, getXPBoost(entity, player));
            } else if (killer instanceof Arrow arrow) {
                CompoundTag tag = killer.getPersistentData();
                if (tag.contains(NBT_KEY) && tag.getInt(NBT_KEY) >= 0) {
                    int level = tag.getInt(NBT_KEY);
                    scheduleXP(entity, getXPBoost(entity, (Player) arrow.getOwner(), level));
                }
            }
        }
    }

    @SubscribeEvent
    public static void handleArrowFire(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            arrow
                .getPersistentData()
                .putInt(NBT_KEY, getXPBoostLevel(arrow.getOwner()));
        }
    }

    @SubscribeEvent
    public static void handleBlockBreak(BlockEvent.BreakEvent event) {
        int level = getXPBoostLevel(event.getPlayer());

        if (level >= 0) {//TODO re add notnullhelper checks if needed
            final @Nonnull BlockState state = event.getState();
            final @Nonnull Level world = (Level) event.getWorld();
            final @Nonnull BlockPos pos = event.getPos();
            final int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, event
                .getPlayer()
                .getMainHandItem());
            final int xp = state
                .getBlock()
                .getExpDrop(state, world, pos, fortune, 0);
            if (xp > 0) {
                world.addFreshEntity(new ExperienceOrb(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, getXPBoost(xp, level)));
            }
        }
    }

    private static int getXPBoost(LivingEntity killed, Player player) {
        return getXPBoost(killed, player, getXPBoostLevel(player));
    }

    private static int getXPBoost(LivingEntity killed, Player player, int level) {
        if (level >= 0) {
            try {
                int xp = (Integer) getExperiencePoints.invoke(killed, player);
                return getXPBoost(xp, level);
            } catch (Exception e) {
                Throwables.propagate(e);
            }
        }
        return 0;
    }

    private static int getXPBoost(int xp, int level) {
        return Math.round(xp * ((float) Math.log10(level + 1) * 2));
    }

    private static int getXPBoostLevel(Entity player) {
        if (player == null || !(player instanceof Player) || player instanceof FakePlayer) {
            return -1;
        }
        ItemStack weapon = ((LivingEntity) player).getMainHandItem();
        if (weapon.isEmpty()) {
            return -1;
        }

        int result = -1;
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(weapon);
        for (Enchantment i : enchants.keySet()) {
            if (i == EIOEnchantments.XP_BOOST.get()) {
                result = enchants.get(i);
            } else if (i == Enchantments.SILK_TOUCH) {
                // No XP boost on silk touch
                return -1;
            }
        }
        return result;
    }

    private static void scheduleXP(Entity entity, int boost) {
        scheduleXP(entity.level, entity.getX(), entity.getY(), entity.getZ(), boost);
    }

    private static void scheduleXP(final Level world, final double x, final double y, final double z, final int boost) {
        if (boost <= 0) {
            return;
        } //TODO do we need a shedular? really not my cup of thee -Ferri_Arnus
        //		Scheduler.instance().schedule(20, new Runnable() {
        //			@Override
        //			public void run() {
        world.addFreshEntity(new ExperienceOrb(world, x, y, z, boost));
        //			}
        //		});
    }
}
