package crazypants.enderio.machine.painter;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import com.enderio.core.common.TileEntityEnder;
import com.enderio.core.common.util.Util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.item.IRotatableFacade;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.util.IFacade;

public class BlockPaintedGlowstone extends BlockEio
        implements ITileEntityProvider, IPaintedBlock, IFacade, IRotatableFacade {

    public static int renderId = -1;

    public static BlockPaintedGlowstone create() {
        BlockPaintedGlowstone result = new BlockPaintedGlowstone();
        result.init();
        return result;
    }

    private IIcon lastRemovedComponetIcon = null;

    private final Random rand = new Random();

    protected BlockPaintedGlowstone() {
        super(ModObject.blockPaintedGlowstone.unlocalisedName, TileEntityPaintedBlock.class, Material.glass);
        setCreativeTab(null);
        setStepSound(soundTypeGlass);
        setHardness(0.7F);
        setLightLevel(1.0f);
    }

    @Override
    protected void init() {
        GameRegistry
                .registerBlock(this, BlockItemPaintedGlowstone.class, ModObject.blockPaintedGlowstone.unlocalisedName);
        GameRegistry.registerTileEntity(
                TileEntityPaintedBlock.class,
                ModObject.blockPaintedGlowstone.unlocalisedName + "TileEntity");
        MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate());
    }

    public static ItemStack createItemStackForSourceBlock(Block block, int damage) {
        ItemStack result = new ItemStack(EnderIO.blockPaintedGlowstone, 1, damage);
        PainterUtil.setSourceBlock(result, block, damage);
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(PainterUtil.applyDefaultPaintedState(new ItemStack(item)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityPaintedBlock) {
            TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
            if (tef.getSourceBlock() != null) {
                return tef.getSourceBlock().colorMultiplier(world, x, y, z);
            }
        }
        return super.colorMultiplier(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        IIcon tex = null;

        TileEntityPaintedBlock cb = (TileEntityPaintedBlock) world
                .getTileEntity(target.blockX, target.blockY, target.blockZ);
        Block b = cb.getSourceBlock();
        if (b != null) {
            tex = b.getIcon(ForgeDirection.NORTH.ordinal(), cb.getSourceBlockMetadata());
        }
        if (tex == null) {
            tex = blockIcon;
        }
        lastRemovedComponetIcon = tex;
        addBlockHitEffects(world, effectRenderer, target.blockX, target.blockY, target.blockZ, target.sideHit, tex);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        IIcon tex = lastRemovedComponetIcon;
        byte b0 = 4;
        for (int j1 = 0; j1 < b0; ++j1) {
            for (int k1 = 0; k1 < b0; ++k1) {
                for (int l1 = 0; l1 < b0; ++l1) {
                    double d0 = x + (j1 + 0.5D) / b0;
                    double d1 = y + (k1 + 0.5D) / b0;
                    double d2 = z + (l1 + 0.5D) / b0;
                    int i2 = rand.nextInt(6);
                    EntityDiggingFX fx = new EntityDiggingFX(
                            world,
                            d0,
                            d1,
                            d2,
                            d0 - x - 0.5D,
                            d1 - y - 0.5D,
                            d2 - z - 0.5D,
                            this,
                            i2,
                            0).applyColourMultiplier(x, y, z);
                    fx.setParticleIcon(tex);
                    effectRenderer.addEffect(fx);
                }
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    private void addBlockHitEffects(World world, EffectRenderer effectRenderer, int x, int y, int z, int side,
            IIcon tex) {
        float f = 0.1F;
        double d0 = x + rand.nextDouble() * (getBlockBoundsMaxX() - getBlockBoundsMinX() - f * 2.0F)
                + f
                + getBlockBoundsMinX();
        double d1 = y + rand.nextDouble() * (getBlockBoundsMaxY() - getBlockBoundsMinY() - f * 2.0F)
                + f
                + getBlockBoundsMinY();
        double d2 = z + rand.nextDouble() * (getBlockBoundsMaxZ() - getBlockBoundsMinZ() - f * 2.0F)
                + f
                + getBlockBoundsMinZ();
        if (side == 0) {
            d1 = y + getBlockBoundsMinY() - f;
        } else if (side == 1) {
            d1 = y + getBlockBoundsMaxY() + f;
        } else if (side == 2) {
            d2 = z + getBlockBoundsMinZ() - f;
        } else if (side == 3) {
            d2 = z + getBlockBoundsMaxZ() + f;
        } else if (side == 4) {
            d0 = x + getBlockBoundsMinX() - f;
        } else if (side == 5) {
            d0 = x + getBlockBoundsMaxX() + f;
        }
        EntityDiggingFX digFX = new EntityDiggingFX(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, this, side, 0);
        digFX.applyColourMultiplier(x, y, z).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
        digFX.setParticleIcon(tex);
        effectRenderer.addEffect(digFX);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityPaintedBlock) {
            TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
            if (tef.getSourceBlock() != null) {
                return tef.getSourceBlock().getIcon(blockSide, tef.getSourceBlockMetadata());
            }
        }
        return Blocks.anvil.getIcon(world, x, y, z, blockSide);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister IIconRegister) {
        blockIcon = IIconRegister.registerIcon("enderio:conduitConnector");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityPaintedBlock();
    }

    @Override
    public boolean tryRotateFacade(World world, int x, int y, int z, ForgeDirection axis) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityPaintedBlock) {
            TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
            int oldMeta = tef.getSourceBlockMetadata();
            int newMeta = PainterUtil.rotateFacadeMetadata(tef.getSourceBlock(), oldMeta, axis);
            if (oldMeta != newMeta) {
                tef.setSourceBlockMetadata(newMeta);
                world.markBlockForUpdate(x, y, z);
                tef.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityEnder te, ItemStack drop) {
        TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
        if (tef != null) {
            ItemStack itemStack = createItemStackForSourceBlock(tef.getSourceBlock(), tef.getSourceBlockMetadata());
            drop.stackTagCompound = (NBTTagCompound) itemStack.stackTagCompound.copy();
        }
    }

    @Override
    public boolean doNormalDrops(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    public final class PainterTemplate extends BasicPainterTemplate {

        public PainterTemplate() {
            super(Blocks.glowstone, BlockPaintedGlowstone.this);
            MinecraftForge.EVENT_BUS.register(this);
        }

        @Override
        public boolean isValidPaintSource(ItemStack paintSource) {
            return super.isValidPaintSource(paintSource) && Util.getBlockFromItemId(paintSource).isOpaqueCube();
        }

        @Override
        public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
            ItemStack paintSource = MachineRecipeInput.getInputForSlot(1, inputs);
            if (paintSource == null) {
                return new ResultStack[0];
            }

            if (paintSource.getItem() == Item.getItemFromBlock(Blocks.glowstone)) {
                ItemStack stack = new ItemStack(Blocks.glowstone);
                stack.stackTagCompound = new NBTTagCompound();
                String tagName = "wasPainted";
                stack.stackTagCompound.setBoolean(tagName, true);
                return new ResultStack[] { new ResultStack(stack) };
            }

            return new ResultStack[] { new ResultStack(
                    createItemStackForSourceBlock(
                            Block.getBlockFromItem(paintSource.getItem()),
                            paintSource.getItemDamage())) };
        }

        @SubscribeEvent
        public void onTooltip(ItemTooltipEvent event) {
            if (event.itemStack != null && Block.getBlockFromItem(event.itemStack.getItem()) == Blocks.glowstone
                    && event.itemStack.stackTagCompound != null) {
                if (event.itemStack.stackTagCompound.getBoolean("wasPainted")) {
                    event.toolTip.add(EnderIO.lang.localize("painter.tooltip.wasPainted"));
                }
            }
        }
    }

    @Override
    public int getFacadeMetadata(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityPaintedBlock) {
            TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
            return tef.getBlockMetadata();
        }
        return 0;
    }

    @Override
    public Block getFacade(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IPaintableTileEntity) {
            Block sourceBlock = ((IPaintableTileEntity) te).getSourceBlock();
            if (sourceBlock != null) {
                return sourceBlock;
            }
        }
        return this;
    }

    @Override
    public Block getVisualBlock(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return getFacade(world, x, y, z, side.ordinal());
    }

    @Override
    public int getVisualMeta(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return getFacadeMetadata(world, x, y, z, side.ordinal());
    }

    @Override
    public boolean supportsVisualConnections() {
        return true;
    }
}
