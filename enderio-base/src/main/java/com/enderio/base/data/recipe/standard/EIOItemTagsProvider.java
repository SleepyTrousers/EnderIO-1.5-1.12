package com.enderio.base.data.recipe.standard;

import com.enderio.base.EnderIO;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EIOItemTagsProvider extends ItemTagsProvider{
    
    public EIOItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider,
            ExistingFileHelper existingFileHelper) {
        super(pGenerator, pBlockTagsProvider, EnderIO.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        //copy(EIOTags.Blocks.CLEAR_GLASS, EIOTags.Items.CLEAR_GLASS);
        //copy(EIOTags.Blocks.FUSED_QUARTZ, EIOTags.Items.FUSED_QUARTZ);
        tag(Tags.Items.DUSTS).addTag(EIOTags.Items.DUSTS_COAL).addTag(EIOTags.Items.DUSTS_LAPIS).addTag(EIOTags.Items.DUSTS_QUARTZ).addTag(EIOTags.Items.DUSTS_IRON).addTag(EIOTags.Items.DUSTS_GOLD).addTag(EIOTags.Items.DUSTS_COPPER).addTag(EIOTags.Items.DUSTS_TIN).addTag(EIOTags.Items.DUSTS_ENDER).addTag(EIOTags.Items.DUSTS_OBSIDIAN).addTag(EIOTags.Items.DUSTS_ARDITE).addTag(EIOTags.Items.DUSTS_COBALT);
        tag(EIOTags.Items.GEARS).addTag(EIOTags.Items.GEARS_WOOD).addTag(EIOTags.Items.GEARS_STONE).addTag(EIOTags.Items.GEARS_IRON).addTag(EIOTags.Items.GEARS_VIBRANT).addTag(EIOTags.Items.GEARS_ENERGIZED).addTag(EIOTags.Items.GEARS_DARK_STEEL);
    }
}
