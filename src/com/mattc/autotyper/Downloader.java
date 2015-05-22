package com.mattc.autotyper;

import com.google.common.hash.Hashing;
import com.mattc.autotyper.meta.IORuntimeException;
import com.mattc.autotyper.util.Console;
import com.mattc.autotyper.util.FileAgeComparator;
import com.mattc.autotyper.util.IOUtils;
import com.mattc.autotyper.util.OS;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Convenience Utilities to handle downloading Files and Pastebins to Temp Files
 *
 * @author Matthew
 */
public class Downloader {

    public static final String PASTEBIN_URL = "http://www.pastebin.com/raw.php?i=%s";
    private static final Path CACHE_DIR = Paths.get(".cctyper-cache");
    private static final Path CACHE_HASHES = CACHE_DIR.resolve(".hashes");
    private static final Properties hashProps = new Properties();

    private static final FileAttribute<Set<PosixFilePermission>> FILE_PERMS = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-rw-rw-"));

    static {
        try {
            Files.createDirectories(CACHE_DIR);
            loadHashes();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Retrieve raw text file of Pastebin using the given Paste Code.
     *
     * @return Pastebin File as TempFile
     */
    public static Path getPastebin(String pastebinCode) {
        return getFile(toURL(String.format(PASTEBIN_URL, pastebinCode)));
    }

    /**
     * Download file of the given URL.
     *
     * @return File as TempFile
     */
    public static Path getFile(URL url) {
        try {
            return download(url, url.getFile().contains("=") ? url.getFile().split("=")[1] : generateFilename(url.toString()));
        } catch (final IOException e) {
            Console.exception(e);
        }

        return null;
    }

    /**
     * Download file of the given URL
     *
     * @return File as TempFile
     */
    public static Path getFile(String url) {
        return getFile(toURL(url));
    }

    private static URL toURL(String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException e) {
            Console.exception(e);
        }

        return null;
    }

    public static Path download(URL url, String filename) throws IOException {

        final Path temp = CACHE_DIR.resolve(filename + ".cctmp");

        if (Files.notExists(temp)) {
            cleanCache();

            BufferedInputStream bis = null;
            OutputStream out = null;

            if (OS.get().isUnixBased())
                Files.createFile(temp, FILE_PERMS);
            else
                Files.createFile(temp);

            try {
                Console.info("Downloading " + filename + " of " + url + "...");
                bis = new BufferedInputStream(url.openStream());
                out = Files.newOutputStream(temp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                final OS.MemoryUnit unit = OS.MemoryUnit.KILOBYTES;
                final byte[] buf = new byte[8192];
                long curTime, tarTime = System.currentTimeMillis();
                long total = 0;
                int accumulator = 0;

                for (int c = bis.read(buf); c != -1; c = bis.read(buf)) {
                    out.write(buf, 0, c);
                    accumulator += c;

                    if ((curTime = System.currentTimeMillis()) >= tarTime) {
                        // Inform of Bytes read every DOWNLOAD_LOG_DELAY seconds
                        // Inform of B/s every DOWNLOAD_LOG_DELAY seconds
                        total += accumulator;
                        final long totalAdj = OS.MemoryUnit.MEGABYTES.convert(total, OS.MemoryUnit.BYTES);
                        final long accAdj = unit.convert(accumulator, OS.MemoryUnit.BYTES);
                        final long rateAdj = unit.convert(Math.round((accumulator / (5.0d + (((double) curTime - (double) tarTime) / 1000.0d)))), OS.MemoryUnit.BYTES);
                        final String msg = String.format("%,d KB (at %,d KB/s) read for %s, Total: %,d MB...", accAdj, rateAdj, filename, totalAdj);
                        Console.info(msg);
                        accumulator = 0;
                        tarTime = System.currentTimeMillis() + 5000;
                    }
                }

                addHash(filename, hash(temp));
                Console.info("Finished!");
            } catch (final IOException e) {
                Console.exception(e);
                throw e;
            } finally {
                IOUtils.close(bis);
                IOUtils.close(out);
            }
        } else if (!compareHash(filename, hash(temp))) {
            Console.bigWarning(filename + " -- SHA-1 Hash is Incorrect! Re-downloading...");

            BufferedInputStream bis = null;
            OutputStream out = null;

            try {
                Console.info("Downloading " + filename + " of " + url + "...");
                bis = new BufferedInputStream(url.openStream());
                out = Files.newOutputStream(temp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                final OS.MemoryUnit unit = OS.MemoryUnit.KILOBYTES;
                final byte[] buf = new byte[8192];
                long curTime, tarTime = System.currentTimeMillis();
                long total = 0;
                int accumulator = 0;

                for (int c = bis.read(buf); c != -1; c = bis.read(buf)) {
                    out.write(buf, 0, c);
                    accumulator += c;

                    if ((curTime = System.currentTimeMillis()) >= tarTime) {
                        // Inform of Bytes read every DOWNLOAD_LOG_DELAY seconds
                        // Inform of B/s every DOWNLOAD_LOG_DELAY seconds
                        total += accumulator;
                        final long totalAdj = OS.MemoryUnit.MEGABYTES.convert(total, OS.MemoryUnit.BYTES);
                        final long accAdj = unit.convert(accumulator, OS.MemoryUnit.BYTES);
                        final long rateAdj = unit.convert(Math.round((accumulator / (5.0d + (((double) curTime - (double) tarTime) / 1000.0d)))), OS.MemoryUnit.BYTES);
                        final String msg = String.format("%,d KB (at %,d KB/s) read for %s, Total: %,d MB...", accAdj, rateAdj, filename, totalAdj);
                        Console.info(msg);
                        accumulator = 0;
                        tarTime = System.currentTimeMillis() + 5000;
                    }
                }

                addHash(filename, hash(temp));
                Console.info("Finished!");
            } catch (final IOException e) {
                Console.exception(e);
                throw e;
            } finally {
                IOUtils.close(bis);
                IOUtils.close(out);
            }
        } else {
            Console.info(temp.getFileName().toString() + " found in Cache! Loading cached version instead...");
        }

        return temp;
    }

    private static void cleanCache() throws IOException {
        List<Path> list = Files.list(CACHE_DIR)
                .filter((p) -> p.toString().endsWith(".cctmp"))
                .sorted(FileAgeComparator.INSTANCE)
                .collect(Collectors.toList());

        // Remove Oldest Files until size() < n
        // Retains n cached files
        while (list.size() >= 20) {
            Path p = list.remove(0);

            String filename = p.getFileName().toString();
            filename = filename.substring(0, filename.indexOf('.'));
            removeHash(filename);

            Files.deleteIfExists(p);
        }
    }

    private static void addHash(String name, long hash) {
        hashProps.setProperty(name, Long.toString(hash));
        saveHashes();
    }

    private static void removeHash(String name) {
        if (hashProps.remove(name) != null)
            saveHashes();
    }

    private static boolean compareHash(String name, long hash) {
        return hash == Long.parseLong(hashProps.getProperty(name));
    }

    private static void saveHashes() {
        try {
            OutputStream os = Files.newOutputStream(CACHE_HASHES, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            IOUtils.doThenClose(os, (stream) -> hashProps.store(stream, "\n Stores the hashes for all files currently in the cache.\n Used Later to determine integrity and for History.\n"));
        } catch (IOException e) {
            // ignore
        }
    }

    private static void loadHashes() throws IOException {
        if (Files.notExists(CACHE_HASHES))
            Files.createFile(CACHE_HASHES);

        InputStream is = Files.newInputStream(CACHE_HASHES);
        IOUtils.doThenClose(is, hashProps::load);
    }

    private static long hash(Path file) {
        try {
            return com.google.common.io.Files.hash(file.toFile(), Hashing.sha1()).asLong();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private static String generateFilename(String identifier) {
        return Integer.toString(Hashing.crc32c().newHasher().putString(identifier, StandardCharsets.UTF_8).hash().asInt(), Character.MAX_RADIX);
    }
}
