/*
 * Copyright (C) 2014 The Android Open Source Project
 * Copyright (C) 2016 Thomas Robert Altstidl
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
package com.yoloo.android.ui.widget.fabmenu;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.drawable.DrawableWrapper;
import com.yoloo.android.R;

/**
 * A rounded rectangle drawable which also includes a shadow around.
 */
public class ShadowDrawableWrapper extends DrawableWrapper {
  // used to calculate content padding
  private final static double COS_45 = Math.cos(Math.toRadians(45));

  private final static float SHADOW_MULTIPLIER = 1.5f;

  private final int mInsetShadow; // extra shadow to avoid gaps between card and shadow
  private final RectF mContentBounds;
  private final int mShadowStartColor;
  private final int mShadowEndColor;
  private Paint mCornerShadowPaint;
  private Paint mEdgeShadowPaint;
  private float mCornerRadius;
  private Path mCornerShadowPath;
  // updated value with inset
  private float mMaxShadowSize;
  // actual value set by developer
  private float mRawMaxShadowSize;
  // multiplied value to account for shadow offset
  private float mShadowSize;
  // actual value set by developer
  private float mRawShadowSize;
  private boolean mDirty = true;
  private boolean mAddPaddingForCorners = false;

  /**
   * If shadow size is set to a value above max shadow, we print a warning
   */
  private boolean mPrintedShadowClipWarning = false;

  public ShadowDrawableWrapper(Resources resources, Drawable content, float radius,
      float shadowSize, float maxShadowSize) {
    super(content);

    mShadowStartColor = resources.getColor(R.color.label_shadow_start_color);
    mShadowEndColor = resources.getColor(R.color.label_shadow_end_color);
    mInsetShadow = resources.getDimensionPixelSize(R.dimen.label_inset_shadow);

    mCornerShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mCornerShadowPaint.setStyle(Paint.Style.FILL);
    mCornerRadius = (int) (radius + .5f);
    mContentBounds = new RectF();
    mEdgeShadowPaint = new Paint(mCornerShadowPaint);
    mEdgeShadowPaint.setAntiAlias(false);
    setShadowSize(shadowSize, maxShadowSize);
  }

  private static float calculateVerticalPadding(float maxShadowSize, float cornerRadius,
      boolean addPaddingForCorners) {
    if (addPaddingForCorners) {
      return (float) (maxShadowSize * SHADOW_MULTIPLIER + (1 - COS_45) * cornerRadius);
    } else {
      return maxShadowSize * SHADOW_MULTIPLIER;
    }
  }

  private static float calculateHorizontalPadding(float maxShadowSize, float cornerRadius,
      boolean addPaddingForCorners) {
    if (addPaddingForCorners) {
      return (float) (maxShadowSize + (1 - COS_45) * cornerRadius);
    } else {
      return maxShadowSize;
    }
  }

  /**
   * Casts the value to an even integer.
   */
  private int toEven(float value) {
    int i = (int) (value + .5f);
    if (i % 2 == 1) {
      return i - 1;
    }
    return i;
  }

  public void setAddPaddingForCorners(boolean addPaddingForCorners) {
    mAddPaddingForCorners = addPaddingForCorners;
    invalidateSelf();
  }

  @Override
  public void setAlpha(int alpha) {
    super.setAlpha(alpha);
    mCornerShadowPaint.setAlpha(alpha);
    mEdgeShadowPaint.setAlpha(alpha);
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    mDirty = true;
  }

  private void setShadowSize(float shadowSize, float maxShadowSize) {
    if (shadowSize < 0f) {
      throw new IllegalArgumentException("Invalid shadow size " + shadowSize +
          ". Must be >= 0");
    }
    if (maxShadowSize < 0f) {
      throw new IllegalArgumentException("Invalid max shadow size " + maxShadowSize +
          ". Must be >= 0");
    }
    shadowSize = toEven(shadowSize);
    maxShadowSize = toEven(maxShadowSize);
    if (shadowSize > maxShadowSize) {
      shadowSize = maxShadowSize;
      if (!mPrintedShadowClipWarning) {
        mPrintedShadowClipWarning = true;
      }
    }
    if (mRawShadowSize == shadowSize && mRawMaxShadowSize == maxShadowSize) {
      return;
    }
    mRawShadowSize = shadowSize;
    mRawMaxShadowSize = maxShadowSize;
    mShadowSize = (int) (shadowSize * SHADOW_MULTIPLIER + mInsetShadow + .5f);
    mMaxShadowSize = maxShadowSize + mInsetShadow;
    mDirty = true;
    invalidateSelf();
  }

  @Override
  public boolean getPadding(Rect padding) {
    int vOffset = (int) Math.ceil(calculateVerticalPadding(mRawMaxShadowSize, mCornerRadius,
        mAddPaddingForCorners));
    int hOffset = (int) Math.ceil(calculateHorizontalPadding(mRawMaxShadowSize, mCornerRadius,
        mAddPaddingForCorners));
    padding.set(hOffset, vOffset, hOffset, vOffset);
    return true;
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override
  public void draw(Canvas canvas) {
    if (mDirty) {
      buildComponents(getBounds());
      mDirty = false;
    }
    canvas.translate(0, mRawShadowSize / 2);
    drawShadow(canvas);
    canvas.translate(0, -mRawShadowSize / 2);

    super.draw(canvas);
  }

  private void drawShadow(Canvas canvas) {
    final float edgeShadowTop = -mCornerRadius - mShadowSize;
    final float inset = mCornerRadius + mInsetShadow + mRawShadowSize / 2;
    final boolean drawHorizontalEdges = mContentBounds.width() - 2 * inset > 0;
    final boolean drawVerticalEdges = mContentBounds.height() - 2 * inset > 0;
    // LT
    int saved = canvas.save();
    canvas.translate(mContentBounds.left + inset, mContentBounds.top + inset);
    canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
    if (drawHorizontalEdges) {
      canvas.drawRect(0, edgeShadowTop,
          mContentBounds.width() - 2 * inset, -mCornerRadius,
          mEdgeShadowPaint);
    }
    canvas.restoreToCount(saved);
    // RB
    saved = canvas.save();
    canvas.translate(mContentBounds.right - inset, mContentBounds.bottom - inset);
    canvas.rotate(180f);
    canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
    if (drawHorizontalEdges) {
      canvas.drawRect(0, edgeShadowTop,
          mContentBounds.width() - 2 * inset, -mCornerRadius + mShadowSize,
          mEdgeShadowPaint);
    }
    canvas.restoreToCount(saved);
    // LB
    saved = canvas.save();
    canvas.translate(mContentBounds.left + inset, mContentBounds.bottom - inset);
    canvas.rotate(270f);
    canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
    if (drawVerticalEdges) {
      canvas.drawRect(0, edgeShadowTop,
          mContentBounds.height() - 2 * inset, -mCornerRadius, mEdgeShadowPaint);
    }
    canvas.restoreToCount(saved);
    // RT
    saved = canvas.save();
    canvas.translate(mContentBounds.right - inset, mContentBounds.top + inset);
    canvas.rotate(90f);
    canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
    if (drawVerticalEdges) {
      canvas.drawRect(0, edgeShadowTop,
          mContentBounds.height() - 2 * inset, -mCornerRadius, mEdgeShadowPaint);
    }
    canvas.restoreToCount(saved);
  }

  private void buildShadowCorners() {
    RectF innerBounds = new RectF(-mCornerRadius, -mCornerRadius, mCornerRadius, mCornerRadius);
    RectF outerBounds = new RectF(innerBounds);
    outerBounds.inset(-mShadowSize, -mShadowSize);

    if (mCornerShadowPath == null) {
      mCornerShadowPath = new Path();
    } else {
      mCornerShadowPath.reset();
    }
    mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
    mCornerShadowPath.moveTo(-mCornerRadius, 0);
    mCornerShadowPath.rLineTo(-mShadowSize, 0);
    // outer arc
    mCornerShadowPath.arcTo(outerBounds, 180f, 90f, false);
    // inner arc
    mCornerShadowPath.arcTo(innerBounds, 270f, -90f, false);
    mCornerShadowPath.close();
    float startRatio = mCornerRadius / (mCornerRadius + mShadowSize);
    mCornerShadowPaint.setShader(new RadialGradient(0, 0, mCornerRadius + mShadowSize,
        new int[] {mShadowStartColor, mShadowStartColor, mShadowEndColor},
        new float[] {0f, startRatio, 1f}
        , Shader.TileMode.CLAMP));

    // we offset the content shadowSize/2 pixels up to make it more realistic.
    // this is why edge shadow shader has some extra space
    // When drawing bottom edge shadow, we use that extra space.
    mEdgeShadowPaint.setShader(new LinearGradient(0, -mCornerRadius + mShadowSize, 0,
        -mCornerRadius - mShadowSize,
        new int[] {mShadowStartColor, mShadowStartColor, mShadowEndColor},
        new float[] {0f, .5f, 1f}, Shader.TileMode.CLAMP));
    mEdgeShadowPaint.setAntiAlias(false);
  }

  private void buildComponents(Rect bounds) {
    // Card is offset SHADOW_MULTIPLIER * maxShadowSize to account for the shadow shift.
    // We could have different top-bottom offsets to avoid extra gap above but in that case
    // center aligning Views inside the CardView would be problematic.
    final float verticalOffset = mRawMaxShadowSize * SHADOW_MULTIPLIER;
    mContentBounds.set(bounds.left + mRawMaxShadowSize, bounds.top + verticalOffset,
        bounds.right - mRawMaxShadowSize, bounds.bottom - verticalOffset);

    getWrappedDrawable().setBounds((int) mContentBounds.left, (int) mContentBounds.top,
        (int) mContentBounds.right, (int) mContentBounds.bottom);

    buildShadowCorners();
  }

  float getCornerRadius() {
    return mCornerRadius;
  }

  public void setCornerRadius(float radius) {
    if (radius < 0f) {
      throw new IllegalArgumentException("Invalid radius " + radius +
          ". Must be >= 0");
    }
    radius = (int) (radius + .5f);
    if (mCornerRadius == radius) {
      return;
    }
    mCornerRadius = radius;
    mDirty = true;
    invalidateSelf();
  }

  void getMaxShadowAndCornerPadding(Rect into) {
    getPadding(into);
  }

  float getShadowSize() {
    return mRawShadowSize;
  }

  void setShadowSize(float size) {
    setShadowSize(size, mRawMaxShadowSize);
  }

  float getMaxShadowSize() {
    return mRawMaxShadowSize;
  }

  void setMaxShadowSize(float size) {
    setShadowSize(mRawShadowSize, size);
  }

  float getMinWidth() {
    final float content = 2 *
        Math.max(mRawMaxShadowSize, mCornerRadius + mInsetShadow + mRawMaxShadowSize / 2);
    return content + (mRawMaxShadowSize + mInsetShadow) * 2;
  }

  float getMinHeight() {
    final float content = 2 * Math.max(mRawMaxShadowSize, mCornerRadius + mInsetShadow
        + mRawMaxShadowSize * SHADOW_MULTIPLIER / 2);
    return content + (mRawMaxShadowSize * SHADOW_MULTIPLIER + mInsetShadow) * 2;
  }
}