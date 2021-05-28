package com.fintek.card_view.widget


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import com.fintek.card_view.R
import kotlin.math.ceil


class ShadowCardView : FrameLayout {
    companion object {
        private val COLOR_BACKGROUND_ATTR = intArrayOf(android.R.attr.colorBackground)
        private val IMPL: CardViewImpl = when {
            Build.VERSION.SDK_INT >= 21 -> CardViewApi21Impl()
            Build.VERSION.SDK_INT >= 17 -> CardViewApi17Impl()
            else -> CardViewBaseImpl()
        }

        init {
            IMPL.initStatic()
        }
    }

    private var mCompatPadding = false

    private var mPreventCornerOverlap = false

    private var mShadowStartColor: Int = resources.getColor(R.color.cardview_shadow_start_color)

    private var mShadowEndColor: Int = resources.getColor(R.color.cardview_shadow_end_color)

    /**
     * CardView requires to have a particular minimum size to draw shadows before API 21. If
     * developer also sets min width/height, they might be overridden.
     *
     * CardView works around this issue by recording user given parameters and using an internal
     * method to set them.
     */
    private var mUserSetMinWidth = 0
    private var mUserSetMinHeight = 0

    private val mContentPadding = Rect()

    private val mShadowBounds = Rect()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.cardViewStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ShadowCardView, defStyleAttr,
            R.style.CardView
        )
        val backgroundColor: ColorStateList? = if (a.hasValue(R.styleable.ShadowCardView_shadowCardBackgroundColor)) {
            a.getColorStateList(R.styleable.ShadowCardView_shadowCardBackgroundColor)
        } else {
            // There isn't one set, so we'll compute one based on the theme
            val aa = getContext().obtainStyledAttributes(COLOR_BACKGROUND_ATTR)
            val themeColorBackground = aa.getColor(0, 0)
            aa.recycle()

            // If the theme colorBackground is light, use our own light color, otherwise dark
            val hsv = FloatArray(3)
            Color.colorToHSV(themeColorBackground, hsv)
            ColorStateList.valueOf(
                if (hsv[2] > 0.5f) resources.getColor(R.color.cardview_light_background) else resources.getColor(
                    R.color.cardview_dark_background
                )
            )
        }
        val radius = a.getDimension(R.styleable.ShadowCardView_shadowCardCornerRadius, 0f)
        val elevation = a.getDimension(R.styleable.ShadowCardView_shadowCardElevation, 0f)
        var maxElevation = a.getDimension(R.styleable.ShadowCardView_shadowCardMaxElevation, 0f)
        mCompatPadding = a.getBoolean(R.styleable.ShadowCardView_shadowCardUseCompatPadding, false)
        mPreventCornerOverlap = a.getBoolean(R.styleable.ShadowCardView_shadowCardPreventCornerOverlap, true)
        val defaultPadding = a.getDimensionPixelSize(R.styleable.ShadowCardView_shadowContentPadding, 0)
        mContentPadding.left = a.getDimensionPixelSize(
            R.styleable.ShadowCardView_shadowContentPaddingLeft,
            defaultPadding
        )
        mContentPadding.top = a.getDimensionPixelSize(
            R.styleable.ShadowCardView_shadowContentPaddingTop,
            defaultPadding
        )
        mContentPadding.right = a.getDimensionPixelSize(
            R.styleable.ShadowCardView_shadowContentPaddingRight,
            defaultPadding
        )
        mContentPadding.bottom = a.getDimensionPixelSize(
            R.styleable.ShadowCardView_shadowContentPaddingBottom,
            defaultPadding
        )
        if (elevation > maxElevation) {
            maxElevation = elevation
        }
        mUserSetMinWidth = a.getDimensionPixelSize(R.styleable.ShadowCardView_android_minWidth, 0)
        mUserSetMinHeight = a.getDimensionPixelSize(R.styleable.ShadowCardView_android_minHeight, 0)

        mShadowStartColor = a.getColor(R.styleable.ShadowCardView_shadowStartColor,
            ContextCompat.getColor(context, R.color.cardview_shadow_start_color))
        mShadowEndColor = a.getColor(R.styleable.ShadowCardView_shadowEndColor,
            ContextCompat.getColor(context, R.color.cardview_shadow_end_color))
        a.recycle()
        IMPL.initialize(
            mCardViewDelegate, context, backgroundColor, radius,
            elevation, maxElevation, mShadowStartColor, mShadowEndColor
        )
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        // NO OP
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        // NO OP
    }

    /**
     * Returns whether CardView will add inner padding on platforms Lollipop and after.
     *
     * @return `true` if CardView adds inner padding on platforms Lollipop and after to
     * have same dimensions with platforms before Lollipop.
     */
    fun getUseCompatPadding(): Boolean {
        return mCompatPadding
    }

    /**
     * CardView adds additional padding to draw shadows on platforms before Lollipop.
     *
     *
     * This may cause Cards to have different sizes between Lollipop and before Lollipop. If you
     * need to align CardView with other Views, you may need api version specific dimension
     * resources to account for the changes.
     * As an alternative, you can set this flag to `true` and CardView will add the same
     * padding values on platforms Lollipop and after.
     *
     *
     * Since setting this flag to true adds unnecessary gaps in the UI, default value is
     * `false`.
     *
     * @param useCompatPadding `true` if CardView should add padding for the shadows on
     * platforms Lollipop and above.
     * @attr ref androidx.cardview.R.styleable#CardView_cardUseCompatPadding
     */
    fun setUseCompatPadding(useCompatPadding: Boolean) {
        if (mCompatPadding != useCompatPadding) {
            mCompatPadding = useCompatPadding
            IMPL.onCompatPaddingChanged(mCardViewDelegate)
        }
    }

    /**
     * Sets the padding between the Card's edges and the children of CardView.
     *
     *
     * Depending on platform version or [getUseCompatPadding] settings, CardView may
     * update these values before calling [android.view.View.setPadding].
     *
     * @param left   The left padding in pixels
     * @param top    The top padding in pixels
     * @param right  The right padding in pixels
     * @param bottom The bottom padding in pixels
     * @attr ref androidx.cardview.R.styleable#CardView_contentPadding
     * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingLeft
     * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingTop
     * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingRight
     * @attr ref androidx.cardview.R.styleable#CardView_contentPaddingBottom
     */
    fun setContentPadding(@Px left: Int, @Px top: Int, @Px right: Int, @Px bottom: Int) {
        mContentPadding[left, top, right] = bottom
        IMPL.updatePadding(mCardViewDelegate)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var internalWidthMeasureSpec = widthMeasureSpec
        var internalHeightMeasureSpec = heightMeasureSpec
        if (IMPL !is CardViewApi21Impl) {
            when (val widthMode = MeasureSpec.getMode(internalWidthMeasureSpec)) {
                MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> {
                    val minWidth =
                        ceil(IMPL.getMinWidth(mCardViewDelegate).toDouble()).toInt()
                    internalWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        minWidth.coerceAtLeast(MeasureSpec.getSize(internalWidthMeasureSpec)), widthMode
                    )
                }
                MeasureSpec.UNSPECIFIED -> {
                }
            }
            when (val heightMode = MeasureSpec.getMode(internalHeightMeasureSpec)) {
                MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> {
                    val minHeight =
                        ceil(IMPL.getMinHeight(mCardViewDelegate).toDouble()).toInt()
                    internalHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        minHeight.coerceAtLeast(MeasureSpec.getSize(internalHeightMeasureSpec)), heightMode
                    )
                }
                MeasureSpec.UNSPECIFIED -> {
                }
            }
            super.onMeasure(internalWidthMeasureSpec, internalHeightMeasureSpec)
        } else {
            super.onMeasure(internalWidthMeasureSpec, internalHeightMeasureSpec)
        }
    }

    override fun setMinimumWidth(minWidth: Int) {
        mUserSetMinWidth = minWidth
        super.setMinimumWidth(minWidth)
    }


    override fun setMinimumHeight(minHeight: Int) {
        mUserSetMinHeight = minHeight
        super.setMinimumHeight(minHeight)
    }


    /**
     * Updates the background color of the CardView
     *
     * @param color The new color to set for the card background
     * @attr ref androidx.cardview.R.styleable#CardView_cardBackgroundColor
     */
    fun setCardBackgroundColor(@ColorInt color: Int) {
        IMPL.setBackgroundColor(mCardViewDelegate, ColorStateList.valueOf(color))
    }

    /**
     * Updates the background ColorStateList of the CardView
     *
     * @param color The new ColorStateList to set for the card background
     * @attr ref androidx.cardview.R.styleable#CardView_cardBackgroundColor
     */
    fun setCardBackgroundColor(color: ColorStateList?) {
        IMPL.setBackgroundColor(mCardViewDelegate, color)
    }

    /**
     * Returns the background color state list of the CardView.
     *
     * @return The background color state list of the CardView.
     */
    fun getCardBackgroundColor(): ColorStateList {
        return IMPL.getBackgroundColor(mCardViewDelegate)
    }

    /**
     * Returns the inner padding after the Card's left edge
     *
     * @return the inner padding after the Card's left edge
     */
    @Px
    fun getContentPaddingLeft(): Int {
        return mContentPadding.left
    }

    /**
     * Returns the inner padding before the Card's right edge
     *
     * @return the inner padding before the Card's right edge
     */
    @Px
    fun getContentPaddingRight(): Int {
        return mContentPadding.right
    }

    /**
     * Returns the inner padding after the Card's top edge
     *
     * @return the inner padding after the Card's top edge
     */
    @Px
    fun getContentPaddingTop(): Int {
        return mContentPadding.top
    }

    /**
     * Returns the inner padding before the Card's bottom edge
     *
     * @return the inner padding before the Card's bottom edge
     */
    @Px
    fun getContentPaddingBottom(): Int {
        return mContentPadding.bottom
    }

    /**
     * Updates the corner radius of the CardView.
     *
     * @param radius The radius in pixels of the corners of the rectangle shape
     * @attr ref androidx.cardview.R.styleable#CardView_cardCornerRadius
     * @see .setRadius
     */
    fun setRadius(radius: Float) {
        IMPL.setRadius(mCardViewDelegate, radius)
    }

    /**
     * Returns the corner radius of the CardView.
     *
     * @return Corner radius of the CardView
     * @see .getRadius
     */
    fun getRadius(): Float {
        return IMPL.getRadius(mCardViewDelegate)
    }

    /**
     * Updates the backward compatible elevation of the CardView.
     *
     * @param elevation The backward compatible elevation in pixels.
     * @attr ref androidx.cardview.R.styleable#CardView_cardElevation
     * @see .getCardElevation
     * @see .setMaxCardElevation
     */
    fun setCardElevation(elevation: Float) {
        IMPL.setElevation(mCardViewDelegate, elevation)
    }

    /**
     * Returns the backward compatible elevation of the CardView.
     *
     * @return Elevation of the CardView
     * @see .setCardElevation
     * @see .getMaxCardElevation
     */
    fun getCardElevation(): Float {
        return IMPL.getElevation(mCardViewDelegate)
    }

    /**
     * Updates the backward compatible maximum elevation of the CardView.
     *
     *
     * Calling this method has no effect if device OS version is Lollipop or newer and
     * [.getUseCompatPadding] is `false`.
     *
     * @param maxElevation The backward compatible maximum elevation in pixels.
     * @attr ref androidx.cardview.R.styleable#CardView_cardMaxElevation
     * @see .setCardElevation
     * @see .getMaxCardElevation
     */
    fun setMaxCardElevation(maxElevation: Float) {
        IMPL.setMaxElevation(mCardViewDelegate, maxElevation)
    }

    /**
     * Returns the backward compatible maximum elevation of the CardView.
     *
     * @return Maximum elevation of the CardView
     * @see .setMaxCardElevation
     * @see .getCardElevation
     */
    fun getMaxCardElevation(): Float {
        return IMPL.getMaxElevation(mCardViewDelegate)
    }

    /**
     * Returns whether CardView should add extra padding to content to avoid overlaps with rounded
     * corners on pre-Lollipop platforms.
     *
     * @return True if CardView prevents overlaps with rounded corners on platforms before Lollipop.
     * Default value is `true`.
     */
    fun getPreventCornerOverlap(): Boolean {
        return mPreventCornerOverlap
    }

    /**
     * On pre-Lollipop platforms, CardView does not clip the bounds of the Card for the rounded
     * corners. Instead, it adds padding to content so that it won't overlap with the rounded
     * corners. You can disable this behavior by setting this field to `false`.
     *
     *
     * Setting this value on Lollipop and above does not have any effect unless you have enabled
     * compatibility padding.
     *
     * @param preventCornerOverlap Whether CardView should add extra padding to content to avoid
     * overlaps with the CardView corners.
     * @attr ref androidx.cardview.R.styleable#CardView_cardPreventCornerOverlap
     * @see .setUseCompatPadding
     */
    fun setPreventCornerOverlap(preventCornerOverlap: Boolean) {
        if (preventCornerOverlap != mPreventCornerOverlap) {
            mPreventCornerOverlap = preventCornerOverlap
            IMPL.onPreventCornerOverlapChanged(mCardViewDelegate)
        }
    }

    private val mCardViewDelegate = object : CardViewDelegate {
        private lateinit var mCardBackground: Drawable

        override fun setCardBackground(drawable: Drawable) {
            mCardBackground = drawable
            setBackgroundDrawable(drawable)
        }

        override fun getCardBackground(): Drawable = mCardBackground

        override fun getUseCompatPadding(): Boolean = this@ShadowCardView.getUseCompatPadding()

        override fun getPreventCornerOverlap(): Boolean = this@ShadowCardView.getPreventCornerOverlap()

        override fun setShadowPadding(left: Int, top: Int, right: Int, bottom: Int) {
            mShadowBounds[left, top, right] = bottom
            super@ShadowCardView.setPadding(
                left + mContentPadding.left, top + mContentPadding.top,
                right + mContentPadding.right, bottom + mContentPadding.bottom
            )
        }

        override fun setMinWidthHeightInternal(width: Int, height: Int) {
            if (width > mUserSetMinWidth) {
                super@ShadowCardView.setMinimumWidth(width)
            }
            if (height > mUserSetMinHeight) {
                super@ShadowCardView.setMinimumHeight(height)
            }
        }

        override fun getCardView(): View = this@ShadowCardView
    }
}