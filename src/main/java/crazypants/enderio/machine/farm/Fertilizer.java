package crazypants.enderio.machine.farm;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import com.enderio.core.common.util.BlockCoord;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;

public enum Fertilizer {

    /**
     * Not a fertilizer. Using this handler class any item can be "used" as a fertilizer. Meaning, fertilizing will
     * always fail.
     */
    NONE((ItemStack) null) {

        @Override
        public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
            return false;
        }
    },

    BONEMEAL(new ItemStack(Items.dye, 1, 15)) {

        public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
            return stack.getItem().onItemUse(stack, player, world, bc.x, bc.y, bc.z, 1, 0.5f, 0.5f, 0.5f);
        }
    },

    FORESTRY_FERTILIZER_COMPOUND(GameRegistry.findItem("Forestry", "fertilizerCompound")) {

        @Override
        public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
            return BONEMEAL.apply(stack, player, world, bc);
        }
    },

    BOTANIA_FLORAL_FERTILIZER(GameRegistry.findItem("Botania", "fertilizer")) {

        public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
            BlockCoord below = bc.getLocation(ForgeDirection.DOWN);
            Block belowBlock = below.getBlock(world);
            if (belowBlock == Blocks.dirt || belowBlock == Blocks.grass) {
                return stack.getItem().onItemUse(stack, player, world, below.x, below.y, below.z, 1, 0.5f, 0.5f, 0.5f);
            }
            return false;
        }

        public boolean applyOnAir() {
            return true;
        }

        public boolean applyOnPlant() {
            return false;
        }
    },

    METALLURGY_FERTILIZER(GameRegistry.findItem("Metallurgy", "fertilizer")) {

        @Override
        public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
            return BONEMEAL.apply(stack, player, world, bc);
        }
    },

    GARDEN_CORE_COMPOST(GameRegistry.findItem("GardenCore", "compost_pile")) {

        @Override
        public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
            return BONEMEAL.apply(stack, player, world, bc);
        }
    },

    MAGICALCROPS_FERTILIZER(GameRegistry.findItem("magicalcrops", "magicalcrops_MagicalCropFertilizer")) {

        @Override
        public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
            return BONEMEAL.apply(stack, player, world, bc);
        }
    };

    private ItemStack stack;

    private Fertilizer(Item item) {
        this(new ItemStack(item));
    }

    private Fertilizer(Block block) {
        this(new ItemStack(block));
    }

    private Fertilizer(ItemStack stack) {
        this.stack = stack == null || stack.getItem() == null ? null : stack;
        if (this.stack != null) {
            FarmStationContainer.slotItemsFertilizer.add(this.stack);
        }
    }

    private static final List<Fertilizer> validFertilizers = Lists.newArrayList();

    static {
        for (Fertilizer f : values()) {
            if (f.stack != null) {
                validFertilizers.add(f);
            }
        }
    }

    /**
     * Returns the singleton instance for the fertilizer that was given as parameter. If the given item is no
     * fertilizer, it will return an instance of Fertilizer.None.
     *
     */
    public static Fertilizer getInstance(ItemStack stack) {
        for (Fertilizer fertilizer : validFertilizers) {
            if (fertilizer.matches(stack)) {
                return fertilizer;
            }
        }
        return NONE;
    }

    /**
     * Returns true if the given item can be used as fertilizer.
     */
    public static boolean isFertilizer(ItemStack stack) {
        return getInstance(stack) != NONE;
    }

    protected boolean matches(ItemStack stack) {
        return OreDictionary.itemMatches(this.stack, stack, false);
    }

    /**
     * Tries to apply the given item on the given block using the type-specific method. SFX is played on success.
     *
     * If the item was successfully applied, the stacksize will be decreased if appropriate. The caller will need to
     * check for stacksize 0 and null the inventory slot if needed.
     *
     * @param stack
     * @param player
     * @param world
     * @param bc
     * @return true if the fertilizer was applied
     */
    public abstract boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc);

    public boolean applyOnAir() {
        return false;
    }

    public boolean applyOnPlant() {
        return true;
    }
}
