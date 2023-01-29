package crazypants.enderio.fluid;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.config.Config;

public class BlockFluidEio extends BlockFluidClassic {

    public static class FireWater extends BlockFluidEio {

        protected FireWater(Fluid fluid, Material material) {
            super(fluid, material);
        }

        @Override
        public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
            if (!world.isRemote) {
                entity.setFire(50);
                super.onEntityCollidedWithBlock(world, x, y, z, entity);
            }
        }

        @Override
        public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
            return true;
        }

        @Override
        public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
            return true;
        }

        @Override
        public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
            return 60;
        }
    }

    public static class Hootch extends BlockFluidEio {

        protected Hootch(Fluid fluid, Material material) {
            super(fluid, material);
        }

        @Override
        public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
            if (!world.isRemote && entity instanceof EntityLivingBase)
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.confusion.id, 150, 0, true));
            super.onEntityCollidedWithBlock(world, x, y, z, entity);
        }

        @Override
        public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
            return true;
        }

        @Override
        public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
            return true;
        }

        @Override
        public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
            return 1;
        }

        @Override
        public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
            return 60;
        }
    }

    public static class RocketFuel extends BlockFluidEio {

        protected RocketFuel(Fluid fluid, Material material) {
            super(fluid, material);
        }

        @Override
        public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
            if (!world.isRemote && entity instanceof EntityLivingBase)
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.jump.id, 150, 3, true));

            super.onEntityCollidedWithBlock(world, x, y, z, entity);
        }

        @Override
        public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
            return true;
        }

        @Override
        public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
            return true;
        }

        @Override
        public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
            return 1;
        }

        @Override
        public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
            return 60;
        }
    }

    public static class NutrientDistillation extends BlockFluidEio {

        private static final String EIO_LAST_FOOD_BOOST = "eioLastFoodBoost";

        protected NutrientDistillation(Fluid fluid, Material material) {
            super(fluid, material);
        }

        @Override
        public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
            if (!world.isRemote && entity instanceof EntityLivingBase) {
                long time = entity.worldObj.getTotalWorldTime();
                EntityPlayerMP player = (EntityPlayerMP) entity;
                if (time % Config.nutrientFoodBoostDelay == 0
                        && player.getEntityData().getLong(EIO_LAST_FOOD_BOOST) != time) {
                    player.getFoodStats().addStats(1, 0.1f);
                    player.getEntityData().setLong(EIO_LAST_FOOD_BOOST, time);
                }
                super.onEntityCollidedWithBlock(world, x, y, z, entity);
            }
        }
    }

    public static class CloudSeedConcentrated extends BlockFluidEio {

        protected CloudSeedConcentrated(Fluid fluid, Material material) {
            super(fluid, material);
        }

        @Override
        public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
            if (!world.isRemote && entity instanceof EntityLivingBase) {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.blindness.id, 40, 0, true));
            }
            super.onEntityCollidedWithBlock(world, x, y, z, entity);
        }
    }

    public static class VapourOfLevity extends BlockFluidEio {

        private static final int[] COLORS = { 0x0c82d0, 0x90c8ec, 0x5174ed, 0x0d2f65, 0x4accee };

        protected VapourOfLevity(Fluid fluid, Material material, int fogColor) {
            super(fluid, material);
        }

        @Override
        public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
            if (entity instanceof EntityPlayer || (!world.isRemote && entity instanceof EntityLivingBase)) {
                ((EntityLivingBase) entity).motionY += 0.1;
            }
            super.onEntityCollidedWithBlock(world, x, y, z, entity);
        }

        @Override
        public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
            if (rand.nextFloat() < .5f) {
                int col = COLORS[rand.nextInt(COLORS.length)];
                world.spawnParticle(
                        "reddust",
                        x,
                        y,
                        z,
                        (col >> 16 & 255) / 255d,
                        (col >> 8 & 255) / 255d,
                        (col & 255) / 255d);
            }
        }
    }

    public static BlockFluidEio create(Fluid fluid, Material material) {
        BlockFluidEio res = new BlockFluidEio(fluid, material);
        res.init();
        fluid.setBlock(res);
        return res;
    }

    protected Fluid fluid;

    protected BlockFluidEio(Fluid fluid, Material material) {
        super(fluid, material);
        this.fluid = fluid;
        setBlockName(fluid.getUnlocalizedName());
    }

    protected void init() {
        GameRegistry.registerBlock(this, "block" + StringUtils.capitalize(fluidName));
    }

    @SideOnly(Side.CLIENT)
    protected IIcon[] icons;

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side != 0 && side != 1 ? this.icons[1] : this.icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[] { iconRegister.registerIcon("enderio:" + fluidName + "_still"),
                iconRegister.registerIcon("enderio:" + fluidName + "_flow") };

        fluid.setIcons(icons[0], icons[1]);
    }

    @Override
    public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
        if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
            return false;
        }
        return super.canDisplace(world, x, y, z);
    }

    @Override
    public boolean displaceIfPossible(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z).getMaterial().isLiquid()) {
            return false;
        }
        return super.displaceIfPossible(world, x, y, z);
    }
}
