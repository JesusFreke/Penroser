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

import android.app.backup.BackupManager;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
    private static Boolean hasBackupManager = null;


    public static void savePreference(SharedPreferences sharedPreferences, String name, String value) {
        String oldValue = sharedPreferences.getString(name, null);
        if (oldValue != null && oldValue.equals(value)) {
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.commit();

        dataChanged();
    }

    public static void savePreference(SharedPreferences sharedPreferences, String name, boolean value, boolean defaultValue) {
        boolean oldValue = sharedPreferences.getBoolean(name, defaultValue);

        if (oldValue == value) {
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name, value);
        editor.commit();

        dataChanged();
    }

    public static void savePreference(SharedPreferences sharedPreferences, String name, int value, int defaultValue) {
        int oldValue = sharedPreferences.getInt(name, defaultValue);

        if (oldValue == value) {
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(name, value);
        editor.commit();

        dataChanged();
    }

    private static void dataChanged() {
        if (hasBackupManager == null) {
            try {
                Class c = Class.forName("android.app.backup.BackupManager");
                hasBackupManager = true;
            } catch (ClassNotFoundException ex) {
                hasBackupManager = false;
            }
        }

        if (hasBackupManager) {
            BackupManager.dataChanged("org.jf.Penroser");
        }
    }
}
