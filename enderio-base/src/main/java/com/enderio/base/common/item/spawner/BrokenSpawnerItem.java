package com.enderio.base.common.item.spawner;

import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.base.common.item.EIOItems;
import com.enderio.base.common.capability.entity.EntityStorage;
import com.enderio.base.common.capability.entity.IEntityStorage;
import com.enderio.base.common.util.EntityCaptureUtils;
import com.enderio.core.common.capability.IMultiCapabilityItem;
import com.enderio.core.common.capability.MultiCapabilityProvider;
import com.enderio.core.common.util.EntityUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BrokenSpawnerItem extends Item implements IMultiCapabilityItem {
    public BrokenSpawnerItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack forType(ResourceLocation type) {
        ItemStack brokenSpawner = new ItemStack(EIOItems.BROKEN_SPAWNER.get());
        setEntityType(brokenSpawner, type);
        return brokenSpawner;
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab pCategory, @Nonnull NonNullList<ItemStack> pItems) {
        if (pCategory == getItemCategory()) {
            pItems.add(new ItemStack(this));
        } else if (pCategory == EIOCreativeTabs.SOULS) {
            // Register for every mob that can be captured.
            for (ResourceLocation entity : EntityCaptureUtils.getCapturableEntities()) {
                pItems.add(forType(entity));
            }
        }
    }

    @Override
    public Collection<CreativeModeTab> getCreativeTabs() {
        return Arrays.asList(getItemCategory(), EIOCreativeTabs.SOULS);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable Level pLevel, @Nonnull List<Component> pTooltipComponents,
        @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        getEntityType(pStack).ifPresent(type -> pTooltipComponents.add(new TranslatableComponent(EntityUtil.getEntityDescriptionId(type))));
    }

    // region Entity Storage

    public static Optional<ResourceLocation> getEntityType(ItemStack stack) {
        return stack.getCapability(EIOCapabilities.ENTITY_STORAGE).map(IEntityStorage::getEntityType).orElse(Optional.empty());
    }

    private static void setEntityType(ItemStack stack, ResourceLocation entityType) {
        stack.getCapability(EIOCapabilities.ENTITY_STORAGE).ifPresent(storage -> storage.setEntityType(entityType));
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSerialized(EIOCapabilities.ENTITY_STORAGE, LazyOptional.of(EntityStorage::new));
        return provider;
    }

    // endregion
}
