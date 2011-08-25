package org.jf.Penroser;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;

public class PenroserActivity extends Activity
{
    private boolean fullScreen = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

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
        }
        return false;
    }

    private void toggleFullScreen() {
        if (fullScreen) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        fullScreen = !fullScreen;
    }
}
