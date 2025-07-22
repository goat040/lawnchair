/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.lawnchair.icons;

import static android.graphics.Paint.FILTER_BITMAP_FLAG;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.WorkerThread;

import com.android.launcher3.icons.BaseIconFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to generate monochrome icons version for a given drawable.
 */
@TargetApi(Build.VERSION_CODES.TIRAMISU)
public class IconAdapter extends Drawable {

    private final Bitmap mFlatBitmap;
    private final Canvas mFlatCanvas;
    private final Paint mCopyPaint;

    private final Bitmap mAlphaBitmap;
    private final Canvas mAlphaCanvas;
    private final byte[] mPixels;

    private final int mBitmapSize;
    private final int mEdgePixelLength;

    private final Paint mDrawPaint;
    private final Rect mSrcRect;

    private final Context mContext;

    public IconAdapter(Context context, int iconBitmapSize) {
        mContext = context;
        float extraFactor = AdaptiveIconDrawable.getExtraInsetFraction();
        float viewPortScale = 1 / (1 + 2 * extraFactor);
        mBitmapSize = Math.round(iconBitmapSize * 2 * viewPortScale);
        mPixels = new byte[mBitmapSize * mBitmapSize];
        mEdgePixelLength = mBitmapSize * (mBitmapSize - iconBitmapSize) / 2;

        mFlatBitmap = Bitmap.createBitmap(mBitmapSize, mBitmapSize, Config.ARGB_8888);
        mFlatCanvas = new Canvas(mFlatBitmap);

        mAlphaBitmap = Bitmap.createBitmap(mBitmapSize, mBitmapSize, Config.ALPHA_8);
        mAlphaCanvas = new Canvas(mAlphaBitmap);

        mDrawPaint = new Paint(FILTER_BITMAP_FLAG);
        mDrawPaint.setColor(Color.WHITE);
        mSrcRect = new Rect(0, 0, mBitmapSize, mBitmapSize);

        mCopyPaint = new Paint(FILTER_BITMAP_FLAG);
        mCopyPaint.setBlendMode(BlendMode.SRC);

        // Crate a color matrix which converts the icon to grayscale and then uses the average
        // of RGB components as the alpha component.
        ColorMatrix satMatrix = new ColorMatrix();
        satMatrix.setSaturation(0);
        float[] vals = satMatrix.getArray();
        vals[15] = vals[16] = vals[17] = .3333f;
        vals[18] = vals[19] = 0;
        mCopyPaint.setColorFilter(new ColorMatrixColorFilter(vals));
    }

    IconAdapter(Context context, Drawable drawable, int iconBitmapSize) {
        this(context, iconBitmapSize);
        drawDrawable(drawable);
        generateMono();
    }

    private void drawDrawable(Drawable drawable) {
        if (drawable != null) {
            drawable.setBounds(0, 0, mBitmapSize, mBitmapSize);
            drawable.draw(mFlatCanvas);
        }
    }

    /**
     * Creates a monochrome version of the provided drawable
     */
    @WorkerThread
    public Drawable wrap(AdaptiveIconDrawable icon) {
        // Create a new bitmap to hold the resized icon.
        Bitmap resizedBitmap = Bitmap.createBitmap(mBitmapSize, mBitmapSize, Config.ARGB_8888);
        Canvas canvas = new Canvas(resizedBitmap);

        // Generate the background color.
        int backgroundColor = generateBackgroundColor(resizedBitmap);

        // Draw the background color.
        canvas.drawColor(backgroundColor);

        // Create the icon shape.
        Path path = createIconShape();

        // Clip the canvas to the icon shape.
        canvas.clipPath(path);

        // Resize the icon.
        icon.setBounds(0, 0, mBitmapSize, mBitmapSize);
        icon.draw(canvas);

        // Create a new drawable from the resized bitmap.
        Drawable resizedDrawable = new BitmapDrawable(mContext.getResources(), resizedBitmap);

        // Create a new IconAdapter from the resized drawable.
        return new IconAdapter(mContext, resizedDrawable, mBitmapSize);
    }

    private Path createIconShape() {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(mBitmapSize, 0);
        path.lineTo(mBitmapSize, mBitmapSize);
        path.lineTo(0, mBitmapSize);
        path.close();
        return path;
    }

    private int generateBackgroundColor(Bitmap bitmap) {
        // Get the dominant color of the icon.
        int dominantColor = getDominantColor(bitmap);

        // Generate a new background color that is complementary to the dominant color.
        int newBackgroundColor = Color.rgb(
                255 - Color.red(dominantColor),
                255 - Color.green(dominantColor),
                255 - Color.blue(dominantColor)
        );

        return newBackgroundColor;
    }

    private int getDominantColor(Bitmap bitmap) {
        // Create a map to store the frequency of each color.
        Map<Integer, Integer> colorMap = new HashMap<>();

        // Iterate over all the pixels in the bitmap.
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                // Get the color of the current pixel.
                int color = bitmap.getPixel(x, y);

                // Increment the frequency of the color in the map.
                Integer count = colorMap.get(color);
                if (count == null) {
                    count = 0;
                }
                colorMap.put(color, count + 1);
            }
        }

        // Find the color with the highest frequency.
        int dominantColor = 0;
        int maxCount = 0;
        for (Map.Entry<Integer, Integer> entry : colorMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                dominantColor = entry.getKey();
            }
        }

        return dominantColor;
    }

    @WorkerThread
    private void generateMono() {
        mAlphaCanvas.drawBitmap(mFlatBitmap, 0, 0, mCopyPaint);

        // Scale the end points:
        ByteBuffer buffer = ByteBuffer.wrap(mPixels);
        buffer.rewind();
        mAlphaBitmap.copyPixelsToBuffer(buffer);

        int min = 0xFF;
        int max = 0;
        for (byte b : mPixels) {
            min = Math.min(min, b & 0xFF);
            max = Math.max(max, b & 0xFF);
        }

        if (min < max) {
            // rescale pixels to increase contrast
            float range = max - min;

            // In order to check if the colors should be flipped, we just take the average color
            // of top and bottom edge which should correspond to be background color. If the edge
            // colors have more opacity, we flip the colors;
            int sum = 0;
            for (int i = 0; i < mEdgePixelLength; i++) {
                sum += (mPixels[i] & 0xFF);
                sum += (mPixels[mPixels.length - 1 - i] & 0xFF);
            }
            float edgeAverage = sum / (mEdgePixelLength * 2f);
            float edgeMapped = (edgeAverage - min) / range;
            boolean flipColor = edgeMapped > .5f;

            for (int i = 0; i < mPixels.length; i++) {
                int p = mPixels[i] & 0xFF;
                int p2 = Math.round((p - min) * 0xFF / range);
                mPixels[i] = flipColor ? (byte) (255 - p2) : (byte) (p2);
            }
            buffer.rewind();
            mAlphaBitmap.copyPixelsFromBuffer(buffer);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(mAlphaBitmap, mSrcRect, getBounds(), mDrawPaint);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int i) {
        mDrawPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mDrawPaint.setColorFilter(colorFilter);
    }
}
