package crazypants.enderio.base.material.glass;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.render.property.EnumMergingBlockRenderMode;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class EnderIOGlassesStateMapper extends StateMapperBase {

  private enum Material implements IStringSerializable {
    QUARTZ,
    GLASS;

    @Override
    public @Nonnull String getName() {
      return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
    }

  }

  @SubscribeEvent
  public static void init(@Nonnull ModelRegistryEvent event) {
    EnderIOGlassesStateMapper mapper = new EnderIOGlassesStateMapper();
    for (FusedQuartzType glasstype : FusedQuartzType.values()) {
      ModelLoader.setCustomStateMapper(glasstype.getBlock(), mapper);
    }
  }

  @Override
  protected @Nonnull ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
    final ImmutableMap<IProperty<?>, Comparable<?>> properties = state.getProperties();
    Map<IProperty<?>, Comparable<?>> map = Maps.<IProperty<?>, Comparable<?>> newLinkedHashMap();

    map.put(FusedQuartzType.KIND, IFusedBlockstate.get(state).isBlastResistant() ? Material.QUARTZ : Material.GLASS);

    map.put(EnumMergingBlockRenderMode.RENDER, properties.get(EnumMergingBlockRenderMode.RENDER));

    return new ModelResourceLocation(Block.REGISTRY.getNameForObject(ModObject.blockFusedQuartz.getBlockNN()), getPropertyString(map));
  }

  @Override
  public @Nonnull String getPropertyString(@Nonnull Map<IProperty<?>, Comparable<?>> values) {
    StringBuilder stringbuilder = new StringBuilder();

    for (Entry<IProperty<?>, Comparable<?>> entry : values.entrySet()) {
      if (stringbuilder.length() != 0) {
        stringbuilder.append(",");
      }

      stringbuilder.append(entry.getKey().getName());
      stringbuilder.append("=");
      stringbuilder.append(((IStringSerializable) entry.getValue()).getName()); // HL: key and value don't match, so we cannot use super's impl
    }

    if (stringbuilder.length() == 0) {
      stringbuilder.append("normal");
    }

    return stringbuilder.toString();
  }

}
