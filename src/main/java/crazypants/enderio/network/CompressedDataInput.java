package crazypants.enderio.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

public class CompressedDataInput extends DataInputStream {

  public CompressedDataInput(byte[] compressed) {
    this(new ByteArrayInputStream(compressed));
  }

  public CompressedDataInput(InputStream in) {
    super(new BufferedInputStream(new InflaterInputStream(in)));
  }

  public int readVariable() throws IOException {
    int b, res = 0;
    int shift = 0;
    do {
      b = readUnsignedByte();
      res |= (b & 0x7F) << shift;
      shift += 7;
    } while(b >= 0x80);
    return res;
  }
}
