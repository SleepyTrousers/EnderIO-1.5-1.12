package crazypants.enderio.machine.painter;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPaintedFence extends BlockFence implements ITileEntityProvider, IPaintedBlock {

    public static BlockPaintedFence create() {
        BlockPaintedFence result = new BlockPaintedFence();
        result.init();
        return result;
    }

    private IIcon lastRemovedComponetIcon = null;

    private Random rand = new Random();

    public BlockPaintedFence() {
        super(ModObject.blockPaintedFence.unlocalisedName, Material.wood);
        setBlockName(ModObject.blockPaintedFence.unlocalisedName);
        setHardness(2.0F);
        setResistance(5.0F);
        setStepSound(soundTypeWood);
        setCreativeTab(null);
    }

    private void init() {
        GameRegistry.registerBlock(this, BlockItemPaintedFence.class, ModObject.blockPaintedFence.unlocalisedName);
        GameRegistry.registerTileEntity(
                TileEntityPaintedBlock.class, ModObject.blockPaintedFence.unlocalisedName + "TileEntity");
        MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate());
    }

    public static ItemStack createItemStackForSourceBlock(Block source, int sourceMeta) {
        ItemStack result = new ItemStack(EnderIO.blockPaintedFence, 1, sourceMeta);
        PainterUtil.setSourceBlock(result, source, sourceMeta);
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(PainterUtil.applyDefaultPaintedState(new ItemStack(item)));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        IIcon tex = null;

        TileEntityPaintedBlock cb =
                (TileEntityPaintedBlock) world.getTileEntity(target.blockX, target.blockY, target.blockZ);
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
                                    world, d0, d1, d2, d0 - x - 0.5D, d1 - y - 0.5D, d2 - z - 0.5D, this, i2, 0)
                            .applyColourMultiplier(x, y, z);
                    fx.setParticleIcon(tex);
                    effectRenderer.addEffect(fx);
                }
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    private void addBlockHitEffects(
            World world, EffectRenderer effectRenderer, int x, int y, int z, int side, IIcon tex) {
        float f = 0.1F;
        double d0 = x
                + rand.nextDouble() * (getBlockBoundsMaxX() - getBlockBoundsMinX() - f * 2.0F)
                + f
                + getBlockBoundsMinX();
        double d1 = y
                + rand.nextDouble() * (getBlockBoundsMaxY() - getBlockBoundsMinY() - f * 2.0F)
                + f
                + getBlockBoundsMinY();
        double d2 = z
                + rand.nextDouble() * (getBlockBoundsMaxZ() - getBlockBoundsMinZ() - f * 2.0F)
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
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityPaintedBlock) {
            TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
            if (tef.getSourceBlock() != null) {
                return Math.min(
                        super.getLightOpacity(world, x, y, z),
                        tef.getSourceBlock().getLightOpacity());
            }
        }
        return super.getLightOpacity(world, x, y, z);
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        Block id = world.getBlock(x, y, z);
        if (id == this) {
            return true;
        }
        return super.canPlaceTorchOnTop(world, x, y, z);
    }

    @Override
    public boolean canConnectFenceTo(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        Block l = par1IBlockAccess.getBlock(par2, par3, par4);
        if (l == EnderIO.blockPaintedFenceGate) {
            return true;
        }
        return super.canConnectFenceTo(par1IBlockAccess, par2, par3, par4);
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
        } else {
            System.out.println("BlockCustFence: No tile entity.");
        }
        return Blocks.anvil.getIcon(world, x, y, z, blockSide);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IIconRegister) {
        blockIcon = par1IIconRegister.registerIcon("enderio:conduitConnector");
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileEntityPaintedBlock();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {

        Block b = PainterUtil.getSourceBlock(stack);

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityPaintedBlock) {
            TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
            tef.setSourceBlock(b);
            tef.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(stack));
        }
        world.markBlockForUpdate(x, y, z);
    }

    /**
     * Remove the tile entity too.
     */
    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {

        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
            TileEntity te = world.getTileEntity(x, y, z);

            if (te instanceof TileEntityPaintedBlock) {
                TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;

                ItemStack itemStack = createItemStackForSourceBlock(tef.getSourceBlock(), tef.getSourceBlockMetadata());

                float f = 0.7F;
                double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
                entityitem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityitem);

            } else {
                System.out.println("dropBlockAsItem_do: No tile entity.");
            }
        }

        world.removeTileEntity(x, y, z);
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 0; // need to do custom dropping to maintain source metadata
    }

    /**
     * Called when the block receives a BlockEvent - see World.addBlockEvent. By
     * default, passes it on to the tile entity at this location. Args: world, x,
     * y, z, blockID, EventID, event parameter
     */
    @Override
    public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
        super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
        TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);
        return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
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

    public final class PainterTemplate extends BasicPainterTemplate {

        public PainterTemplate() {
            super(Blocks.fence, Blocks.nether_brick_fence, BlockPaintedFence.this);
        }

        @Override
        public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
            ItemStack paintSource = MachineRecipeInput.getInputForSlot(1, inputs);
            return new ResultStack[] {
                new ResultStack(createItemStackForSourceBlock(
                        getBlockFromItem(paintSource.getItem()), paintSource.getItemDamage()))
            };
        }
    }
}
