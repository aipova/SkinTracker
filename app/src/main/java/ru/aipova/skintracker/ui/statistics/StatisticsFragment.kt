package ru.aipova.skintracker.ui.statistics

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.statistics_fragment.*
import ru.aipova.skintracker.R

class StatisticsFragment : Fragment() {
    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onCreateNewTrack()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.statistics_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        add_evaluations.setOnClickListener { actionView ->
            callbacks?.onCreateNewTrack()
        }
    }

    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
