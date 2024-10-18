package com.example.notesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val dates: List<Date>,
    private val onDateSelected: (Date?) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var selectedPosition = -1

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val cardView: CardView = itemView.findViewById(R.id.dateCardView)

        init {
            itemView.setOnClickListener {
                if (selectedPosition == adapterPosition) {
                    selectedPosition = -1
                    onDateSelected(null)
                } else {
                    selectedPosition = adapterPosition
                    onDateSelected(dates[adapterPosition])
                }
                notifyDataSetChanged()
            }
        }

        fun bind(date: Date, isSelected: Boolean) {
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

            dayTextView.text = dayFormat.format(date)
            dateTextView.text = dateFormat.format(date)

            if (isSelected) {
                cardView.setCardBackgroundColor(itemView.context.getColor(R.color.selectedText))
            } else {
                cardView.setCardBackgroundColor(itemView.context.getColor(R.color.lightBlue))
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_date, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(dates[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = dates.size
}
