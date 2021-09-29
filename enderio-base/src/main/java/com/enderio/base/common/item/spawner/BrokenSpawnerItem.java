package com.enderio.base.common.item.spawner;

import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.base.common.item.EIOItems;
import com.enderio.base.common.util.EntityCaptureUtils;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BrokenSpawnerItem extends Item {
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
        pTooltipComponents.add(new TranslatableComponent(EntityUtil.getEntityDescriptionId(getEntityType(pStack))));
    }

    // region Entity Type storage

    public static ResourceLocation getEntityType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("id"))
            return new ResourceLocation(tag.getString("id"));
        return new ResourceLocation("minecraft", "pig");
    }

    private static void setEntityType(ItemStack stack, ResourceLocation entityType) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("id", entityType.toString());
    }

    // endregion
}
