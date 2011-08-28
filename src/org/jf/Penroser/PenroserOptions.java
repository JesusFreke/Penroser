package org.jf.Penroser;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class PenroserOptions extends Activity {
    private HalfRhombusButton leftSkinny = null;
    private HalfRhombusButton rightSkinny = null;
    private HalfRhombusButton leftFat = null;
    private HalfRhombusButton rightFat = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.options);

        leftSkinny = (HalfRhombusButton)findViewById(R.id.left_skinny);
        rightSkinny = (HalfRhombusButton)findViewById(R.id.right_skinny);
        leftFat = (HalfRhombusButton)findViewById(R.id.left_fat);
        rightFat = (HalfRhombusButton)findViewById(R.id.right_fat);


        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        leftSkinny.setColor(getLeftSkinnyColor(preferences));
        rightSkinny.setColor(getRightSkinnyColor(preferences));
        leftFat.setColor(getLeftFatColor(preferences));
        rightFat.setColor(getRightFatColor(preferences));

        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("left_skinny_color")) {
                    leftSkinny.setColor(getLeftSkinnyColor(preferences));
                } else if (key.equals("right_skinny_color")) {
                    rightSkinny.setColor(getRightSkinnyColor(preferences));
                } else if (key.equals("left_fat_color")) {
                    leftFat.setColor(getLeftFatColor(preferences));
                } else if (key.equals("right_fat_color")) {
                    rightFat.setColor(getRightFatColor(preferences));
                }
            }
        });
    }

    private int getLeftSkinnyColor(SharedPreferences preferences) {
        return preferences.getInt("left_skinny_color", ColorUtil.glToAndroid(0x000000));
    }

    private int getRightSkinnyColor(SharedPreferences preferences) {
        return preferences.getInt("right_skinny_color", ColorUtil.glToAndroid(0xd19672));
    }

    private int getLeftFatColor(SharedPreferences preferences) {
        return preferences.getInt("right_fat_color", ColorUtil.glToAndroid(0xd19672));
    }

    private int getRightFatColor(SharedPreferences preferences) {
        return preferences.getInt("left_fat_color", ColorUtil.glToAndroid(0x000000));
    }
}