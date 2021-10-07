package com.app.findyourlobster.ui

data class Spot(
        val id: Long = counter++,
        val name: String,
        val description: String,
        val url: String,
        val email: String
) {
    companion object {
        private var counter = 0L
    }

}
