package com.enderio.base.data.model.item;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;

public class ItemModelUtils {
    public static ItemModelBuilder fakeBlockModel(DataGenContext<Item, ? extends Item> ctx, RegistrateItemModelProvider prov) {
        return prov.withExistingParent(prov.name(ctx), prov.mcLoc("block/cube_all")).texture("all", prov.itemTexture(ctx));
    }

    public static ItemModelBuilder mimicItem(DataGenContext<Item, ? extends Item> ctx, ItemEntry<? extends Item> item, RegistrateItemModelProvider prov) {
        return prov.generated(ctx, prov.itemTexture(item));
    }
}
