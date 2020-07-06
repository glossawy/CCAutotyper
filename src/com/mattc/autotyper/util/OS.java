package com.mattc.autotyper.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Small Utility to Determine OS and OS Architecture based on an enumeration of
 * supported OS's and Architectures. Plus other utilities.
 *
 * @author Glossawy
 */
public final class OS {

    /**
     * Convenience class for dealing with conversions to and of different units of
     * memory storage. Includes SI Units (Kilobytes, Megabytes, Gigabytes) and non-SI
     * Units (Kibibytes, Mebibytes, Gibibytes) assisted by the common base unit they
     * share, the Byte. <br />
     * <br />
     * Currently this class only deals with 64-bit Integer Values (Although 32-bit
     * Integers can be used).
     *
     * @author Glossawy
     */
    public enum MemoryUnit {
        BYTES("B") {
            @Override
            public long toBytes(long amt) {
                return amt;
            }

            @Override
            public long toKilobytes(long amt) {
                return amt / C_KB;
            }

            @Override
            public long toKibibytes(long amt) {
                return amt / C_KiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return amt / C_MB;
            }

            @Override
            public long toMebibytes(long amt) {
                return amt / C_MiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return amt / C_GB;
            }

            @Override
            public long toGibibytes(long amt) {
                return amt / C_GiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toBytes(amt);
            }
        },
        KILOBYTES("KB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_KB;
            }

            @Override
            public long toKilobytes(long amt) {
                return amt;
            }

            @Override
            public long toKibibytes(long amt) {
                return (amt * C_KB) / C_KiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return amt / C_KB;
            }

            @Override
            public long toMebibytes(long amt) {
                return (amt * C_KB) / C_MiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return amt / C_MB;
            }

            @Override
            public long toGibibytes(long amt) {
                return (amt * C_KB) / C_GiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toKilobytes(amt);
            }
        },
        KIBIBYTES("KiB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_KiB;
            }

            @Override
            public long toKilobytes(long amt) {
                return (amt * C_KiB) / C_KB;
            }

            @Override
            public long toKibibytes(long amt) {
                return amt;
            }

            @Override
            public long toMegabytes(long amt) {
                return (amt * C_KiB) / C_MB;
            }

            @Override
            public long toMebibytes(long amt) {
                return amt / C_KiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return (amt * C_KiB) / C_GB;
            }

            @Override
            public long toGibibytes(long amt) {
                return amt / C_MiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toKibibytes(amt);
            }
        },
        MEGABYTES("MB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_MB;
            }

            @Override
            public long toKilobytes(long amt) {
                return amt * C_KB;
            }

            @Override
            public long toKibibytes(long amt) {
                return (amt * C_MB) / C_KiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return amt;
            }

            @Override
            public long toMebibytes(long amt) {
                return (amt * C_MB) / C_MiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return amt / C_KB;
            }

            @Override
            public long toGibibytes(long amt) {
                return (amt * C_MB) / C_GiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toMegabytes(amt);
            }
        },
        MEBIBYTES("MiB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_MiB;
            }

            @Override
            public long toKilobytes(long amt) {
                return (amt * C_MiB) / C_KB;
            }

            @Override
            public long toKibibytes(long amt) {
                return amt * C_KiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return (amt * C_MiB) / C_MB;
            }

            @Override
            public long toMebibytes(long amt) {
                return amt;
            }

            @Override
            public long toGigabytes(long amt) {
                return (amt * C_MiB) / C_GB;
            }

            @Override
            public long toGibibytes(long amt) {
                return amt / C_KiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toMebibytes(amt);
            }
        },
        GIGABYTES("GB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_GB;
            }

            @Override
            public long toKilobytes(long amt) {
                return amt * C_MB;
            }

            @Override
            public long toKibibytes(long amt) {
                return (amt * C_GB) / C_KiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return amt * C_KB;
            }

            @Override
            public long toMebibytes(long amt) {
                return (amt * C_GB) / C_MiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return amt;
            }

            @Override
            public long toGibibytes(long amt) {
                return (amt * C_GB) / C_GiB;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toGigabytes(amt);
            }
        },
        GIBIBYTES("GiB") {
            @Override
            public long toBytes(long amt) {
                return amt * C_GiB;
            }

            @Override
            public long toKilobytes(long amt) {
                return (amt * C_GiB) / C_KB;
            }

            @Override
            public long toKibibytes(long amt) {
                return amt * C_MiB;
            }

            @Override
            public long toMegabytes(long amt) {
                return (amt * C_GiB) / C_MB;
            }

            @Override
            public long toMebibytes(long amt) {
                return amt * C_KiB;
            }

            @Override
            public long toGigabytes(long amt) {
                return (amt * C_GiB) / C_GB;
            }

            @Override
            public long toGibibytes(long amt) {
                return amt;
            }

            @Override
            public long convert(long amt, MemoryUnit source) {
                return source.toGibibytes(amt);
            }
        };

        // SI Unit Memory Size Constants (Base 10)
        static final long C_KB = 1_000; // Bytes per Kilobyte
        static final long C_MB = C_KB * 1_000; // Bytes per Megabyte
        static final long C_GB = C_MB * 1_000; // Bytes per Gigabyte

        // Non-SI Unit Memory Size Constants (Base 2)
        static final long C_KiB = 1_024; // Bytes per Kibibyte
        static final long C_MiB = C_KiB * 1_024; // Bytes per Mebibyte
        static final long C_GiB = C_MiB * 1_024; // Bytes per Gibibyte

        private final String tag;

        MemoryUnit(String tag) {
            this.tag = tag;
        }

        /**
         * Convert to Bytes
         */
        public abstract long toBytes(long amt);

        /**
         * Convert to Kilobytes (1000 Bytes or 10^3 Bytes)
         */
        public abstract long toKilobytes(long amt);

        /**
         * Convert to Kibibytes (1024 Bytes or 2^10 Bytes)
         */
        public abstract long toKibibytes(long amt);

        /**
         * Convert to Megabytes (1000 Kilobytes or 10^6 Bytes)
         */
        public abstract long toMegabytes(long amt);

        /**
         * Convert to Mebibytes (1024 Kibibytes or 2^20 Bytes)
         */
        public abstract long toMebibytes(long amt);

        /**
         * Convert to Gigabytes (1000 Megabytes or 10^9 Bytes)
         */
        public abstract long toGigabytes(long amt);

        /**
         * Convert to Gibibytes (1024 Mebibytes or 2^30 Bytes)
         */
        public abstract long toGibibytes(long amt);

        /**
         * Convert source unit to this unit.
         */
        public abstract long convert(long amt, MemoryUnit source);

        /**
         * The appropriate tag for the Unit of Memory Storage, e.g., MB, MiB, GiB,
         * GB, etc.
         */
        public String getTag() {
            return this.tag;
        }

        @Override
        public String toString() {
            return this.tag;
        }
    }

    /**
     * Enumeration of simple computer architectures, often referred to as the "bitness" of a system, i.e.
     * x86 (32-bit) and x64 (64-bit) architectures.
     */
    public enum Bit {
        BIT_32("x86", "32-bit", 32), BIT_64("x64", "64-bit", 64);

        private final String tag;
        private final String mnemonic;
        private final int num;

        Bit(String tag, String mnemonic, int num) {
            this.tag = tag;
            this.mnemonic = mnemonic;
            this.num = num;
        }

        /**
         * Retrieve the "tag" for this bitness. e.g. 'x86' for 32-bit systems
         *
         * @return The associated bitness tag
         */
        public String getTag() {
            return this.tag;
        }

        /**
         * Returns the numerical bitness. e.g. '32' for 32-bit systems
         *
         * @return Numerical Bitness Value
         */
        public int getInt() {
            return this.num;
        }

        /**
         * Get the 'mnemonic' associated. e.g. '32-bit' for 32-bit systems.
         *
         * @return Bitness Mnemonic
         */
        public String getMnemonic() {
            return this.mnemonic;
        }

        @Override
        public String toString() {
            return this.tag;
        }
    }

    /**
     * Microsoft Windows with 'exe' binary executables
     */
    public static final OS WINDOWS = new OS(false, ".exe", "win", "windows");

    /**
     * Non-specific Unix System with no definitive executable extension
     */
    public static final OS UNIX = new OS(true, "", "lin", "linux", "nux");

    /**
     * Apple Mac OSX with 'app' binary executables (packages)
     */
    public static final OS MAC_OSX = new OS(true, ".app", "mac", "osx", "apple");

    /**
     * Sun Microsystems' Solaris OS with no definitive executable extension
     */
    public static final OS SOLARIS = new OS(true, "", "sunos", "solaris");

    /**
     * A Placeholder for an Unsupported or Undefined OS, thus having an undefined executable extension
     */
    public static final OS UNSUPPORTED = new OS(false, "");

    /**
     * Name to OS and OS to Name Lookup Table [to mimic an enum]
     */
    private static final BiMap<OS, String> nameLookup = HashBiMap.create(5);

    /**
     * Values Set, for mimicking {@link Enum Enum's} values() method
     */
    private static final Set<OS> values = ImmutableSet.of(WINDOWS, MAC_OSX, SOLARIS, UNIX, UNSUPPORTED);
    private static final Runtime rt = Runtime.getRuntime();

    public final String suffix;
    public final String[] aliases;

    private final boolean unix;

    static {
        nameLookup.put(WINDOWS, "Windows");
        nameLookup.put(UNIX, "Unix");
        nameLookup.put(MAC_OSX, "Mac OSX");
        nameLookup.put(SOLARIS, "Solaris");
        nameLookup.put(UNSUPPORTED, "Unsupported");
    }

    private OS(boolean unixBased, String executable, String... aliases) {
        this.suffix = executable;
        this.aliases = aliases;
        this.unix = unixBased;
    }

    /**
     * Determines if the given alias is a valid alias for any supported OS'. If
     * the alias is not a registered alias or name of an OS then false is returned.
     *
     * @param alias Name Alias
     * @return True if valid OS, otherwise false.
     */
    public boolean isValidAlias(String alias) {
        Preconditions.checkNotNull(alias);
        for (final String s : this.aliases)
            if (s.equalsIgnoreCase(alias)) return true;

        return nameLookup.containsValue(alias);
    }

    /**
     * Determines if this system is a Unix based system that may need unix-specific operations done such
     * as assigning POSIX File Permissions to files for access.
     *
     * @return True if unix based, false otherwise.
     */
    public boolean isUnixBased() {
        return unix;
    }

    /**
     * Determines the desired OS from the given OS. If the alias is not a registered alias or name
     * of a supported OS then OS.UNSUPPORTED is returned.
     *
     * @param alias Name Alias
     * @return The Desired OS if supported, else OS.UNSUPPORTED
     */
    public static OS forAlias(String alias) {
        Preconditions.checkNotNull(alias);
        for (final OS os : OS.values())
            if (os.isValidAlias(alias)) return os;

        return OS.get(alias);
    }

    /**
     * Get this System OS or OS.UNSUPPORTED
     */
    public static OS get() {
        final String name = System.getProperty("os.name").toLowerCase();

        if (name.contains("win"))
            return WINDOWS;
        else if (name.contains("mac"))
            return MAC_OSX;
        else if (name.contains("sunos"))
            return SOLARIS;
        else if ((name.contains("nix")) || (name.contains("nux")) || (name.contains("nax")) || name.contains("aix"))
            return UNIX;
        else
            return UNSUPPORTED;
    }

    public static OS get(String name) {
        return nameLookup.inverse().getOrDefault(name, OS.UNIX);
    }

    /**
     * Get the intended Architecture of the JVM. This is not necessarily the
     * Computer's architecture. <br />
     * A 32-bit JVM can be run on a 64-bit Computer. <br />
     * <br />
     * To get the Computer Architecture, use {@link #getArch()}.
     *
     * @return JVM Architecture (x86/x64)
     */
    public static Bit getArchJVM() {
        final String arch = System.getProperty("os.arch");

        if (!arch.contains("64"))
            return Bit.BIT_32;
        else
            return Bit.BIT_64;
    }

    /**
     * Get Architecture of the computer. This determines the ACTUAL Bitness of the
     * computer <br />
     * this means that the COMPUTER may be 64 bit, but may be running a 32 bit JVM.
     * To get <br />
     * the JVM Bitness, use {@link #getArchJVM()}.
     *
     * @return Actual Architecture of the Computer (x86 or x64)
     */
    public static Bit getArch() {
        final String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        final String wow64 = System.getenv("PROCESSOR_ARCHITEW6432");

        if (arch == null)
            return getArchJVM();

        if ((arch.indexOf("64") > 0) || ((wow64 != null) && (wow64.indexOf("64") > 0)))
            return Bit.BIT_64;
        else
            return Bit.BIT_32;
    }

    public static int processorCount() {
        return rt.availableProcessors();
    }

    public static long getTotalMemory(MemoryUnit units) {
        return units.convert(rt.totalMemory(), MemoryUnit.BYTES);
    }

    public static long getUsedMemory(MemoryUnit units) {
        return units.convert(rt.totalMemory() - rt.freeMemory(), MemoryUnit.BYTES);
    }

    public static long getFreeMemory(MemoryUnit units) {
        return units.convert(rt.freeMemory(), MemoryUnit.BYTES);
    }

    public static double getUsedMemoryToTotalMemory(MemoryUnit units) {
        final double used = getUsedMemory(units);
        final double total = getTotalMemory(units);

        return used / total;
    }

    public static Set<OS> values() {
        return values;
    }

    public String name() {
        return nameLookup.get(this);
    }

    @Override
    public String toString() {
        return String.format("%s %s [%s]", name(), getArch().getTag(), getArch().getMnemonic());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
