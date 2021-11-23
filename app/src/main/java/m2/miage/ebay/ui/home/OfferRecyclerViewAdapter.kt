package m2.miage.ebay.ui.home

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColorStateList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import m2.miage.ebay.R
import m2.miage.ebay.data.Offer

class OfferRecyclerViewAdapter(listOffers: List<Offer>) : RecyclerView.Adapter<OfferRecyclerViewAdapter.ViewHolder>() {

    var offersList = listOffers
    lateinit var context: Context

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer: Offer = offersList.get(position)

        holder.name_post.text = offer.name
        holder.price_post.text = offer.price.toString() + " €"
        holder.owner_post.text = offer.ownerId
        if (offer.active == true) {
            holder.chip_active.setChipBackgroundColorResource(R.color.teal_200)
            holder.chip_active.text = "dispo"
            holder.time_post.text = "en cours"
        } else {
            holder.chip_active.setChipBackgroundColorResource(R.color.red)
            holder.chip_active.text = "non dispo"
            holder.time_post.text = offer.startDate?.time.toString()
        }

        Glide.with(context).load(offer.image).into(holder.image_post)

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val context = v?.context as AppCompatActivity

                Toast.makeText(context, "Clic", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int {
        return offersList.size
    }

    fun addOffer(arrList: List<Offer>) {
        this.offersList = arrList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.offer_item_activity, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val name_post = itemview.findViewById<TextView>(R.id.txt_name)
        val price_post = itemview.findViewById<TextView>(R.id.txt_price)
        val image_post = itemview.findViewById<ImageView>(R.id.img_post)
        val owner_post = itemview.findViewById<TextView>(R.id.txt_owner)
        val time_post = itemview.findViewById<TextView>(R.id.txt_time)
        val chip_active = itemview.findViewById<Chip>(R.id.chip_active)
    }
}