package crazypants.enderio.config;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NBTHelper
{
  public static String VAL = "V";
  public static String TYPE = "T";
  public static final int TAG_END = 0;
  public static final int TAG_BYTE = 1;
  public static final int TAG_SHORT = 2;
  public static final int TAG_INT = 3;
  public static final int TAG_LONG = 4;
  public static final int TAG_FLOAT = 5;
  public static final int TAG_DOUBLE = 6;
  public static final int TAG_BYTE_ARRAY = 7;
  public static final int TAG_STRING = 8;
  public static final int TAG_LIST = 9;
  public static final int TAG_COMPOUND = 10;
  public static final int TAG_INT_ARRAY = 11;

  public static abstract class NBT<T>
  {
    public abstract NBTBase getNBT(Object o);

    public abstract T getValue(NBTTagCompound o);

    public abstract boolean canConvert(Object o);
  }

  public static List<NBT> NBT_TYPES = new ArrayList<NBT>();

  static
  {
    NBT_TYPES.add(new NBT<Byte>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagByte((Byte)o);
      }

      @Override
      public Byte getValue(NBTTagCompound o)
      {
        return o.getByte(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof Byte;
      }
    });
    NBT_TYPES.add(new NBT<Short>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagShort((Short)o);
      }

      @Override
      public Short getValue(NBTTagCompound o)
      {
        return o.getShort(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof Short;
      }
    });
    NBT_TYPES.add(new NBT<Integer>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagInt((Integer)o);
      }

      @Override
      public Integer getValue(NBTTagCompound o)
      {
        return o.getInteger(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof Integer;
      }
    });
    NBT_TYPES.add(new NBT<Long>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagLong((Long)o);
      }

      @Override
      public Long getValue(NBTTagCompound o)
      {
        return o.getLong(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof Long;
      }
    });
    NBT_TYPES.add(new NBT<Double>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagDouble((Double)o);
      }

      @Override
      public Double getValue(NBTTagCompound o)
      {
        return o.getDouble(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof Double;
      }
    });
    NBT_TYPES.add(new NBT<Float>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagFloat((Float)o);
      }

      @Override
      public Float getValue(NBTTagCompound o)
      {
        return o.getFloat(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof Float;
      }
    });
    NBT_TYPES.add(new NBT<Boolean>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagByte((byte)((Boolean)o ? 1 : 0));
      }

      @Override
      public Boolean getValue(NBTTagCompound o)
      {
        return o.getBoolean(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof Boolean;
      }
    });
    NBT_TYPES.add(new NBT<String>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagString((String)o);
      }

      @Override
      public String getValue(NBTTagCompound o)
      {
        return o.getString(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof String;
      }
    });
    NBT_TYPES.add(new NBT<ItemStack>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return ((ItemStack)o).writeToNBT(new NBTTagCompound());
      }

      @Override
      public ItemStack getValue(NBTTagCompound o)
      {
        return ItemStack.loadItemStackFromNBT(o.getCompoundTag(VAL));
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof ItemStack;
      }
    });
    NBT_TYPES.add(new NBT<byte[]>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagByteArray((byte[])o);
      }

      @Override
      public byte[] getValue(NBTTagCompound o)
      {
        return o.getByteArray(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof byte[];
      }
    });
    NBT_TYPES.add(new NBT<int[]>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagIntArray((int[])o);
      }

      @Override
      public int[] getValue(NBTTagCompound o)
      {
        return o.getIntArray(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof int[];
      }
    });
    NBT_TYPES.add(new NBT<String[]>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        String[] array = (String[])o;
        NBTTagList list = new NBTTagList();
        for (String string : array)
        {
          list.appendTag(new NBTTagString(string));
        }
        return list;
      }

      @Override
      public String[] getValue(NBTTagCompound o)
      {
        NBTTagList list = o.getTagList(VAL, TAG_STRING);
        String[] result = new String[list.tagCount()];
        for (int i = 0; i < list.tagCount(); i++)
        {
          result[i] = list.getStringTagAt(i);
        }
        return result;
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof String[];
      }
    });
    NBT_TYPES.add(new NBT<ItemStack[]>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        ItemStack[] array = (ItemStack[])o;
        NBTTagList list = new NBTTagList();
        for (ItemStack stack : array)
        {
          list.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }
        return list;
      }

      @Override
      public ItemStack[] getValue(NBTTagCompound o)
      {
        NBTTagList list = o.getTagList(VAL, TAG_COMPOUND);
        ItemStack[] result = new ItemStack[list.tagCount()];
        for (int i = 0; i < list.tagCount(); i++)
        {
          result[i] = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
        }
        return result;
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof ItemStack[];
      }
    });
    NBT_TYPES.add(new NBT<List<ItemStack>>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        Collection<ItemStack> stacks = (Collection<ItemStack>)o;
        NBTTagList list = new NBTTagList();
        for (ItemStack stack : stacks)
        {
          list.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }
        return list;
      }

      @Override
      public List<ItemStack> getValue(NBTTagCompound o)
      {
        NBTTagList list = o.getTagList(VAL, TAG_COMPOUND);
        List<ItemStack> result = new ArrayList<ItemStack>();
        for (int i = 0; i < list.tagCount(); i++)
        {
          result.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
        }
        return result;
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof List && (((List)o).size() == 0 || ((List)o).get(0) instanceof ItemStack);
      }
    });
    NBT_TYPES.add(new NBT<NBTTagCompound>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return (NBTTagCompound)o;
      }

      @Override
      public NBTTagCompound getValue(NBTTagCompound o)
      {
        return o.getCompoundTag(VAL);
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof NBTTagCompound;
      }
    });
    NBT_TYPES.add(new NBT<Class>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        return new NBTTagString(((Class)o).getName());
      }

      @Override
      public Class getValue(NBTTagCompound o)
      {
        try
        {
          return Class.forName(o.getString(VAL));
        } catch (ClassNotFoundException e)
        {
          return null;
        }
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof Class;
      }
    });
    NBT_TYPES.add(new NBT<Class[]>()
    {
      @Override
      public NBTBase getNBT(Object o)
      {
        Class[] classes = (Class[])o;
        NBTTagList list = new NBTTagList();
        for (Class clazz : classes)
        {
          list.appendTag(new NBTTagString(clazz.getName()));
        }
        return list;
      }

      @Override
      public Class[] getValue(NBTTagCompound o)
      {
        NBTTagList list = o.getTagList(VAL, TAG_STRING);
        Class[] result = new Class[list.tagCount()];
        for (int i = 0; i < list.tagCount(); i++)
        {
          try
          {
            result[i] = Class.forName(list.getStringTagAt(i));
          } catch (ClassNotFoundException e)
          {
          }
        }
        return result;
      }

      @Override
      public boolean canConvert(Object o)
      {
        return o instanceof Class[];
      }
    });
  }

  public static NBTTagCompound writeToNBT(Object o)
  {
    NBTTagCompound tagCompound = new NBTTagCompound();
    for (byte i = 0; i < NBT_TYPES.size(); i++)
    {
      NBT nbt = NBT_TYPES.get(i);
      if (nbt.canConvert(o))
      {
        tagCompound.setTag(VAL, nbt.getNBT(o));
        tagCompound.setByte(TYPE, i);
        return tagCompound;
      }
    }
    return new NBTTagCompound();
  }

  public static Object rawFromNBT(NBTTagCompound tag)
  {
    if (tag == null || tag.hasNoTags()) return null;
    byte type = tag.getByte(TYPE);
    return NBT_TYPES.get(type).getValue(tag);
  }

  public static <T> T readFromNBT(NBTTagCompound tag)
  {
    byte type = tag.getByte(TYPE);
    return (T)NBT_TYPES.get(type).getValue(tag);
  }
}
