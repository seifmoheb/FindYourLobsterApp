package com.app.findyourlobster.ui

import androidx.recyclerview.widget.DiffUtil

class SpotDiffCallback(
        private val old: ArrayList<Any>,
        private val new: List<Any>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        if (old[oldPosition] is Spot && new[newPosition] is Spot) {
            return (old[oldPosition] as Spot).id == (new[newPosition] as Spot).id
        } else
            return false
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
