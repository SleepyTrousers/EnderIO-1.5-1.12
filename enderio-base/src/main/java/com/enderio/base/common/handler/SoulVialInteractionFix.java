package com.enderio.base.common.handler;

import com.enderio.base.EnderIO;
import com.enderio.base.common.item.EIOItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Fix for certain mobs that don't work with the soul vial.
 * So far the list includes:
 * - Donkey
 * - Mule
 * - Llama
 * - Villagers
 */
@Mod.EventBusSubscriber(modid = EnderIO.MODID)
public class SoulVialInteractionFix {
    @SubscribeEvent
    public static void onLivingInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getItemStack();
        if (stack.is(EIOItems.EMPTY_SOUL_VIAL.get())) {
            if (event.getTarget() instanceof AbstractChestedHorse || event.getTarget() instanceof Villager) {
                stack.getItem().interactLivingEntity(stack, event.getPlayer(), (LivingEntity) event.getTarget(), event.getHand());
            }
        }
    }
}
