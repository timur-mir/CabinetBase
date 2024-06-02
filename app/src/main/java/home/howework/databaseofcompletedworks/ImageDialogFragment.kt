package home.howework.databaseofcompletedworks
import androidx.fragment.app.DialogFragment
import android.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import android.graphics.PointF

import android.graphics.Matrix;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.R.attr.spacing
import androidx.core.view.doOnLayout


class ImageDialogFragment : DialogFragment(),OnTouchListener {
    private lateinit var exitButton: ImageButton
    private lateinit var imView: ImageView
    private val TAG = "Touch"

    // These matrices will be used to move and zoom image
    var matrix: Matrix = Matrix()
    var savedMatrix: Matrix = Matrix()

    // We can be in one of these 3 states
    val NONE = 0
    val DRAG = 1
    val ZOOM = 2
    var mode = NONE

    // Remember some things for zooming
    var start = PointF()
    var mid = PointF()
    var oldDist = 1f
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView: View = inflater.inflate(R.layout.dialog_image_fragment, container, false)
        exitButton = dialogView.findViewById(R.id.exit) as ImageButton
        imView = dialogView.findViewById(R.id.image_photo) as ImageView
        imView.setScaleType(ImageView.ScaleType.FIT_CENTER)
        imView.setOnTouchListener(this)
        val imagePath: String? = arguments?.getString("path")
        if(imagePath==null){}
        else {
            imView.doOnLayout {measuredView ->
                val bitmap = getScaledBitmap(imagePath,measuredView.width,measuredView.height)
                imView.setImageBitmap(bitmap)

            }

        }
        exitButton.setOnClickListener {
            dismiss()
        }
        return dialogView
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val view = v as ImageView
        // make the image scalable as a matrix
        // make the image scalable as a matrix
        view.scaleType = ImageView.ScaleType.MATRIX
        val scale: kotlin.Float
        val action=event?.actionMasked
        // Handle touch events here...
        when ( action) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start[event.getX()] = event.getY()
                android.util.Log.d(TAG, "mode=DRAG")
                mode = DRAG
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                android.util.Log.d(TAG, "mode=NONE")
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist =
                    spacing(event) // calculates the distance between two points where user touched.
                android.util.Log.d(TAG, "oldDist=$oldDist")
                // minimal distance between both the fingers
                if (oldDist > 5f) {
                    savedMatrix.set(matrix)
                    midPoint(
                        mid,
                        event
                    ) // sets the mid-point of the straight line between two points where user touched.
                    mode = ZOOM
                    android.util.Log.d(TAG, "mode=ZOOM")
                }
            }
            MotionEvent.ACTION_MOVE -> if (mode === DRAG) { //movement of first finger
                matrix.set(savedMatrix)
                if (view.left >= -392) {
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y)
                }
            } else if (mode === ZOOM) { //pinch zooming
                val newDist: kotlin.Float = spacing(event)
                android.util.Log.d(TAG, "newDist=$newDist")
                if (newDist > 5f) {
                    matrix.set(savedMatrix)
                    scale = newDist / oldDist // XXX may need to play with this value to limit it
                    matrix.postScale(scale, scale, mid.x, mid.y)
                }
            }
        }

        // Perform the transformation

        // Perform the transformation
        view.imageMatrix = matrix

        return true // indicate event was handled

    }
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2,y / 2)
    }



}