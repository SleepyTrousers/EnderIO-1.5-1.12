package crazypants.enderio.material.endergy;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;

public class BlockIngotStorageEndergy extends BlockEio implements IAdvancedTooltipProvider {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public static BlockIngotStorageEndergy create() {
        BlockIngotStorageEndergy res = new BlockIngotStorageEndergy();
        res.init();
        return res;
    }

    private BlockIngotStorageEndergy() {
        super(ModObject.blockIngotStorageEndergy.unlocalisedName, null, Material.iron);
        setStepSound(soundTypeMetal);
    }

    @Override
    protected void init() {
        GameRegistry.registerBlock(
                this,
                BlockItemIngotStorageEndergy.class,
                ModObject.blockIngotStorageEndergy.unlocalisedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icons = new IIcon[AlloyEndergy.values().length];
        for (AlloyEndergy alloy : AlloyEndergy.values()) {
            icons[alloy.ordinal()] = register.registerIcon(alloy.iconKey + "Block");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        meta = MathHelper.clamp_int(meta, 0, AlloyEndergy.values().length - 1);
        return icons[meta];
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        return AlloyEndergy.values()[world.getBlockMetadata(x, y, z)].getHardness();
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX,
            double explosionY, double explosionZ) {
        return getBlockHardness(world, x, y, z) * 2.0f; // vanilla default is / 5.0f, this means hardness*2 = resistance
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
        return true;
    }

    @Override
    protected boolean shouldWrench(World world, int x, int y, int z, EntityPlayer entityPlayer, int side) {
        return false;
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        list.add(EnderIO.lang.localize("tooltip.isBeaconBase"));
    }

    @Override
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {}

    @Override
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {}
}
