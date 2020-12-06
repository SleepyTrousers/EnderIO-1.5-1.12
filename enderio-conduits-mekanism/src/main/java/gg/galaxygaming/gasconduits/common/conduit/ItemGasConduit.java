package gg.galaxygaming.gasconduits.common.conduit;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitBuilder;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduits.conduit.AbstractItemConduit;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import gg.galaxygaming.gasconduits.GasConduitsConstants;
import gg.galaxygaming.gasconduits.common.conduit.advanced.AdvancedGasConduit;
import gg.galaxygaming.gasconduits.common.conduit.advanced.AdvancedGasConduitRenderer;
import gg.galaxygaming.gasconduits.common.conduit.basic.GasConduit;
import gg.galaxygaming.gasconduits.common.conduit.basic.GasConduitRenderer;
import gg.galaxygaming.gasconduits.common.conduit.ender.EnderGasConduit;
import gg.galaxygaming.gasconduits.common.conduit.ender.EnderGasConduitRenderer;
import gg.galaxygaming.gasconduits.common.config.GasConduitConfig;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGasConduit extends AbstractItemConduit implements IAdvancedTooltipProvider {

    public static ItemGasConduit create(@Nonnull IModObject modObject, @Nullable Block block) {
        return new ItemGasConduit(modObject);
    }

    protected ItemGasConduit(@Nonnull IModObject modObject) {
        super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()),
              new ItemConduitSubtype(modObject.getUnlocalisedName() + "_advanced", modObject.getRegistryName().toString() + "_advanced"),
              new ItemConduitSubtype(modObject.getUnlocalisedName() + "_ender", modObject.getRegistryName().toString() + "_ender"));
        ConduitRegistry.register(ConduitBuilder.start().setUUID(new ResourceLocation(GasConduitsConstants.MOD_ID, "gas"))
              .setClass(getBaseConduitType()).setOffsets(Offset.EAST_DOWN, Offset.SOUTH_DOWN, Offset.SOUTH_EAST, Offset.EAST_DOWN).build()
              .setUUID(new ResourceLocation(GasConduitsConstants.MOD_ID, "gas_conduit")).setClass(GasConduit.class).build()
              .setUUID(new ResourceLocation(GasConduitsConstants.MOD_ID, "advanced_gas_conduit")).setClass(AdvancedGasConduit.class).build()
              .setUUID(new ResourceLocation(GasConduitsConstants.MOD_ID, "ender_gas_conduit")).setClass(EnderGasConduit.class).build().finish());
        ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_GAS, IconEIO.WRENCH_OVERLAY_GAS_OFF));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerRenderers(@Nonnull IModObject modObject) {
        super.registerRenderers(modObject);
        ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(GasConduitRenderer.create());
        ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new AdvancedGasConduitRenderer());
        ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new EnderGasConduitRenderer());
    }

    @Override
    @Nonnull
    public Class<? extends IConduit> getBaseConduitType() {
        return IGasConduit.class;
    }

    @Override
    public IServerConduit createConduit(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
        if (stack.getItemDamage() == 1) {
            return new AdvancedGasConduit();
        } else if (stack.getItemDamage() == 2) {
            return new EnderGasConduit();
        }
        return new GasConduit();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
        int extractRate;
        int maxIo;

        if (itemstack.getItemDamage() == 0) {
            extractRate = GasConduitConfig.tier1_extractRate.get();
            maxIo = GasConduitConfig.tier1_maxIO.get();
        } else if (itemstack.getItemDamage() == 1) {
            extractRate = GasConduitConfig.tier2_extractRate.get();
            maxIo = GasConduitConfig.tier2_maxIO.get();
        } else {
            extractRate = GasConduitConfig.tier3_extractRate.get();
            maxIo = GasConduitConfig.tier3_maxIO.get();
        }

        String mbt = new TextComponentTranslation("gasconduits.gas.millibuckets_tick").getUnformattedComponentText();
        list.add(new TextComponentTranslation("gasconduits.item_gas_conduit.tooltip.max_extract").getUnformattedComponentText() + " " + extractRate + mbt);
        list.add(new TextComponentTranslation("gasconduits.item_gas_conduit.tooltip.max_io").getUnformattedComponentText() + " " + maxIo + mbt);

        if (itemstack.getItemDamage() == 0) {
            SpecialTooltipHandler.addDetailedTooltipFromResources(list, "gasconduits.item_gas_conduit");
        }
    }

    @Override
    public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
        return true;
    }
}