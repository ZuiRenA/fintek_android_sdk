package com.fintek.card_view.widget

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.RequiresApi
import kotlin.math.ceil

/**
 * Created by ChaoShen on 2021/5/19
 */
@RequiresApi(21)
internal class CardViewApi21Impl : CardViewBaseImpl() {
    override fun initialize(
        cardView: CardViewDelegate,
        context: Context?,
        backgroundColor: ColorStateList?,
        radius: Float,
        elevation: Float,
        maxElevation: Float,
        shadowStartColor: Int,
        shadowEndColor: Int
    ) {
        val background = RoundRectDrawable(backgroundColor, radius, shadowStartColor, shadowEndColor)
        cardView.cardBackground = background
        val view = cardView.cardView
        view.clipToOutline = true
        view.elevation = elevation
        setMaxElevation(cardView, maxElevation)
    }

    override fun setRadius(cardView: CardViewDelegate, radius: Float) {
        getCardBackground(cardView).radius = radius
    }

    override fun initStatic() {}

    override fun setMaxElevation(cardView: CardViewDelegate, maxElevation: Float) {
        getCardBackground(cardView).setPadding(
            maxElevation,
            cardView.useCompatPadding, cardView.preventCornerOverlap
        )
        updatePadding(cardView)
    }

    override fun getMaxElevation(cardView: CardViewDelegate): Float {
        return getCardBackground(cardView).mPadding
    }

    override fun getMinWidth(cardView: CardViewDelegate): Float {
        return getRadius(cardView) * 2
    }

    override fun getMinHeight(cardView: CardViewDelegate): Float {
        return getRadius(cardView) * 2
    }

    override fun getRadius(cardView: CardViewDelegate): Float {
        return getCardBackground(cardView).radius
    }

    override fun setElevation(cardView: CardViewDelegate, elevation: Float) {
        cardView.cardView.elevation = elevation
    }

    override fun getElevation(cardView: CardViewDelegate): Float {
        return cardView.cardView.elevation
    }

    override fun updatePadding(cardView: CardViewDelegate) {
        if (!cardView.useCompatPadding) {
            cardView.setShadowPadding(0, 0, 0, 0)
            return
        }
        val elevation = getMaxElevation(cardView)
        val radius = getRadius(cardView)
        val hPadding = ceil(
            RoundRectDrawableWithShadow.calculateHorizontalPadding(
                elevation,
                radius,
                cardView.preventCornerOverlap
            ).toDouble()
        ).toInt()
        val vPadding = ceil(
            RoundRectDrawableWithShadow.calculateVerticalPadding(
                elevation,
                radius,
                cardView.preventCornerOverlap
            ).toDouble()
        ).toInt()
        cardView.setShadowPadding(hPadding, vPadding, hPadding, vPadding)
    }

    override fun onCompatPaddingChanged(cardView: CardViewDelegate) {
        setMaxElevation(cardView, getMaxElevation(cardView))
    }

    override fun onPreventCornerOverlapChanged(cardView: CardViewDelegate) {
        setMaxElevation(cardView, getMaxElevation(cardView))
    }

    override fun setBackgroundColor(cardView: CardViewDelegate, color: ColorStateList?) {
        getCardBackground(cardView).color = color
    }

    override fun getBackgroundColor(cardView: CardViewDelegate): ColorStateList? {
        return getCardBackground(cardView).color
    }

    private fun getCardBackground(cardView: CardViewDelegate): RoundRectDrawable {
        return cardView.cardBackground as RoundRectDrawable
    }
}