package com.tterrag.registrate.providers;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.LogicalSide;

public class RegistrateItemModelProvider extends ItemModelProvider implements RegistrateProvider {
    
    private final AbstractRegistrate<?> parent;

    public RegistrateItemModelProvider(AbstractRegistrate<?> parent, DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, parent.getModid(), existingFileHelper);
        this.parent = parent;
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.CLIENT;
    }
    
    @Override
    protected void registerModels() {
        parent.genData(ProviderType.ITEM_MODEL, this);
    }
    
    @Override
    public String getName() {
        return "Item models";
    }
    
    public String modid(NonNullSupplier<? extends ItemLike> item) {
        return item.get().asItem().getRegistryName().getNamespace();
    }
    
    public String name(NonNullSupplier<? extends ItemLike> item) {
        return item.get().asItem().getRegistryName().getPath();
    }
    
    public ResourceLocation itemTexture(NonNullSupplier<? extends ItemLike> item) {
        return modLoc("item/" + name(item));
    }
    
    public ItemModelBuilder blockItem(NonNullSupplier<? extends ItemLike> block) {
        return blockItem(block, "");
    }
    
    public ItemModelBuilder blockItem(NonNullSupplier<? extends ItemLike> block, String suffix) {
        return withExistingParent(name(block), new ResourceLocation(modid(block), "block/" + name(block) + suffix));
    }

    public ItemModelBuilder blockWithInventoryModel(NonNullSupplier<? extends ItemLike> block) {
        return withExistingParent(name(block), new ResourceLocation(modid(block), "block/" + name(block) + "_inventory"));
    }
    
    public ItemModelBuilder blockSprite(NonNullSupplier<? extends ItemLike> block) {
        return blockSprite(block, modLoc("block/" + name(block)));
    }
    
    public ItemModelBuilder blockSprite(NonNullSupplier<? extends ItemLike> block, ResourceLocation texture) {
        return generated(() -> block.get().asItem(), texture);
    }
    
    public ItemModelBuilder generated(NonNullSupplier<? extends ItemLike> item) {
        return generated(item, itemTexture(item));
    }

    public ItemModelBuilder generated(NonNullSupplier<? extends ItemLike> item, ResourceLocation... layers) {
        ItemModelBuilder ret = getBuilder(name(item)).parent(new UncheckedModelFile("item/generated"));
        for (int i = 0; i < layers.length; i++) {
            ret = ret.texture("layer" + i, layers[i]);
        }
        return ret;
    }
    
    public ItemModelBuilder handheld(NonNullSupplier<? extends ItemLike> item) {
        return handheld(item, itemTexture(item));
    }
    
    public ItemModelBuilder handheld(NonNullSupplier<? extends ItemLike> item, ResourceLocation texture) {
        return withExistingParent(name(item), "item/handheld").texture("layer0", texture);
    }
}
