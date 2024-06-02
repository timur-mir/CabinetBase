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
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import home.howework.databaseofcompletedworks.ArhiveActivity.CabInst.cabinetIn
import home.howework.databaseofcompletedworks.ArhiveActivity.CabInst.fotoUri
import home.howework.databaseofcompletedworks.databinding.ArhiveActivityBinding
import home.howework.databaseofcompletedworks.databinding.ListItemCabinetArhiveBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

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
    private val cabinetListViewModel: CabinetListViewModel by viewModels()

    companion object {
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
                    binding.arhiveRecyclerView.adapter =
                        ArhiveAdapter(cabinets) { cab -> deleteProcess(cab) }
                }
            }
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
        Toast.makeText(this@ArhiveActivity, "плотность экрана телефона:${coefficient}", Toast.LENGTH_SHORT)
            .show()
    }

    fun toastAboutDelete(cabinet: Cabinet) {

        val inflater = getLayoutInflater();
        val layout = inflater.inflate(R.layout.toast_layout, findViewById(R.id.toast_layout_root));
        val image = layout.findViewById<ImageView>(R.id.image_toast);
        val text =layout.findViewById<TextView>(R.id.toast_text);
       // text.text = "Удаление ${cabinet.title} время сборки: ${cabinet.date}";
        text.text = "${cabinet.title} удалён!";
        val toast = Toast(applicationContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 60, 260);
        toast.duration = Toast.LENGTH_LONG;
        if(photoFile.path!=null&&photoFile.exists()) {
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
       var fotoUri: Uri?=null
    }

}