package crazypants.enderio.conduit.power.endergy;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.power.ICapacitor;

public class ItemPowerConduitEndergy extends ItemPowerConduit {

    private static String PREFIX;
    private static String POSTFIX;

    static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
            new ItemConduitSubtype(ModObject.itemPowerConduitEndergy.name() + "Crude", "enderio:itemPowerConduitCrude"),
            new ItemConduitSubtype(ModObject.itemPowerConduitEndergy.name() + "Iron", "enderio:itemPowerConduitIron"),
            new ItemConduitSubtype(
                    ModObject.itemPowerConduitEndergy.name() + "Aluminum",
                    "enderio:itemPowerConduitAluminum"),
            new ItemConduitSubtype(ModObject.itemPowerConduitEndergy.name() + "Gold", "enderio:itemPowerConduitGold"),
            new ItemConduitSubtype(
                    ModObject.itemPowerConduitEndergy.name() + "Copper",
                    "enderio:itemPowerConduitCopper"),
            new ItemConduitSubtype(
                    ModObject.itemPowerConduitEndergy.name() + "Silver",
                    "enderio:itemPowerConduitSilver"),
            new ItemConduitSubtype(
                    ModObject.itemPowerConduitEndergy.name() + "Electrum",
                    "enderio:itemPowerConduitElectrum"),
            new ItemConduitSubtype(
                    ModObject.itemPowerConduitEndergy.name() + "EnergeticSilver",
                    "enderio:itemPowerConduitEnergeticSilver"),
            new ItemConduitSubtype(
                    ModObject.itemPowerConduitEndergy.name() + "Crystalline",
                    "enderio:itemPowerConduitCrystalline"),
            new ItemConduitSubtype(
                    ModObject.itemPowerConduitEndergy.name() + "CrystallinePinkSlime",
                    "enderio:itemPowerConduitCrystallinePinkSlime"),
            new ItemConduitSubtype(
                    ModObject.itemPowerConduitEndergy.name() + "Melodic",
                    "enderio:itemPowerConduitMelodic"),
            new ItemConduitSubtype(
                    ModObject.itemPowerConduitEndergy.name() + "Stellar",
                    "enderio:itemPowerConduitStellar"), };

    public static ItemPowerConduitEndergy create() {
        ItemPowerConduitEndergy result = new ItemPowerConduitEndergy();
        result.init();
        return result;
    }

    protected ItemPowerConduitEndergy() {
        super(ModObject.itemPowerConduitEndergy, subtypes);
    }

    @Override
    public Class<? extends IConduit> getBaseConduitType() {
        return IPowerConduit.class;
    }

    @Override
    public IConduit createConduit(ItemStack stack, EntityPlayer player) {
        return new PowerConduitEndergy(stack.getItemDamage());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        if (PREFIX == null) {
            POSTFIX = " " + PowerDisplayUtil.abrevation() + PowerDisplayUtil.perTickStr();
            PREFIX = EnderIO.lang.localize("power.maxOutput") + " ";
        }
        ICapacitor cap = PowerConduitEndergy.getCapacitors()[itemStack.getItemDamage()];
        list.add(PREFIX + PowerDisplayUtil.formatPower(cap.getMaxEnergyExtracted()) + POSTFIX);
    }

    @Override
    public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
        return true;
    }
}
