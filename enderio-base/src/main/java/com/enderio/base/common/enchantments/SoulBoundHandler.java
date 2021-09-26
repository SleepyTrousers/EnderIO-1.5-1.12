package com.enderio.base.common.enchantments;

import java.util.ArrayList;
import java.util.Iterator;

import com.enderio.base.EIOEnchantments;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.RespawnEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

//TODO
@EventBusSubscriber
public class SoulBoundHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void deathHandler(LivingDropsEvent event) {
        // TODO: Integration with EnderIO graves system.
        if (event.getEntityLiving() == null || event.getEntityLiving() instanceof FakePlayer || event.isCanceled()) {
            return;
        }
        if (event.getEntityLiving().level
            .getGameRules()
            .getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        if (event.getEntityLiving() instanceof Player player) {
            Iterator<ItemEntity> iter = event
                .getDrops()
                .iterator();
            while (iter.hasNext()) {
                ItemEntity ei = iter.next();
                ItemStack item = ei.getItem();
                if (isSoulBound(item)) {
                    if (player.addItem(item)) {
                        iter.remove();
                    }
                    drops.add(item);
                }
            }
            if (!drops.isEmpty()) {
                return;
            }
            drops.forEach((item) -> player.addItem(item));
        }
    }

    public static void reviveHandler(RespawnEvent event) {

        if (!event
            .getOldPlayer()
            .isDeadOrDying()) {
            return;
        }
        event
            .getOldPlayer()
            .getInventory().items.forEach((item -> event
                .getNewPlayer()
                .addItem(item)));
    }

    public static boolean isSoulBound(ItemStack item) {
        return EnchantmentHelper.getItemEnchantmentLevel(EIOEnchantments.SOULBOUND.get(), item) > -1;
    }
}
