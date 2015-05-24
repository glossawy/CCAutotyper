package com.mattc.autotyper.minify;

import com.mattc.autotyper.Ref;
import com.mattc.autotyper.util.IOUtils;

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
            nashornJS.eval(Files.newBufferedReader(SCRIPT_DIR.resolve(Paths.get("luamin", "node_modules", "luaparse", "luaparse.js"))));
            nashornJS.eval(Files.newBufferedReader(SCRIPT_DIR.resolve(Paths.get("luamin", "luamin.js"))));
            nashornJS.eval("var _PARSE_VARS = {scope: true}");

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
            res = nashorn.invokeMethod(luaparseObj, "parse", content);
            s = nashorn.invokeMethod(luaminObj, "minify", res).toString();
        } catch (NoSuchMethodException e) {
            throw new IOException("Something is wrong with luaparse.parse and/or luamin.minify! Methods not found!", e);
        }

        return HEADER_COMMENT + repair(s.replace(";", "\n"));
    }

    public static Path minifyFileToCopy(Path file) throws IOException, ScriptException {
        String result = minifyFile(file);

        Path temp = Files.createTempFile("ccminify", "", IOUtils.STANDARD_PERMS);
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

    public static void main(String[] args) throws Exception {
        System.out.println(minifyFile(Paths.get("test.lua")));
        System.out.println(minify("a = ((1 + 2) - 3) * (4 / (5 ^ 6)) -- foo"));
    }

    /**
     * Repair Strings that are broken up by luamin into multiple lines. This simply adds a '\' to the end
     * which allows Lua to move the the next line, continuing the string.
     */
    private static String repair(String s) {
        StringBuilder sb = new StringBuilder();

        boolean start = false;
        boolean doublequote = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"') {
                if (start && doublequote) {
                    start = false;
                    doublequote = false;
                } else if (!start) {
                    start = true;
                    doublequote = true;
                }
            } else if (c == '\'') {
                if (start && !doublequote) {
                    start = false;
                } else if (!start) {
                    start = true;
                    doublequote = false;
                }
            } else if (c == '\n' && start) {
                sb.append('\\');
            }

            sb.append(c);
        }

        return sb.toString();
    }

}
