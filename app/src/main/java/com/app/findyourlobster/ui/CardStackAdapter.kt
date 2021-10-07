package com.app.findyourlobster.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.findyourlobster.R
import com.bumptech.glide.Glide
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.material.floatingactionbutton.FloatingActionButton


class CardStackAdapter(
        private val spots: ArrayList<Any>

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val AD_TYPE = 2
    private val CONTENT_TYPE = 1
    private var mcontext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context

        if (viewType == AD_TYPE) {
            val itemView: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.ad_template_layout, parent, false)
            return AdViewHolder(itemView)
        }
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_spot, parent, false)
        return ViewHolder(itemView)
        //val inflater = LayoutInflater.from(parent.context)
        //return ViewHolder(inflater.inflate(R.layout.item_spot, parent, false))
    }

    override fun getItemCount(): Int {
        return spots!!.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var city: TextView = view.findViewById(R.id.item_city)
        var email: TextView = view.findViewById(R.id.email)
        var image: ImageView = view.findViewById(R.id.item_image)
        val more: FloatingActionButton = view.findViewById(R.id.more)
        val context: Context = view.context
    }

    class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val template: TemplateView = itemView.findViewById(R.id.my_template)
        val context: Context = view.context
        fun setUnifiedNativeAd(ads: UnifiedNativeAd?) {
            template.setNativeAd(ads)
        }

    }

    override fun getItemViewType(position: Int): Int {

        return if (spots[position] is UnifiedNativeAd) AD_TYPE else CONTENT_TYPE

    }

    fun setData(list: ArrayList<Any>) {
        spots.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == AD_TYPE) {
            val adHolder: AdViewHolder = holder as AdViewHolder
            adHolder.setUnifiedNativeAd(spots[position] as UnifiedNativeAd)


        }

        val spot: Spot?

        if (spots[position] is Spot) {

            spot = spots[position] as Spot
            val contentHolder: ViewHolder = holder as ViewHolder
            contentHolder.name.text = spot!!.name
            contentHolder.city.text = spot.description
            if (spot.url != "") {
                Glide.with(contentHolder.image)
                        .load(spot!!.url)
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                        .into(contentHolder.image)
                Log.i("has image", spot.name)
            } else {
                Log.i("no image", spot.name)
                contentHolder.image.setImageDrawable(ContextCompat.getDrawable(
                        mcontext!!, // Context
                        R.drawable.com_facebook_profile_picture_blank_square // Drawable
                ))
            }
            contentHolder.email.text = spot.email
            contentHolder.itemView.setOnClickListener { v ->
                Toast.makeText(v.context, spot.name, Toast.LENGTH_SHORT).show()
            }
            contentHolder.more.setOnClickListener(View.OnClickListener {
                val intent = Intent(contentHolder.context, MoreInfoActivity::class.java)
                intent.putExtra("email", spot.email)
                contentHolder.context.startActivity(intent)
            })
        }
    }
}


