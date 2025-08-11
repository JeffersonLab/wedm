package org.jlab.wedm.persistence.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IO Utilities.
 *
 * @author slominskir
 */
public final class IOUtil {

  private static final Logger LOGGER = Logger.getLogger(IOUtil.class.getName());

  private IOUtil() {
    // Can't instantiate publicly
  }

  /**
   * Closes a Closeable without generating any checked Exceptions. If an IOException does occur
   * while closing it is logged as a WARNING.
   *
   * @param c The Closeable
   */
  public static void closeQuietly(Closeable c) {
    if (c != null) {
      try {
        c.close();
      } catch (IOException e) {
        // Supressed, but logged
        LOGGER.log(Level.WARNING, "Unable to close resource.", e);
      }
    }
  }

  /**
   * Fully reads in a file and returns an array of the bytes representing the file. Be careful
   * reading in large files because they may result in an OutOfMemoryError.
   *
   * <p>This method uses the File length to efficiently allocate memory.
   *
   * @param file The file to load into memory.
   * @return The bytes
   * @throws IOException If an error occurs reading in the file.
   */
  public static byte[] fileToBytes(final File file) throws IOException {
    final byte[] bytes = new byte[(int) file.length()];

    DataInputStream dis = null;

    try {
      dis = new DataInputStream(new FileInputStream(file));

      dis.readFully(bytes);
    } finally {
      closeQuietly(dis);
    }

    return bytes;
  }

  /**
   * Read input stream as byte array
   *
   * @param stream Input stream
   * @return The bytes
   * @throws IOException on error
   */
  public static byte[] streamToBytes(final InputStream stream) throws IOException {
    final ByteArrayOutputStream buf = new ByteArrayOutputStream();

    try {
      final byte[] section = new byte[4096];
      int len;
      while ((len = stream.read(section)) >= 0) buf.write(section, 0, len);
      buf.flush();
      buf.close();
    } finally {
      closeQuietly(stream);
    }

    return buf.toByteArray();
  }

  /**
   * Encodes an array of bytes to base64.
   *
   * @param data The bytes
   * @return A base64 encoded String
   */
  public static String encodeBase64(byte[] data) {
    Base64.Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(data);
  }
}
