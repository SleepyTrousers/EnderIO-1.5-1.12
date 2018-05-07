package thaumcraft.api;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.crafting.IngredientNBTTC;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;

public class ThaumcraftApiHelper {
	
	public static final IAttribute CHAMPION_MOD = (new RangedAttribute((IAttribute)null, "tc.mobmod", -2D, -2D, 100D)).setDescription("Champion modifier").setShouldWatch(true);
	
	public static boolean areItemsEqual(ItemStack s1,ItemStack s2)
    {
		if (s1.isItemStackDamageable() && s2.isItemStackDamageable())
		{
			return s1.getItem() == s2.getItem();
		} else
			return s1.getItem() == s2.getItem() && s1.getItemDamage() == s2.getItemDamage();
    }
		
	public static boolean containsMatch(boolean strict, ItemStack[] inputs, List<ItemStack> targets)
    {
        for (ItemStack input : inputs)
        {
            for (ItemStack target : targets)
            {
                if (OreDictionary.itemMatches(target, input, strict) && ItemStack.areItemStackTagsEqual(target, input))
                {
                    return true;
                }
            }
        }
        return false;
    }
	
	public static boolean areItemStacksEqualForCrafting(ItemStack stack0, Object in)
    {
		if (stack0==null && in!=null) return false;
		if (stack0!=null && in==null) return false;
		if (stack0==null && in==null) return true;
		
		if (in instanceof Object[]) return true;
		
		if (in instanceof String) {
			List<ItemStack> l = OreDictionary.getOres((String) in,false);
			return containsMatch(false, new ItemStack[]{stack0}, l);
		}
		
		if (in instanceof ItemStack) {
			//nbt
			boolean t1= !stack0.hasTagCompound() || areItemStackTagsEqualForCrafting(stack0, (ItemStack) in);		
			if (!t1) return false;	
	        return OreDictionary.itemMatches((ItemStack) in, stack0, false);
		}
		
		return false;
    }
	
	public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem,ItemStack recipeItem)
    {
    	if (recipeItem == null || slotItem == null) return false;
    	if (recipeItem.getTagCompound()!=null && slotItem.getTagCompound()==null ) return false;
    	if (recipeItem.getTagCompound()==null ) return true;
    	
    	Iterator iterator = recipeItem.getTagCompound().getKeySet().iterator();
        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            if (slotItem.getTagCompound().hasKey(s)) {
            	if (!slotItem.getTagCompound().getTag(s).toString().equals(
            			recipeItem.getTagCompound().getTag(s).toString())) {
            		return false;
            	}
            } else {
        		return false;
            }
            
        }
        return true;
    }
   
    
    public static TileEntity getConnectableTile(World world, BlockPos pos, EnumFacing face) {
		TileEntity te = world.getTileEntity(pos.offset(face));
		if (te instanceof IEssentiaTransport && ((IEssentiaTransport)te).isConnectable(face.getOpposite())) 
			return te;
		else
			return null;
	}
    
    public static TileEntity getConnectableTile(IBlockAccess world, BlockPos pos, EnumFacing face) {
		TileEntity te = world.getTileEntity(pos.offset(face));
		if (te instanceof IEssentiaTransport && ((IEssentiaTransport)te).isConnectable(face.getOpposite())) 
			return te;
		else
			return null;
	}  
    
	public static RayTraceResult rayTraceIgnoringSource(World world, Vec3d v1, Vec3d v2, 
			boolean bool1, boolean bool2, boolean bool3)
	{
	    if (!Double.isNaN(v1.x) && !Double.isNaN(v1.y) && !Double.isNaN(v1.z))
	    {
	        if (!Double.isNaN(v2.x) && !Double.isNaN(v2.y) && !Double.isNaN(v2.z))
	        {
	            int i = MathHelper.floor(v2.x);
	            int j = MathHelper.floor(v2.y);
	            int k = MathHelper.floor(v2.z);
	            int l = MathHelper.floor(v1.x);
	            int i1 = MathHelper.floor(v1.y);
	            int j1 = MathHelper.floor(v1.z);
	            IBlockState block = world.getBlockState(new BlockPos(l, i1, j1));
	
	            RayTraceResult rayTraceResult2 = null;
	            int k1 = 200;
	
	            while (k1-- >= 0)
	            {
	                if (Double.isNaN(v1.x) || Double.isNaN(v1.y) || Double.isNaN(v1.z))
	                {
	                    return null;
	                }
	
	                if (l == i && i1 == j && j1 == k)
	                {
	                    continue;
	                }
	
	                boolean flag6 = true;
	                boolean flag3 = true;
	                boolean flag4 = true;
	                double d0 = 999.0D;
	                double d1 = 999.0D;
	                double d2 = 999.0D;
	
	                if (i > l)
	                {
	                    d0 = (double)l + 1.0D;
	                }
	                else if (i < l)
	                {
	                    d0 = (double)l + 0.0D;
	                }
	                else
	                {
	                    flag6 = false;
	                }
	
	                if (j > i1)
	                {
	                    d1 = (double)i1 + 1.0D;
	                }
	                else if (j < i1)
	                {
	                    d1 = (double)i1 + 0.0D;
	                }
	                else
	                {
	                    flag3 = false;
	                }
	
	                if (k > j1)
	                {
	                    d2 = (double)j1 + 1.0D;
	                }
	                else if (k < j1)
	                {
	                    d2 = (double)j1 + 0.0D;
	                }
	                else
	                {
	                    flag4 = false;
	                }
	
	                double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = v2.x - v1.x;
                    double d7 = v2.y - v1.y;
                    double d8 = v2.z - v1.z;

                    if (flag6)
                    {
                        d3 = (d0 - v1.x) / d6;
                    }

                    if (flag3)
                    {
                        d4 = (d1 - v1.y) / d7;
                    }

                    if (flag4)
                    {
                        d5 = (d2 - v1.z) / d8;
                    }

                    if (d3 == -0.0D)
                    {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D)
                    {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D)
                    {
                        d5 = -1.0E-4D;
                    }
	
	                EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5)
                    {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        v1 = new Vec3d(d0, v1.y + d7 * d3, v1.z + d8 * d3);
                    }
                    else if (d4 < d5)
                    {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        v1 = new Vec3d(v1.x + d6 * d4, d1, v1.z + d8 * d4);
                    }
                    else
                    {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        v1 = new Vec3d(v1.x + d6 * d5, v1.y + d7 * d5, d2);
                    }

                    l = MathHelper.floor(v1.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(v1.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(v1.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
	
	                IBlockState block1 = world.getBlockState(new BlockPos(l, i1, j1));
	
	                if (!bool2 || block1.getCollisionBoundingBox(world, new BlockPos(l, i1, j1)) != null)
	                {
	                    if (block1.getBlock().canCollideCheck(block1, bool1))
	                    {
	                        RayTraceResult rayTraceResult1 = block1.collisionRayTrace(world, new BlockPos(l, i1, j1), v1, v2);
	
	                        if (rayTraceResult1 != null)
	                        {
	                            return rayTraceResult1;
	                        }
	                    }
	                    else
	                    {
	                        rayTraceResult2 = new RayTraceResult(RayTraceResult.Type.MISS, v1, enumfacing, new BlockPos(l, i1, j1));
	                    }
	                }
	            }
	
	            return bool3 ? rayTraceResult2 : null;
	        }
	        else
	        {
	            return null;
	        }
	    }
	    else
	    {
	        return null;
	    }
	}
	
	public static Object getNBTDataFromId(NBTTagCompound nbt, byte id, String key) {
		switch (id) {
		case 1: return nbt.getByte(key);
		case 2: return nbt.getShort(key);
		case 3: return nbt.getInteger(key);
		case 4: return nbt.getLong(key);
		case 5: return nbt.getFloat(key);
		case 6: return nbt.getDouble(key);
		case 7: return nbt.getByteArray(key);
		case 8: return nbt.getString(key);
		case 9: return nbt.getTagList(key, (byte) 10);
		case 10: return nbt.getTag(key);
		case 11: return nbt.getIntArray(key);
		default: return null;
		}
	}
	
	public static int setByteInInt(int data, byte b, int index)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(0,data);
		bb.put(index, b);
	    return bb.getInt(0);
	}
	
	public static byte getByteInInt(int data, int index) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(0,data);
		return bb.get(index);
	}	
	
	public static long setByteInLong(long data, byte b, int index)
	{
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(0,data);
		bb.put(index, b);
	    return bb.getLong(0);
	}
	
	public static byte getByteInLong(long data, int index) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(0,data);
		return bb.get(index);
	}	
	
	public static int setNibbleInInt(int data, int nibble, int nibbleIndex)
	{
	    int shift = nibbleIndex * 4;
	    return (data & ~(0xf << shift)) | (nibble << shift);
	}
	
	public static int getNibbleInInt(int data, int nibbleIndex) {
		return (data >> (nibbleIndex << 2)) & 0xF;
	}

	/**
	 * Create a crystal itemstack from a sent aspect. 
	 * @param aspect
	 * @param stackSize stack size
	 * @return
	 */
	public static ItemStack makeCrystal(Aspect aspect, int stackSize) {
		if (aspect==null) return null;
		ItemStack is = new ItemStack(ItemsTC.crystalEssence,stackSize,0);
		((ItemGenericEssentiaContainer)ItemsTC.crystalEssence).setAspects(is, new AspectList().add(aspect, 1));
		return is;
	}

	/**
	 * Create a crystal itemstack from a sent aspect. Sending a null will result in a balanced shard (one of each primal).
	 * @param aspect
	 * @return
	 */
	public static ItemStack makeCrystal(Aspect aspect) {
		return makeCrystal(aspect,1);
	}

	public static List<ItemStack> getOresWithWildCards(String oreDict) {
		if (oreDict.trim().endsWith("*")) {
			ArrayList<ItemStack> ores = new ArrayList<>(); 
			String[] names = OreDictionary.getOreNames();
			String m = oreDict.trim().replaceAll("\\*", "");
			for (String name:names) {
				if (name.startsWith(m)) {
					ores.addAll(OreDictionary.getOres(name,false));
				}
			}
			return ores;
		} else
			return  OreDictionary.getOres(oreDict,false);
	}

	
	public static Ingredient getIngredient(Object obj)
    {
        if (obj!=null && obj instanceof ItemStack && ((ItemStack)obj).hasTagCompound())
            return new IngredientNBTTC((ItemStack)obj);
        else 
        	return CraftingHelper.getIngredient(obj);
    }

	public static IItemHandler getItemHandlerAt(World world, BlockPos pos, EnumFacing side) {
		Pair<IItemHandler, Object> dest = VanillaInventoryCodeHooks.getItemHandler(world, pos.getX(), pos.getY(), pos.getZ(), side);
		if (dest!=null && dest.getLeft()!=null) {
			return dest.getLeft();
		} else {
			TileEntity tileentity = world.getTileEntity(pos);
	        if (tileentity != null && tileentity instanceof IInventory) {            	
	        	return wrapInventory ((IInventory) tileentity, side);
	        }
		}
		return null;
	}

	public static IItemHandler wrapInventory(IInventory inventory, EnumFacing side) {
		return inventory instanceof ISidedInventory? new SidedInvWrapper((ISidedInventory) inventory, side) : new InvWrapper((IInventory) inventory);
	}
	
}
