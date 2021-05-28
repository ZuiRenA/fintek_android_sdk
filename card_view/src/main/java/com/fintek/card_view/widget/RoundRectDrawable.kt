package com.fintek.card_view.widget

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.RequiresApi
import com.fintek.card_view.R
import kotlin.math.ceil
import kotlin.math.cos

/**
 * A rounded rectangle drawable which also includes a shadow around.
 */
internal class RoundRectDrawableWithShadow @JvmOverloads constructor(
    resources: Resources, backgroundColor: ColorStateList?, radius: Float,
    shadowSize: Float, maxShadowSize: Float,
    shadowStartColor: Int = resources.getColor(R.color.cardview_shadow_start_color),
    shadowEndColor: Int = resources.getColor(R.color.cardview_shadow_end_color)
) : Drawable() {
    private val mCornerShadowPaint: Paint
    private val mEdgeShadowPaint: Paint
    private val mCardBounds: RectF
    private var mCornerRadius: Float

    // extra shadow to avoid gaps between card and shadow
    private val mInsetShadow: Int =
        resources.getDimensionPixelSize(R.dimen.cardview_compat_inset_shadow)
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private var mCornerShadowPath: Path? = null

    // actual value set by developer
    private var mRawMaxShadowSize = 0f

    // multiplied value to account for shadow offset
    private var mShadowSize = 0f

    // actual value set by developer
    private var mRawShadowSize = 0f
    private var mBackground: ColorStateList? = null
    private var mDirty = true
    private val mShadowStartColor: Int = shadowStartColor
    private val mShadowEndColor: Int = shadowEndColor
    private var mAddPaddingForCorners = true

    /**
     * If shadow size is set to a value above max shadow, we print a warning
     */
    private var mPrintedShadowClipWarning = false
    private fun setBackground(color: ColorStateList?) {
        mBackground = color ?: ColorStateList.valueOf(Color.TRANSPARENT)
        mPaint.color = mBackground!!.getColorForState(state, mBackground!!.defaultColor)
    }

    /**
     * Casts the value to an even integer.
     */
    private fun toEven(value: Float): Int {
        val i = (value + .5f).toInt()
        return if (i % 2 == 1) {
            i - 1
        } else i
    }

    fun setAddPaddingForCorners(addPaddingForCorners: Boolean) {
        mAddPaddingForCorners = addPaddingForCorners
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        mCornerShadowPaint.alpha = alpha
        mEdgeShadowPaint.alpha = alpha
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mDirty = true
    }

    private fun setShadowSize(shadowSize: Float, maxShadowSize: Float) {
        var internalShadowSize = shadowSize
        var internalMaxShadowSize = maxShadowSize
        require(internalShadowSize >= 0f) {
            ("Invalid shadow size " + internalShadowSize
                    + ". Must be >= 0")
        }
        require(internalMaxShadowSize >= 0f) {
            ("Invalid max shadow size " + internalMaxShadowSize
                    + ". Must be >= 0")
        }
        internalShadowSize = toEven(internalShadowSize).toFloat()
        internalMaxShadowSize = toEven(internalMaxShadowSize).toFloat()
        if (internalShadowSize > internalMaxShadowSize) {
            internalShadowSize = internalMaxShadowSize
            if (!mPrintedShadowClipWarning) {
                mPrintedShadowClipWarning = true
            }
        }
        if (mRawShadowSize == internalShadowSize && mRawMaxShadowSize == internalMaxShadowSize) {
            return
        }
        mRawShadowSize = internalShadowSize
        mRawMaxShadowSize = internalMaxShadowSize
        mShadowSize = internalShadowSize * SHADOW_MULTIPLIER + mInsetShadow + .5f
        mDirty = true
        invalidateSelf()
    }

    override fun getPadding(padding: Rect): Boolean {
        val vOffset = ceil(
            calculateVerticalPadding(
                mRawMaxShadowSize, mCornerRadius,
                mAddPaddingForCorners
            ).toDouble()
        ).toInt()
        val hOffset = ceil(
            calculateHorizontalPadding(
                mRawMaxShadowSize, mCornerRadius,
                mAddPaddingForCorners
            ).toDouble()
        ).toInt()
        padding[hOffset, vOffset, hOffset] = vOffset
        return true
    }

    override fun onStateChange(stateSet: IntArray): Boolean {
        val newColor = mBackground!!.getColorForState(stateSet, mBackground!!.defaultColor)
        if (mPaint.color == newColor) {
            return false
        }
        mPaint.color = newColor
        mDirty = true
        invalidateSelf()
        return true
    }

    override fun isStateful(): Boolean {
        return mBackground != null && mBackground!!.isStateful || super.isStateful()
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun draw(canvas: Canvas) {
        if (mDirty) {
            buildComponents(bounds)
            mDirty = false
        }
        canvas.translate(0f, mRawShadowSize / 2)
        drawShadow(canvas)
        canvas.translate(0f, -mRawShadowSize / 2)
        sRoundRectHelper!!.drawRoundRect(canvas, mCardBounds, mCornerRadius, mPaint)
    }

    private fun drawShadow(canvas: Canvas) {
        val edgeShadowTop = -mCornerRadius - mShadowSize
        val inset = mCornerRadius + mInsetShadow + mRawShadowSize / 2
        val drawHorizontalEdges = mCardBounds.width() - 2 * inset > 0
        val drawVerticalEdges = mCardBounds.height() - 2 * inset > 0
        // LT
        var saved = canvas.save()
        canvas.translate(mCardBounds.left + inset, mCardBounds.top + inset)
        canvas.drawPath(mCornerShadowPath!!, mCornerShadowPaint)
        if (drawHorizontalEdges) {
            canvas.drawRect(
                0f, edgeShadowTop,
                mCardBounds.width() - 2 * inset, -mCornerRadius,
                mEdgeShadowPaint
            )
        }
        canvas.restoreToCount(saved)
        // RB
        saved = canvas.save()
        canvas.translate(mCardBounds.right - inset, mCardBounds.bottom - inset)
        canvas.rotate(180f)
        canvas.drawPath(mCornerShadowPath!!, mCornerShadowPaint)
        if (drawHorizontalEdges) {
            canvas.drawRect(
                0f, edgeShadowTop,
                mCardBounds.width() - 2 * inset, -mCornerRadius + mShadowSize,
                mEdgeShadowPaint
            )
        }
        canvas.restoreToCount(saved)
        // LB
        saved = canvas.save()
        canvas.translate(mCardBounds.left + inset, mCardBounds.bottom - inset)
        canvas.rotate(270f)
        canvas.drawPath(mCornerShadowPath!!, mCornerShadowPaint)
        if (drawVerticalEdges) {
            canvas.drawRect(
                0f, edgeShadowTop,
                mCardBounds.height() - 2 * inset, -mCornerRadius, mEdgeShadowPaint
            )
        }
        canvas.restoreToCount(saved)
        // RT
        saved = canvas.save()
        canvas.translate(mCardBounds.right - inset, mCardBounds.top + inset)
        canvas.rotate(90f)
        canvas.drawPath(mCornerShadowPath!!, mCornerShadowPaint)
        if (drawVerticalEdges) {
            canvas.drawRect(
                0f, edgeShadowTop,
                mCardBounds.height() - 2 * inset, -mCornerRadius, mEdgeShadowPaint
            )
        }
        canvas.restoreToCount(saved)
    }

    private fun buildShadowCorners() {
        val innerBounds = RectF(-mCornerRadius, -mCornerRadius, mCornerRadius, mCornerRadius)
        val outerBounds = RectF(innerBounds)
        outerBounds.inset(-mShadowSize, -mShadowSize)
        if (mCornerShadowPath == null) {
            mCornerShadowPath = Path()
        } else {
            mCornerShadowPath!!.reset()
        }
        mCornerShadowPath!!.fillType = Path.FillType.EVEN_ODD
        mCornerShadowPath!!.moveTo(-mCornerRadius, 0f)
        mCornerShadowPath!!.rLineTo(-mShadowSize, 0f)
        // outer arc
        mCornerShadowPath!!.arcTo(outerBounds, 180f, 90f, false)
        // inner arc
        mCornerShadowPath!!.arcTo(innerBounds, 270f, -90f, false)
        mCornerShadowPath!!.close()
        val startRatio = mCornerRadius / (mCornerRadius + mShadowSize)
        mCornerShadowPaint.shader = RadialGradient(
            0f,
            0f,
            mCornerRadius + mShadowSize,
            intArrayOf(mShadowStartColor, mShadowStartColor, mShadowEndColor),
            floatArrayOf(0f, startRatio, 1f),
            Shader.TileMode.CLAMP
        )

        // we offset the content shadowSize/2 pixels up to make it more realistic.
        // this is why edge shadow shader has some extra space
        // When drawing bottom edge shadow, we use that extra space.
        mEdgeShadowPaint.shader = LinearGradient(
            0f,
            -mCornerRadius + mShadowSize,
            0f,
            -mCornerRadius - mShadowSize,
            intArrayOf(mShadowStartColor, mShadowStartColor, mShadowEndColor),
            floatArrayOf(0f, .5f, 1f),
            Shader.TileMode.CLAMP
        )
        mEdgeShadowPaint.isAntiAlias = false
    }

    private fun buildComponents(bounds: Rect) {
        // Card is offset SHADOW_MULTIPLIER * maxShadowSize to account for the shadow shift.
        // We could have different top-bottom offsets to avoid extra gap above but in that case
        // center aligning Views inside the CardView would be problematic.
        val verticalOffset = mRawMaxShadowSize * SHADOW_MULTIPLIER
        mCardBounds[bounds.left + mRawMaxShadowSize, bounds.top + verticalOffset, bounds.right - mRawMaxShadowSize] =
            bounds.bottom - verticalOffset
        buildShadowCorners()
    }

    var cornerRadius: Float
        get() = mCornerRadius
        set(radius) {
            var internalRadius = radius
            require(internalRadius >= 0f) { "Invalid radius $internalRadius. Must be >= 0" }
            internalRadius += .5f
            if (mCornerRadius == internalRadius) {
                return
            }
            mCornerRadius = internalRadius
            mDirty = true
            invalidateSelf()
        }

    fun getMaxShadowAndCornerPadding(into: Rect) {
        getPadding(into)
    }

    var shadowSize: Float
        get() = mRawShadowSize
        set(size) {
            setShadowSize(size, mRawMaxShadowSize)
        }
    var maxShadowSize: Float
        get() = mRawMaxShadowSize
        set(size) {
            setShadowSize(mRawShadowSize, size)
        }
    val minWidth: Float
        get() {
            val content = (2
                    * mRawMaxShadowSize.coerceAtLeast(mCornerRadius + mInsetShadow + mRawMaxShadowSize / 2))
            return content + (mRawMaxShadowSize + mInsetShadow) * 2
        }
    val minHeight: Float
        get() {
            val content = 2 * mRawMaxShadowSize.coerceAtLeast(
                mCornerRadius + mInsetShadow
                        + mRawMaxShadowSize * SHADOW_MULTIPLIER / 2
            )
            return content + (mRawMaxShadowSize * SHADOW_MULTIPLIER + mInsetShadow) * 2
        }
    var color: ColorStateList?
        get() = mBackground
        set(color) {
            setBackground(color)
            invalidateSelf()
        }

    internal interface RoundRectHelper {
        fun drawRoundRect(canvas: Canvas, bounds: RectF, cornerRadius: Float, paint: Paint)
    }

    companion object {
        // used to calculate content padding
        private val COS_45 = cos(Math.toRadians(45.0))
        private const val SHADOW_MULTIPLIER = 1.5f

        @JvmField var sRoundRectHelper: RoundRectHelper? = null
        fun calculateVerticalPadding(
            maxShadowSize: Float, cornerRadius: Float,
            addPaddingForCorners: Boolean
        ): Float {
            return if (addPaddingForCorners) {
                (maxShadowSize * SHADOW_MULTIPLIER + (1 - COS_45) * cornerRadius).toFloat()
            } else {
                maxShadowSize * SHADOW_MULTIPLIER
            }
        }

        fun calculateHorizontalPadding(
            maxShadowSize: Float, cornerRadius: Float,
            addPaddingForCorners: Boolean
        ): Float {
            return if (addPaddingForCorners) {
                (maxShadowSize + (1 - COS_45) * cornerRadius).toFloat()
            } else {
                maxShadowSize
            }
        }
    }

    init {
        setBackground(backgroundColor)
        mCornerShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        mCornerShadowPaint.style = Paint.Style.FILL
        mCornerRadius = radius + .5f
        mCardBounds = RectF()
        mEdgeShadowPaint = Paint(mCornerShadowPaint)
        mEdgeShadowPaint.isAntiAlias = false
        setShadowSize(shadowSize, maxShadowSize)
    }
}

@RequiresApi(21)
internal class RoundRectDrawable(
    backgroundColor: ColorStateList?,
    private var mRadius: Float,
    private val shadowStartColor: Int,
    private val shadowEndColor: Int,
) : Drawable() {
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
    private val mBoundsF: RectF
    private val mBoundsI: Rect
    internal var mPadding = 0f
    private var mInsetForPadding = false
    private var mInsetForRadius = true
    private var mBackground: ColorStateList? = null
    private var mTintFilter: PorterDuffColorFilter? = null
    private var mTint: ColorStateList? = null
    private var mTintMode: PorterDuff.Mode? = PorterDuff.Mode.SRC_IN

    private fun setBackground(color: ColorStateList?) {
        mBackground = color ?: ColorStateList.valueOf(Color.TRANSPARENT)
        mPaint.color = mBackground!!.getColorForState(state, mBackground!!.defaultColor)
    }

    fun setPadding(padding: Float, insetForPadding: Boolean, insetForRadius: Boolean) {
        if (padding == this.mPadding && mInsetForPadding == insetForPadding && mInsetForRadius == insetForRadius) {
            return
        }
        this.mPadding = padding
        mInsetForPadding = insetForPadding
        mInsetForRadius = insetForRadius
        updateBounds(null)
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        val paint = mPaint
        val clearColorFilter: Boolean
        if (mTintFilter != null && paint.colorFilter == null) {
            paint.colorFilter = mTintFilter
            clearColorFilter = true
        } else {
            clearColorFilter = false
        }
        canvas.drawRoundRect(mBoundsF, mRadius, mRadius, paint)
        if (clearColorFilter) {
            paint.colorFilter = null
        }
    }

    private fun updateBounds(bounds: Rect?) {
        var internalBounds = bounds
        if (internalBounds == null) {
            internalBounds = getBounds()
        }
        mBoundsF[internalBounds.left.toFloat(), internalBounds.top.toFloat(), internalBounds.right.toFloat()] =
            internalBounds.bottom.toFloat()
        mBoundsI.set(internalBounds)
        if (mInsetForPadding) {
            val vInset = RoundRectDrawableWithShadow.calculateVerticalPadding(
                mPadding, mRadius, mInsetForRadius
            )
            val hInset = RoundRectDrawableWithShadow.calculateHorizontalPadding(
                mPadding, mRadius, mInsetForRadius
            )
            mBoundsI.inset(
                ceil(hInset.toDouble()).toInt(),
                ceil(vInset.toDouble()).toInt()
            )
            // to make sure they have same bounds.
            mBoundsF.set(mBoundsI)
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        updateBounds(bounds)
    }

    override fun getOutline(outline: Outline) {
        outline.setRoundRect(mBoundsI, mRadius)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    var radius: Float
        get() = mRadius
        set(radius) {
            if (radius == mRadius) {
                return
            }
            mRadius = radius
            updateBounds(null)
            invalidateSelf()
        }
    var color: ColorStateList?
        get() = mBackground
        set(color) {
            setBackground(color)
            invalidateSelf()
        }

    override fun setTintList(tint: ColorStateList?) {
        mTint = tint
        mTintFilter = createTintFilter(mTint, mTintMode)
        invalidateSelf()
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        mTintMode = tintMode
        mTintFilter = createTintFilter(mTint, mTintMode)
        invalidateSelf()
    }

    override fun onStateChange(stateSet: IntArray): Boolean {
        val newColor = mBackground!!.getColorForState(stateSet, mBackground!!.defaultColor)
        val colorChanged = newColor != mPaint.color
        if (colorChanged) {
            mPaint.color = newColor
        }
        if (mTint != null && mTintMode != null) {
            mTintFilter = createTintFilter(mTint, mTintMode)
            return true
        }
        return colorChanged
    }

    override fun isStateful(): Boolean {
        return (mTint != null && mTint!!.isStateful
                || mBackground != null && mBackground!!.isStateful || super.isStateful())
    }

    /**
     * Ensures the tint filter is consistent with the current tint color and
     * mode.
     */
    private fun createTintFilter(
        tint: ColorStateList?,
        tintMode: PorterDuff.Mode?
    ): PorterDuffColorFilter? {
        if (tint == null || tintMode == null) {
            return null
        }
        val color = tint.getColorForState(state, Color.TRANSPARENT)
        return PorterDuffColorFilter(color, tintMode)
    }

    init {
        setBackground(backgroundColor)
        mBoundsF = RectF()
        mBoundsI = Rect()
    }
}