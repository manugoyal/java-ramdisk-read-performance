import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public class Main {
    // The file to be reading on ramdisk
    static File ramdiskFile = new File("/Volumes/ramdisk/largefile");
    // The size of the file
    static long fileSize = (long) (Math.pow(2, 30) * 2);

    static void createFile() throws IOException {
        // Create a large file in the ramdisk if it doesn't already exist
        if (!ramdiskFile.exists()) {
            FileChannel fc = FileChannel.open(ramdiskFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            int bufDiv = 100;
            int bufSize = (int) (fileSize / bufDiv);
            byte[] buf = new byte[bufSize];
            for (int i = 0; i < bufDiv; ++i) {
                fc.write(ByteBuffer.wrap(buf));
            }
            fc.close();
        }
    }

    static void timeFileChannelRead(int bufSize) throws IOException {
        byte[] buf = new byte[bufSize];
        FileChannel fc = FileChannel.open(ramdiskFile.toPath(), StandardOpenOption.READ);
        try {
            int result;
            long startTime = System.currentTimeMillis();
            do {
                result = fc.read(ByteBuffer.wrap(buf));
            } while (result != -1);
            long endTime = System.currentTimeMillis();
            System.out.println("timeFileChannelRead with bufSize = " + bufSize + " took " + (endTime - startTime) + "ms");
        } finally {
            fc.close();
        }
    }

    static void timeByteBufferRead(int bufSize) throws IOException {
        byte[] buf = new byte[bufSize];
        FileChannel fc = FileChannel.open(ramdiskFile.toPath(), StandardOpenOption.READ);
        try {
            MappedByteBuffer byteBuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, ramdiskFile.length());
            long startTime = System.currentTimeMillis();
            do {
                byteBuf.get(buf, 0, Math.min(buf.length, byteBuf.remaining()));
            } while (byteBuf.remaining() > 0);
            long endTime = System.currentTimeMillis();
            System.out.println("timeByteBufferRead with bufSize = " + bufSize + " took " + (endTime - startTime) + "ms");
        } finally {
            fc.close();
        }
    }

    public static void main(String[] args) throws IOException {
        createFile();

        timeFileChannelRead((int) Math.pow(2, 10));
        timeByteBufferRead((int) Math.pow(2, 10));
        System.out.println();
        timeFileChannelRead((int) Math.pow(2, 11));
        timeByteBufferRead((int) Math.pow(2, 11));
        System.out.println();
        timeFileChannelRead((int) Math.pow(2, 12));
        timeByteBufferRead((int) Math.pow(2, 12));
        System.out.println();
        timeFileChannelRead((int) Math.pow(2, 13));
        timeByteBufferRead((int) Math.pow(2, 13));
        System.out.println();
        timeFileChannelRead((int) Math.pow(2, 14));
        timeByteBufferRead((int) Math.pow(2, 14));
        System.out.println();
        timeFileChannelRead((int) Math.pow(2, 20));
        timeByteBufferRead((int) Math.pow(2, 20));
        System.out.println();
    }
}
