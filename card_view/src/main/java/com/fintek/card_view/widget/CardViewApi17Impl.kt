package com.fintek.card_view.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.RequiresApi

/**
 * Created by ChaoShen on 2021/5/19
 */
@RequiresApi(17)
internal class CardViewApi17Impl : CardViewBaseImpl(){
    override fun initStatic() {
        RoundRectDrawableWithShadow.sRoundRectHelper = object :
            RoundRectDrawableWithShadow.RoundRectHelper {
            override fun drawRoundRect(
                canvas: Canvas,
                bounds: RectF,
                cornerRadius: Float,
                paint: Paint
            ) {
                canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint)
            }
        }
    }
}