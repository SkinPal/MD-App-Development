package com.capstone.skinpal.ui.reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.skinpal.R

class ReminderFragment : Fragment(), HorizontalCalendarAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvDateMonth: TextView
    private lateinit var ivCalendarNext: ImageView
    private lateinit var ivCalendarPrevious: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reminder, container, false)

        tvDateMonth = view.findViewById(R.id.text_date_month)
        ivCalendarNext = view.findViewById(R.id.iv_calendar_next)
        ivCalendarPrevious = view.findViewById(R.id.iv_calendar_previous)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )

        val calendarSetUp = HorizontalCalendarSetUp()
        val tvMonth = calendarSetUp.setUpCalendarAdapter(recyclerView, this@ReminderFragment)
        tvDateMonth.text = tvMonth

        calendarSetUp.setUpCalendarPrevNextClickListener(ivCalendarNext, ivCalendarPrevious, this@ReminderFragment) {
            tvDateMonth.text = it
        }

        return view
    }

    override fun onItemClick(ddMmYy: String, dd: String, day: String) {
        view?.findViewById<TextView>(R.id.selectedDate)?.text = ddMmYy
    }
}
