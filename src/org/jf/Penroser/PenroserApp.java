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

import android.app.Application;
import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.util.Random;


public class PenroserApp extends Application {
    /*This is the number of levels to use for the static vbo data*/
    /*package*/ static final int VBO_LEVEL=5;

    /*The default initial scale, based on the number of levels down that we are pre-generating*/
    public static final float DEFAULT_INITIAL_SCALE = (float)(500 * Math.pow((Math.sqrt(5)+1)/2, VBO_LEVEL-5));

    //TODO: need to move this to GLContext
    public static final HalfRhombusPool halfRhombusPool = new HalfRhombusPool();
    public static final Random random = new Random();

    private Handler handler;

    public PenroserApp() {
        super();

        handler = new Handler();
    }

    @Override
    public void onCreate() {
        attemptUpgrade();
    }

    private void attemptUpgrade() {
        SharedPreferences oldPreferences = getApplicationContext().getSharedPreferences("penroser_live_wallpaper_prefs", MODE_PRIVATE);
        SharedPreferences newPreferences = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);

        if (oldPreferences.contains("left_skinny_color")) {
            PenroserPreferences preferences = new PenroserPreferences();
            for (HalfRhombusType rhombusType: HalfRhombusType.values()) {
                preferences.setColor(rhombusType, ColorUtil.swapOrder(oldPreferences.getInt(rhombusType.colorKey, rhombusType.defaultColor)));
            }
            preferences.saveTo(newPreferences, PenroserLiveWallpaper.PREFERENCE_NAME);
            oldPreferences.edit().clear().commit();
        }

        oldPreferences = getApplicationContext().getSharedPreferences("penroser_activity_prefs", MODE_PRIVATE);
        boolean clearPref = false;
        if (oldPreferences.contains("left_skinny_color")) {
            PenroserPreferences preferences = new PenroserPreferences();
            for (HalfRhombusType rhombusType: HalfRhombusType.values()) {
                preferences.setColor(rhombusType, ColorUtil.swapOrder(oldPreferences.getInt(rhombusType.colorKey, rhombusType.defaultColor)));
            }
            preferences.saveTo(newPreferences, PenroserActivity.PREFERENCE_NAME);
            clearPref = true;
        }
        if (oldPreferences.contains("full_screen")) {
            SharedPreferenceUtil.savePreference(newPreferences, "full_screen", oldPreferences.getBoolean("full_screen", false), false);
            clearPref = true;
        }
        if (clearPref) {
            oldPreferences.edit().clear().commit();
        }

        if (newPreferences.getInt("first_run", 1) != 0) {
            SharedPreferenceUtil.savePreference(newPreferences, "saved",
                    "[" +
                            "{\"scale\":1,\"left_skinny_color\":0,\"left_fat_color\":7509713,\"right_fat_color\":0,\"right_skinny_color\":7509713}, " +
                            "{\"scale\":1,\"left_skinny_color\":2112,\"left_fat_color\":33331,\"right_fat_color\":9498,\"right_skinny_color\":11382}, " +
                            "{\"scale\":0.367832458,\"left_skinny_color\":13920,\"left_fat_color\":0,\"right_fat_color\":0,\"right_skinny_color\":27554}" +
                            "]");

            SharedPreferenceUtil.savePreference(newPreferences, "first_run", 0, 1);
        }
    }
}
