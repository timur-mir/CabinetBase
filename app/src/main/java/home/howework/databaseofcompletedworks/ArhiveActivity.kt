package home.howework.databaseofcompletedworks


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import home.howework.databaseofcompletedworks.ArhiveActivity.CabInst.cabinetIn
import home.howework.databaseofcompletedworks.databinding.ArhiveActivityBinding
import home.howework.databaseofcompletedworks.databinding.ListItemCabinetArhiveBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.IndexedColors
import java.io.File
import java.io.FileOutputStream
import java.util.Date


class ArhiveActivity : AppCompatActivity() {
    private lateinit var photoFile: File
    private lateinit var photoFile2: File
    private lateinit var photoFile3: File
    private lateinit var photoFile4: File
    private var photoUri: Uri? = null
    private var photoUri2: Uri? = null
    private var photoUri3: Uri? = null
    private var photoUri4: Uri? = null
    var density = 0.0f
    lateinit var filesDir2: File
    private var adapter: ArhiveAdapter? = ArhiveAdapter(emptyList()) { }
    private val arhiveRepository = SharedRepo()
    private val cabinetListViewModel: CabinetListViewModel by viewModels()
    var excelCabinets: List<Cabinet> = emptyList<Cabinet>()

    companion object {
        const val DEFAULT_TEMP_DIR_NAME = "CABINET"
        var DEFAULT_TEMP_FILENAME = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ArhiveActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        filesDir2 = MainApp.appContext?.filesDir!!
        binding.arhiveRecyclerView.adapter = adapter
        binding.arhiveRecyclerView.layoutManager = LinearLayoutManager(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cabinetListViewModel.cabinets.collect { cabinets ->
                    excelCabinets = cabinets
                    binding.arhiveRecyclerView.adapter =
                        ArhiveAdapter(cabinets) { cab -> deleteProcess(cab) }
                }
            }
        }
        binding.sendExcelFile.setOnClickListener {
             sendExcelList(excelCabinets)
        }
        when (resources.displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> {
                density = 0.75f
                toast(density)
            }

            DisplayMetrics.DENSITY_140 -> {
                density = 0.75f
                toast(density)
            }

            DisplayMetrics.DENSITY_MEDIUM -> {
                density = 1.0f
                toast(density)
            }

            DisplayMetrics.DENSITY_180, DisplayMetrics.DENSITY_200, DisplayMetrics.DENSITY_220 -> density =
                1.3f

            DisplayMetrics.DENSITY_HIGH -> {
                density = 1.5f
                toast(density)
            }

            DisplayMetrics.DENSITY_260, DisplayMetrics.DENSITY_280, DisplayMetrics.DENSITY_300 -> {
                density = 1.6f
                toast(density)
            }

            DisplayMetrics.DENSITY_XHIGH -> {
                density = 2.0f
                toast(density)
            }

            DisplayMetrics.DENSITY_340, DisplayMetrics.DENSITY_360, DisplayMetrics.DENSITY_400, DisplayMetrics.DENSITY_420, DisplayMetrics.DENSITY_440 -> {
                density =
                    2.5f
                toast(density)
            }

            DisplayMetrics.DENSITY_XXHIGH -> {
                density = 3.0f
                toast(density)
            }

            DisplayMetrics.DENSITY_560, DisplayMetrics.DENSITY_600 -> {
                density = 3.6f
                toast(density)
            }

            DisplayMetrics.DENSITY_XXXHIGH -> {
                density = 4.0f
                toast(density)
            }
            //  DisplayMetrics.DENSITY_TV -> density = "TVDPI"
            else -> {
                density = 1.0f
                toast(density)
            }

        }
    }

    fun toast(coefficient: Float) {
        Toast.makeText(
            this@ArhiveActivity,
            "плотность экрана телефона:${coefficient}",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun toastAboutDelete(cabinet: Cabinet) {

        val inflater = getLayoutInflater();
        val layout = inflater.inflate(R.layout.toast_layout, findViewById(R.id.toast_layout_root));
        val image = layout.findViewById<ImageView>(R.id.image_toast);
        val text = layout.findViewById<TextView>(R.id.toast_text);
        // text.text = "Удаление ${cabinet.title} время сборки: ${cabinet.date}";
        text.text = "${cabinet.title} удалён!";
        val toast = Toast(applicationContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 60, 260);
        toast.duration = Toast.LENGTH_LONG;
        if (photoFile.path != null && photoFile.exists()) {
            val scaledBitmap = getScaledBitmap(
                photoFile.path,
                130,
                180
            )
            image.setImageBitmap(scaledBitmap)
        }
        toast.setView(layout);
        toast.show()
    }

    fun deleteProcess(cabinet: Cabinet) {
        if (!arhiveRepository.getResumeDayState(this)) {
            cabinetIn = cabinet
            val closeWarnings = AlertDialog.Builder(this)
            closeWarnings.setTitle("Вы действительно хотите удалить кабинет?")
            closeWarnings.setMessage("Будут удалены  фотографии и записи кабинета! ")
            closeWarnings.setIcon(R.drawable.reset)
            closeWarnings.setNegativeButton("Отмена", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }
            })
            closeWarnings.setCancelable(false)
            closeWarnings.setPositiveButton("Удалить",
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        deleteProcessImpl(cabinetIn)
                    }
                })
            closeWarnings.show()
        } else {
            Toast.makeText(
                this@ArhiveActivity,
                "Нельзя удалять кабинеты в режиме Пауза!",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    fun deleteProcessImpl(cabinet: Cabinet) {
        if (cabinet.photoFileName != null) {
            photoFile = File(filesDir2, cabinet.photoFileName)
            photoUri = FileProvider.getUriForFile(
                this@ArhiveActivity,
                "home.howework.databaseofcompletedworks.fileprovider",
                photoFile
            )
            toastAboutDelete(cabinet)
            CabInst.fotoUri = (photoUri as Uri?)!!
            photoUri?.let { it1 ->
                this@ArhiveActivity.contentResolver.delete(
                    it1,
                    null,
                    null
                )
            }
        }
        ////////////////////////////////////////////////////////////
        if (cabinet.photoFileName2 != null) {
            photoFile2 = File(filesDir2, cabinet.photoFileName2)
            photoUri2 = FileProvider.getUriForFile(
                this@ArhiveActivity,
                "home.howework.databaseofcompletedworks.fileprovider",
                photoFile2
            )
            photoUri2?.let { it1 ->
                this@ArhiveActivity.contentResolver.delete(
                    it1,
                    null,
                    null
                )
            }
        }
        ////////////////////////////////////////////////////////////
        if (cabinet.photoFileName3 != null) {
            photoFile3 = File(filesDir2, cabinet.photoFileName3 ?: "")
            photoUri3 = FileProvider.getUriForFile(
                this@ArhiveActivity,
                "home.howework.databaseofcompletedworks.fileprovider",
                photoFile3
            )
            photoUri3?.let { it1 ->
                this@ArhiveActivity.contentResolver.delete(
                    it1,
                    null,
                    null
                )
            }
        }
        ////////////////////////////////////////////////////////////
        if (cabinet.photoFileName4 != null) {
            photoFile4 = File(filesDir2, cabinet.photoFileName4 ?: "")
            photoUri4 = FileProvider.getUriForFile(
                this@ArhiveActivity,
                "home.howework.databaseofcompletedworks.fileprovider",
                photoFile4
            )
            photoUri4?.let { it1 ->
                this@ArhiveActivity.contentResolver.delete(
                    it1,
                    null,
                    null
                )
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            cabinetListViewModel.deleteCabinet(cabinet)
        }

    }

    private fun sendExcelList(cabinets: List<Cabinet>) {
        if (cabinets.isNotEmpty()) {
            val date: Date = Date()
            val DATE_FORMAT = "dd MM yyyy HH:mm:ss"
            val DATE_FORMAT2 = "dd MM yyyy"
            DEFAULT_TEMP_FILENAME = "/" + arhiveRepository.getName(this@ArhiveActivity)
                .toString() + "_" + DateFormat.format(DATE_FORMAT, date) + ".xls"
            //val imgPath: File = File(this.cacheDir, DEFAULT_TEMP_DIR_NAME)
            val newFile = File(filesDir2, DEFAULT_TEMP_FILENAME)
            val uriExcelFile = FileProvider.getUriForFile(
                this@ArhiveActivity,
                "home.howework.databaseofcompletedworks.fileprovider",
                newFile
            )
            val hssfWorkbook = HSSFWorkbook()
            val hssfSheet =
                hssfWorkbook.createSheet("Лист отчёта ${arhiveRepository.getName(this@ArhiveActivity)}а")
//            val styleCell = hssfWorkbook.createCellStyle()
//            styleCell.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            val size = cabinets.size
            val hssfRow = hssfSheet.createRow(4)
            val hssfCell = hssfRow.createCell(5)
            hssfCell.setCellValue(" Итоговая сводка из архива собранных кабинетов ")
            var n = 0
            repeat(size) {
                val hssfRowIn = hssfSheet.createRow(6 + n)
                val hssfCellIn = hssfRowIn.createCell(4)
                if (cabinets[n].isMainCabinet) {
                    val styleCell = hssfWorkbook.getCellStyleAt(1)
                    //     styleCell.fillBackgroundColor = HSSFColor.GREEN.index

                    //   hssfCellIn.setCellStyle(styleCell)
                    //  val cell= hssfRowIn.getCell(4)
                    // hssfCellIn.setCellValue(cabinets[n].title)
                    //   cell.setCellStyle(styleCell)
                    hssfCellIn.setCellValue(cabinets[n].title)
                    // hssfCellIn.hyperlink
                } else {
                    hssfCellIn.setCellValue(cabinets[n].title)
                }
                val hssfCellInDate = hssfRowIn.createCell(5)
                hssfCellInDate.setCellValue("${DateFormat.format(DATE_FORMAT2, cabinets[n].date)}")
                n += 1
            }


            try {
                if (!newFile.exists()) {
                    newFile.createNewFile()
                }
                val fileOutputStream: FileOutputStream = FileOutputStream(newFile)
                hssfWorkbook.write(fileOutputStream)
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                if (uriExcelFile != null) {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        uriExcelFile
                    )
                    sendIntent.setType("text/html")
                    sendIntent.setPackage("com.whatsapp")
                    startActivity(sendIntent)
//                val shareIntent = ShareCompat.IntentBuilder.from(this@ArhiveActivity)
//                    .setType("text/html")
//                    .setStream(uriExcelFile)
//                    .setSubject("Отправка книги Excel")
//                    .setEmailTo(arrayOf<String>("example@example.com"))
//                    .intent
//                shareIntent.data = uriExcelFile
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                this@ArhiveActivity.startActivity(
//                    Intent.createChooser(
//                        shareIntent, "Передача контента"
//                    )
//                )
                }

            } catch (e: Exception) {

            }

        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            supportFragmentManager.popBackStack()
        }
    }


    private inner class ArhiveAdapter(
        var cabinets: List<Cabinet>,
        private val onCabinetInfo: (cabinet: Cabinet) -> Unit
    ) :
        RecyclerView.Adapter<ArhiveHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArhiveHolder {
            return ArhiveHolder(
                ListItemCabinetArhiveBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
            )
        }

        override fun onBindViewHolder(holder: ArhiveHolder, position: Int) {
            val cabinet = cabinets[position]
            holder.bind(cabinet, onCabinetInfo)
        }

        override fun getItemCount() = cabinets.size
    }

    private inner class ArhiveHolder(val binding: ListItemCabinetArhiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val DATE_FORMAT = "dd MMM yyyy"
        val DATE_FORMAT2 = "dd MM yyyy"

        init {
            binding.delArhive.setOnClickListener {
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(cabinet: Cabinet, onCabinetInfo: (cabinet: Cabinet) -> Unit) {
            binding.delArhive.setOnClickListener {
                onCabinetInfo(cabinet)
            }
            if (cabinet.isMainCabinet) {
                binding.cabinetTitleArhive.setBackgroundColor(Color.rgb(171, 39, 79))
                binding.cabinetTitleArhive.text = "${cabinet.title} ${
                    DateFormat.format(DATE_FORMAT2, cabinet.date).toString()
                }"
                binding.cabinetDateTitleArhive.text =
                    DateFormat.format(DATE_FORMAT2, cabinet.date).toString()
            } else {
                binding.cabinetTitleArhive.text = cabinet.title.toString()
                binding.cabinetDateTitleArhive.text =
                    DateFormat.format(DATE_FORMAT2, cabinet.date).toString()
            }
        }

    }

    object CabInst {
        lateinit var cabinetIn: Cabinet
        var fotoUri: Uri? = null
    }

}