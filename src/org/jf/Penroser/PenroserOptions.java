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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class PenroserOptions extends PenroserBaseActivity {
    private HalfRhombusButton leftSkinny = null;
    private HalfRhombusButton rightSkinny = null;
    private HalfRhombusButton leftFat = null;
    private HalfRhombusButton rightFat = null;
    private PenroserGLView penroserView = null;

    private Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.options);

        leftSkinny = (HalfRhombusButton)findViewById(R.id.left_skinny);
        rightSkinny = (HalfRhombusButton)findViewById(R.id.right_skinny);
        leftFat = (HalfRhombusButton)findViewById(R.id.left_fat);
        rightFat = (HalfRhombusButton)findViewById(R.id.right_fat);

        leftSkinny.setOnClickListener(rhombusClickListener);
        rightSkinny.setOnClickListener(rhombusClickListener);
        leftFat.setOnClickListener(rhombusClickListener);
        rightFat.setOnClickListener(rhombusClickListener);

        penroserView = (PenroserGLView)findViewById(R.id.penroser_view);
        penroserView.onPause();

        leftSkinny.setColor(penroserView.penroserContext.getRhombusColor(HalfRhombusType.LEFT_SKINNY));
        rightSkinny.setColor(penroserView.penroserContext.getRhombusColor(HalfRhombusType.RIGHT_SKINNY));
        leftFat.setColor(penroserView.penroserContext.getRhombusColor(HalfRhombusType.LEFT_FAT));
        rightFat.setColor(penroserView.penroserContext.getRhombusColor(HalfRhombusType.RIGHT_FAT));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            int rhombusId = requestCode;
            int color = resultCode;

            HalfRhombusType rhombusType = PenroserApp.mapRhombusIdToRhombusType(rhombusId);

            penroserView.penroserContext.setRhombusColor(rhombusType, color);
            penroserView.penroserContext.storeRhombusColors();

            HalfRhombusButton button = (HalfRhombusButton)findViewById(rhombusId);
            button.setColor(color);
        }
    }

    @Override
    protected void onResume() {
        if (penroserView != null) {
            //work-around on 2.1. Needed because the wallpaper's visibility isn't changed until after we are displayed,
            //and we hang on start up because the wallpaper still has the gl context... I think
            handler.post(new Runnable() {
                public void run() {
                    penroserView.onResume();
                }
            });
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (penroserView != null) {
            penroserView.onPause();
        }
        super.onPause();
    }

    private final View.OnClickListener rhombusClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(PenroserOptions.this, PenroserColorPicker.class);
            intent.putExtra("rhombus", v.getId());
            intent.putExtra("color", ((HalfRhombusButton)v).getColor());
            intent.putExtra("sharedPrefName", getSharedPreferenceName());
            startActivityForResult(intent, v.getId());
        }
    };

    private int getColor(SharedPreferences preferences, HalfRhombusType rhombusType) {
        return preferences.getInt(rhombusType.colorKey, rhombusType.defaultColor);
    }
}