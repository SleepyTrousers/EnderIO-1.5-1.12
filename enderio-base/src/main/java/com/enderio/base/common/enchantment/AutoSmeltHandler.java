package com.enderio.base.common.enchantment;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

@EventBusSubscriber
public class AutoSmeltHandler {

    @SubscribeEvent
    public static void handleBlockBreak(BlockEvent.BreakEvent event) {
        // Checks if running on server and enchant is on tool
        if (!event.getWorld().isClientSide()
            && EnchantmentHelper.getItemEnchantmentLevel(EIOEnchantments.AUTO_SMELT.get(), event.getPlayer().getMainHandItem()) > 0) {
            ServerLevel serverWorld = ((ServerLevel) event.getWorld()); // Casts IWorld to ServerWorld
            LootContext.Builder lootcontext$builder = (new LootContext.Builder(serverWorld)
                .withRandom(serverWorld.random)
                .withParameter(LootContextParams.ORIGIN, new Vec3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))
                .withParameter(LootContextParams.TOOL, event.getPlayer().getMainHandItem())); // Makes lootcontext
            // to calculate drops
            List<ItemStack> drops = event.getState().getDrops(lootcontext$builder); // Calculates drops
            for (ItemStack item : drops) { // Iteration
                ItemStack stack = serverWorld
                    .getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(item), serverWorld)
                    .map((r) -> r.assemble(new SimpleContainer(item)))
                    .filter(itemStack -> !itemStack.isEmpty())
                    .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, item.getCount() * itemStack.getCount()))
                    .orElse(item); // Recipe as var
                Containers.dropItemStack(event.getPlayer().level, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
            }
            event.getPlayer().level.removeBlock(event.getPos(), false); // Breaks block
            event.setResult(Event.Result.DENY);
        }
    }
}
