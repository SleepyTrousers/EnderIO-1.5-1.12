package crazypants.enderio.conduit.redstone;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;

public class ItemRedstoneConduit extends AbstractItemConduit {

    private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
            new ItemConduitSubtype(ModObject.itemRedstoneConduit.name(), "enderio:itemRedstoneConduit"),
            new ItemConduitSubtype(ModObject.itemRedstoneConduit.name() + "Switch", "enderio:itemRedstoneSwitch"),
            new ItemConduitSubtype(
                    ModObject.itemRedstoneConduit.name() + "Insulated",
                    "enderio:itemRedstoneInsulatedConduit") };

    public static ItemRedstoneConduit create() {
        ItemRedstoneConduit result = new ItemRedstoneConduit();
        result.init();
        return result;
    }

    protected ItemRedstoneConduit() {
        super(ModObject.itemRedstoneConduit, subtypes);
    }

    @Override
    public Class<? extends IConduit> getBaseConduitType() {
        return IRedstoneConduit.class;
    }

    @Override
    public IConduit createConduit(ItemStack stack, EntityPlayer player) {
        if (stack.getItemDamage() == 0) {
            return new RedstoneConduit();
        }
        if (stack.getItemDamage() == 1) {
            return new RedstoneSwitch();
        } else {
            return new InsulatedRedstoneConduit();
        }
    }

    @Override
    public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
        return true;
    }
}
