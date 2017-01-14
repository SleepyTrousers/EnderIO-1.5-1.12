package crazypants.enderio.fluid;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.config.Config.rocketFuelIsExplosive;

public class BlockFluidEio extends BlockFluidClassic {

  public static BlockFluidEio create(Fluid fluid, Material material, int fogColor) {
    BlockFluidEio res;
    if (fluid == Fluids.fluidFireWater) {
      res = new FireWater(fluid, material, fogColor);
    } else if (fluid == Fluids.fluidHootch) {
      res = new Hootch(fluid, material, fogColor);
    } else if (fluid == Fluids.fluidRocketFuel) {
      res = new RocketFuel(fluid, material, fogColor);
    } else if (fluid == Fluids.fluidNutrientDistillation) {
      res = new NutrientDistillation(fluid, material, fogColor);
    } else if (fluid == Fluids.fluidLiquidSunshine) {
      res = new LiquidSunshine(fluid, material, fogColor);
    } else if (fluid == Fluids.fluidCloudSeedConcentrated) {
      res = new CloudSeedConcentrated(fluid, material, fogColor);
    } else if (fluid == Fluids.fluidVaporOfLevity) {
      res = new VaporOfLevity(fluid, material, fogColor);
    } else {
      res = new BlockFluidEio(fluid, material, fogColor);
    }
    res.init();
    fluid.setBlock(res);
    return res;
  }

  public static BlockFluidEio createMetal(Fluid fluid, Material material, int fogColor) {
    BlockFluidEio res = new MoltenMetal(fluid, material, fogColor);
    res.init();
    fluid.setBlock(res);
    return res;
  }

  static {
    MinecraftForge.EVENT_BUS.register(BlockFluidEio.class);
  }

  public static void onEntitySpawn(LivingSpawnEvent.CheckSpawn evt) {
    if (evt.getResult() != Result.DENY
        && EntitySpawnPlacementRegistry.getPlacementForEntity(evt.getEntity().getClass()) == EntityLiving.SpawnPlacementType.IN_WATER
        && evt.getWorld().getBlockState(evt.getEntityLiving().getPosition()).getBlock() instanceof BlockFluidEio) {
      evt.setResult(Result.DENY);
    }
    return;
  }

  protected final Fluid fluid;
  protected float fogColorRed = 1;
  protected float fogColorGreen = 1;
  protected float fogColorBlue = 1;

  protected BlockFluidEio(Fluid fluid, Material material, int fogColor) {
    super(fluid, new MaterialLiquid(material.getMaterialMapColor()) {
      // new Material for each liquid so neighboring different liquids render correctly and don't bleed into each other
      @Override
      public boolean blocksMovement() {
        return true; // so our liquids are not replaced by water
      }
    });
    this.fluid = fluid;

    // darken fog color to fit the fog rendering
    float dim = 1;
    while (fogColorRed > .2f || fogColorGreen > .2f || fogColorBlue > .2f) {
      fogColorRed = (fogColor >> 16 & 255) / 255f * dim;
      fogColorGreen = (fogColor >> 8 & 255) / 255f * dim;
      fogColorBlue = (fogColor & 255) / 255f * dim;
      dim *= .9f;
    }

    setNames(fluid);
  }

  protected void setNames(Fluid fluid) {
    setUnlocalizedName(fluid.getUnlocalizedName());
    setRegistryName("block" + StringUtils.capitalize(fluid.getName()));
  }

  protected void init() {
    GameRegistry.register(this);
  }

  /////////////////////////////////////////////////////////////////////////
  // START VISUALS
  /////////////////////////////////////////////////////////////////////////

  static {
    MinecraftForge.EVENT_BUS.register(BlockFluidEio.class);
  }

  // net.minecraft.client.renderer.EntityRenderer.getNightVisionBrightness(EntityLivingBase, float) is private :-(
  @SideOnly(Side.CLIENT)
  private static float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks) {
    @SuppressWarnings("null")
    int i = entitylivingbaseIn.getActivePotionEffect(MobEffects.NIGHT_VISION).getDuration();
    return i > 200 ? 1.0F : 0.7F + MathHelper.sin((i - partialTicks) * (float) Math.PI * 0.2F) * 0.3F;
  }

  @Override
  public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn,
      boolean testingHead) {
    if (materialIn == Material.WATER || materialIn == this.blockMaterial) {
      return Boolean.TRUE;
    }
    return super.isEntityInsideMaterial(world, blockpos, iblockstate, entity, yToTest, materialIn, testingHead);
  }

  private static Field FfogColor1, FfogColor2, FbossColorModifier, FbossColorModifierPrev, FcloudFog;

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onFOVModifier(EntityViewRenderEvent.FOVModifier event) {
    if (event.getState() instanceof BlockFluidEio) {
      event.setFOV(event.getFOV() * 60.0F / 70.0F);
    }
  }

  private static final ResourceLocation RES_UNDERFLUID_OVERLAY = new ResourceLocation(EnderIO.DOMAIN, "textures/misc/underfluid.png");

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
    if (event.getOverlayType() == OverlayType.WATER) {
      final EntityPlayer player = event.getPlayer();
      // the event has the wrong BlockPos (entity center instead of eyes)
      final BlockPos blockpos = new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ);
      final Block block = player.worldObj.getBlockState(blockpos).getBlock();

      if (block instanceof BlockFluidEio) {
        float fogColorRed = ((BlockFluidEio) block).fogColorRed;
        float fogColorGreen = ((BlockFluidEio) block).fogColorGreen;
        float fogColorBlue = ((BlockFluidEio) block).fogColorBlue;

        Minecraft.getMinecraft().getTextureManager().bindTexture(RES_UNDERFLUID_OVERLAY);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        float f = player.getBrightness(event.getRenderPartialTicks());
        GlStateManager.color(f * fogColorRed, f * fogColorGreen, f * fogColorBlue, 0.5F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        float f7 = -player.rotationYaw / 64.0F;
        float f8 = player.rotationPitch / 64.0F;
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(-1.0D, -1.0D, -0.5D).tex(4.0F + f7, 4.0F + f8).endVertex();
        vertexbuffer.pos(1.0D, -1.0D, -0.5D).tex(0.0F + f7, 4.0F + f8).endVertex();
        vertexbuffer.pos(1.0D, 1.0D, -0.5D).tex(0.0F + f7, 0.0F + f8).endVertex();
        vertexbuffer.pos(-1.0D, 1.0D, -0.5D).tex(4.0F + f7, 0.0F + f8).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();

        event.setCanceled(true);
      }
    }
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onFogDensity(EntityViewRenderEvent.FogDensity event) throws IllegalArgumentException, IllegalAccessException {
    if (FcloudFog == null) {
      FcloudFog = ReflectionHelper.findField(EntityRenderer.class, "cloudFog", "field_78500_U");
    }
    if (event.getState() instanceof BlockFluidEio) {
      final EntityRenderer renderer = event.getRenderer();
      final Entity entity = event.getEntity();
      final boolean cloudFog = FcloudFog.getBoolean(renderer);

      // again the event is fired at a bad location...
      if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.BLINDNESS)) {
        return;
      } else if (cloudFog) {
        return;
      }

      GlStateManager.setFog(GlStateManager.FogMode.EXP);

      if (entity instanceof EntityLivingBase) {
        if (((EntityLivingBase) entity).isPotionActive(MobEffects.WATER_BREATHING)) {
          event.setDensity(0.01F);
        } else {
          event.setDensity(0.1F - EnchantmentHelper.getRespirationModifier((EntityLivingBase) entity) * 0.03F);
        }
      } else {
        event.setDensity(0.1F);
      }
    }
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public static void onFogColor(EntityViewRenderEvent.FogColors event) throws IllegalArgumentException, IllegalAccessException {
    if (FfogColor1 == null || FfogColor2 == null || FbossColorModifier == null || FbossColorModifierPrev == null) {
      FfogColor1 = ReflectionHelper.findField(EntityRenderer.class, "fogColor1", "field_78539_ae");
      FfogColor2 = ReflectionHelper.findField(EntityRenderer.class, "fogColor2", "field_78535_ad");
      FbossColorModifier = ReflectionHelper.findField(EntityRenderer.class, "bossColorModifier", "field_82831_U");
      FbossColorModifierPrev = ReflectionHelper.findField(EntityRenderer.class, "bossColorModifierPrev", "field_82832_V");
    }

    if (event.getState().getBlock() instanceof BlockFluidEio) {

      float fogColorRed = ((BlockFluidEio) event.getState().getBlock()).fogColorRed;
      float fogColorGreen = ((BlockFluidEio) event.getState().getBlock()).fogColorGreen;
      float fogColorBlue = ((BlockFluidEio) event.getState().getBlock()).fogColorBlue;

      // the following was copied as-is from net.minecraft.client.renderer.EntityRenderer.updateFogColor() because that %&!$ Forge event is fired after the
      // complete fog color calculation is done

      final EntityRenderer renderer = event.getRenderer();
      final float fogColor1 = FfogColor1.getFloat(renderer);
      final float fogColor2 = FfogColor2.getFloat(renderer);
      final float partialTicks = (float) event.getRenderPartialTicks();
      final Entity entity = event.getEntity();
      final World world = entity.getEntityWorld();
      final float bossColorModifier = FbossColorModifier.getFloat(renderer);
      final float bossColorModifierPrev = FbossColorModifierPrev.getFloat(renderer);

      float f12 = 0.0F;

      if (entity instanceof EntityLivingBase) {
        f12 = EnchantmentHelper.getRespirationModifier((EntityLivingBase) entity) * 0.2F;

        if (((EntityLivingBase) entity).isPotionActive(MobEffects.WATER_BREATHING)) {
          f12 = f12 * 0.3F + 0.6F;
        }
      }

      fogColorRed += f12;
      fogColorGreen += f12;
      fogColorBlue += f12;

      float f13 = fogColor2 + (fogColor1 - fogColor2) * partialTicks;
      fogColorRed *= f13;
      fogColorGreen *= f13;
      fogColorBlue *= f13;
      double d1 = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) * world.provider.getVoidFogYFactor();

      if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.BLINDNESS)) {
        @SuppressWarnings("null")
        int i = ((EntityLivingBase) entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();

        if (i < 20) {
          d1 *= 1.0F - i / 20.0F;
        } else {
          d1 = 0.0D;
        }
      }

      if (d1 < 1.0D) {
        if (d1 < 0.0D) {
          d1 = 0.0D;
        }

        d1 = d1 * d1;
        fogColorRed = (float) (fogColorRed * d1);
        fogColorGreen = (float) (fogColorGreen * d1);
        fogColorBlue = (float) (fogColorBlue * d1);
      }

      if (bossColorModifier > 0.0F) {
        float f14 = bossColorModifierPrev + (bossColorModifier - bossColorModifierPrev) * partialTicks;
        fogColorRed = fogColorRed * (1.0F - f14) + fogColorRed * 0.7F * f14;
        fogColorGreen = fogColorGreen * (1.0F - f14) + fogColorGreen * 0.6F * f14;
        fogColorBlue = fogColorBlue * (1.0F - f14) + fogColorBlue * 0.6F * f14;
      }

      if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.NIGHT_VISION)) {
        float f15 = getNightVisionBrightness((EntityLivingBase) entity, partialTicks);
        float f6 = 1.0F / fogColorRed;

        if (f6 > 1.0F / fogColorGreen) {
          f6 = 1.0F / fogColorGreen;
        }

        if (f6 > 1.0F / fogColorBlue) {
          f6 = 1.0F / fogColorBlue;
        }

        fogColorRed = fogColorRed * (1.0F - f15) + fogColorRed * f6 * f15;
        fogColorGreen = fogColorGreen * (1.0F - f15) + fogColorGreen * f6 * f15;
        fogColorBlue = fogColorBlue * (1.0F - f15) + fogColorBlue * f6 * f15;
      }

      if (Minecraft.getMinecraft().gameSettings.anaglyph) {
        float f16 = (fogColorRed * 30.0F + fogColorGreen * 59.0F + fogColorBlue * 11.0F) / 100.0F;
        float f17 = (fogColorRed * 30.0F + fogColorGreen * 70.0F) / 100.0F;
        float f7 = (fogColorRed * 30.0F + fogColorBlue * 70.0F) / 100.0F;
        fogColorRed = f16;
        fogColorGreen = f17;
        fogColorBlue = f7;
      }

      event.setRed(fogColorRed);
      event.setGreen(fogColorGreen);
      event.setBlue(fogColorBlue);
    }
  }

  /////////////////////////////////////////////////////////////////////////
  // END VISUALS
  /////////////////////////////////////////////////////////////////////////

  @Override
  public boolean canDisplace(IBlockAccess world, BlockPos pos) {
    IBlockState bs = world.getBlockState(pos);
    if (bs.getMaterial().isLiquid()) {
      return false;
    }
    return super.canDisplace(world, pos);
  }

  @Override
  public boolean displaceIfPossible(World world, BlockPos pos) {
    IBlockState bs = world.getBlockState(pos);
    if (bs.getMaterial().isLiquid()) {
      return false;
    }
    return super.displaceIfPossible(world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

  /////////////////////////////////////////////////////////////////////////
  // Fire Water
  /////////////////////////////////////////////////////////////////////////

  private static class FireWater extends BlockFluidEio {

    protected FireWater(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote) {
        entity.setFire(50);
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return true;
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
      return true;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 60;
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Hootch
  /////////////////////////////////////////////////////////////////////////

  private static class Hootch extends BlockFluidEio {

    protected Hootch(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 150, 0, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return true;
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
      return true;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 1;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 60;
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Rocket Fuel
  /////////////////////////////////////////////////////////////////////////

  private static class RocketFuel extends BlockFluidEio {

    protected RocketFuel(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 150, 3, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return true;
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
      return true;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 60;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
      checkForFire(worldIn, pos);
      super.neighborChanged(state, worldIn, pos, blockIn);
    }

    protected void checkForFire(World worldIn, BlockPos pos) {
      if (rocketFuelIsExplosive) {
        for (EnumFacing side : EnumFacing.values()) {
          IBlockState neighbor = worldIn.getBlockState(pos.offset(side));
          if (neighbor.getBlock() instanceof BlockFire && neighbor.getBlock() != ModObject.blockColdFire.getBlock()) {
            if (worldIn.rand.nextFloat() < .5f) {
              List<BlockPos> explosions = new ArrayList<BlockPos>();
              explosions.add(pos);
              BlockPos up = pos.up();
              while (worldIn.getBlockState(up).getBlock() == this) {
                explosions.add(up);
                up = up.up();
              }

              if (isSourceBlock(worldIn, pos)) {
                worldIn.newExplosion(null, pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, 2, true, true);
              }
              float strength = .5f;
              for (BlockPos explosion : explosions) {
                worldIn.newExplosion(null, explosion.getX() + .5f, explosion.getY() + .5f, explosion.getZ() + .5f, strength, true, true);
                strength = Math.min(strength * 1.05f, 7f);
              }

              return;
            }
          }
        }
      }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
      checkForFire(world, pos);
      super.updateTick(world, pos, state, rand);
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Nutrient Distillation
  /////////////////////////////////////////////////////////////////////////

  private static class NutrientDistillation extends BlockFluidEio {

    protected NutrientDistillation(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote && entity instanceof EntityPlayerMP) {
        long time = entity.worldObj.getTotalWorldTime();
        EntityPlayerMP player = (EntityPlayerMP) entity;
        if (time % Config.nutrientFoodBoostDelay == 0 && player.getEntityData().getLong("eioLastFoodBoost") != time) {
          player.getFoodStats().addStats(1, 0.1f);
          player.getEntityData().setLong("eioLastFoodBoost", time);
        }
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Liquid Sunshine
  /////////////////////////////////////////////////////////////////////////

  private static class LiquidSunshine extends BlockFluidEio {

    protected LiquidSunshine(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 50, 0, true, true));
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.GLOWING, 1200, 0, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Cloud Seed, Concentrated
  /////////////////////////////////////////////////////////////////////////

  private static class CloudSeedConcentrated extends BlockFluidEio {

    protected CloudSeedConcentrated(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40, 0, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Vapor Of Levity
  /////////////////////////////////////////////////////////////////////////

  private static class VaporOfLevity extends BlockFluidEio {

    protected VaporOfLevity(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (entity instanceof EntityPlayer || (!world.isRemote && entity instanceof EntityLivingBase)) {
        ((EntityLivingBase) entity).motionY += 0.1;
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    private static final int[] COLORS = { 0x0c82d0, 0x90c8ec, 0x5174ed, 0x0d2f65, 0x4accee };

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
      if (rand.nextFloat() < .5f) {
        final EnumFacing face = EnumFacing.values()[rand.nextInt(EnumFacing.values().length)];
        final BlockPos neighborPos = pos.offset(face);
        final IBlockState neighborState = worldIn.getBlockState(neighborPos);
        if (!neighborState.isFullCube()) {
          double xd = face.getFrontOffsetX() == 0 ? rand.nextDouble() : face.getFrontOffsetX() < 0 ? -0.05 : 1.05;
          double yd = face.getFrontOffsetY() == 0 ? rand.nextDouble() : face.getFrontOffsetY() < 0 ? -0.05 : 1.05;
          double zd = face.getFrontOffsetZ() == 0 ? rand.nextDouble() : face.getFrontOffsetZ() < 0 ? -0.05 : 1.05;

          double x = pos.getX() + xd;
          double y = pos.getY() + yd;
          double z = pos.getZ() + zd;

          int col = COLORS[rand.nextInt(COLORS.length)];

          worldIn.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, (col >> 16 & 255) / 255d, (col >> 8 & 255) / 255d, (col & 255) / 255d);
        }
      }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
      if (!world.isRemote && rand.nextFloat() < .1f) {
        final BlockPos neighborPos = getNeighbor(pos, rand);
        final IBlockState neighborState = world.getBlockState(neighborPos);
        final Block neighborBlock = neighborState.getBlock();
        final BlockPos belowNeighborPos = neighborPos.down();
        if (neighborBlock != Blocks.SNOW_LAYER && !(neighborBlock instanceof IFluidBlock) && !(neighborBlock instanceof BlockLiquid)
            && neighborBlock.isReplaceable(world, neighborPos) && world.getBlockState(belowNeighborPos).isSideSolid(world, belowNeighborPos, EnumFacing.UP)) {
          world.setBlockState(neighborPos, Blocks.SNOW_LAYER.getDefaultState());
        } else if (neighborBlock == Blocks.WATER && neighborState.getValue(BlockLiquid.LEVEL) == 0
            && world.canBlockBePlaced(Blocks.ICE, neighborPos, false, EnumFacing.DOWN, (Entity) null, (ItemStack) null)) {
          world.setBlockState(neighborPos, Blocks.ICE.getDefaultState());
        }
      }
      super.updateTick(world, pos, state, rand);
      if (!world.isUpdateScheduled(pos, this)) {
        world.scheduleUpdate(pos, this, tickRate * 10);
      }
    }

    protected BlockPos getNeighbor(BlockPos pos, Random rand) {
      EnumFacing face = EnumFacing.values()[rand.nextInt(EnumFacing.values().length)];
      if (face.getAxis() != Axis.Y && rand.nextBoolean()) {
        return pos.offset(face).offset(face.rotateY());
      } else {
        return pos.offset(face);
      }
    }

    @Override
    public float getFluidHeightForRender(IBlockAccess world, BlockPos pos) {
      IBlockState down = world.getBlockState(pos.down());
      if (down.getMaterial().isLiquid() || down.getBlock() instanceof IFluidBlock) {
        return 1;
      } else {
        return 0.995F;
      }
    }
  }

  /////////////////////////////////////////////////////////////////////////
  // Molten Metal
  /////////////////////////////////////////////////////////////////////////

  private static class MoltenMetal extends BlockFluidEio {

    protected MoltenMetal(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote && !entity.isImmuneToFire()) {
        entity.attackEntityFrom(DamageSource.lava, 4.0F);
        entity.setFire(15);
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn,
        boolean testingHead) {
      if (materialIn == Material.LAVA || materialIn == this.blockMaterial) {
        return Boolean.TRUE;
      }
      // Note: There's no callback for Entity.isInLava(), so just pretend we're also WATER. It has some drawbacks, but we don't really expect people to go
      // swimming in molten metals, do we?
      return super.isEntityInsideMaterial(world, blockpos, iblockstate, entity, yToTest, materialIn, testingHead);
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // TiC Fluids
  /////////////////////////////////////////////////////////////////////////

  public static abstract class TicFluids extends BlockFluidEio {

    protected TicFluids(Fluid fluid, Material material, int fogColor) {
      super(fluid, material, fogColor);
    }

    @Override
    protected void setNames(Fluid fluid) {
      setUnlocalizedName(fluid.getUnlocalizedName());
      setRegistryName("fluid" + StringUtils.capitalize(fluid.getName()));
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Molten Glowstone
  /////////////////////////////////////////////////////////////////////////

  public static class MoltenGlowstone extends TicFluids {

    public MoltenGlowstone(Fluid fluid, Material material, int fogColor) { // 0xffbc5e
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 200, 0, true, true));
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.GLOWING, 2400, 0, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public void init() {
      super.init();
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Molten Redstone
  /////////////////////////////////////////////////////////////////////////

  public static class MoltenRedstone extends TicFluids {

    public MoltenRedstone(Fluid fluid, Material material, int fogColor) { // 0xff0000
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote && entity instanceof EntityLivingBase) {
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HASTE, 20 * 60, 0, true, true));
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 20 * 60, 0, true, true));
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 60, 0, true, true));
        ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HUNGER, 20 * 60, 0, true, true));
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public void init() {
      super.init();
    }

  }

  /////////////////////////////////////////////////////////////////////////
  // Molten Ender
  /////////////////////////////////////////////////////////////////////////

  public static class MoltenEnder extends TicFluids {

    private static final Random rand = new Random();
    private static final ResourceLocation SOUND = new ResourceLocation("entity.endermen.teleport");

    public MoltenEnder(Fluid fluid, Material material, int fogColor) { // 0xff0000
      super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (!world.isRemote && entity.timeUntilPortal == 0) {
        teleportEntity(world, entity);
      }
      super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    private void teleportEntity(World world, Entity entity) {
      double origX = entity.posX, origY = entity.posY, origZ = entity.posZ;
      for (int i = 0; i < 5; i++) {
        double targetX = origX + rand.nextGaussian() * 16f;
        double targetY = -1;
        while (targetY < 1.1) {
          targetY = origY + rand.nextGaussian() * 8f;
        }
        double targetZ = origZ + rand.nextGaussian() * 16f;
        if (isClear(world, entity, targetX, targetY, targetZ) && doTeleport(world, entity, targetX, targetY, targetZ, rand)) {
          final SoundEvent sound = SoundEvent.REGISTRY.getObject(SOUND);
          if (sound != null) {
            world.playSound(null, origX, origY, origZ, sound, SoundCategory.BLOCKS, 1, 1);
            world.playSound(null, targetX, targetY, targetZ, sound, SoundCategory.BLOCKS, 1, 1);
          }
          entity.timeUntilPortal = 5;
          return;
        }
      }
    }

    private boolean isClear(World world, Entity entity, double targetX, double targetY, double targetZ) {
      double origX = entity.posX, origY = entity.posY, origZ = entity.posZ;
      try {
        entity.setPosition(targetX, targetY, targetZ);
        boolean result = world.checkNoEntityCollision(entity.getEntityBoundingBox(), entity)
            && world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty();
        return result;
      } finally {
        entity.setPosition(origX, origY, origZ);
      }
    }

    private static boolean doTeleport(World world, Entity entity, double targetX, double targetY, double targetZ, Random rand) {
      if (entity instanceof EntityLivingBase) {
        return doTeleport(world, (EntityLivingBase) entity, targetX, targetY, targetZ, rand);
      }

      if (entity.isRiding()) {
        entity.dismountRidingEntity();
      }
      if (entity.isBeingRidden()) {
        for (Entity passenger : entity.getPassengers()) {
          passenger.dismountRidingEntity();
        }
      }

      entity.setPositionAndRotation(targetX, targetY, targetZ, entity.rotationYaw, entity.rotationPitch);
      return true;
    }

    private static boolean doTeleport(World world, EntityLivingBase entity, double targetX, double targetY, double targetZ, Random rand) {
      float damage = 5f;
      if (entity.getMaxHealth() < 10f) {
        damage = 1f;
      }
      EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(entity, targetX, targetY, targetZ, damage);
      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
        if (rand.nextFloat() < 0.15F && world.getGameRules().getBoolean("doMobSpawning")) {
          EntityEndermite entityendermite = new EntityEndermite(world);
          entityendermite.setSpawnedByPlayer(true);
          entityendermite.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
          world.spawnEntityInWorld(entityendermite);
        }

        if (entity.isRiding()) {
          entity.dismountRidingEntity();
        }
        if (entity.isBeingRidden()) {
          for (Entity passenger : entity.getPassengers()) {
            passenger.dismountRidingEntity();
          }
        }

        if (entity instanceof EntityPlayerMP) {
          ((EntityPlayerMP) entity).connection.setPlayerLocation(event.getTargetX(), event.getTargetY(), event.getTargetZ(), entity.rotationYaw,
              entity.rotationPitch);
        } else {
          entity.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        }
        entity.fallDistance = 0.0F;
        entity.attackEntityFrom(DamageSource.fall, event.getAttackDamage());
        return true;
      }
      return false;
    }

    @Override
    public void init() {
      super.init();
    }

  }

}
