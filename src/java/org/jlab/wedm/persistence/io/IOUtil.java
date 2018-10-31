package org.jlab.wedm.persistence.io;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

/**
 * IO Utilities.
 *
 * @author slominskir
 */
public final class IOUtil {

    private static final Logger LOGGER = Logger.getLogger(
            IOUtil.class.getName());

    private IOUtil() {
        // Can't instantiate publicly
    }

    /**
     * Closes a Closeable without generating any checked Exceptions.
     * If an IOException does occur while closing it is logged as a WARNING.
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
     * Fully reads in a file and returns an array of the bytes representing the
     * file. Be careful reading in large files because they may result in an
     * OutOfMemoryError.
     *
     * This method uses the File length to efficiently allocate memory.
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
     * Encodes an array of bytes to base64.
     *
     * @param data The bytes
     * @return A base64 encoded String
     */
    public static String encodeBase64(byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }
}
