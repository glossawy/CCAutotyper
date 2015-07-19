package com.mattc.autotyper.minify;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mattc.autotyper.Ref;
import com.mattc.autotyper.util.IOUtils;
import com.mattc.autotyper.util.OS;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A central location for the minification of Lua Files. This will likely
 * be expanded in the future, or at the least an API will be provided allowing for
 * independent Minifiers to be created and used as needed.
 *
 * @author Matthew Crocco
 */
public final class Minifier {

    public static final Path SCRIPT_DIR = Paths.get("scripts");

    private static final String HEADER_COMMENT = "-- Minified by " + Ref.APP_NAME + " v" + Ref.VERSION + " by " + Ref.AUTHOR + " (https://matthewcrocco.us) \n" +
            "-- Using luamin by Mathias Bynens (http://mathiasbynens.be) via Nashorn \n\n";

    /**
     * Nashorn JavaScript Engine -- {@link ScriptEngine} Object
     */
    private static final ScriptEngine nashornJS = new ScriptEngineManager().getEngineByName("nashorn");
    /**
     * Nashorn JavaScript Engine as Invocable -- {@link Invocable} Object
     */
    private static final Invocable nashorn = (Invocable) nashornJS;

    private static final Object luaparseObj, luaminObj;

    static {
        try {
            // Load luaparse requirement, then load luamin
            nashornJS.eval(Files.newBufferedReader(SCRIPT_DIR.resolve(Paths.get("luamin", "node_modules", "luaparse", "luaparse.js"))));
            nashornJS.eval(Files.newBufferedReader(SCRIPT_DIR.resolve(Paths.get("luamin", "luamin.js"))));

            // Store global Parse Options as JavaScript Object
            nashornJS.eval("var _PARSE_VARS = {scope: true}");

            // Retrieve LuaParse and LuaMin Objects on which to execute Lua Methods from
            luaparseObj = nashornJS.get("luaparse");
            luaminObj = nashornJS.get("luamin");
        } catch (IOException | ScriptException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Reads in the entire file and passes to LuaMin for minification.
     *
     * @param path File Path
     * @return Minified Code as String
     * @throws IOException     If luamin or luaparse is corrupted in some way
     * @throws ScriptException If an error occurs during evaluation
     */
    public static String minifyFile(Path path) throws IOException, ScriptException {
        String content = IOUtils.fileToString(path.toFile(), StandardCharsets.UTF_8);

        final Object res;
        final String s;
        try {
            // Get Result of luaparse#parse, pass to luamin#minify and get String result
            res = nashorn.invokeMethod(luaparseObj, "parse", content);
            s = nashorn.invokeMethod(luaminObj, "minify", res).toString();
        } catch (NoSuchMethodException e) {
            throw new IOException("Something is wrong with luaparse.parse and/or luamin.minify! Methods not found!", e);
        }

        return HEADER_COMMENT + repairBrokenLuaStrings(s.replace(";", "\n"));
    }

    /**
     * Minify contents of file the standard way and then write out minified contents
     * to a temporary file.
     *
     * @param file Path to file to minify
     * @return Minified File
     * @throws IOException If failed to create or write to temporary file
     * @throws ScriptException If failed to minify
     */
    public static Path minifyFileToCopy(Path file) throws IOException, ScriptException {
        String result = minifyFile(file);

        Path temp;
        if(OS.get() == OS.WINDOWS || OS.get() == OS.UNSUPPORTED)
            temp = Files.createTempFile("ccminify", "");
        else
            temp = Files.createTempFile("ccminify", "", IOUtils.POSIX_STANDARD_PERMS);

        Files.write(temp, result.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return temp;
    }

    /**
     * Use LuaMin to minify a specific piece of code (as a string).
     *
     * @param code Code to minify
     * @return Minified Code
     * @throws ScriptException
     */
    public static String minify(String code) throws ScriptException {
        final Object ast;
        final String res;

        try {
            ast = nashorn.invokeMethod(luaparseObj, "parse", code, nashornJS.get("_PARSE_VARS"));
            res = nashorn.invokeMethod(luaminObj, "minify", ast).toString();
        } catch (NoSuchMethodException e) {
            throw new ScriptException("Something is wrong with luaparse.parse and/or luamin.minify! Methods not found!");
        }

        return HEADER_COMMENT + "\n" + res.replace(";", "\n");
    }

    // Pre-compiled Regex Pattern for Single or Double Quoted String
    private static Pattern singleQuotePattern = Pattern.compile("('(?:[^']+|'')')", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    private static Pattern doublequotePattern = Pattern.compile("(\"(?:[^\"]+|\"\")\")", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    private static Pattern ccLineEndingPattern = Pattern.compile("\n");

    // Fast Hash Function at 32 bits. We don't need a high quality hash function. Minimal bit count.
    private static HashFunction captureHasher = Hashing.goodFastHash(32);

    // FIXME The current regular expression of the type:
    /*
     "This is aa string \"containing
     \" another escaped string"

     The line ending after 'containing' will not be fixed and the LuaJ Interpreter will fail
     claiming the string is unterminated.

     This can possibly be fixed by placing an exception for [\"] and [\'] in the regexs.
     */

    /**
     * Repair Lua Strings that are broken by LuaMin during minification. This induces some overhead
     * but attempts to use pre-compiled regular expression.
     *
     * Specifically this checks if a line contains any line endings. If they do, a '\' is inserted
     * before the line ending to allow Lua to continue to the next line. This is done for each
     * line ending in the string. <br />
     * <br />
     * This method makes extensive use of Regular Expressions. Any patterns are pre-compiled and thus
     * cached for performance later on.
     *
     * @param s String to repair
     * @return String repaired as described above
     */
    private static String repairBrokenLuaStrings(String s) {
        Matcher singleMatcher = singleQuotePattern.matcher(s);
        Matcher doubleMatcher = doublequotePattern.matcher(s);
        Set<HashCode> hashes = Sets.newHashSetWithExpectedSize(singleMatcher.groupCount() + doubleMatcher.groupCount());

        // First fix Single Quote Strings, then fix Double Quote Strings
        s = applyLineEndingFix(s, singleMatcher, hashes);
        s = applyLineEndingFix(s, doubleMatcher, hashes);

        return s;
    }


    /**
     * Applies the line ending fix which will replace any line ending characters ('\n' for CC) with
     * '\\\n' which inserts a backslash before the line ending, this is so Lua will move on to the next line
     * without claiming something like a String is unterminated when the end quote is in the proceeding lines. <br />
     * <br />
     * If a HashSet is passed in, the HashSet will be populated with any HashCodes generated in this method
     * as a side effect. <br />
     * <br />
     * If no HashSet is provided, a new one is created for temporary use.
     *
     * @param text Text to Scan
     * @param matcher Regular Expression Matcher to use
     * @param usedStringHashes HashSet to store HashCodes so that we do not evaluate the same capture over again
     * @return The String with any captures containing '\n' being fixed with '\\\n'
     */
    private static String applyLineEndingFix(String text, Matcher matcher, Set<HashCode> usedStringHashes) {
        if(usedStringHashes == null)
            usedStringHashes = Sets.newHashSetWithExpectedSize(matcher.groupCount());

        if(matcher.find()) {
            for(int i = 1; i <= matcher.groupCount(); i++) {
                // Get the next capture and generate it's accompanying GoodFastHash
                String capture = matcher.group(i);
                HashCode cHash = captureHasher.hashString(capture, Charsets.UTF_8);

                // If we have already evaluated this capture, continue to next iteration
                if(usedStringHashes.contains(cHash))
                    continue;

                // Only apply fix if '\n' exists in the capture.
                Matcher cMatcher = ccLineEndingPattern.matcher(capture);
                if(cMatcher.find())
                    text = text.replace(capture, cMatcher.replaceAll("\\\\\n"));

                usedStringHashes.add(cHash);
            }
        }

        return text;
    }
}
