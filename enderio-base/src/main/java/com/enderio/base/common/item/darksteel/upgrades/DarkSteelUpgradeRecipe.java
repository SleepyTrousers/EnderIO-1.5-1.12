package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.base.EnderIO;
import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.capability.darksteel.DarkSteelUpgradeable;
import com.enderio.base.common.capability.darksteel.IDarkSteelUpgradable;
import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = EnderIO.MODID)
public class DarkSteelUpgradeRecipe extends UpgradeRecipe {

    // region Register recipe

    public static final RecipeSerializer<DarkSteelUpgradeRecipe> SERIALIZER = new Serializer();

    @SubscribeEvent
    public static void registerSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        DarkSteelUpgradeRecipe.SERIALIZER.setRegistryName(new ResourceLocation(EnderIO.MODID, "dark_steel_upgrade"));
        event.getRegistry().register(SERIALIZER);
    }

    // endregion

    public DarkSteelUpgradeRecipe(ResourceLocation pRecipeId) {
        super(pRecipeId, Ingredient.EMPTY,Ingredient.EMPTY,ItemStack.EMPTY);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(Container pInv, Level pLevel) {
        Optional<IDarkSteelUpgradable> target = getUpgradableFromItem(pInv.getItem(0));
        Optional<IDarkSteelUpgrade> upgrade = getUpgradeFromItem(pInv.getItem(1));
        return target.map(upgradable -> upgrade.map(upgradable::canApplyUpgrade).orElse(false)).orElse(false);
    }

    @Override
    public ItemStack assemble(Container pInv) {
        ItemStack resultItem = pInv.getItem(0).copy();
        Optional<IDarkSteelUpgradable> target = getUpgradableFromItem(resultItem);
        Optional<IDarkSteelUpgrade> upgrade = getUpgradeFromItem(pInv.getItem(1));
        return target.map(upgradable -> upgrade.map(up -> DarkSteelUpgradeable.addUpgrade(resultItem, up)).orElse(ItemStack.EMPTY)).orElse(ItemStack.EMPTY);
    }

    private Optional<IDarkSteelUpgradable> getUpgradableFromItem(ItemStack item) {
        return item.getCapability(EIOCapabilities.DARK_STEEL_UPGRADABLE).resolve();
    }

    private Optional<IDarkSteelUpgrade> getUpgradeFromItem(ItemStack item) {
        return DarkSteelUpgradeRegistry.instance().readUpgradeFromStack(item);
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<DarkSteelUpgradeRecipe> {

        @Override
        public DarkSteelUpgradeRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            return new DarkSteelUpgradeRecipe(pRecipeId);
        }

        @Override
        public DarkSteelUpgradeRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            return new DarkSteelUpgradeRecipe(pRecipeId);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, DarkSteelUpgradeRecipe pRecipe) {
        }

    }
}

