package com.tterrag.registrate.providers;

import java.nio.file.Path;

import com.tterrag.registrate.AbstractRegistrate;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.LogicalSide;

public class RegistrateTagsProvider<T> extends TagsProvider<T> implements RegistrateProvider {

    private final AbstractRegistrate<?> owner;
    private final ProviderType<? extends RegistrateTagsProvider<T>> type;
    private final String name;

    public RegistrateTagsProvider(AbstractRegistrate<?> owner, ProviderType<? extends RegistrateTagsProvider<T>> type, String name, DataGenerator generatorIn, Registry<T> registryIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, registryIn, owner.getModid(), existingFileHelper);
        this.owner = owner;
        this.type = type;
        this.name = name;
    }

    protected Path getPath(ResourceLocation id) {
        return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/" + name + "/" + id.getPath() + ".json");
    }

    public String getName() {
        return "Tags (" + name + ")";
    }

    @Override
    protected void addTags() {
        owner.genData(type, this);
    }

    @Override
    public LogicalSide getSide() {
        return LogicalSide.SERVER;
    }

    @Override
    public TagAppender<T> tag(Tag.Named<T> tag) { return super.tag(tag); }

    @Override
    public Tag.Builder getOrCreateRawBuilder(Tag.Named<T> tag) { return super.getOrCreateRawBuilder(tag); }
}
