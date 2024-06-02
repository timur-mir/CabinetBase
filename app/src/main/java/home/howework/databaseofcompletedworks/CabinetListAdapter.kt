package home.howework.databaseofcompletedworks
import android.graphics.Color
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import home.howework.databaseofcompletedworks.databinding.ListItemCabinetBinding
import java.util.UUID
class CabinetHolder(
    private val binding: ListItemCabinetBinding
) : RecyclerView.ViewHolder(binding.root) {
    val DATE_FORMAT = "dd MMM yyyy"
    val DATE_FORMAT2 = "dd MM yyyy"
    fun bind(cabinet: Cabinet, onCabinetClicked: (cabinetId: UUID) -> Unit) {
        if (!cabinet.isMainCabinet) {
                binding.cabinetTitle.text = cabinet.title
                binding.cabinetDateTitle.text =  DateFormat.format(DATE_FORMAT, cabinet.date).toString()
        }
            else
            {
          binding.textLayout.setBackgroundColor(Color.parseColor("#21bad1"))
                binding.cabinetTitle.text = buildString {
        append(cabinet.title)
        append(
            "  ${DateFormat.format(DATE_FORMAT2, cabinet.date).toString()}"
        )
    }
                binding.cabinetDateTitle.text =  DateFormat.format(DATE_FORMAT, cabinet.date).toString()
            }

                binding.root.setOnClickListener {
                    onCabinetClicked(cabinet.id)
                }
            }
    }


class CabinetListAdapter(
    private val cabinets: List<Cabinet>,
    private val onCabinetClicked: (cabinetId: UUID) -> Unit
) : RecyclerView.Adapter<CabinetHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CabinetHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCabinetBinding.inflate(inflater, parent, false)
        return CabinetHolder(binding)
    }

    override fun onBindViewHolder(holder: CabinetHolder, position: Int) {
        val cabinet = cabinets[position]
        holder.bind(cabinet, onCabinetClicked)
    }

    override fun getItemCount() = cabinets.size
}