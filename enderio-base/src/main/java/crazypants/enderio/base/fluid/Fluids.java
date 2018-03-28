package crazypants.enderio.base.fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.fluid.BlockFluidEnder;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.integration.railcraft.RailcraftUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID)
public enum Fluids {

  NUTRIENT_DISTILLATION("nutrient_distillation", Material.WATER, 0x5a5e00) {
    @Override
    protected Fluid init(@Nonnull Fluid fluid) {
      return fluid.setDensity(1500).setViscosity(3000);
    }

    @Override
    protected @Nonnull BlockFluidEnder init() {
      return new BlockFluidEio.NutrientDistillation(getFluid(), material, color);
    }
  },
  ENDER_DISTILLATION("ender_distillation", Material.WATER, 0x149535) { // Dew of the Void
    @Override
    protected Fluid init(@Nonnull Fluid fluid) {
      return fluid.setDensity(200).setViscosity(1000).setTemperature(175);
    }
  },
  VAPOR_OF_LEVITY("vapor_of_levity", Material.WATER, 0x41716a) {
    @Override
    protected Fluid init(@Nonnull Fluid fluid) {
      return fluid.setDensity(-10).setViscosity(100).setTemperature(5).setGaseous(true);
    }

    @Override
    protected @Nonnull BlockFluidEnder init() {
      BlockFluidEnder result = new BlockFluidEio.VaporOfLevity(getFluid(), material, color);
      result.setQuantaPerBlock(1);
      return result;
    }
  },
  HOOTCH("hootch", Material.WATER, 0xffffff) {
    @Override
    protected Fluid init(@Nonnull Fluid fluid) {
      return fluid.setDensity(900).setViscosity(1000);
    }

    @Override
    protected @Nonnull BlockFluidEnder init() {
      return new BlockFluidEio.Hootch(getFluid(), material, color);
    }
  },
  ROCKET_FUEL("rocket_fuel", Material.WATER, 0x707044) {
    @Override
    protected Fluid init(@Nonnull Fluid fluid) {
      return fluid.setDensity(900).setViscosity(1000);
    }

    @Override
    protected @Nonnull BlockFluidEnder init() {
      return new BlockFluidEio.RocketFuel(getFluid(), material, color);
    }
  },
  FIRE_WATER("fire_water", Material.LAVA, 0x8a490f) {
    @Override
    protected Fluid init(@Nonnull Fluid fluid) {
      return fluid.setDensity(900).setViscosity(1000).setTemperature(2000);
    }

    @Override
    protected @Nonnull BlockFluidEnder init() {
      return new BlockFluidEio.FireWater(getFluid(), material, color);
    }
  },
  XP_JUICE("xpjuice") {
    @Override
    protected Fluid init(@Nonnull Fluid fluid) {
      return fluid.setLuminosity(10).setDensity(800).setViscosity(1500);
    }
  },
  LIQUID_SUNSHINE("liquid_sunshine", Material.WATER, 0xd2c561) {
    @Override
    protected Fluid init(@Nonnull Fluid fluid) {
      return fluid.setDensity(200).setViscosity(400);
    }

    @Override
    protected @Nonnull BlockFluidEnder init() {
      BlockFluidEnder result = new BlockFluidEio.LiquidSunshine(getFluid(), material, color);
      result.setLightLevel(1);
      return result;
    }
  },
  CLOUD_SEED("cloud_seed", Material.WATER, 0x248589) {
    @Override
    protected Fluid init(@Nonnull Fluid fluid) {
      return fluid.setDensity(500).setViscosity(800);
    }
  },
  CLOUD_SEED_CONCENTRATED("cloud_seed_concentrated", Material.WATER, 0x3f5c5d) {
    @Override
    protected Fluid init(@Nonnull Fluid fluid) {
      return fluid.setDensity(1000).setViscosity(1200);
    }

    @Override
    protected @Nonnull BlockFluidEnder init() {
      return new BlockFluidEio.CloudSeedConcentrated(getFluid(), material, color);
    }
  };

  private final @Nonnull String name;
  private final boolean hasBlock;
  protected final @Nonnull Material material;
  protected final int color;

  private Fluids(@Nonnull String name) {
    this.name = name;
    this.hasBlock = false;
    this.material = Material.AIR;
    this.color = -1;
  }

  private Fluids(@Nonnull String name, @Nonnull Material material, int color) {
    this.name = name;
    this.hasBlock = true;
    this.material = material;
    this.color = color;
  }

  protected abstract Fluid init(@Nonnull Fluid fluid);

  protected @Nonnull BlockFluidEnder init() {
    return new BlockFluidEnder(getFluid(), material, color) {
    };
  }

  public @Nonnull ResourceLocation getStill() {
    return new ResourceLocation(EnderIO.DOMAIN, "blocks/fluid_" + name + "_still");
  }

  public @Nonnull ResourceLocation getFlowing() {
    return new ResourceLocation(EnderIO.DOMAIN, "blocks/fluid_" + name + "_flow");
  }

  public @Nonnull Fluid getFluid() {
    return NullHelper.notnull(FluidRegistry.getFluid(name), "Fluid missing: " + name);
  }

  public @Nonnull ItemStack getBucket() {
    return getBucket(getFluid());
  }

  public static @Nonnull ItemStack getBucket(@Nonnull Fluid fluid) {
    final FluidStack fluidStack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
    try {
      fluidStack.getFluid();
    } catch (NullPointerException e) {
      throw new RuntimeException("The fluid " + fluid + " (" + fluid.getUnlocalizedName()
          + ") is registered in the FluidRegistry, but the FluidRegistry has no delegate for it. This is impossible.", e);
    }
    return FluidUtil.getFilledBucket(fluidStack);
  }

  public static @Nonnull NNList<ItemStack> getBuckets() {
    NNList<ItemStack> result = new NNList<>();
    for (Fluids fluid : values()) {
      if (FluidRegistry.getBucketFluids().contains(fluid.getFluid())) {
        result.add(getBucket(fluid.getFluid()));
      }
    }
    return result;
  }

  public static @Nonnull NNList<ItemStack> getAllBuckets() {
    NNList<ItemStack> result = new NNList<>();
    for (Fluid fluid : FluidRegistry.getBucketFluids()) {
      result.add(getBucket(NullHelper.notnullF(fluid, "FluidRegistry.getBucketFluids() has null fluid")));
    }
    return result;
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void registerFluids(@Nonnull RegistryEvent.Register<Block> event) {
    for (Fluids fluid : values()) {
      FluidRegistry.registerFluid(fluid.init(new Fluid(fluid.name, fluid.getStill(), fluid.getFlowing())));
    }
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
    for (Fluids fluid : values()) {
      if (fluid.hasBlock) {
        event.getRegistry().register(fluid.init());
      }
    }
  }

  @SubscribeEvent
  public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
    for (Fluids fluid : values()) {
      if (!FluidRegistry.getBucketFluids().contains(fluid.getFluid())) {
        FluidRegistry.addBucketForFluid(fluid.getFluid());
      }
    }
  }

  public static void registerFuels() {
    FluidFuelRegister.instance.addFuel(HOOTCH.getFluid(), Config.hootchPowerPerCycleRF, Config.hootchPowerTotalBurnTime);
    FluidFuelRegister.instance.addFuel(ROCKET_FUEL.getFluid(), Config.rocketFuelPowerPerCycleRF, Config.rocketFuelPowerTotalBurnTime);
    FluidFuelRegister.instance.addFuel(FIRE_WATER.getFluid(), Config.fireWaterPowerPerCycleRF, Config.fireWaterPowerTotalBurnTime);

    RailcraftUtil.registerFuels();
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public static void registerRenderers(@Nonnull ModelRegistryEvent event) {
    for (Fluids fluid : values()) {
      if (fluid.hasBlock) {
        registerFluidBlockRendering(fluid.getFluid());
      }
    }
  }

  @SideOnly(Side.CLIENT)
  public static void registerFluidBlockRendering(@Nullable Fluid fluid) {
    if (fluid == null) {
      return;
    }
    Block block = fluid.getBlock();
    if (block == null) {
      return;
    }
    FluidStateMapper mapper = new FluidStateMapper(fluid);
    // block-model
    ModelLoader.setCustomStateMapper(block, mapper);

    Item item = Item.getItemFromBlock(block);
    // item-model
    if (item != Items.AIR) {
      ModelLoader.registerItemVariants(item);
      ModelLoader.setCustomMeshDefinition(item, mapper);
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public static void onIconLoad(TextureStitchEvent.Pre event) {
    event.getMap().registerSprite(XP_JUICE.getFluid().getStill());
    event.getMap().registerSprite(XP_JUICE.getFluid().getFlowing());
  }

}
