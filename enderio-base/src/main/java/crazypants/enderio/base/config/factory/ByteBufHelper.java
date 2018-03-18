package crazypants.enderio.base.config.factory;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;

enum ByteBufHelper {
  INTEGER {
    @Override
    protected void saveValue(ByteBuf buf, @Nonnull Object value) {
      buf.writeInt((int) value);
    }

    @Override
    protected Object readValue(ByteBuf buf) {
      return buf.readInt();
    }
  },
  DOUBLE {
    @Override
    protected void saveValue(ByteBuf buf, @Nonnull Object value) {
      buf.writeDouble((double) value);
    }

    @Override
    protected Object readValue(ByteBuf buf) {
      return buf.readDouble();
    }
  },
  FLOAT {
    @Override
    protected void saveValue(ByteBuf buf, @Nonnull Object value) {
      buf.writeFloat((float) value);
    }

    @Override
    protected Object readValue(ByteBuf buf) {
      return buf.readFloat();
    }
  },
  STRING {
    @Override
    protected void saveValue(ByteBuf buf, @Nonnull Object value) {
      final byte[] vbytes = ((String) value).getBytes(Charset.forName("UTF-8"));
      if (vbytes.length > 0x7FFF) {
        throw new RuntimeException("String too long");
      }
      buf.writeShort(vbytes.length);
      buf.writeBytes(vbytes);
    }

    @Override
    protected Object readValue(ByteBuf buf) {
      final int len = buf.readShort();
      final byte[] bytes = new byte[len];
      buf.readBytes(bytes, 0, len);
      return new String(bytes, Charset.forName("UTF-8"));
    }
  },
  STRING127 {
    @Override
    protected void saveValue(ByteBuf buf, @Nonnull Object value) {
      final byte[] vbytes = ((String) value).getBytes(Charset.forName("UTF-8"));
      if (vbytes.length > 0x7F) {
        throw new RuntimeException("String too long");
      }
      buf.writeByte(vbytes.length); // Important: Keep in sync with ENDMARKER
      buf.writeBytes(vbytes);
    }

    @Override
    protected Object readValue(ByteBuf buf) {
      final int len = buf.readByte();
      final byte[] bytes = new byte[len];
      buf.readBytes(bytes, 0, len);
      return new String(bytes, Charset.forName("UTF-8"));
    }
  },
  BOOLEAN {
    @Override
    protected void saveValue(ByteBuf buf, @Nonnull Object value) {
      buf.writeBoolean((boolean) value);
    }

    @Override
    protected Object readValue(ByteBuf buf) {
      return buf.readBoolean();
    }
  },
  /**
   * A marker that is written instead of a {@link #STRING127} to indicate the end of a list. When read, it will return <code>null</code> if there's a
   * {@link #STRING127} in the buffer (and <em>not</em> consume bytes from the buffer) or <code>true</code> if there's an end marker (and consume it from the
   * buffer).
   */
  ENDMARKER {
    @Override
    protected void saveValue(ByteBuf buf, @Nonnull Object value) {
      buf.writeByte(0);
    }

    @Override
    protected Object readValue(ByteBuf buf) {
      buf.markReaderIndex();
      final int val = buf.readByte();
      if (val == 0) {
        return Boolean.TRUE;
      } else {
        buf.resetReaderIndex();
        return null;
      }
    }
  },
  NONE {
    @Override
    protected void saveValue(ByteBuf buf, @Nonnull Object value) {
    }

    @Override
    protected Object readValue(ByteBuf buf) {
      return null;
    }
  };

  protected abstract void saveValue(final ByteBuf buf, @Nonnull Object value);

  protected abstract Object readValue(final ByteBuf buf);
}