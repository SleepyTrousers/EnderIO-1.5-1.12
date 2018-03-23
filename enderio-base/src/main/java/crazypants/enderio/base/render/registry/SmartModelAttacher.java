package crazypants.enderio.base.render.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.init.IModObject.Registerable;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.ICustomItemResourceLocation;
import crazypants.enderio.base.render.ICustomSubItems;
import crazypants.enderio.base.render.IDefaultRenderers;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.ITintedBlock;
import crazypants.enderio.base.render.ITintedItem;
import crazypants.enderio.base.render.model.RelayingBakedModel;
import crazypants.enderio.base.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.base.render.pipeline.OverlayHolder;
import crazypants.enderio.base.render.property.EnumRenderMode;
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
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
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

  /**
   * Registers the default ModelResourceLocation for the items of all blocks that have registered for MachineSmartModel-based rendering.
   * <p>
   * For items that have subtypes, all subtypes are registered. All subtypes are registered to the same model, as the smart model can be damage-aware.
   */
  @SideOnly(Side.CLIENT)
  public static void registerBlockItemModels() {
    for (RegistrationHolder<?, ?> holder : blocks) {
      Block block = holder.block;
      Registerable modObject = ModObjectRegistry.getModObject(holder.block);
      if (modObject == null) {
        Log.debug("Block " + block + " has no modObject. What?");
      } else {
        Item item = modObject.getItem();
        if (item instanceof IHaveRenderers || block instanceof IHaveRenderers) {
          // Nothing to do for us, the item/block handles it for itself
          Log.debug(block.getClass() + " handles its item registrations itself");
          if (item instanceof ICustomSubItems || block instanceof ICustomSubItems) {
            throw new RuntimeException(block.getClass() + " implements both IHaveRenderers and ICustomSubItems");
          }
        } else if (block instanceof IDefaultRenderers) {
          // Nothing to do for us, the block wants ClientProxy to handle it
          Log.debug(block.getClass() + " has default item registrations");
          if (item instanceof ICustomSubItems || block instanceof ICustomSubItems) {
            throw new RuntimeException(block.getClass() + " implements both IDefaultRenderers and ICustomSubItems");
          }
        } else if (item != null && item != Items.AIR) {
          final @Nonnull ResourceLocation registryName = item instanceof ICustomItemResourceLocation
              ? ((ICustomItemResourceLocation) item).getRegistryNameForCustomModelResourceLocation()
              : NullHelper.notnullF(item.getRegistryName(), "Item.getItemFromBlock() returned an unregistered item");
          ModelResourceLocation location = new ModelResourceLocation(registryName, "inventory");
          if (item.getHasSubtypes()) {
            NNList<ItemStack> subItems;
            if (item instanceof ICustomSubItems) {
              subItems = ((ICustomSubItems) item).getSubItems();
            } else if (block instanceof ICustomSubItems) {
              subItems = ((ICustomSubItems) block).getSubItems();
            } else {
              throw new RuntimeException(block.getClass() + " has subitems but it does not implement ICustomSubItems");
            }
            for (ItemStack itemStack : subItems) {
              Log.debug("Registering RL " + location + " for " + itemStack);
              ModelLoader.setCustomModelResourceLocation(item, itemStack.getItemDamage(), location);
            }
          } else {
            Log.debug("Registering RL " + location + " for " + item);
            ModelLoader.setCustomModelResourceLocation(item, 0, location);
          }
        } else {
          Log.debug("Block " + block + " has no item, is it intended?");
        }
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
  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void bakeModels(@Nonnull ModelBakeEvent event) {
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
