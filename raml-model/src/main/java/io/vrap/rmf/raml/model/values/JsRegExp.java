package io.vrap.rmf.raml.model.values;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This class provides an interface to a javascript regular expression hosted in a
 * javascript engine context.
 */
public class JsRegExp implements RegExp {
    private final String pattern;
    private final ScriptEngine jsEngine;
    private final Bindings bindings;

    private JsRegExp(final String pattern) {
        this.pattern = pattern;
        ScriptEngineManager engineManager = new ScriptEngineManager();
        jsEngine = engineManager.getEngineByName("js");
        bindings = jsEngine.createBindings();
        try {
            bindings.put("pattern", pattern);
            jsEngine.eval("var regex = new RegExp(pattern)", bindings);
        } catch (ScriptException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean test(final String value) {
        bindings.put("value", value);
        try {
            final Boolean result = (Boolean) jsEngine.eval("regex.test(value)", bindings);
            return result;
        } catch (final ScriptException e) {
            throw new IllegalStateException(e);
        }
    }

    public String toString() {
        return pattern;
    }

    public static RegExp of(final String pattern) {
        return new JsRegExp(pattern);
    }
}
