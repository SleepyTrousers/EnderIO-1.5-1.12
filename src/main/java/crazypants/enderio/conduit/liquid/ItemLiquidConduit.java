package crazypants.enderio.conduit.liquid;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.config.Config;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemLiquidConduit extends AbstractItemConduit implements IAdvancedTooltipProvider {

    private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
        new ItemConduitSubtype(ModObject.itemLiquidConduit.name(), "enderio:itemLiquidConduit"),
        new ItemConduitSubtype(ModObject.itemLiquidConduit.name() + "Advanced", "enderio:itemLiquidConduitAdvanced"),
        new ItemConduitSubtype(ModObject.itemLiquidConduit.name() + "Ender", "enderio:itemLiquidConduitEnder"),
        new ItemConduitSubtype(
                ModObject.itemLiquidConduit.name() + "CrystallineEnder", "enderio:itemLiquidConduitCrystallineEnder"),
        new ItemConduitSubtype(
                ModObject.itemLiquidConduit.name() + "CrystallinePinkSlimeEnder",
                "enderio:itemLiquidConduitCrystallinePinkSlimeEnder"),
        new ItemConduitSubtype(
                ModObject.itemLiquidConduit.name() + "MelodicEnder", "enderio:itemLiquidConduitMelodicEnder"),
        new ItemConduitSubtype(
                ModObject.itemLiquidConduit.name() + "StellarEnder", "enderio:itemLiquidConduitStellarEnder")
    };

    public static ItemLiquidConduit create() {
        ItemLiquidConduit result = new ItemLiquidConduit();
        result.init();
        return result;
    }

    protected ItemLiquidConduit() {
        super(ModObject.itemLiquidConduit, subtypes);
    }

    @Override
    public Class<? extends IConduit> getBaseConduitType() {
        return ILiquidConduit.class;
    }

    @Override
    public IConduit createConduit(ItemStack stack, EntityPlayer player) {
        switch (stack.getItemDamage()) {
            case 0:
                return new LiquidConduit();
            case 1:
                return new AdvancedLiquidConduit();
            case EnderLiquidConduit.METADATA:
                return new EnderLiquidConduit();
            case CrystallineEnderLiquidConduit.METADATA:
                return new CrystallineEnderLiquidConduit();
            case CrystallinePinkSlimeEnderLiquidConduit.METADATA:
                return new CrystallinePinkSlimeEnderLiquidConduit();
            case MelodicEnderLiquidConduit.METADATA:
                return new MelodicEnderLiquidConduit();
            case StellarEnderLiquidConduit.METADATA:
                return new StellarEnderLiquidConduit();
            default:
                throw new IllegalArgumentException("Unrecognized ender fluid conduit type: " + stack.getItemDamage());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        int extractRate;
        int maxIo;

        switch (itemstack.getItemDamage()) {
            case 0:
                extractRate = Config.fluidConduitExtractRate;
                maxIo = Config.fluidConduitMaxIoRate;
                break;

            case 1:
                extractRate = Config.advancedFluidConduitExtractRate;
                maxIo = Config.advancedFluidConduitMaxIoRate;
                break;

            case EnderLiquidConduit.METADATA:
                extractRate = EnderLiquidConduit.TYPE.getMaxExtractPerTick();
                maxIo = EnderLiquidConduit.TYPE.getMaxIoPerTick();
                break;

            case CrystallineEnderLiquidConduit.METADATA:
                extractRate = CrystallineEnderLiquidConduit.TYPE.getMaxExtractPerTick();
                maxIo = CrystallineEnderLiquidConduit.TYPE.getMaxIoPerTick();
                break;

            case CrystallinePinkSlimeEnderLiquidConduit.METADATA:
                extractRate = CrystallinePinkSlimeEnderLiquidConduit.TYPE.getMaxExtractPerTick();
                maxIo = CrystallinePinkSlimeEnderLiquidConduit.TYPE.getMaxIoPerTick();
                break;

            case MelodicEnderLiquidConduit.METADATA:
                extractRate = MelodicEnderLiquidConduit.TYPE.getMaxExtractPerTick();
                maxIo = MelodicEnderLiquidConduit.TYPE.getMaxIoPerTick();
                break;

            case StellarEnderLiquidConduit.METADATA:
                extractRate = StellarEnderLiquidConduit.TYPE.getMaxExtractPerTick();
                maxIo = StellarEnderLiquidConduit.TYPE.getMaxIoPerTick();
                break;

            default:
                throw new IllegalArgumentException(
                        "Unrecognized ender fluid conduit type: " + itemstack.getItemDamage());
        }

        String mbt = " " + EnderIO.lang.localize("fluid.millibucketsTick");
        list.add(EnderIO.lang.localize("itemLiquidConduit.tooltip.maxExtract") + " " + extractRate + mbt);
        list.add(EnderIO.lang.localize("itemLiquidConduit.tooltip.maxIo") + " " + maxIo + mbt);

        if (itemstack.getItemDamage() == 0) {
            SpecialTooltipHandler.addDetailedTooltipFromResources(list, "enderio.itemLiquidConduit");
        } else if (itemstack.getItemDamage() > 1) {
            SpecialTooltipHandler.addDetailedTooltipFromResources(list, "enderio.itemLiquidConduitEnder");
        }
    }

    @Override
    public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
        return true;
    }
}
