package com.ravelsoftware.ravtech.scripts.luajs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.client.JavaToJavascript;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.scripts.Script;
import com.ravelsoftware.ravtech.scripts.WebGLScriptManager;
import com.ravelsoftware.ravtech.util.Debug;

public class MoonshineJSScript extends Script {
    
    ObjectMap<String, JavaScriptObject> functions = new ObjectMap<String, JavaScriptObject>();
    String script;
    boolean loaded = false;
    
    public MoonshineJSScript(String script) {
        this.script = script;
        processScript();
        ObjectMap<String, Object> values = new ObjectMap<String, Object>();
        setEnviroment(values);
        WebGLScriptManager.registerScript(this);
    }
    
    public MoonshineJSScript(String script, GameObject selfObject) {
        this.script = script;
        processScript();
        ObjectMap<String, Object> values = new ObjectMap<String, Object>();
        values.put("this", selfObject);
        values.put("Keys", Keys.class);
        values.put("Input", Gdx.input);
        setEnviroment(values);
        WebGLScriptManager.registerScript(this);
    }

    private void processScript() {
        Array<String> functions = new Array<String>();
        
        String[] lines = script.split("\n");
        String lastFunction = null;
        int indentation = 0;
        for (String line : lines) {
            if(line.contains("function")) {        
                indentation ++;
                if(lastFunction == null) {
                    lastFunction = line.substring(line.indexOf("function") + 9);
                    int parenteciesIndex = lastFunction.indexOf('(');
                    int spaceIndex = lastFunction.indexOf(' ');
                    lastFunction = lastFunction.substring(0, Math.min(parenteciesIndex, spaceIndex));
                }
            }
            
            if(line.contains("if"))
                indentation ++;
            
            if(line.contains("end")) {
                indentation --;
                if(indentation == 0) {
                    functions.add(lastFunction);
                    lastFunction = null; 
                }
            }
        }
        
        for(int i = 0; i < functions.size; i++)
            script += "\nmoonshineJSScript:registerFunction(\"" + functions.get(i) + "\", " + functions.get(i) + ")";
        
        script += "\nmoonshineJSScript:setLoaded()";
    }

    @Override
    public void init() {
        this.callFunction(functions.get("init"));
    }

    @Override
    public void update() {
        this.callFunction(functions.get("update"));
    }
        
    
           
    public native void callFunction(JavaScriptObject callback)/*-{
        callback.call();
    }-*/;
    
    public void registerFunction(String name, JavaScriptObject object) {
        Debug.logError("register", name + " " + object);
        this.functions.put(name, object);
    }

    @Override
    public void setEnviroment(ObjectMap<String, Object> enviroment) {
        enviroment.put("moonshineJSScript", this);
        JsArrayString keys = JavaScriptObject.createArray().cast();        
        JsArray<JavaScriptObject> values = JavaScriptObject.createArray().cast();
        Entries<String, Object> iterator = enviroment.iterator();
        while(iterator.hasNext) {
            Entry<String, Object> entry = iterator.next();
            keys.push(entry.key);
            values.push(JavaToJavascript.convertObject(entry.value));
        }
        Debug.logError("initEnviroment", values.length());
        this.initJSEnviroment(this, keys, values, script);
    }
    
    public native float initJSEnviroment(MoonshineJSScript script, JsArrayString keys, JsArray<JavaScriptObject> values, String scriptString)/*-{
        env = {
            Debug: {
                log: function log (self, message) {
                    console.error('Lua: ' + message);
                }
            }           
        };
        
        for (var i = 0; i < keys.length; i++) {
            env[keys[i]] = values[i];
            console.error("key: " + keys[i] + " value" + values[i]);
        }
        
        var vm = new $wnd.shine.VM(env);
        $wnd.shine.luac.init(vm);
        $wnd.shine.luac.compile(scriptString, function(err, bc) {
             if (err) {
                $wnd.alert('Compile error: ' + err);
            } else {
                vm.load(bc);
            }
        });
    }-*/;

    @Override
    public boolean isLoaded() {
        return loaded;
    }
    
    public void setLoaded() {
        loaded = true;
    }
}
