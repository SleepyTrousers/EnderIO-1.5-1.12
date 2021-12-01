package com.enderio.base.common.loot;

import com.enderio.base.EnderIO;
import com.enderio.base.common.item.spawner.BrokenSpawnerItem;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.config.base.BaseConfig;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BrokenSpawnerLootModifier extends LootModifier {
    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected BrokenSpawnerLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        BlockEntity entity = context.getParam(LootContextParams.BLOCK_ENTITY);
        if (entity instanceof SpawnerBlockEntity spawnerBlockEntity) {
            if (context.getParam(LootContextParams.TOOL).is(EIOTags.Items.BROKEN_SPAWNER_BLACKLIST)) {
                if (context.getRandom().nextFloat() < BaseConfig.COMMON.BLOCKS.BROKEN_SPAWNER_DROP_CHANCE.get()) {
                    BaseSpawner spawner = spawnerBlockEntity.getSpawner();
                    ItemStack brokenSpawner = BrokenSpawnerItem.forType(spawner.getEntityId(context.getLevel(), entity.getBlockPos()));
                    return Lists.newArrayList(brokenSpawner);
                }
            }
        }

        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<BrokenSpawnerLootModifier> {
        @Override
        public BrokenSpawnerLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            return new BrokenSpawnerLootModifier(ailootcondition);
        }

        @Override
        public JsonObject write(BrokenSpawnerLootModifier instance) {
            return makeConditions(instance.conditions);
        }
    }

    @SubscribeEvent
    public static void register(@Nonnull RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().register(new Serializer().setRegistryName(EnderIO.loc("broken_spawner")));
    }
}
