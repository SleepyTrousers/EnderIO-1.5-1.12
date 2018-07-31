package crazypants.enderio.conduits.refinedstorage.conduit;

import javax.annotation.Nonnull;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.capability.IFilterHolder;
import crazypants.enderio.conduits.capability.IUpgradeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IRefinedStorageConduit extends IClientConduit, IServerConduit, INetworkNodeProxy<ConduitRefinedStorageNode>, IUpgradeHolder, IFilterHolder<IFilter> {

  public static final int INDEX_INPUT_REFINED_STORAGE = 7;
  public static final int INDEX_OUTPUT_REFINED_STROAGE = 8;

  public static final String ICON_KEY = "blocks/refined_storage_conduit";
  public static final String ICON_CORE_KEY = "blocks/refined_storage_conduit_core";

  void setInputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack);

  void setOutputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack);

  @Nonnull
  ItemStack getInputFilterUpgrade(@Nonnull EnumFacing dir);

  @Nonnull
  ItemStack getOutputFilterUpgrade(@Nonnull EnumFacing dir);

  void setInputFilter(@Nonnull EnumFacing dir, @Nonnull IFilter filter);

  void setOutputFilter(@Nonnull EnumFacing dir, @Nonnull IFilter filter);

  IFilter getInputFilter(@Nonnull EnumFacing dir);

  IFilter getOutputFilter(@Nonnull EnumFacing dir);

}
