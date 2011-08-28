package org.jf.Penroser;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;

public class PenroserActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (getFullScreen()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        PenroserGLView penroserGLView = new PenroserGLView(this);
        setContentView(penroserGLView);
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
                intent.setComponent(new ComponentName(this, PenroserOptions.class));
                startActivity(intent);
                return true;
        }
        return false;
    }

    private boolean getFullScreen() {
        return getPreferences(MODE_PRIVATE).getBoolean("full_screen", false);
    }

    private void setFullScreen(boolean fullScreen) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
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
