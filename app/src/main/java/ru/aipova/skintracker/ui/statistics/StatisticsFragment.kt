package ru.aipova.skintracker.ui.statistics

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import kotlinx.android.synthetic.main.statistics_fragment.*
import ru.aipova.skintracker.R

class StatisticsFragment : Fragment() {
    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onCreateNewTrack()
        fun onShowTrackTypes()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
//        TODO check type and throw appropriate exception
        callbacks = activity as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_statistics, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_settings -> {
                callbacks?.onShowTrackTypes()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.statistics_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        track_add_fab.setOnClickListener { callbacks?.onCreateNewTrack() }
    }

    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
