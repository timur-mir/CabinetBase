package home.howework.databaseofcompletedworks

import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import home.howework.databaseofcompletedworks.MainActivity.CallMenu.cabinetCount
import home.howework.databaseofcompletedworks.MainActivity.CallMenu.newCabinetFlag
import home.howework.databaseofcompletedworks.databinding.FragmentCabinetListBinding
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

private const val TAG = "CabinetListFragment"

class CabinetListFragment : Fragment() {
    private var _binding: FragmentCabinetListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private val cabinetListViewModel: CabinetListViewModel by viewModels()
    private val cabCountRepository = SharedRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCabinetListBinding.inflate(inflater, container, false)

        binding.cabinetRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ///////////////////////////////////
        cabCountRepository.saveTurnState(true,requireActivity())
        ///////////////////////////////////

        if (cabCountRepository.getDayState(requireActivity()) && !cabCountRepository.getResumeDayState(requireActivity())

        ) {
            cabinetCount = 0
            MainActivity.CallMenu.newWorkDay = true
        } else
            if (cabCountRepository.getDayState(requireActivity()) && cabCountRepository.getResumeDayState(
                    requireActivity()
                )

            ) {
                MainActivity.CallMenu.cabinetCount =
                    cabCountRepository.getCabCount(requireActivity())
                MainActivity.CallMenu.newWorkDay = true
            }
        //////////////////////////////////
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cabinetListViewModel.cabinets.collect { cabinets ->
                    binding.cabinetRecyclerView.adapter =
                        CabinetListAdapter(cabinets) { cabinetId ->
                            findNavController().navigate(
                                CabinetListFragmentDirections.actionCabinetListFragmentToCabinetDetailFragment(
                                    cabinetId
                                )
                            )
                        }
                }
            }
            ///////////////////////////////

        }

        binding.cabinetAdd.setOnClickListener {
            showNewCabinet()
        }
        binding.closeDay.setOnClickListener{
            closeDayCabinet()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onDetach() {
        super.onDetach()
          setHasOptionsMenu(false)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cabinet_list_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_cabinet -> {
                 showNewCabinet()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showNewCabinet() {
        viewLifecycleOwner.lifecycleScope.launch {
            var newCabinet = Cabinet()
            cabCountRepository.saveTurnState(false, requireActivity())

            if (cabCountRepository.getDayState(requireActivity()) &&

                !cabCountRepository.getResumeDayState(requireActivity()) ||
                cabCountRepository.getDayState(requireActivity()) &&

                cabCountRepository.getCabCount(requireActivity()) == 0
            ) {
                cabCountRepository.saveResumeDayState(true, requireActivity())
                newCabinet.isMainCabinet = true
                newCabinet.title="Кабинет 1"
                newCabinetFlag = true
                cabinetCount += 1
                cabinetListViewModel.addCabinet(newCabinet)
                cabCountRepository.saveCabCount(cabinetCount.toInt(), requireActivity())
                cabCountRepository.saveDayState(true, requireActivity())
                findNavController().navigate(
                    CabinetListFragmentDirections.actionCabinetListFragmentToCabinetDetailFragment(
                        newCabinet.id
                    )
                )
            } else
                if (cabCountRepository.getDayState(requireActivity()) && cabCountRepository.getResumeDayState(requireActivity())
                ) {
                    newCabinet.title="Кабинет ${cabinetCount+1}"
                    newCabinetFlag = true
                    cabinetCount += 1
                    cabinetListViewModel.addCabinet(newCabinet)
                    cabCountRepository.saveCabCount(cabinetCount.toInt(), requireActivity())
                    findNavController().navigate(
                        CabinetListFragmentDirections.actionCabinetListFragmentToCabinetDetailFragment(
                            newCabinet.id
                        )
                    )
                }

        }
    }

    private fun closeDayCabinet(){
        val exitWarnings = AlertDialog.Builder(requireActivity())
        exitWarnings.setTitle("Вы действительно хотите завершить день?")
        exitWarnings.setMessage("Можете выйти в режим 'Пауза' если рабочий день не закончен!")
        exitWarnings.setIcon(R.drawable.exit)
        exitWarnings.setCancelable(false)
        exitWarnings.setPositiveButton("Завершить день") { _: DialogInterface, _: Int ->
            cabCountRepository.saveDayState(false, requireActivity())
            cabCountRepository.saveCabCount(0, requireActivity())
            cabCountRepository.saveResumeDayState(false, requireActivity())
            cabCountRepository.savePauseState(false, requireActivity())
            var audio2 = MediaPlayer.create(requireContext(), R.raw.close)
            audio2.start()
            Toast.makeText(context, "Завершение рабочего дня...", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable {
                activity?.finish()
                audio2.reset()
                audio2.release()
                audio2 = null
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)

            }, 2000)
        }
        exitWarnings.setNegativeButton("Пауза") { _: DialogInterface, _: Int ->
            cabCountRepository.saveDayState(true, requireActivity())
            cabCountRepository.saveResumeDayState(true, requireActivity())
            cabCountRepository.saveCabCount(cabinetCount.toInt(), requireActivity())
            cabCountRepository.savePauseState(true,requireActivity())
            Handler().postDelayed(Runnable {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                activity?.finish()
            }, 1000)
        }
        exitWarnings.show()
    }
}