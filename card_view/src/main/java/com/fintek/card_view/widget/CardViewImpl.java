package com.fintek.card_view.widget;

import android.content.Context;
import android.content.res.ColorStateList;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * Interface for platform specific CardView implementations.
 */
interface CardViewImpl {
    void initialize(CardViewDelegate cardView, Context context, ColorStateList backgroundColor,
                    float radius, float elevation, float maxElevation,
                    @ColorInt int shadowStartColor,
                    @ColorInt int shadowEndColor);

    void setRadius(CardViewDelegate cardView, float radius);

    float getRadius(CardViewDelegate cardView);

    void setElevation(CardViewDelegate cardView, float elevation);

    float getElevation(CardViewDelegate cardView);

    void initStatic();

    void setMaxElevation(CardViewDelegate cardView, float maxElevation);

    float getMaxElevation(CardViewDelegate cardView);

    float getMinWidth(CardViewDelegate cardView);

    float getMinHeight(CardViewDelegate cardView);

    void updatePadding(CardViewDelegate cardView);

    void onCompatPaddingChanged(CardViewDelegate cardView);

    void onPreventCornerOverlapChanged(CardViewDelegate cardView);

    void setBackgroundColor(CardViewDelegate cardView, @Nullable ColorStateList color);

    ColorStateList getBackgroundColor(CardViewDelegate cardView);
}
