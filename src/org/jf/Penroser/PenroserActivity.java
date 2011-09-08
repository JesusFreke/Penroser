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

import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;

public class PenroserActivity extends Activity {
    public static final String PREFERENCE_NAME = "current_pref_activity";

    private SharedPreferences sharedPreferences = null;
    private PenroserGLView penroserView = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (getFullScreen()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        penroserView = new PenroserGLView(this);
        penroserView.setPreferences(new PenroserPreferences(sharedPreferences, PREFERENCE_NAME));
        setContentView(penroserView);
    }

    @Override
    protected void onResume() {
        if (penroserView != null) {
            penroserView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (penroserView != null) {
            penroserView.onPause();
            penroserView.getPreferences().saveTo(sharedPreferences, PenroserActivity.PREFERENCE_NAME);
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.full_screen:
                toggleFullScreen();
                return true;
            case R.id.options:
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(this, PenroserGallery.class));
                intent.putExtra("preferences", penroserView.getPreferences());
                startActivityForResult(intent, 0);
                return true;
            case R.id.set_wallpaper:
                penroserView.getPreferences().saveTo(sharedPreferences, PenroserLiveWallpaper.PREFERENCE_NAME);

                WallpaperInfo wallpaperInfo = WallpaperManager.getInstance(this).getWallpaperInfo();
                if (wallpaperInfo == null || wallpaperInfo.getComponent().compareTo(new ComponentName(this, PenroserLiveWallpaper.class)) != 0) {
                    Intent i = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                    startActivity(i);
                }
                finish();

                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            PenroserPreferences preferences = data.getExtras().getParcelable("preferences");
            preferences.saveTo(sharedPreferences, PREFERENCE_NAME);
            this.penroserView.setPreferences(preferences);
        }
    }

    private boolean getFullScreen() {
        return sharedPreferences.getBoolean("full_screen", false);
    }

    private void setFullScreen(boolean fullScreen) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("full_screen", fullScreen);
        editor.commit();
    }

    private void toggleFullScreen() {
        boolean fullScreen = getFullScreen();
        if (fullScreen) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setFullScreen(!fullScreen);
    }
}
