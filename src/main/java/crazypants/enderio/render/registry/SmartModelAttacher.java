package crazypants.enderio.render.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.Log;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.ICustomItemResourceLocation;
import crazypants.enderio.render.ITintedBlock;
import crazypants.enderio.render.ITintedItem;
import crazypants.enderio.render.model.RelayingBakedModel;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.pipeline.OverlayHolder;
import crazypants.enderio.render.property.EnumRenderMode;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SmartModelAttacher {

  private static class RegistrationHolder<T extends Comparable<T>, V extends T> {
    final @Nonnull Block block;
    final IProperty<T> property;
    final V defaultsValue;
    final V autoValue;
    boolean itemOnly;

    protected RegistrationHolder(@Nonnull Block block, IProperty<T> property, V defaultsValue, V autoValue, boolean itemOnly) {
      this.block = block;
      this.property = property;
      this.defaultsValue = defaultsValue;
      this.autoValue = autoValue;
      this.itemOnly = itemOnly;
    }
  }

  @SuppressWarnings("rawtypes")
  private static final List<RegistrationHolder> blocks = new ArrayList<RegistrationHolder>();

  public static void register(@Nonnull Block block) {
    register(block, EnumRenderMode.RENDER, EnumRenderMode.DEFAULTS, EnumRenderMode.AUTO);
  }

  /**
   * Register a block that does not have one of our special rendering properties. All its blockstates will be rendered by our smart model, so the render mapper
   * cannot reference them and must get its blockstates from elsewhere.
   */
  public static void registerNoProps(@Nonnull Block block) {
    register(block, null, null, null, false);
  }

  public static void registerItemOnly(@Nonnull Block block) {
    register(block, null, null, null, true);
  }

  public static <T extends Comparable<T>, V extends T> void register(@Nonnull Block block, IProperty<T> property, V defaultsValue, V autoValue) {
    register(block, property, defaultsValue, autoValue, false);
  }

  private static <T extends Comparable<T>, V extends T> void register(@Nonnull Block block, IProperty<T> property, V defaultsValue, V autoValue,
      boolean itemOnly) {
    blocks.add(new RegistrationHolder<T, V>(block, property, defaultsValue, autoValue, itemOnly));
  }

  public static void create() {
    MinecraftForge.EVENT_BUS.register(new SmartModelAttacher());
  }

  /**
   * Registers the default ModelResourceLocation for the items of all blocks that have registered for MachineSmartModel-based rendering.
   * <p>
   * For items that have subtypes, all subtypes that are exposed to the creative inventory are registered. All subtypes are registered to the same model, as the
   * smart model can be damage-aware.
   */
  @SideOnly(Side.CLIENT)
  public static void registerBlockItemModels() {
    for (RegistrationHolder<?, ?> holder : blocks) {
      Block block = holder.block;
      Item item = Item.getItemFromBlock(block);
      if (item != Items.AIR) {
        final @Nonnull ResourceLocation registryName = item instanceof ICustomItemResourceLocation
            ? ((ICustomItemResourceLocation) item).getRegistryNameForCustomModelResourceLocation()
            : NullHelper.notnullF(item.getRegistryName(), "Item.getItemFromBlock() returned an unregistered item");
        ModelResourceLocation location = new ModelResourceLocation(registryName, "inventory");
        if (item.getHasSubtypes()) {
          NNList<ItemStack> list = new NNList<ItemStack>();
          item.getSubItems(item, EnderIOTab.tabNoTab, list);
          for (ItemStack itemStack : list) {
            ModelLoader.setCustomModelResourceLocation(item, itemStack.getItemDamage(), location);
          }
        } else {
          ModelLoader.setCustomModelResourceLocation(item, 0, location);
        }
      } else {
        Log.debug("Block " + block + " has no item, is it intended?");
      }
    }
  }

  @SideOnly(Side.CLIENT)
  public static void registerColoredBlocksAndItems() {
    NNList<Block> blocklist = new NNList<Block>();
    NNList<Item> itemlist = new NNList<Item>();
    for (RegistrationHolder<?, ?> holder : blocks) {
      Block block = holder.block;
      Item item = Item.getItemFromBlock(block);
      if (block instanceof IPaintable || block instanceof ITintedBlock || block instanceof ITintedItem || item instanceof ITintedItem) {
        blocklist.add(block);
        if (item != Items.AIR) {
          itemlist.add(item);
        }
      } else {
        if (block instanceof IBlockColor) {
          Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor) block, block);
        }
        if (item instanceof IItemColor) {
          Minecraft.getMinecraft().getItemColors().registerItemColorHandler((IItemColor) item, item);
        }
      }
    }

    PaintTintHandler handler = new PaintTintHandler();
    Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(handler, blocklist.toArray(new Block[0]));
    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(handler, itemlist.toArray(new Item[0]));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SubscribeEvent()
  @SideOnly(Side.CLIENT)
  public void bakeModels(@Nonnull ModelBakeEvent event) {
    for (RegistrationHolder holder : blocks) {
      Block block = holder.block;
      Map<IBlockState, ModelResourceLocation> locations = event.getModelManager().getBlockModelShapes().getBlockStateMapper().getVariants(block);

      final IProperty holder_property = holder.property;
      final Comparable holder_defaultsValue = holder.defaultsValue;
      final IBakedModel missingModel = event.getModelManager().getMissingModel();
      if (holder_property != null && block.getDefaultState().getPropertyKeys().contains(holder_property) && holder_defaultsValue != null) {
        IBlockState defaultState = block.getDefaultState().withProperty(holder_property, holder_defaultsValue);
        ModelResourceLocation defaultMrl = locations.get(defaultState);
        if (defaultMrl == null) {
          throw new RuntimeException("Model for state " + defaultState + " failed to load from " + defaultMrl + ". "
              + debugOutput(event.getModelRegistry(), defaultMrl, missingModel));
        }
        IBakedModel defaultBakedModel = event.getModelRegistry().getObject(defaultMrl);
        if (defaultBakedModel == null) {
          Log.warn("ModelBakeEvent: cannot register smart model over null model for " + holder.block + ". See model errors below.");
          continue;
        }
        RelayingBakedModel model = new RelayingBakedModel(defaultBakedModel);

        ModelResourceLocation itemMrl = new ModelResourceLocation(defaultMrl.getResourceDomain() + ":" + defaultMrl.getResourcePath() + "#inventory");
        event.getModelRegistry().putObject(itemMrl, model);

        if (defaultBakedModel == missingModel) {
          // This is normal on the first pass. We register our synthetic models above anyway to avoid model loading errors.
          continue;
        }

        for (Entry<IBlockState, ModelResourceLocation> entry : locations.entrySet()) {
          final ModelResourceLocation entryMrl = NullHelper.notnullF(entry.getValue(), "BlockModelShapes contains null keys");
          final IBlockState entryBlockstate = entry.getKey();
          final IBakedModel existingModel = event.getModelRegistry().getObject(entryMrl);

          if (existingModel == null || existingModel == missingModel) {
            event.getModelRegistry().putObject(entryMrl, defaultBakedModel);
          } else if (entryBlockstate.getValue(holder_property) == holder.autoValue) {
            event.getModelRegistry().putObject(entryMrl, model);
          }
        }
      } else {
        IBlockState defaultState = block.getDefaultState();
        ModelResourceLocation defaultMrl = locations.get(defaultState);
        if (defaultMrl == null) {
          throw new RuntimeException("Model for state " + defaultState + " failed to load from " + defaultMrl + ". "
              + debugOutput(event.getModelRegistry(), defaultMrl, missingModel));
        }
        IBakedModel defaultBakedModel = event.getModelRegistry().getObject(defaultMrl);
        if (defaultBakedModel == null || defaultBakedModel == missingModel) {
          continue;
        }

        if (!holder.itemOnly) {
          for (ModelResourceLocation mrl0 : locations.values()) {
            final ModelResourceLocation mrl = NullHelper.notnullF(mrl0, "BlockModelShapes contains null keys");
            IBakedModel model = event.getModelRegistry().getObject(mrl);
            if (model == null || model == missingModel) {

            } else {
              event.getModelRegistry().putObject(mrl, new RelayingBakedModel(NullHelper.first(model, defaultBakedModel)));
            }
          }
        }

        ModelResourceLocation itemMrl = new ModelResourceLocation(defaultMrl.getResourceDomain() + ":" + defaultMrl.getResourcePath() + "#inventory");
        final IBakedModel model = event.getModelRegistry().getObject(itemMrl);
        if (model == null || model == missingModel) {
          event.getModelRegistry().putObject(itemMrl, new RelayingBakedModel(defaultBakedModel));
        } else {
          event.getModelRegistry().putObject(itemMrl, new RelayingBakedModel(model));
        }
      }
    }

    OverlayHolder.collectOverlayQuads(event);
    BlockStateWrapperBase.invalidate();
    // TODO 1.11 move this to conduit sub-mod
    // BlockStateWrapperConduitBundle.invalidate();
  }

  @SuppressWarnings("null")
  private static String debugOutput(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, ModelResourceLocation defaultMrl, IBakedModel missingModel) {
    String prefix = defaultMrl.getResourceDomain() + ":" + defaultMrl.getResourcePath();
    if (modelRegistry instanceof RegistrySimple) {
      RegistrySimple<?, ?> rg = (RegistrySimple<?, ?>) modelRegistry;
      StringBuilder sb = new StringBuilder();
      for (Object key : rg.getKeys()) {
        if (modelRegistry.getObject((ModelResourceLocation) key) != missingModel && key.toString().startsWith(prefix)) {
          sb.append(key + "; ");
        }
      }
      if (sb.length() > 0) {
        sb.setLength(sb.length() - 2);
      } else {
        sb.append("(none)");
      }
      return "Loaded states for " + prefix + " are: " + sb.toString();
    } else {
      return "Loaded states could not be determined because modelRegistry is not a RegistrySimple.";
    }
  }

}
