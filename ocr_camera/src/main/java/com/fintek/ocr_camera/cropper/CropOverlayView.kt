package com.fintek.ocr_camera.cropper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2018/6/24
 * Desc	        ${裁剪区域布局}
 */
internal class CropOverlayView : View {
    private var defaultMargin = 10
    private val minDistance = 100
    private val vertexSize = 30
    private val gridSize = 3
    private var bitmap: Bitmap? = null
    private var topLeft: Point? = null
    private var topRight: Point? = null
    private var bottomLeft: Point? = null
    private var bottomRight: Point? = null
    private var touchDownX = 0f
    private var touchDownY = 0f
    private var cropPosition: CropPosition? = null
    private var currentWidth = 0
    private var currentHeight = 0
    private var minX = 0
    private var maxX = 0
    private var minY = 0
    private var maxY = 0
    private val cropEnable = false

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun setBitmap(bitmap: Bitmap?) {
        this.bitmap = bitmap
        resetPoints()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width != currentWidth || height != currentHeight) {
            currentWidth = width
            currentHeight = height
            resetPoints()
        }
        Log.e("stk", "canvasSize=" + width + "x" + height)
        if (cropEnable) {
            drawBackground(canvas)
            drawVertex(canvas)
            drawEdge(canvas)
        }
        //        drawGrid(canvas);//裁剪框内部线条
    }

    private fun resetPoints() {
        Log.e("stk", "resetPoints, bitmap=$bitmap")

        // 1. calculate bitmap size in new canvas
        val scaleX = bitmap!!.width * 1.0f / width
        val scaleY = bitmap!!.height * 1.0f / height
        val maxScale = Math.max(scaleX, scaleY)

        // 2. determine minX , maxX if maxScale = scaleY | minY, maxY if maxScale = scaleX
        var minX = 0
        var maxX = width
        var minY = 0
        var maxY = height
        if (maxScale == scaleY) { // image very tall
            val bitmapInCanvasWidth = (bitmap!!.width / maxScale).toInt()
            minX = (width - bitmapInCanvasWidth) / 2
            maxX = width - minX
        } else { // image very wide
            val bitmapInCanvasHeight = (bitmap!!.height / maxScale).toInt()
            minY = (height - bitmapInCanvasHeight) / 2
            maxY = height - minY
        }
        this.minX = minX
        this.minY = minY
        this.maxX = maxX
        this.maxY = maxY
        defaultMargin =
            if (maxX - minX < defaultMargin || maxY - minY < defaultMargin) 0 // remove min
            else {
                if (cropEnable) {
                    30
                } else {
                    0
                }
            }
        Log.e("stk", "maxX - minX=" + (maxX - minX))
        Log.e("stk", "maxY - minY=" + (maxY - minY))
        topLeft = Point(minX + defaultMargin, minY + defaultMargin)
        topRight = Point(maxX - defaultMargin, minY + defaultMargin)
        bottomLeft = Point(minX + defaultMargin, maxY - defaultMargin)
        bottomRight = Point(maxX - defaultMargin, maxY - defaultMargin)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun drawBackground(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.parseColor("#66000000")
        paint.style = Paint.Style.FILL
        val path = Path()
        path.moveTo(topLeft!!.x.toFloat(), topLeft!!.y.toFloat())
        path.lineTo(topRight!!.x.toFloat(), topRight!!.y.toFloat())
        path.lineTo(bottomRight!!.x.toFloat(), bottomRight!!.y.toFloat())
        path.lineTo(bottomLeft!!.x.toFloat(), bottomLeft!!.y.toFloat())
        path.close()
        canvas.save()
        canvas.clipPath(path, Region.Op.DIFFERENCE)
        canvas.drawColor(Color.parseColor("#66000000"))
        canvas.restore()
    }

    private fun drawVertex(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        canvas.drawCircle(topLeft!!.x.toFloat(), topLeft!!.y.toFloat(), vertexSize.toFloat(), paint)
        canvas.drawCircle(
            topRight!!.x.toFloat(),
            topRight!!.y.toFloat(),
            vertexSize.toFloat(),
            paint
        )
        canvas.drawCircle(
            bottomLeft!!.x.toFloat(),
            bottomLeft!!.y.toFloat(),
            vertexSize.toFloat(),
            paint
        )
        canvas.drawCircle(
            bottomRight!!.x.toFloat(),
            bottomRight!!.y.toFloat(),
            vertexSize.toFloat(),
            paint
        )
        Log.e(
            "stk",
            "vertextPoints=" +
                    topLeft.toString() + " " + topRight.toString() + " " + bottomRight.toString() + " " + bottomLeft.toString()
        )
    }

    private fun drawEdge(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.strokeWidth = 3f
        paint.isAntiAlias = true
        canvas.drawLine(
            topLeft!!.x.toFloat(),
            topLeft!!.y.toFloat(),
            topRight!!.x.toFloat(),
            topRight!!.y.toFloat(),
            paint
        )
        canvas.drawLine(
            topLeft!!.x.toFloat(),
            topLeft!!.y.toFloat(),
            bottomLeft!!.x.toFloat(),
            bottomLeft!!.y.toFloat(),
            paint
        )
        canvas.drawLine(
            bottomRight!!.x.toFloat(),
            bottomRight!!.y.toFloat(),
            topRight!!.x.toFloat(),
            topRight!!.y.toFloat(),
            paint
        )
        canvas.drawLine(
            bottomRight!!.x.toFloat(),
            bottomRight!!.y.toFloat(),
            bottomLeft!!.x.toFloat(),
            bottomLeft!!.y.toFloat(),
            paint
        )
    }

    private fun drawGrid(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.strokeWidth = 2f
        paint.isAntiAlias = true
        for (i in 1..gridSize) {
            val topDistanceX = Math.abs(topLeft!!.x - topRight!!.x) / (gridSize + 1) * i
            val topDistanceY = Math.abs((topLeft!!.y - topRight!!.y) / (gridSize + 1) * i)
            val top = Point(
                if (topLeft!!.x < topRight!!.x) topLeft!!.x + topDistanceX else topLeft!!.x - topDistanceX,
                if (topLeft!!.y < topRight!!.y) topLeft!!.y + topDistanceY else topLeft!!.y - topDistanceY
            )
            val bottomDistanceX = Math.abs((bottomLeft!!.x - bottomRight!!.x) / (gridSize + 1) * i)
            val bottomDistanceY = Math.abs((bottomLeft!!.y - bottomRight!!.y) / (gridSize + 1) * i)
            val bottom = Point(
                if (bottomLeft!!.x < bottomRight!!.x) bottomLeft!!.x + bottomDistanceX else bottomLeft!!.x - bottomDistanceX,
                if (bottomLeft!!.y < bottomRight!!.y) bottomLeft!!.y + bottomDistanceY else bottomLeft!!.y - bottomDistanceY
            )
            canvas.drawLine(
                top.x.toFloat(),
                top.y.toFloat(),
                bottom.x.toFloat(),
                bottom.y.toFloat(),
                paint
            )
            val leftDistanceX = Math.abs((topLeft!!.x - bottomLeft!!.x) / (gridSize + 1) * i)
            val leftDistanceY = Math.abs((topLeft!!.y - bottomLeft!!.y) / (gridSize + 1) * i)
            val left = Point(
                if (topLeft!!.x < bottomLeft!!.x) topLeft!!.x + leftDistanceX else topLeft!!.x - leftDistanceX,
                if (topLeft!!.y < bottomLeft!!.y) topLeft!!.y + leftDistanceY else topLeft!!.y - leftDistanceY
            )
            val rightDistanceX = Math.abs((topRight!!.x - bottomRight!!.x) / (gridSize + 1) * i)
            val rightDistanceY = Math.abs((topRight!!.y - bottomRight!!.y) / (gridSize + 1) * i)
            val right = Point(
                if (topRight!!.x < bottomRight!!.x) topRight!!.x + rightDistanceX else topRight!!.x - rightDistanceX,
                if (topRight!!.y < bottomRight!!.y) topRight!!.y + rightDistanceY else topRight!!.y - rightDistanceY
            )
            canvas.drawLine(
                left.x.toFloat(),
                left.y.toFloat(),
                right.x.toFloat(),
                right.y.toFloat(),
                paint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!cropEnable) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_UP -> parent.requestDisallowInterceptTouchEvent(false)
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(false)
                onActionDown(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                onActionMove(event)
                return true
            }
        }
        return false
    }

    private fun onActionDown(event: MotionEvent) {
        touchDownX = event.x
        touchDownY = event.y
        val touchPoint = Point(
            event.x.toInt(), event.y.toInt()
        )
        var minDistance = distance(touchPoint, topLeft)
        cropPosition = CropPosition.TOP_LEFT
        if (minDistance > distance(touchPoint, topRight)) {
            minDistance = distance(touchPoint, topRight)
            cropPosition = CropPosition.TOP_RIGHT
        }
        if (minDistance > distance(touchPoint, bottomLeft)) {
            minDistance = distance(touchPoint, bottomLeft)
            cropPosition = CropPosition.BOTTOM_LEFT
        }
        if (minDistance > distance(touchPoint, bottomRight)) {
            minDistance = distance(touchPoint, bottomRight)
            cropPosition = CropPosition.BOTTOM_RIGHT
        }
    }

    private fun distance(src: Point, dst: Point?): Int {
        return Math.sqrt(
            Math.pow(
                src.x - dst!!.x.toDouble(),
                2.0
            ) + Math.pow(src.y - dst.y.toDouble(), 2.0)
        ).toInt()
    }

    private fun onActionMove(event: MotionEvent) {
        val deltaX = (event.x - touchDownX).toInt()
        val deltaY = (event.y - touchDownY).toInt()
        when (cropPosition) {
            CropPosition.TOP_LEFT -> {
                adjustTopLeft(deltaX, deltaY)
                invalidate()
            }
            CropPosition.TOP_RIGHT -> {
                adjustTopRight(deltaX, deltaY)
                invalidate()
            }
            CropPosition.BOTTOM_LEFT -> {
                adjustBottomLeft(deltaX, deltaY)
                invalidate()
            }
            CropPosition.BOTTOM_RIGHT -> {
                adjustBottomRight(deltaX, deltaY)
                invalidate()
            }
        }
        touchDownX = event.x
        touchDownY = event.y
    }

    private fun adjustTopLeft(deltaX: Int, deltaY: Int) {
        var newX = topLeft!!.x + deltaX
        if (newX < minX) newX = minX
        if (newX > maxX) newX = maxX
        var newY = topLeft!!.y + deltaY
        if (newY < minY) newY = minY
        if (newY > maxY) newY = maxY
        topLeft!![newX] = newY
    }

    private fun adjustTopRight(deltaX: Int, deltaY: Int) {
        var newX = topRight!!.x + deltaX
        if (newX > maxX) newX = maxX
        if (newX < minX) newX = minX
        var newY = topRight!!.y + deltaY
        if (newY < minY) newY = minY
        if (newY > maxY) newY = maxY
        topRight!![newX] = newY
    }

    private fun adjustBottomLeft(deltaX: Int, deltaY: Int) {
        var newX = bottomLeft!!.x + deltaX
        if (newX < minX) newX = minX
        if (newX > maxX) newX = maxX
        var newY = bottomLeft!!.y + deltaY
        if (newY > maxY) newY = maxY
        if (newY < minY) newY = minY
        bottomLeft!![newX] = newY
    }

    private fun adjustBottomRight(deltaX: Int, deltaY: Int) {
        var newX = bottomRight!!.x + deltaX
        if (newX > maxX) newX = maxX
        if (newX < minX) newX = minX
        var newY = bottomRight!!.y + deltaY
        if (newY > maxY) newY = maxY
        if (newY < minY) newY = minY
        bottomRight!![newX] = newY
    }

    fun crop(cropListener: CropListener, needStretch: Boolean) {
        if (topLeft == null) return

        // calculate bitmap size in new canvas
        val scaleX = bitmap!!.width * 1.0f / width
        val scaleY = bitmap!!.height * 1.0f / height
        val maxScale = Math.max(scaleX, scaleY)

        // re-calculate coordinate in original bitmap
        Log.e("stk", "maxScale=$maxScale")
        val bitmapTopLeft = Point(
            ((topLeft!!.x - minX) * maxScale).toInt(),
            ((topLeft!!.y - minY) * maxScale).toInt()
        )
        val bitmapTopRight = Point(
            ((topRight!!.x - minX) * maxScale).toInt(),
            ((topRight!!.y - minY) * maxScale).toInt()
        )
        val bitmapBottomLeft = Point(
            ((bottomLeft!!.x - minX) * maxScale).toInt(),
            ((bottomLeft!!.y - minY) * maxScale).toInt()
        )
        val bitmapBottomRight = Point(
            ((bottomRight!!.x - minX) * maxScale).toInt(),
            ((bottomRight!!.y - minY) * maxScale).toInt()
        )
        Log.e(
            "stk", "bitmapPoints="
                    + bitmapTopLeft.toString() + " "
                    + bitmapTopRight.toString() + " "
                    + bitmapBottomRight.toString() + " "
                    + bitmapBottomLeft.toString() + " "
        )
        val output =
            Bitmap.createBitmap(bitmap!!.width + 1, bitmap!!.height + 1, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        // 1. draw path
        val path = Path()
        path.moveTo(bitmapTopLeft.x.toFloat(), bitmapTopLeft.y.toFloat())
        path.lineTo(bitmapTopRight.x.toFloat(), bitmapTopRight.y.toFloat())
        path.lineTo(bitmapBottomRight.x.toFloat(), bitmapBottomRight.y.toFloat())
        path.lineTo(bitmapBottomLeft.x.toFloat(), bitmapBottomLeft.y.toFloat())
        path.close()
        canvas.drawPath(path, paint)

        // 2. draw original bitmap
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap!!, 0f, 0f, paint)

        // 3. cut
        val cropRect = Rect(
            Math.min(bitmapTopLeft.x, bitmapBottomLeft.x),
            Math.min(bitmapTopLeft.y, bitmapTopRight.y),
            Math.max(bitmapBottomRight.x, bitmapTopRight.x),
            Math.max(bitmapBottomRight.y, bitmapBottomLeft.y)
        )
        if (cropRect.width() <= 0 || cropRect.height() <= 0) { //用户裁剪的宽或高为0
            cropListener.onFinish(null)
            return
        }
        val cut = Bitmap.createBitmap(
            output,
            cropRect.left,
            cropRect.top,
            cropRect.width(),
            cropRect.height()
        )
        if (!needStretch) {
            cropListener.onFinish(cut)
        } else {
            // 4. re-calculate coordinate in cropRect
            val cutTopLeft = Point()
            val cutTopRight = Point()
            val cutBottomLeft = Point()
            val cutBottomRight = Point()
            cutTopLeft.x =
                if (bitmapTopLeft.x > bitmapBottomLeft.x) bitmapTopLeft.x - bitmapBottomLeft.x else 0
            cutTopLeft.y =
                if (bitmapTopLeft.y > bitmapTopRight.y) bitmapTopLeft.y - bitmapTopRight.y else 0
            cutTopRight.x =
                if (bitmapTopRight.x > bitmapBottomRight.x) cropRect.width() else cropRect.width() - Math.abs(
                    bitmapBottomRight.x - bitmapTopRight.x
                )
            cutTopRight.y =
                if (bitmapTopLeft.y > bitmapTopRight.y) 0 else Math.abs(bitmapTopLeft.y - bitmapTopRight.y)
            cutBottomLeft.x =
                if (bitmapTopLeft.x > bitmapBottomLeft.x) 0 else Math.abs(bitmapTopLeft.x - bitmapBottomLeft.x)
            cutBottomLeft.y =
                if (bitmapBottomLeft.y > bitmapBottomRight.y) cropRect.height() else cropRect.height() - Math.abs(
                    bitmapBottomRight.y - bitmapBottomLeft.y
                )
            cutBottomRight.x =
                if (bitmapTopRight.x > bitmapBottomRight.x) cropRect.width() - Math.abs(
                    bitmapBottomRight.x - bitmapTopRight.x
                ) else cropRect.width()
            cutBottomRight.y =
                if (bitmapBottomLeft.y > bitmapBottomRight.y) cropRect.height() - Math.abs(
                    bitmapBottomRight.y - bitmapBottomLeft.y
                ) else cropRect.height()
            Log.e("stk", cut.width.toString() + "x" + cut.height)
            Log.e(
                "stk", "cutPoints="
                        + cutTopLeft.toString() + " "
                        + cutTopRight.toString() + " "
                        + cutBottomRight.toString() + " "
                        + cutBottomLeft.toString() + " "
            )
            val width = cut.width.toFloat()
            val height = cut.height.toFloat()
            val src = floatArrayOf(
                cutTopLeft.x.toFloat(),
                cutTopLeft.y.toFloat(),
                cutTopRight.x.toFloat(),
                cutTopRight.y.toFloat(),
                cutBottomRight.x.toFloat(),
                cutBottomRight.y.toFloat(),
                cutBottomLeft.x.toFloat(),
                cutBottomLeft.y.toFloat()
            )
            val dst = floatArrayOf(0f, 0f, width, 0f, width, height, 0f, height)
            val matrix = Matrix()
            matrix.setPolyToPoly(src, 0, dst, 0, 4)
            val stretch = Bitmap.createBitmap(cut.width, cut.height, Bitmap.Config.ARGB_8888)
            val stretchCanvas = Canvas(stretch)
            //            stretchCanvas.drawBitmap(cut, matrix, null);
            stretchCanvas.concat(matrix)
            stretchCanvas.drawBitmapMesh(
                cut,
                WIDTH_BLOCK,
                HEIGHT_BLOCK,
                generateVertices(cut.width, cut.height),
                0,
                null,
                0,
                null
            )
            cropListener.onFinish(stretch)
        }
    }

    private val WIDTH_BLOCK = 40
    private val HEIGHT_BLOCK = 40
    private fun generateVertices(widthBitmap: Int, heightBitmap: Int): FloatArray {
        val vertices = FloatArray((WIDTH_BLOCK + 1) * (HEIGHT_BLOCK + 1) * 2)
        val widthBlock = widthBitmap.toFloat() / WIDTH_BLOCK
        val heightBlock = heightBitmap.toFloat() / HEIGHT_BLOCK
        for (i in 0..HEIGHT_BLOCK) for (j in 0..WIDTH_BLOCK) {
            vertices[i * ((HEIGHT_BLOCK + 1) * 2) + j * 2] = j * widthBlock
            vertices[i * ((HEIGHT_BLOCK + 1) * 2) + j * 2 + 1] = i * heightBlock
        }
        return vertices
    }
}