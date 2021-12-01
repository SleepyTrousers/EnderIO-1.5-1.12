package com.enderio.base.common.loot;

import com.enderio.base.EnderIO;
import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.item.EIOItems;
import com.enderio.base.common.util.CapacitorUtil;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapacitorLootModifier extends LootModifier {
    /**
     * The minimum base value
     */
    private final float min;
    /**
     * The maximum base value
     */
    private final float max;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected CapacitorLootModifier(LootItemCondition[] conditionsIn, float min, float max) {
        super(conditionsIn);
        this.min = min;
        this.max = max;
    }

    /**
     * Makes a loot capacitor with random stats and adds it to the loot.
     */
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        ItemStack capacitor = new ItemStack(EIOItems.LOOT_CAPACITOR.get());
        capacitor.getCapability(EIOCapabilities.CAPACITOR).ifPresent(cap -> {
            cap.setBase(UniformGenerator.between(min, max).getFloat(context));
            cap.addNewSpecialization(CapacitorUtil.getRandomType(), UniformGenerator.between(0.0F, 4.5F).getFloat(context));
        });
        generatedLoot.add(capacitor);
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<CapacitorLootModifier> {

        @Override
        public CapacitorLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            float min = GsonHelper.getAsFloat(object, "min");
            float max = GsonHelper.getAsFloat(object, "max");
            return new CapacitorLootModifier(ailootcondition, min, max);
        }

        @Override
        public JsonObject write(CapacitorLootModifier instance) {
            JsonObject obj = this.makeConditions(instance.conditions);
            obj.addProperty("min", instance.min);
            obj.addProperty("max", instance.max);
            return obj;
        }

    }

    @SubscribeEvent
    public static void register(@Nonnull RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().register(new Serializer().setRegistryName(new ResourceLocation(EnderIO.DOMAIN, "capacitor_loot")));
    }

}
