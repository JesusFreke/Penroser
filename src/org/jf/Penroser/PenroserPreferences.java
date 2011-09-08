/*
 * [The "BSD licence"]
 * Copyright (c) 2011 Ben Gruver
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jf.Penroser;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PenroserPreferences implements Parcelable {
    private static final String TAG = "PenroserPreferences";

    private int[] colors = new int[4];
    private float scale;

    public PenroserPreferences() {
        initDefault();
    }

    public PenroserPreferences(PenroserPreferences preferences) {
        this.colors = preferences.colors.clone();
        this.scale = preferences.scale;
    }

    public PenroserPreferences(SharedPreferences sharedPreferences, String savedPreferenceName) {
        try {
            String jsonString = sharedPreferences.getString(savedPreferenceName, null);
            if (jsonString == null) {
                initDefault();
                return;
            }

            initFromJsonString(jsonString);
        } catch (JSONException ex) {
            initDefault();
        }
    }

    public PenroserPreferences(String jsonString) throws JSONException {
        initFromJsonString(jsonString);
    }

    public PenroserPreferences(JSONObject jsonObject) throws JSONException {
        initFromJson(jsonObject);
    }

    private void initFromJsonString(String jsonString) throws JSONException {
        initFromJson(new JSONObject(jsonString));
    }

    private void initFromJson(JSONObject json) throws JSONException {
        for (HalfRhombusType type: HalfRhombusType.values()) {
            colors[type.index] = json.optInt(type.colorKey, type.defaultColor);
        }
        scale = (float)json.optDouble("scale", 1);
    }

    public void setPreferences(PenroserPreferences preferences) {
        for (int i=0; i<4; i++) {
            colors[i] = preferences.colors[i];
        }
        this.scale = preferences.scale;
    }

    private void initDefault() {
        for (HalfRhombusType type: HalfRhombusType.values()) {
            colors[type.index] = type.defaultColor;
        }
        scale = 1;
    }

    public int getColor(HalfRhombusType type) {
        return colors[type.index];
    }

    public void setColor(HalfRhombusType type, int color) {
        colors[type.index] = color;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void saveTo(SharedPreferences sharedPreferences, String savedPreferenceName) {
        JSONObject jsonObject = toJsonObject();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(savedPreferenceName, jsonObject.toString());
        editor.commit();
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            for (HalfRhombusType type: HalfRhombusType.values()) {
                jsonObject.put(type.colorKey, colors[type.index]);
            }
            jsonObject.put("scale", scale);
        } catch (JSONException ex) {
            Log.e(TAG, "Error creating JSON object for preferences", ex);
            return null;
        }
        return jsonObject;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        for (int i=0; i<colors.length; i++) {
            out.writeInt(colors[i]);
        }
        out.writeFloat(scale);
    }

    public static final Parcelable.Creator<PenroserPreferences> CREATOR
            = new Parcelable.Creator<PenroserPreferences>() {
        public PenroserPreferences createFromParcel(Parcel in) {
            return new PenroserPreferences(in);
        }

        public PenroserPreferences[] newArray(int size) {
            return new PenroserPreferences[size];
        }
    };

    private PenroserPreferences(Parcel in) {
        for (int i=0; i<colors.length; i++) {
            colors[i] = in.readInt();
        }
        scale = in.readFloat();
    }
}
