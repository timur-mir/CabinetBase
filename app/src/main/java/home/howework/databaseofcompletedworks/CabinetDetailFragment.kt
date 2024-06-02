package home.howework.databaseofcompletedworks

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import home.howework.databaseofcompletedworks.MainActivity.CallMenu.cabinetCount
import home.howework.databaseofcompletedworks.MainActivity.CallMenu.orientationChange
import home.howework.databaseofcompletedworks.databinding.FragmentCabinetBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import kotlin.random.Random

private const val DATE_FORMAT = "dd MM yyyy HH:mm:ss"
private const val REQUEST_PHOTO = 2
class CabinetDetailFragment : Fragment() {
    private var _binding: FragmentCabinetBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private lateinit var cabinetForSave: Cabinet
    private lateinit var photoFile: File
    private lateinit var photoFile2: File
    private lateinit var photoFile3: File
    private lateinit var photoFile4: File
    private var photoUriAny: Uri? = null
    private var photoUri: Uri? = null
    private var photoUri2: Uri? = null
    private var photoUri3: Uri? = null
    private var photoUri4: Uri? = null
    private val workerRepository = SharedRepo()
    private val args:CabinetDetailFragmentArgs by navArgs()
    private val cabinetDetailViewModel: CabinetDetailViewModel by viewModels {
        CabinetDetailViewModelFactory(args.cabinetId)
    }
//    private val selectSuspect = registerForActivityResult(
//        ActivityResultContracts.PickContact()
//    ) { uri: Uri? ->
//        uri?.let { parseContactSelection(it) }
//    }
    private var photoName: String? = null
    private var photoName2: String? = null
    private var photoName3: String? = null
    private var photoName4: String? = null
    var imageDelFlag1 = false
    var imageDelFlag2 = false
    var imageDelFlag3 = false
    var imageDelFlag4 = false
    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName != null) {
            cabinetDetailViewModel.updateCabinet { oldCabinet ->
                oldCabinet.copy(photoFileName = photoName)
            }
            if (photoFile.exists()) {
                updatePhotoViewWhenFotoCapture(1)
            }
        }
    }
    private val takePhoto2 = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName2 != null) {
            cabinetDetailViewModel.updateCabinet { oldCabinet ->
                oldCabinet.copy(photoFileName2 = photoName2)
            }
            if (photoFile2.exists()) {
                updatePhotoViewWhenFotoCapture(2)
            }

        }
    }
    private val takePhoto3 = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName3 != null) {
            cabinetDetailViewModel.updateCabinet { oldCabinet ->
                oldCabinet.copy(photoFileName3 = photoName3)
            }
            if (photoFile3.exists()) {
                updatePhotoViewWhenFotoCapture(3)
            }
        }
    }
    private val takePhoto4 = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto && photoName4 != null) {
            cabinetDetailViewModel.updateCabinet { oldCabinet ->
                oldCabinet.copy(photoFileName4 = photoName4)
            }
            if (photoFile4.exists()) {
                updatePhotoViewWhenFotoCapture(4)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cabinetForSave=Cabinet()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentCabinetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
//            cabinetTitle.doOnTextChanged { text, _, _, _ ->
//                cabinetDetailViewModel.updateCrime { oldCrime ->
//                    oldCrime.copy(title = text.toString())
//                }
//            }
//
//            }

            cabinetSuspect.setOnClickListener {
                // selectSuspect.launch(null)
            }
//
//            val selectSuspectIntent = selectSuspect.contract.createIntent(
//                requireContext(),
//                null
//            )
//            cabinetSuspect.isEnabled = canResolveIntent(selectSuspectIntent)

            cabinetCamera.setOnClickListener {


                if (!(::photoFile.isInitialized)) {
                    photoName = "IMG_${(0..100).random()}_${(0..100).random()}.JPG"
                    //  cabinetForSave.photoFileName=photoName
                    cabinetDetailViewModel.updateCabinet { oldCabinet ->
                        oldCabinet.copy(photoFileName = photoName)
                    }
                    photoFile = File(
                        requireContext().applicationContext.filesDir,
                        photoName
                    )
                    photoUri = FileProvider.getUriForFile(
                        requireContext(),
                        "home.howework.databaseofcompletedworks.fileprovider",
                        photoFile
                    )
                    takePhoto.launch(photoUri)

                } else
                    if ((::photoFile.isInitialized) && imageDelFlag1||cabinetPhoto.drawable.intrinsicHeight<80) {
                        photoName = "IMG_${(0..100).random()}_${(0..100).random()}.JPG"
                        //  cabinetForSave.photoFileName=photoName
                        cabinetDetailViewModel.updateCabinet { oldCabinet ->
                            oldCabinet.copy(photoFileName = photoName)
                        }
                        photoFile = File(
                            requireContext().applicationContext.filesDir,
                            photoName
                        )
                        photoUri = FileProvider.getUriForFile(
                            requireContext(),
                            "home.howework.databaseofcompletedworks.fileprovider",
                            photoFile
                        )
                        imageDelFlag1 = false
                        takePhoto.launch(photoUri)
                    }


                    //////////////////////////////////////////////////
                    else if (!(::photoFile2.isInitialized)) {
                        photoName2 = "IMG_${(0..100).random()}_${(0..100).random()}.JPG"
                        // cabinetForSave.photoFileName2=photoName2
                        cabinetDetailViewModel.updateCabinet { oldCabinet ->
                            oldCabinet.copy(photoFileName2 = photoName2)
                        }
                        photoFile2 = File(
                            requireContext().applicationContext.filesDir,
                            photoName2
                        )
                        photoUri2 = FileProvider.getUriForFile(
                            requireContext(),
                            "home.howework.databaseofcompletedworks.fileprovider",
                            photoFile2
                        )
                        takePhoto2.launch(photoUri2)
                    } else if ((::photoFile2.isInitialized) && imageDelFlag2||cabinetPhoto2.drawable.intrinsicHeight<80) {
                        photoName2 = "IMG_${(0..100).random()}_${(0..100).random()}.JPG"
                        // cabinetForSave.photoFileName2=photoName2
                        cabinetDetailViewModel.updateCabinet { oldCabinet ->
                            oldCabinet.copy(photoFileName2 = photoName2)
                        }
                        photoFile2 = File(
                            requireContext().applicationContext.filesDir,
                            photoName2
                        )
                        photoUri2 = FileProvider.getUriForFile(
                            requireContext(),
                            "home.howework.databaseofcompletedworks.fileprovider",
                            photoFile2
                        )
                        imageDelFlag2 = false
                        takePhoto2.launch(photoUri2)
                    }
////////////////////////////////////////////
                    else if (!(::photoFile3.isInitialized)) {
                        photoName3 = "IMG_${(0..100).random()}_${(0..100).random()}.JPG"
                        // cabinetForSave.photoFileName3=photoName3
                        cabinetDetailViewModel.updateCabinet { oldCabinet ->
                            oldCabinet.copy(photoFileName3 = photoName3)
                        }
                        photoFile3 = File(
                            requireContext().applicationContext.filesDir,
                            photoName3
                        )
                        photoUri3 = FileProvider.getUriForFile(
                            requireContext(),
                            "home.howework.databaseofcompletedworks.fileprovider",
                            photoFile3
                        )
                        takePhoto3.launch(photoUri3)
                    } else if ((::photoFile3.isInitialized) && imageDelFlag3||cabinetPhoto3.drawable.intrinsicHeight<80) {
                        photoName3 = "IMG_${(0..100).random()}_${(0..100).random()}.JPG"
                        // cabinetForSave.photoFileName3=photoName3
                        cabinetDetailViewModel.updateCabinet { oldCabinet ->
                            oldCabinet.copy(photoFileName3 = photoName3)
                        }
                        photoFile3 = File(
                            requireContext().applicationContext.filesDir,
                            photoName3
                        )
                        photoUri3 = FileProvider.getUriForFile(
                            requireContext(),
                            "home.howework.databaseofcompletedworks.fileprovider",
                            photoFile3
                        )
                        imageDelFlag3 = false
                        takePhoto3.launch(photoUri3)
                    } else if (!(::photoFile4.isInitialized)) {
                        photoName4 = "IMG_${(0..100).random()}_${(0..100).random()}.JPG"
                        //  cabinetForSave.photoFileName4=photoName4
                        cabinetDetailViewModel.updateCabinet { oldCabinet ->
                            oldCabinet.copy(photoFileName4 = photoName4)
                        }
                        photoFile4 = File(
                            requireContext().applicationContext.filesDir,
                            photoName4
                        )
                        photoUri4 = FileProvider.getUriForFile(
                            requireContext(),
                            "home.howework.databaseofcompletedworks.fileprovider",
                            photoFile4
                        )
                        takePhoto4.launch(photoUri4)
                    } else if ((::photoFile4.isInitialized) && imageDelFlag4||cabinetPhoto4.drawable.intrinsicHeight<80) {
                        photoName4 = "IMG_${(0..100).random()}_${(0..100).random()}.JPG"
                        //  cabinetForSave.photoFileName4=photoName4
                        cabinetDetailViewModel.updateCabinet { oldCabinet ->
                            oldCabinet.copy(photoFileName4 = photoName4)
                        }
                        photoFile4 = File(
                            requireContext().applicationContext.filesDir,
                            photoName4
                        )
                        photoUri4 = FileProvider.getUriForFile(
                            requireContext(),
                            "home.howework.databaseofcompletedworks.fileprovider",
                            photoFile4
                        )
                        imageDelFlag4 = false
                        takePhoto4.launch(photoUri4)
                    }
            }


//            val captureImageIntent = takePhoto.contract.createIntent(
//                requireContext(),
//                null
//            )
//            cabinetCamera.isEnabled = canResolveIntent(captureImageIntent)


            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    cabinetDetailViewModel.cabinet.collect { cabinet ->
                        if (cabinet != null) {
                            if (cabinet.title == "" && MainActivity.CallMenu.newCabinetFlag) {
                                cabinet.title =
                                    "Кабинет ${MainActivity.CallMenu.cabinetCount.toString()}"
                                MainActivity.CallMenu.newCabinetFlag = false
                                // cabinetForSave=cabinet
                            } else
                                if (cabinet.title != "" && !MainActivity.CallMenu.newCabinetFlag) {
                                }
                        }
                        cabinet?.let { updateUi(it) }
                    }
                }
            }

            setFragmentResultListener(
                DatePickerFragment.REQUEST_KEY_DATE
            ) { _, bundle ->
                val newDate =
                    bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
                //  cabinetDetailViewModel.updateCabinet { it.copy(date = newDate) }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
//        cabinetDetailViewModel.updateCabinet { oldCabinet ->
//              oldCabinet.copy(title = "Кабинет ${cabinetCount}")
//            }
    }


    fun updatePhotoViewWhenFotoCapture(numberFoto: Int) {
        var fos: FileOutputStream? = null
        val options = BitmapFactory.Options()
        lateinit var bitmap: Bitmap
        lateinit var bitmap2: Bitmap
        lateinit var bitmap3: Bitmap
        lateinit var bitmap4: Bitmap
        if (numberFoto == 1) {
            if (photoFile.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                    val source = ImageDecoder.createSource(
                        photoFile
                    )

                    bitmap = ImageDecoder.decodeBitmap(source)
                  if(bitmap.density!=0) {
                      binding.cabinetPhoto.setImageBitmap(bitmap)
                  }
                } else {
                    binding.cabinetPhoto.doOnLayout { measuredView ->
                        bitmap = getScaledBitmap(photoFile.path, measuredView.width,measuredView.height)
                        if(bitmap.density!=0) {
                            binding.cabinetPhoto.setImageBitmap(bitmap)
                        }
                    }
                }

                val cabNumberText = "Кабинет ${cabinetCount}"
                val nameWorker = "${workerRepository.getName(requireActivity())}"
//            val dateMontaj="${DateFormat.format(DATE_FORMAT, crime.date).toString()}"
                val brackProduct = "Брак 0"
                fos = FileOutputStream(photoFile.path)
                //options.inMutable = true

                val bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)

                val canvas = Canvas(bitmapCopy)
                val paint = Paint()
                paint.setColor(Color.WHITE)
                val height = bitmapCopy.height
                val width = bitmapCopy.width
                orientationChange = width > height

                if (!orientationChange) {
                    if (context?.resources?.displayMetrics?.densityDpi == DisplayMetrics.DENSITY_HIGH||
                        context?.resources?.displayMetrics?.densityDpi== DisplayMetrics.DENSITY_MEDIUM||
                        context?.resources?.displayMetrics?.densityDpi== DisplayMetrics.DENSITY_LOW||
                        context?.resources?.displayMetrics?.densityDpi==  DisplayMetrics.DENSITY_220||
                        context?.resources?.displayMetrics?.densityDpi==  DisplayMetrics.DENSITY_200||
                        context?.resources?.displayMetrics?.densityDpi==  DisplayMetrics.DENSITY_180||
                        context?.resources?.displayMetrics?.densityDpi==  DisplayMetrics.DENSITY_220||
                        context?.resources?.displayMetrics?.densityDpi==  DisplayMetrics.DENSITY_140

                    ) {
                        paint.textSize = convertToPixels28(requireActivity(), 20)
//            paint.setStrokeWidth(12f)

                        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OVER))

                        canvas.drawBitmap(bitmapCopy, 0f, 0f, paint)
                        canvas.drawRect(
                            convertToPixels28(requireActivity(), 0),
                            convertToPixels28(requireActivity(), 464),
                            convertToPixels28(requireActivity(), 430),
                            convertToPixels28(requireActivity(), 600),
                            paint
                        );
                        paint.setColor(Color.BLACK)
                        canvas.drawText(
                            cabNumberText, convertToPixels28(requireActivity(), 36),
                            convertToPixels28(requireActivity(), 480), paint
                        );

                        canvas.drawText(
                            nameWorker, convertToPixels28(requireActivity(), 36),
                            convertToPixels28(requireActivity(), 500), paint
                        );

                        canvas.drawText(
                            "${DateFormat.format(DATE_FORMAT, Date()).toString()}",
                            convertToPixels28(requireActivity(), 36),
                            convertToPixels(requireActivity(), 520),
                            paint
                        );

                        canvas.drawText(
                            brackProduct, convertToPixels28(requireActivity(), 36),
                            convertToPixels28(requireActivity(), 540), paint
                        );
                    }
                    else {
                        paint.textSize = convertToPixels(requireActivity(), 20)
//            paint.setStrokeWidth(12f)

                        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OVER))

                        canvas.drawBitmap(bitmapCopy, 0f, 0f, paint)
                        canvas.drawRect(
                            convertToPixels(requireActivity(), 0),
                            convertToPixels(requireActivity(), 464),
                            convertToPixels(requireActivity(), 430),
                            convertToPixels(requireActivity(), 600),
                            paint
                        );
                        paint.setColor(Color.BLACK)
                        canvas.drawText(
                            cabNumberText, convertToPixels(requireActivity(), 36),
                            convertToPixels(requireActivity(), 480), paint
                        );

                        canvas.drawText(
                            nameWorker, convertToPixels(requireActivity(), 36),
                            convertToPixels(requireActivity(), 500), paint
                        );

                        canvas.drawText(
                            "${DateFormat.format(DATE_FORMAT, Date()).toString()}",
                            convertToPixels(requireActivity(), 36),
                            convertToPixels(requireActivity(), 520),
                            paint
                        );

                        canvas.drawText(
                            brackProduct, convertToPixels(requireActivity(), 36),
                            convertToPixels(requireActivity(), 540), paint
                        );
                    }
                } else {
                    val dateMontajSlice =
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}".substring(0, 10)
                    val timeMontajSlice =
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}".substring(11, 19)
                    if (context?.resources?.displayMetrics?.densityDpi == DisplayMetrics.DENSITY_HIGH||
                        context?.resources?.displayMetrics?.densityDpi== DisplayMetrics.DENSITY_MEDIUM||
                        context?.resources?.displayMetrics?.densityDpi== DisplayMetrics.DENSITY_LOW) {

                        paint.textSize = convertToPixels28(requireActivity(), 11)
//            paint.setStrokeWidth(12f)

                        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OVER))

                        canvas.drawBitmap(bitmapCopy, 0f, 0f, paint)
                        canvas.drawRect(
                            convertToPixels28(requireActivity(), 0),
                            convertToPixels28(requireActivity(), 0),
                            convertToPixels28(requireActivity(), 62),
                            convertToPixels28(requireActivity(), 420),
                            paint
                        );
                        paint.setColor(Color.BLACK)
                        canvas.drawText(
                            cabNumberText, convertToPixels28(requireActivity(), 1),
                            convertToPixels28(requireActivity(), 80), paint
                        );

                        canvas.drawText(
                            nameWorker, convertToPixels28(requireActivity(), 1),
                            convertToPixels28(requireActivity(), 100), paint
                        );

                        canvas.drawText(
                            dateMontajSlice, convertToPixels28(requireActivity(), 1),
                            convertToPixels28(requireActivity(), 120), paint
                        );
                        canvas.drawText(
                            timeMontajSlice, convertToPixels28(requireActivity(), 1),
                            convertToPixels28(requireActivity(), 140), paint
                        );

                        canvas.drawText(
                            brackProduct, convertToPixels28(requireActivity(), 1),
                            convertToPixels28(requireActivity(), 160), paint
                        );


                    }
                    else{
                        paint.textSize = convertToPixels(requireActivity(), 11)
//            paint.setStrokeWidth(12f)

                        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OVER))

                        canvas.drawBitmap(bitmapCopy, 0f, 0f, paint)
                        canvas.drawRect(
                            convertToPixels(requireActivity(), 0),
                            convertToPixels(requireActivity(), 0),
                            convertToPixels(requireActivity(), 62),
                            convertToPixels(requireActivity(), 420),
                            paint
                        );
                        paint.setColor(Color.BLACK)
                        canvas.drawText(
                            cabNumberText, convertToPixels(requireActivity(), 1),
                            convertToPixels(requireActivity(), 80), paint
                        );

                        canvas.drawText(
                            nameWorker, convertToPixels(requireActivity(), 1),
                            convertToPixels(requireActivity(), 100), paint
                        );

                        canvas.drawText(
                            dateMontajSlice, convertToPixels(requireActivity(), 1),
                            convertToPixels(requireActivity(), 120), paint
                        );
                        canvas.drawText(
                            timeMontajSlice, convertToPixels(requireActivity(), 1),
                            convertToPixels(requireActivity(), 140), paint
                        );

                        canvas.drawText(
                            brackProduct, convertToPixels(requireActivity(), 1),
                            convertToPixels(requireActivity(), 160), paint
                        );

                    }




                }

                bitmapCopy.compress(Bitmap.CompressFormat.JPEG, 100, fos)

                if (fos != null) {

                    fos!!.flush()
                    fos!!.close()
                }


            } else {
               // binding.cabinetPhoto.setImageDrawable(null)
            }
        }
        if (numberFoto == 2) {
            if (photoFile2.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                    val source = ImageDecoder.createSource(
                        photoFile2
                    )
                    bitmap2 = ImageDecoder.decodeBitmap(source)
                    if(bitmap2.density!=0) {
                        binding.cabinetPhoto2.setImageBitmap(bitmap2)
                    }
                } else {
                    binding.cabinetPhoto2.doOnLayout { measuredView ->
                        bitmap = getScaledBitmap(photoFile.path, measuredView.width,measuredView.height)
                        if(bitmap2.density!=0) {
                            binding.cabinetPhoto2.setImageBitmap(bitmap2)
                        }
                    }

                }
                val cabNumberText = "Кабинет ${cabinetCount}"
                val nameWorker = "${workerRepository.getName(requireActivity())}"
//            val dateMontaj="${DateFormat.format(DATE_FORMAT, Date().toString()}"
                val brackProduct = "Брак 1"
                fos = FileOutputStream(photoFile2.path)
                // options.inMutable = true
                val bitmapCopy = bitmap2.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(bitmapCopy)
                val paint = Paint()
                paint.setColor(Color.WHITE)
//                val exif = ExifInterface(photoFile2.getAbsolutePath())
//                val orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL
//                )
                val height = bitmapCopy.height
                val width = bitmapCopy.width
                orientationChange = width > height
                if (!orientationChange) {
                    paint.textSize = convertToPixels(requireActivity(), 20)
//            paint.setStrokeWidth(12f)

                    paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OVER))

                    canvas.drawBitmap(bitmapCopy, 0f, 0f, paint)
                    canvas.drawRect(
                        convertToPixels(requireActivity(), 0),
                        convertToPixels(requireActivity(), 464),
                        convertToPixels(requireActivity(), 430),
                        convertToPixels(requireActivity(), 600),
                        paint
                    );
                    paint.setColor(Color.BLACK)
                    canvas.drawText(
                        cabNumberText, convertToPixels(requireActivity(), 36),
                        convertToPixels(requireActivity(), 480), paint
                    );

                    canvas.drawText(
                        nameWorker, convertToPixels(requireActivity(), 36),
                        convertToPixels(requireActivity(), 500), paint
                    );

                    canvas.drawText(
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}",
                        convertToPixels(requireActivity(), 36),
                        convertToPixels(requireActivity(), 520),
                        paint
                    );

                    canvas.drawText(
                        brackProduct, convertToPixels(requireActivity(), 36),
                        convertToPixels(requireActivity(), 540), paint
                    );

                } else {
                    val dateMontajSlice =
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}".substring(0, 10)
                    val timeMontajSlice =
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}".substring(11, 19)
                    paint.textSize = convertToPixels(requireActivity(), 11)
//            paint.setStrokeWidth(12f)

                    paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OVER))

                    canvas.drawBitmap(bitmapCopy, 0f, 0f, paint)
                    canvas.drawRect(
                        convertToPixels(requireActivity(), 0),
                        convertToPixels(requireActivity(), 0),
                        convertToPixels(requireActivity(), 62),
                        convertToPixels(requireActivity(), 420),
                        paint
                    );
                    paint.setColor(Color.BLACK)
                    canvas.drawText(
                        cabNumberText, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 80), paint
                    );

                    canvas.drawText(
                        nameWorker, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 100), paint
                    );

                    canvas.drawText(
                        dateMontajSlice, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 120), paint
                    );
                    canvas.drawText(
                        timeMontajSlice, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 140), paint
                    );

                    canvas.drawText(
                        brackProduct, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 160), paint
                    );


                }

                bitmapCopy.compress(Bitmap.CompressFormat.JPEG, 100, fos)

                if (fos != null) {

                    fos!!.flush()
                    fos!!.close()
                }


            } else {
              //binding.cabinetPhoto2.setImageDrawable(null)
            }
        }
        if (numberFoto == 3) {
            if (photoFile3.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                    val source = ImageDecoder.createSource(
                        photoFile3
                    )
                    bitmap3 = ImageDecoder.decodeBitmap(source)
                    if(bitmap3.density!=0) {
                        binding.cabinetPhoto3.setImageBitmap(bitmap3)
                    }
                } else {
                    binding.cabinetPhoto3.doOnLayout { measuredView ->
                        bitmap = getScaledBitmap(photoFile.path, measuredView.width,measuredView.height)
                        if(bitmap3.density!=0) {
                            binding.cabinetPhoto3.setImageBitmap(bitmap3)
                        }
                    }

                }
                val cabNumberText = "Кабинет ${cabinetCount}"
                val nameWorker = "${workerRepository.getName(requireActivity())}"
//            val dateMontaj="${DateFormat.format(DATE_FORMAT, Date().toString()}"
                val brackProduct = "Брак 2"
                fos = FileOutputStream(photoFile3.path)
                // options.inMutable = true

                val bitmapCopy = bitmap3.copy(Bitmap.Config.ARGB_8888, true)

                val canvas = Canvas(bitmapCopy)
                val paint = Paint()
                paint.setColor(Color.WHITE)
//                val exif = ExifInterface(photoFile3.getAbsolutePath())
//                val orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL
//                )
                val height = bitmapCopy.height
                val width = bitmapCopy.width
                orientationChange = width > height
                if (!orientationChange) {
                    paint.textSize = convertToPixels(requireActivity(), 20)
//            paint.setStrokeWidth(12f)

                    paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OVER))

                    canvas.drawBitmap(bitmapCopy, 0f, 0f, paint)
                    canvas.drawRect(
                        convertToPixels(requireActivity(), 0),
                        convertToPixels(requireActivity(), 464),
                        convertToPixels(requireActivity(), 430),
                        convertToPixels(requireActivity(), 600),
                        paint
                    );
                    paint.setColor(Color.BLACK)
                    canvas.drawText(
                        cabNumberText, convertToPixels(requireActivity(), 36),
                        convertToPixels(requireActivity(), 480), paint
                    );

                    canvas.drawText(
                        nameWorker, convertToPixels(requireActivity(), 36),
                        convertToPixels(requireActivity(), 500), paint
                    );

                    canvas.drawText(
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}",
                        convertToPixels(requireActivity(), 36),
                        convertToPixels(requireActivity(), 520),
                        paint
                    );

                    canvas.drawText(
                        brackProduct, convertToPixels(requireActivity(), 36),
                        convertToPixels(requireActivity(), 540), paint
                    );

                } else {
                    val dateMontajSlice =
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}".substring(0, 10)
                    val timeMontajSlice =
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}".substring(11, 19)
                    paint.textSize = convertToPixels(requireActivity(), 11)
//            paint.setStrokeWidth(12f)

                    paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OVER))

                    canvas.drawBitmap(bitmapCopy, 0f, 0f, paint)
                    canvas.drawRect(
                        convertToPixels(requireActivity(), 0),
                        convertToPixels(requireActivity(), 0),
                        convertToPixels(requireActivity(), 62),
                        convertToPixels(requireActivity(), 420),
                        paint
                    );
                    paint.setColor(Color.BLACK)
                    canvas.drawText(
                        cabNumberText, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 80), paint
                    );

                    canvas.drawText(
                        nameWorker, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 100), paint
                    );

                    canvas.drawText(
                        dateMontajSlice, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 120), paint
                    );
                    canvas.drawText(
                        timeMontajSlice, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 140), paint
                    );

                    canvas.drawText(
                        brackProduct, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 160), paint
                    );


                }

                bitmapCopy.compress(Bitmap.CompressFormat.JPEG, 100, fos)

                if (fos != null) {

                    fos!!.flush()
                    fos!!.close()
                }

            } else {
               //binding.cabinetPhoto3.setImageDrawable(null)
            }
        }
        if (numberFoto == 4) {
            if (photoFile4.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                    val source = ImageDecoder.createSource(
                        photoFile4
                    )
                    bitmap4 = ImageDecoder.decodeBitmap(source)
                    if(bitmap4.density!=0) {
                        binding.cabinetPhoto4.setImageBitmap(bitmap4)
                    }
                } else {
                    binding.cabinetPhoto4.doOnLayout { measuredView ->
                        bitmap = getScaledBitmap(photoFile.path, measuredView.width,measuredView.height)
                        if(bitmap4.density!=0) {
                            binding.cabinetPhoto4.setImageBitmap(bitmap4)
                        }
                    }

                }
                val cabNumberText = "Кабинет ${cabinetCount}"
                val nameWorker = "${workerRepository.getName(requireActivity())}"
//            val dateMontaj="${DateFormat.format(DATE_FORMAT, Date().toString()}"
                val brackProduct = "Брак 3"
                fos = FileOutputStream(photoFile4.path)
                // options.inMutable = true

                val bitmapCopy = bitmap4.copy(Bitmap.Config.ARGB_8888, true)


                val canvas = Canvas(bitmapCopy)
                val paint = Paint()
                paint.setColor(Color.WHITE)
//                val exif = ExifInterface(photoFile4.getAbsolutePath())
//                val orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL
//                )
                val height = bitmapCopy.height
                val width = bitmapCopy.width
                orientationChange = width > height
                if (!orientationChange) {
                    paint.textSize = convertToPixels(requireActivity(), 20)
//            paint.setStrokeWidth(12f)

                    paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OVER))

                    canvas.drawBitmap(bitmapCopy, 0f, 0f, paint)
                    canvas.drawRect(
                        convertToPixels(requireActivity(), 0),
                        convertToPixels(requireActivity(), 464),
                        convertToPixels(requireActivity(), 430),
                        convertToPixels(requireActivity(), 600),
                        paint
                    );
                    paint.setColor(Color.BLACK)
                    canvas.drawText(
                        cabNumberText, convertToPixels(requireActivity(), 34),
                        convertToPixels(requireActivity(), 480), paint
                    );

                    canvas.drawText(
                        nameWorker, convertToPixels(requireActivity(), 34),
                        convertToPixels(requireActivity(), 500), paint
                    );

                    canvas.drawText(
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}",
                        convertToPixels(requireActivity(), 34),
                        convertToPixels(requireActivity(), 520),
                        paint
                    );

                    canvas.drawText(
                        brackProduct, convertToPixels(requireActivity(), 34),
                        convertToPixels(requireActivity(), 540), paint
                    );

                } else {
                    val dateMontajSlice =
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}".substring(0, 10)
                    val timeMontajSlice =
                        "${DateFormat.format(DATE_FORMAT, Date()).toString()}".substring(11, 19)
                    paint.textSize = convertToPixels(requireActivity(), 11)
//            paint.setStrokeWidth(12f)

                    paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OVER))

                    canvas.drawBitmap(bitmapCopy, 0f, 0f, paint)
                    canvas.drawRect(
                        convertToPixels(requireActivity(), 0),
                        convertToPixels(requireActivity(), 0),
                        convertToPixels(requireActivity(), 62),
                        convertToPixels(requireActivity(), 420),
                        paint
                    );
                    paint.setColor(Color.BLACK)
                    canvas.drawText(
                        cabNumberText, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 80), paint
                    );

                    canvas.drawText(
                        nameWorker, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 100), paint
                    );

                    canvas.drawText(
                        dateMontajSlice, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 120), paint
                    );
                    canvas.drawText(
                        timeMontajSlice, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 140), paint
                    );

                    canvas.drawText(
                        brackProduct, convertToPixels(requireActivity(), 1),
                        convertToPixels(requireActivity(), 160), paint
                    );


                }

                bitmapCopy.compress(Bitmap.CompressFormat.JPEG, 100, fos)

                if (fos != null) {

                    fos!!.flush()
                    fos!!.close()
                }


            } else {
                //binding.cabinetPhoto4.setImageDrawable(null)
            }
        }
    }
    private fun updateUi(cabinet: Cabinet) {
        binding.apply {
            if (workerRepository.getTurnGadjetState(requireActivity())) {
                cabinetClose.isEnabled = true
               cabinetClose.background=resources.getDrawable(R.drawable.blue_rounded)
                cabinetReport.isEnabled = false
                cabinetReport.background=resources.getDrawable(R.drawable.grey_all)
                "Режим просмотра ${cabinet.title}".also { cabinetTitle2.text = it }
                if (cabinet.master.isNotEmpty()) {
                    cabinetSuspect.text = cabinet.master
                }
                cabinetCamera.isEnabled=false
                cabinetDate.text = DateFormat.format(DATE_FORMAT, cabinet.date).toString()
                cabinetSuspect.text = workerRepository.getName(requireContext())
                //  updatePhotoView()
            } else {
                cabinetTitle2.text = "${cabinet.title}"
                cabinetDate.text = DateFormat.format(DATE_FORMAT, cabinet.date).toString()
                if (cabinet.master.isNotEmpty()) {
                    cabinetSuspect.text = cabinet.master
                }
                cabinetSuspect.text = workerRepository.getName(requireContext())
            }
            // updatePhotoView()
            cabinetDate.setOnClickListener {
            //    findNavController().navigate(
                  //  CabinetDetailFragmentDirections.selectDate(cabinet.date)
              //  )
            }
            cabinetClose.setOnClickListener{
                findNavController().navigate(
                    CabinetDetailFragmentDirections.actionCabinetDetailFragmentToCabinetListFragment())
            }
            ////////////////////////////////////////////////
            cabinetPhoto.setOnClickListener {
                val imageDialog = ImageDialogFragment()
                var args: Bundle = Bundle()
                if(::photoFile.isInitialized) {
                    if (photoFile.exists()) {
                        args.putString("path", photoFile.path);
                        imageDialog.setArguments(args);
                        imageDialog.show(childFragmentManager, "imajeViewDialog")
                    }
                }
            }
            cabinetPhoto.setOnLongClickListener {
                if(::photoFile.isInitialized) {
                    if (photoFile.exists()) {
                        if (photoUri != null) {
                            requireActivity().contentResolver.delete(photoUri!!, null, null)

                            imageDelFlag1 = true
                            //photoName = null
                            Toast.makeText(
                                requireActivity(),
                                "Удаление фото:${photoUri}!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            cabinetPhoto.setImageResource(0)

                        }
                    }
                }
                true

                }
           cabinetPhoto2.setOnClickListener {
               val imageDialog = ImageDialogFragment()
               var args: Bundle = Bundle()
               if(::photoFile2.isInitialized) {
               if (photoFile2.exists()) {
                   args.putString("path", photoFile2.path);
                   imageDialog.setArguments(args);
                   imageDialog.show(childFragmentManager, "imajeViewDialog")
               }
           }
            }
           cabinetPhoto2.setOnLongClickListener {
               if(::photoFile2.isInitialized) {
                   if (photoFile2.exists()) {
                       if (photoUri2 != null) {
                           requireActivity().contentResolver.delete(photoUri2!!, null, null)
                           imageDelFlag2 = true
                           photoName2 = null
                           Toast.makeText(
                               requireActivity(),
                               "Удаление фото:${photoUri2}!",
                               Toast.LENGTH_SHORT
                           )
                               .show()
                           cabinetPhoto2.setImageResource(0)
                       }
                   }
               }
                true

            }
            cabinetPhoto3.setOnClickListener {
                val imageDialog = ImageDialogFragment()
                var args: Bundle = Bundle()
                if(::photoFile3.isInitialized) {
                    if (photoFile3.exists()) {
                        args.putString("path", photoFile3.path);
                        imageDialog.setArguments(args);
                        imageDialog.show(childFragmentManager, "imajeViewDialog")
                    }
                }
            }
            cabinetPhoto3.setOnLongClickListener {
                if(::photoFile3.isInitialized) {
                    if (photoFile3.exists()) {
                        if (photoUri3 != null) {
                            requireActivity().contentResolver.delete(photoUri3!!, null, null)
                            imageDelFlag3 = true
                            photoName3 = null
                            Toast.makeText(
                                requireActivity(),
                                "Удаление фото:${photoUri3}!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            cabinetPhoto3.setImageResource(0)
                        }
                    }
                }
                true

            }
           cabinetPhoto4.setOnClickListener {
                val imageDialog = ImageDialogFragment()
                var args: Bundle = Bundle()
               if(::photoFile4.isInitialized) {
                   if (photoFile4.exists()) {
                       args.putString("path", photoFile4.path);
                       imageDialog.setArguments(args);
                       imageDialog.show(childFragmentManager, "imajeViewDialog")
                   }
               }
            }
           cabinetPhoto4.setOnLongClickListener {
               if(::photoFile4.isInitialized) {
                   if (photoFile4.exists()) {
                       if (photoUri4 != null) {
                           requireActivity().contentResolver.delete(photoUri4!!, null, null)
                           imageDelFlag4 = true
                           photoName4 = null
                           Toast.makeText(
                               requireActivity(),
                               "Удаление фото:${photoUri4}!",
                               Toast.LENGTH_SHORT
                           )
                               .show()
                           cabinetPhoto4.setImageResource(0)
                       }
                   }
               }
                true

            }
            ///////////////////////////////////////////////
            if(cabinet.photoFileName!=null){
                updatePhoto(cabinet.photoFileName)
            }
            if(cabinet.photoFileName2!=null){
                updatePhoto2(cabinet.photoFileName2)
            }

            if(cabinet.photoFileName3!=null){
                updatePhoto3(cabinet.photoFileName3)
            }
            if(cabinet.photoFileName4!=null){
                updatePhoto4(cabinet.photoFileName4)
            }


            cabinetReport.setOnClickListener {
                var arrayUri: ArrayList<out Parcelable>? = null

                if(photoUri!=null&&photoUri2!=null&&photoUri3!=null&&photoUri4!=null){
                    arrayUri = mutableListOf(photoUri, photoUri2, photoUri3, photoUri4) as ArrayList< out Parcelable>
            }
                if(photoUri!=null&&photoUri2!=null&&photoUri3!=null&&photoUri4==null){
                     arrayUri = mutableListOf(photoUri, photoUri2, photoUri3) as ArrayList< out Parcelable>
                }
                if(photoUri!=null&&photoUri2!=null&&photoUri3==null&&photoUri4==null){
                   arrayUri = mutableListOf(photoUri, photoUri2) as ArrayList< out Parcelable>
                }
                if(photoUri!=null&&photoUri2==null&&photoUri3==null&&photoUri4==null){
                     arrayUri = mutableListOf(photoUri) as ArrayList< out Parcelable>
                }

                if(photoUri!=null&&photoFile.isFile||photoUri2!=null&&photoFile2.isFile||photoUri3!=null&&photoFile2.isFile||photoUri4!=null&&photoFile4.isFile){
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND_MULTIPLE
                    sendIntent.putParcelableArrayListExtra(
                        Intent.EXTRA_STREAM,
                        arrayUri
                    )
                    sendIntent.setType("image/jpeg")
                    sendIntent.setPackage("com.whatsapp")
                    startActivityForResult(sendIntent,REQUEST_PHOTO)
                } else {

                    Toast.makeText(
                        requireActivity(),
                        "По данному кабинету в хранилище нет фото",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_PHOTO  -> {
                binding.cabinetClose.isEnabled=true
//                val message: Toast = Toast.makeText(
//                    requireActivity(),
//                    "Успешно отправлено",
//                    Toast.LENGTH_SHORT
//                )
//                message.setGravity(
//                    Gravity.CENTER, message.xOffset / 2,
//                    message.yOffset / 2
//                )
//                message.show()
                binding.cabinetClose.isEnabled=true
                binding.cabinetClose.background=resources.getDrawable(R.drawable.blue_rounded)
            }
        }
    }
    private fun getCabinetReport(cabinet: Cabinet): String {
        val solvedString = if (cabinet.isMainCabinet) {
            getString(R.string.cabinet_report_solved)
        } else {
            getString(R.string.cabinet_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, cabinet.date).toString()
        val suspectText = if (cabinet.master.isBlank()) {
            getString(R.string.cabinet_report_no_master)
        } else {
            getString(R.string.cabinet_report_master, cabinet.master)
        }

        return getString(
            R.string.cabinet_report,
            cabinet.title, dateString, solvedString, suspectText
        )
    }
    private fun parseContactSelection(contactUri: Uri) {
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        val queryCursor = requireActivity().contentResolver
            .query(contactUri, queryFields, null, null, null)

        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val suspect = cursor.getString(0)
//              cabinetDetailViewModel.updateCabinet { oldCabinet ->
//                    oldCabinet.copy(master = suspect)
//                }
            }
        }
    }
    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.cabinetPhoto.tag != photoFileName) {
            photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }!!

            if (photoFile.exists()) {
                binding.cabinetPhoto.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width ,
                        measuredView.height
                    )
                    binding.cabinetPhoto.setImageBitmap(scaledBitmap)
                    binding.cabinetPhoto.tag = photoFileName
                    binding.cabinetPhoto.contentDescription =
                        getString(R.string.cabinet_photo_image_description)
                }
            } else {
                binding.cabinetPhoto.setImageBitmap(null)
                binding.cabinetPhoto.tag = null
                binding.cabinetPhoto.contentDescription =
                    getString(R.string.cabinet_photo_no_image_description)
            }
        }
    }
    private fun updatePhoto2(photoFileName2: String?) {
        if (binding.cabinetPhoto2.tag != photoFileName2) {
             photoFile2 = photoFileName2?.let {
                 File(requireContext().applicationContext.filesDir, it)
             }!!

            if (photoFile2?.exists() == true) {
                binding.cabinetPhoto2.doOnLayout { measuredView ->
                    val scaledBitmap2 = getScaledBitmap(
                        photoFile2.path,
                        measuredView.width ,
                        measuredView.height
                    )
                    binding.cabinetPhoto2.setImageBitmap(scaledBitmap2)
                    binding.cabinetPhoto2.tag = photoFileName2
                    binding.cabinetPhoto2.contentDescription =
                        getString(R.string.cabinet_photo_image_description)
                }
            } else {
                binding.cabinetPhoto2.setImageBitmap(null)
                binding.cabinetPhoto2.tag = null
                binding.cabinetPhoto2.contentDescription =
                    getString(R.string.cabinet_photo_no_image_description)
            }
        }
    }
    private fun updatePhoto3(photoFileName3: String?) {
        if (binding.cabinetPhoto3.tag != photoFileName3) {
          photoFile3 = photoFileName3?.let {
              File(requireContext().applicationContext.filesDir, it)
          }!!

            if (photoFile3?.exists() == true) {
                binding.cabinetPhoto3.doOnLayout { measuredView ->
                    val scaledBitmap3 = getScaledBitmap(
                        photoFile3.path,
                        measuredView.width ,
                        measuredView.height
                    )
                    binding.cabinetPhoto3.setImageBitmap(scaledBitmap3)
                    binding.cabinetPhoto3.tag = photoFileName3
                    binding.cabinetPhoto3.contentDescription =
                        getString(R.string.cabinet_photo_image_description)
                }
            } else {
                binding.cabinetPhoto3.setImageBitmap(null)
                binding.cabinetPhoto3.tag = null
                binding.cabinetPhoto3.contentDescription =
                    getString(R.string.cabinet_photo_no_image_description)
            }
        }
    }
    private fun updatePhoto4(photoFileName4: String?) {
        if (binding.cabinetPhoto4.tag != photoFileName4) {
             photoFile4 = photoFileName4?.let {
                 File(requireContext().applicationContext.filesDir, it)
             }!!

            if (photoFile4?.exists() == true) {
                binding.cabinetPhoto4.doOnLayout { measuredView ->
                    val scaledBitmap4 = getScaledBitmap(
                        photoFile4.path,
                        measuredView.width ,
                        measuredView.height
                    )
                    binding.cabinetPhoto4.setImageBitmap(scaledBitmap4)
                    binding.cabinetPhoto4.tag = photoFileName4
                    binding.cabinetPhoto4.contentDescription =
                        getString(R.string.cabinet_photo_image_description)
                }
            } else {
                binding.cabinetPhoto4.setImageBitmap(null)
                binding.cabinetPhoto4.tag = null
                binding.cabinetPhoto4.contentDescription =
                    getString(R.string.cabinet_photo_no_image_description)
            }
        }
    }
}