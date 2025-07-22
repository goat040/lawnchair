package app.lawnchair.icons;

import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class IconAdapterTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.getApplication();
    }

    @Test
    public void testWrap() {
        // Create a new IconAdapter.
        IconAdapter iconAdapter = new IconAdapter(mContext, 100);

        // Create a new AdaptiveIconDrawable.
        Drawable background = new ColorDrawable(Color.RED);
        Drawable foreground = new ColorDrawable(Color.BLUE);
        AdaptiveIconDrawable adaptiveIconDrawable = new AdaptiveIconDrawable(background, foreground);

        // Wrap the AdaptiveIconDrawable.
        Drawable wrappedDrawable = iconAdapter.wrap(adaptiveIconDrawable);

        // Check that the wrapped drawable is not null.
        assertNotNull(wrappedDrawable);
    }
}
