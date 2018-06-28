package ru.aipova.skintracker

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.statistics_fragment.*

class StatisticsFragment : Fragment() {

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
            Snackbar.make(actionView, "Add today's skin track", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
