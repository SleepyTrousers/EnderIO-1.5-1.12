package crazypants.enderio.conduit.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;

public class ItemItemConduit extends AbstractItemConduit {

    // private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
    // new ItemConduitSubtype(ModObject.itemItemConduit.name(), "enderio:itemItemConduit"),
    // new ItemConduitSubtype(ModObject.itemItemConduit.name() + "Empowered", "enderio:itemItemConduitAdvanced")
    // };
    private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
            new ItemConduitSubtype(ModObject.itemItemConduit.name(), "enderio:itemItemConduit") };

    public static ItemItemConduit create() {
        ItemItemConduit result = new ItemItemConduit();
        result.init();
        return result;
    }

    protected ItemItemConduit() {
        super(ModObject.itemItemConduit, subtypes);
    }

    @Override
    public Class<? extends IConduit> getBaseConduitType() {
        return IItemConduit.class;
    }

    @Override
    public IConduit createConduit(ItemStack item, EntityPlayer player) {
        return new ItemConduit(item.getItemDamage());
    }

    @Override
    public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
        return true;
    }
}
