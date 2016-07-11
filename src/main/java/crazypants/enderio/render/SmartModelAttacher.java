package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.Log;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.pipeline.OverlayHolder;
import crazypants.enderio.render.pipeline.RelayingBakedModel;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    Block block;
    IProperty<T> property;
    V defaultsValue;
    V autoValue;
    boolean itemOnly;

    protected RegistrationHolder(Block block, IProperty<T> property, V defaultsValue, V autoValue, boolean itemOnly) {
      this.block = block;
      this.property = property;
      this.defaultsValue = defaultsValue;
      this.autoValue = autoValue;
      this.itemOnly = itemOnly;
    }
  }

  @SuppressWarnings("rawtypes")
  private static final List<RegistrationHolder> blocks = new ArrayList<RegistrationHolder>();

  public static void register(Block block) {
    register(block, EnumRenderMode.RENDER, EnumRenderMode.DEFAULTS, EnumRenderMode.AUTO);
  }

  /**
   * Register a block that does not have one of our special rendering properties. All its blockstates will be rendered by our smart model, to the render mapper
   * cannot reference them and must get its blockstates from elsewhere.
   */
  public static void registerNoProps(Block block) {
    register(block, null, null, null, false);
  }

  public static void registerItemOnly(Block block) {
    register(block, null, null, null, true);
  }

  public static <T extends Comparable<T>, V extends T> void register(Block block, IProperty<T> property, V defaultsValue, V autoValue) {
    register(block, property, defaultsValue, autoValue, false);
  }

  private static <T extends Comparable<T>, V extends T> void register(Block block, IProperty<T> property, V defaultsValue, V autoValue, boolean itemOnly) {
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
      if (item != null) {
        ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName(), "inventory");
        if (item.getHasSubtypes()) {
          List<ItemStack> list = new ArrayList<ItemStack>();
          item.getSubItems(item, EnderIOTab.tabNoTab, list);
          for (ItemStack itemStack : list) {
            ModelLoader.setCustomModelResourceLocation(item, itemStack.getItemDamage(), location);
          }
        } else {
          ModelLoader.setCustomModelResourceLocation(item, 0, location);
        }
      } else {
        Log.debug("Block " + block + " has no item, is is intended?");
      }
    }
  }

  @SideOnly(Side.CLIENT)
  public static void registerColoredBlocksAndItems() {
    List<Block> blocklist = new ArrayList<Block>();
    List<Item> itemlist = new ArrayList<Item>();
    for (RegistrationHolder<?, ?> holder : blocks) {
      Block block = holder.block;
      Item item = Item.getItemFromBlock(block);
      if (block instanceof IPaintable || block instanceof ITintedBlock || block instanceof ITintedItem || item instanceof ITintedItem) {
        blocklist.add(block);
        if (item != null) {
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
  public void bakeModels(ModelBakeEvent event) {
    for (RegistrationHolder holder : blocks) {
      Block block = holder.block;
      Map<IBlockState, ModelResourceLocation> locations = new DefaultStateMapper().putStateModelLocations(block);

      if (holder.property != null && block.getDefaultState().getPropertyNames().contains(holder.property)) {
        IBlockState defaultState = block.getDefaultState().withProperty(holder.property, holder.defaultsValue);
        ModelResourceLocation defaultMrl = locations.get(defaultState);
        IBakedModel defaultBakedModel = event.getModelRegistry().getObject(defaultMrl);
        if (defaultBakedModel == null) {
          throw new RuntimeException("Model for state " + defaultState + " failed to load from " + defaultMrl + ". "
              + debugOutput(event.getModelRegistry(), defaultMrl));
        }
        RelayingBakedModel model = new RelayingBakedModel(defaultBakedModel);

        ModelResourceLocation itemMrl = new ModelResourceLocation(defaultMrl.getResourceDomain() + ":" + defaultMrl.getResourcePath() + "#inventory");
        event.getModelRegistry().putObject(itemMrl, model);

        for (Entry<IBlockState, ModelResourceLocation> entry : locations.entrySet()) {
          if (entry.getKey().getValue(holder.property) == holder.autoValue) {
            event.getModelRegistry().putObject(entry.getValue(), model);
          } else if (event.getModelRegistry().getObject(entry.getValue()) == null) {
            event.getModelRegistry().putObject(entry.getValue(), defaultBakedModel);
          }
        }
      } else {
        IBlockState defaultState = block.getDefaultState();
        ModelResourceLocation defaultMrl = locations.get(defaultState);
        IBakedModel defaultBakedModel = event.getModelRegistry().getObject(defaultMrl);

        if (!holder.itemOnly) {
          for (ModelResourceLocation mrl : locations.values()) {
            IBakedModel model = event.getModelRegistry().getObject(mrl);
            event.getModelRegistry().putObject(mrl, new RelayingBakedModel(model != null ? model : defaultBakedModel));
          }
        }

        ModelResourceLocation itemMrl = new ModelResourceLocation(defaultMrl.getResourceDomain() + ":" + defaultMrl.getResourcePath() + "#inventory");
        if (event.getModelRegistry().getObject(itemMrl) == null) {
          event.getModelRegistry().putObject(itemMrl, new RelayingBakedModel(defaultBakedModel));
        } else {
          event.getModelRegistry().putObject(itemMrl, new RelayingBakedModel(event.getModelRegistry().getObject(itemMrl)));
        }
      }
    }

    OverlayHolder.collectOverlayQuads(event);
    BlockStateWrapperBase.invalidate();
    BlockStateWrapperConduitBundle.invalidate();
  }

  private static String debugOutput(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, ModelResourceLocation defaultMrl) {
    String prefix = defaultMrl.getResourceDomain()+ ":" + defaultMrl.getResourcePath();
    if (modelRegistry instanceof RegistrySimple) {
      RegistrySimple<?, ?> rg = (RegistrySimple<?, ?>) modelRegistry;
      StringBuilder sb = new StringBuilder();
      for (Object key : rg.getKeys()) {
        if (key.toString().startsWith(prefix)) {
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
