package org.jf.Penroser;

import android.app.Activity;
import android.os.Bundle;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

public class Penroser extends Activity
{
    public static final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING_SINGLE));

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        PenroserGLView penroserGLView = new PenroserGLView(this);
        setContentView(penroserGLView);
    }
}
