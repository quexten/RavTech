
package com.quexten.ravtech.client;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.google.gwt.core.client.JavaScriptObject;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.components.Transform;
import com.quexten.ravtech.scripts.luajs.MoonshineJSScript;

public class JavaToJavascript {

    public static JavaScriptObject convertObject (Object object) {
        if (object instanceof Vector2)
            return JavaToJavascript.vector((Vector2)object);
        else if (object instanceof MoonshineJSScript)
            return JavaToJavascript.moonshineJSScript((MoonshineJSScript)object);
        else if (object instanceof GameObject)
            return JavaToJavascript.gameObject((GameObject)object);
        else if (object instanceof Transform)
            return JavaToJavascript.transform((Transform)object);
        else if (object.equals(Keys.class))
            return JavaToJavascript.keys();
        else if (object instanceof Input)
            return JavaToJavascript.input((Input)object);
        else
            return null;
    }

    public static native JavaScriptObject input (Input input)/*-{
                                                             var Input = {};
                                                             Input.isKeyPressed = function(self, key) {
                                                             return input.@com.badlogic.gdx.Input::isKeyPressed(I)(key);
                                                             };
                                                             return Input;
                                                             }-*/;

    public static native JavaScriptObject keys ()/*-{
                                                 var Keys = {};
                                                 Keys.ANY_KEY = @com.badlogic.gdx.Input.Keys::ANY_KEY;
                                                 Keys.NUM_0 = @com.badlogic.gdx.Input.Keys::NUM_0;
                                                 Keys.NUM_1 = @com.badlogic.gdx.Input.Keys::NUM_1;
                                                 Keys.NUM_2 = @com.badlogic.gdx.Input.Keys::NUM_2;
                                                 Keys.NUM_3 = @com.badlogic.gdx.Input.Keys::NUM_3;
                                                 Keys.NUM_4 = @com.badlogic.gdx.Input.Keys::NUM_4;
                                                 Keys.NUM_5 = @com.badlogic.gdx.Input.Keys::NUM_5;
                                                 Keys.NUM_6 = @com.badlogic.gdx.Input.Keys::NUM_6;
                                                 Keys.NUM_7 = @com.badlogic.gdx.Input.Keys::NUM_7;
                                                 Keys.NUM_8 = @com.badlogic.gdx.Input.Keys::NUM_8;
                                                 Keys.NUM_9 = @com.badlogic.gdx.Input.Keys::NUM_9;
                                                 Keys.A = @com.badlogic.gdx.Input.Keys::A;
                                                 Keys.ALT_LEFT = @com.badlogic.gdx.Input.Keys::ALT_LEFT;
                                                 Keys.ALT_RIGHT = @com.badlogic.gdx.Input.Keys::ALT_RIGHT;
                                                 Keys.APOSTROPHE = @com.badlogic.gdx.Input.Keys::APOSTROPHE;
                                                 Keys.AT = @com.badlogic.gdx.Input.Keys::AT;
                                                 Keys.B = @com.badlogic.gdx.Input.Keys::B;
                                                 Keys.BACK = @com.badlogic.gdx.Input.Keys::BACK;
                                                 Keys.BACKSLASH = @com.badlogic.gdx.Input.Keys::BACKSLASH;
                                                 Keys.C = @com.badlogic.gdx.Input.Keys::C;
                                                 Keys.CALL = @com.badlogic.gdx.Input.Keys::CALL;
                                                 Keys.RavCamera = @com.badlogic.gdx.Input.Keys::RavCamera;
                                                 Keys.CLEAR = @com.badlogic.gdx.Input.Keys::CLEAR;
                                                 Keys.COMMA = @com.badlogic.gdx.Input.Keys::COMMA;
                                                 Keys.D = @com.badlogic.gdx.Input.Keys::D;
                                                 Keys.DEL = @com.badlogic.gdx.Input.Keys::DEL;
                                                 Keys.BACKSPACE = @com.badlogic.gdx.Input.Keys::BACKSPACE;
                                                 Keys.FORWARD_DEL = @com.badlogic.gdx.Input.Keys::FORWARD_DEL;
                                                 Keys.DPAD_CENTER = @com.badlogic.gdx.Input.Keys::DPAD_CENTER;
                                                 Keys.DPAD_DOWN = @com.badlogic.gdx.Input.Keys::DPAD_DOWN;
                                                 Keys.DPAD_LEFT = @com.badlogic.gdx.Input.Keys::DPAD_LEFT;
                                                 Keys.DPAD_RIGHT = @com.badlogic.gdx.Input.Keys::DPAD_RIGHT;
                                                 Keys.DPAD_UP = @com.badlogic.gdx.Input.Keys::DPAD_UP;
                                                 Keys.CENTER = @com.badlogic.gdx.Input.Keys::CENTER;
                                                 Keys.DOWN = @com.badlogic.gdx.Input.Keys::DOWN;
                                                 Keys.LEFT = @com.badlogic.gdx.Input.Keys::LEFT;
                                                 Keys.RIGHT = @com.badlogic.gdx.Input.Keys::RIGHT;
                                                 Keys.UP = @com.badlogic.gdx.Input.Keys::UP;
                                                 Keys.E = @com.badlogic.gdx.Input.Keys::E;
                                                 Keys.ENDCALL = @com.badlogic.gdx.Input.Keys::ENDCALL;
                                                 Keys.ENTER = @com.badlogic.gdx.Input.Keys::ENTER;
                                                 Keys.ENVELOPE = @com.badlogic.gdx.Input.Keys::ENVELOPE;
                                                 Keys.EQUALS = @com.badlogic.gdx.Input.Keys::EQUALS;
                                                 Keys.EXPLORER = @com.badlogic.gdx.Input.Keys::EXPLORER;
                                                 Keys.F = @com.badlogic.gdx.Input.Keys::F;
                                                 Keys.FOCUS = @com.badlogic.gdx.Input.Keys::FOCUS;
                                                 Keys.G = @com.badlogic.gdx.Input.Keys::G;
                                                 Keys.GRAVE = @com.badlogic.gdx.Input.Keys::GRAVE;
                                                 Keys.H = @com.badlogic.gdx.Input.Keys::H;
                                                 Keys.HEADSETHOOK = @com.badlogic.gdx.Input.Keys::HEADSETHOOK;
                                                 Keys.HOME = @com.badlogic.gdx.Input.Keys::HOME;
                                                 Keys.I = @com.badlogic.gdx.Input.Keys::I;
                                                 Keys.J = @com.badlogic.gdx.Input.Keys::J;
                                                 Keys.K = @com.badlogic.gdx.Input.Keys::K;
                                                 Keys.L = @com.badlogic.gdx.Input.Keys::L;
                                                 Keys.LEFT_BRACKET = @com.badlogic.gdx.Input.Keys::LEFT_BRACKET;
                                                 Keys.M = @com.badlogic.gdx.Input.Keys::M;
                                                 Keys.MEDIA_FAST_FORWARD = @com.badlogic.gdx.Input.Keys::MEDIA_FAST_FORWARD;
                                                 Keys.MEDIA_NEXT = @com.badlogic.gdx.Input.Keys::MEDIA_NEXT;
                                                 Keys.MEDIA_PLAY_PAUSE = @com.badlogic.gdx.Input.Keys::MEDIA_PLAY_PAUSE;
                                                 Keys.MEDIA_PREVIOUS = @com.badlogic.gdx.Input.Keys::MEDIA_PREVIOUS;
                                                 Keys.MEDIA_REWIND = @com.badlogic.gdx.Input.Keys::MEDIA_REWIND;
                                                 Keys.MEDIA_STOP = @com.badlogic.gdx.Input.Keys::MEDIA_STOP;
                                                 Keys.MENU = @com.badlogic.gdx.Input.Keys::MENU;
                                                 Keys.MINUS = @com.badlogic.gdx.Input.Keys::MINUS;
                                                 Keys.MUTE = @com.badlogic.gdx.Input.Keys::MUTE;
                                                 Keys.N = @com.badlogic.gdx.Input.Keys::N;
                                                 Keys.NOTIFICATION = @com.badlogic.gdx.Input.Keys::NOTIFICATION;
                                                 Keys.NUM = @com.badlogic.gdx.Input.Keys::NUM;
                                                 Keys.O = @com.badlogic.gdx.Input.Keys::O;
                                                 Keys.P = @com.badlogic.gdx.Input.Keys::P;
                                                 Keys.PERIOD = @com.badlogic.gdx.Input.Keys::PERIOD;
                                                 Keys.PLUS = @com.badlogic.gdx.Input.Keys::PLUS;
                                                 Keys.POUND = @com.badlogic.gdx.Input.Keys::POUND;
                                                 Keys.POWER = @com.badlogic.gdx.Input.Keys::POWER;
                                                 Keys.Q = @com.badlogic.gdx.Input.Keys::Q;
                                                 Keys.R = @com.badlogic.gdx.Input.Keys::R;
                                                 Keys.RIGHT_BRACKET = @com.badlogic.gdx.Input.Keys::RIGHT_BRACKET;
                                                 Keys.S = @com.badlogic.gdx.Input.Keys::S;
                                                 Keys.SEARCH = @com.badlogic.gdx.Input.Keys::SEARCH;
                                                 Keys.SEMICOLON = @com.badlogic.gdx.Input.Keys::SEMICOLON;
                                                 Keys.SHIFT_LEFT = @com.badlogic.gdx.Input.Keys::SHIFT_LEFT;
                                                 Keys.SHIFT_RIGHT = @com.badlogic.gdx.Input.Keys::SHIFT_RIGHT;
                                                 Keys.SLASH = @com.badlogic.gdx.Input.Keys::SLASH;
                                                 Keys.SOFT_LEFT = @com.badlogic.gdx.Input.Keys::SOFT_LEFT;
                                                 Keys.SOFT_RIGHT = @com.badlogic.gdx.Input.Keys::SOFT_RIGHT;
                                                 Keys.SPACE = @com.badlogic.gdx.Input.Keys::SPACE;
                                                 Keys.STAR = @com.badlogic.gdx.Input.Keys::STAR;
                                                 Keys.SYM = @com.badlogic.gdx.Input.Keys::SYM;
                                                 Keys.T = @com.badlogic.gdx.Input.Keys::T;
                                                 Keys.TAB = @com.badlogic.gdx.Input.Keys::TAB;
                                                 Keys.U = @com.badlogic.gdx.Input.Keys::U;
                                                 Keys.UNKNOWN = @com.badlogic.gdx.Input.Keys::UNKNOWN;
                                                 Keys.V = @com.badlogic.gdx.Input.Keys::V;
                                                 Keys.VOLUME_DOWN = @com.badlogic.gdx.Input.Keys::VOLUME_DOWN;
                                                 Keys.VOLUME_UP = @com.badlogic.gdx.Input.Keys::VOLUME_UP;
                                                 Keys.W = @com.badlogic.gdx.Input.Keys::W;
                                                 Keys.X = @com.badlogic.gdx.Input.Keys::X;
                                                 Keys.Y = @com.badlogic.gdx.Input.Keys::Y;
                                                 Keys.Z = @com.badlogic.gdx.Input.Keys::Z;
                                                 Keys.META_ALT_LEFT_ON = @com.badlogic.gdx.Input.Keys::META_ALT_LEFT_ON;
                                                 Keys.META_ALT_ON = @com.badlogic.gdx.Input.Keys::META_ALT_ON;
                                                 Keys.META_ALT_RIGHT_ON = @com.badlogic.gdx.Input.Keys::META_ALT_RIGHT_ON;
                                                 Keys.META_SHIFT_LEFT_ON = @com.badlogic.gdx.Input.Keys::META_SHIFT_LEFT_ON;
                                                 Keys.META_SHIFT_ON = @com.badlogic.gdx.Input.Keys::META_SHIFT_ON;
                                                 Keys.META_SHIFT_RIGHT_ON = @com.badlogic.gdx.Input.Keys::META_SHIFT_RIGHT_ON;
                                                 Keys.META_SYM_ON = @com.badlogic.gdx.Input.Keys::META_SYM_ON;
                                                 Keys.CONTROL_LEFT = @com.badlogic.gdx.Input.Keys::CONTROL_LEFT;
                                                 Keys.CONTROL_RIGHT = @com.badlogic.gdx.Input.Keys::CONTROL_RIGHT;
                                                 Keys.ESCAPE = @com.badlogic.gdx.Input.Keys::ESCAPE;
                                                 Keys.END = @com.badlogic.gdx.Input.Keys::END;
                                                 Keys.INSERT = @com.badlogic.gdx.Input.Keys::INSERT;
                                                 Keys.PAGE_UP = @com.badlogic.gdx.Input.Keys::PAGE_UP;
                                                 Keys.PAGE_DOWN = @com.badlogic.gdx.Input.Keys::PAGE_DOWN;
                                                 Keys.PICTSYMBOLS = @com.badlogic.gdx.Input.Keys::PICTSYMBOLS;
                                                 Keys.SWITCH_CHARSET = @com.badlogic.gdx.Input.Keys::SWITCH_CHARSET;
                                                 Keys.BUTTON_CIRCLE = @com.badlogic.gdx.Input.Keys::BUTTON_CIRCLE;
                                                 Keys.BUTTON_A = @com.badlogic.gdx.Input.Keys::BUTTON_A;
                                                 Keys.BUTTON_B = @com.badlogic.gdx.Input.Keys::BUTTON_B;
                                                 Keys.BUTTON_C = @com.badlogic.gdx.Input.Keys::BUTTON_C;
                                                 Keys.BUTTON_X = @com.badlogic.gdx.Input.Keys::BUTTON_X;
                                                 Keys.BUTTON_Y = @com.badlogic.gdx.Input.Keys::BUTTON_Y;
                                                 Keys.BUTTON_Z = @com.badlogic.gdx.Input.Keys::BUTTON_Z;
                                                 Keys.BUTTON_L1 = @com.badlogic.gdx.Input.Keys::BUTTON_L1;
                                                 Keys.BUTTON_R1 = @com.badlogic.gdx.Input.Keys::BUTTON_R1;
                                                 Keys.BUTTON_L2 = @com.badlogic.gdx.Input.Keys::BUTTON_L2;
                                                 Keys.BUTTON_R2 = @com.badlogic.gdx.Input.Keys::BUTTON_R2;
                                                 Keys.BUTTON_THUMBL = @com.badlogic.gdx.Input.Keys::BUTTON_THUMBL;
                                                 Keys.BUTTON_THUMBR = @com.badlogic.gdx.Input.Keys::BUTTON_THUMBR;
                                                 Keys.BUTTON_START = @com.badlogic.gdx.Input.Keys::BUTTON_START;
                                                 Keys.BUTTON_SELECT = @com.badlogic.gdx.Input.Keys::BUTTON_SELECT;
                                                 Keys.BUTTON_MODE = @com.badlogic.gdx.Input.Keys::BUTTON_MODE;
                                                 Keys.NUMPAD_0 = @com.badlogic.gdx.Input.Keys::NUMPAD_0;
                                                 Keys.NUMPAD_1 = @com.badlogic.gdx.Input.Keys::NUMPAD_1;
                                                 Keys.NUMPAD_2 = @com.badlogic.gdx.Input.Keys::NUMPAD_2;
                                                 Keys.NUMPAD_3 = @com.badlogic.gdx.Input.Keys::NUMPAD_3;
                                                 Keys.NUMPAD_4 = @com.badlogic.gdx.Input.Keys::NUMPAD_4;
                                                 Keys.NUMPAD_5 = @com.badlogic.gdx.Input.Keys::NUMPAD_5;
                                                 Keys.NUMPAD_6 = @com.badlogic.gdx.Input.Keys::NUMPAD_6;
                                                 Keys.NUMPAD_7 = @com.badlogic.gdx.Input.Keys::NUMPAD_7;
                                                 Keys.NUMPAD_8 = @com.badlogic.gdx.Input.Keys::NUMPAD_8;
                                                 Keys.NUMPAD_9 = @com.badlogic.gdx.Input.Keys::NUMPAD_9;
                                                 Keys.COLON = @com.badlogic.gdx.Input.Keys::COLON;
                                                 Keys.F1 = @com.badlogic.gdx.Input.Keys::F1;
                                                 Keys.F2 = @com.badlogic.gdx.Input.Keys::F2;
                                                 Keys.F3 = @com.badlogic.gdx.Input.Keys::F3;
                                                 Keys.F4 = @com.badlogic.gdx.Input.Keys::F4;
                                                 Keys.F5 = @com.badlogic.gdx.Input.Keys::F5;
                                                 Keys.F6 = @com.badlogic.gdx.Input.Keys::F6;
                                                 Keys.F7 = @com.badlogic.gdx.Input.Keys::F7;
                                                 Keys.F8 = @com.badlogic.gdx.Input.Keys::F8;
                                                 Keys.F9 = @com.badlogic.gdx.Input.Keys::F9;
                                                 Keys.F10 = @com.badlogic.gdx.Input.Keys::F10;
                                                 Keys.F11 = @com.badlogic.gdx.Input.Keys::F11;
                                                 Keys.F12 = @com.badlogic.gdx.Input.Keys::F12;
                                                 return Keys;
                                                 }-*/;

    public static native JavaScriptObject vector (Vector2 child)/*-{
                                                                var Vector2 = {};
                                                                Vector2.child = child;
                                                                return Vector2;
                                                                }-*/;

    public static native JavaScriptObject moonshineJSScript (MoonshineJSScript child)/*-{
                                                                                     var moonshineJSScript = {};
                                                                                     moonshineJSScript.child = child;
                                                                                     moonshineJSScript.registerFunction = function(self, name, callback) {
                                                                                     child.@com.quexten.ravtech.scripts.luajs.MoonshineJSScript::registerFunction(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(name, callback);           
                                                                                     };  
                                                                                     moonshineJSScript.setLoaded = function(self) {
                                                                                     child.@com.quexten.ravtech.scripts.luajs.MoonshineJSScript::setLoaded()();
                                                                                     };
                                                                                     return moonshineJSScript;
                                                                                     }-*/;

    public static native JavaScriptObject gameObject (GameObject child)/*-{
                                                                       var gameObject = {};
                                                                       gameObject.child = child;
                                                                       gameObject.transform = @com.quexten.ravtech.client.JavaToJavascript::convertObject(Ljava/lang/Object;)(child.@com.quexten.ravtech.components.GameObject::transform);        
                                                                       return gameObject;
                                                                       }-*/;

    public static native JavaScriptObject transform (Transform child)/*-{
                                                                     var Transform = {};
                                                                     Transform.child = child;
                                                                     Transform.setPosition = function(self, var0, var1) { return child.@com.quexten.ravtech.components.Transform::setPosition(FF)(var0, var1); };
                                                                     Transform.setRotation = function(self, var0) { return child.@com.quexten.ravtech.components.Transform::setRotation(F)(var0); };
                                                                     Transform.getRotation = function(self) { return child.@com.quexten.ravtech.components.Transform::getRotation()(); };
                                                                     return Transform;    
                                                                     }-*/;
}
