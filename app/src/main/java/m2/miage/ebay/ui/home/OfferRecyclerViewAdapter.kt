package m2.miage.ebay.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.offer_item_activity.view.*
import m2.miage.ebay.R
import m2.miage.ebay.data.Offer
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class OfferRecyclerViewAdapter(listOffers: List<Offer>) : RecyclerView.Adapter<OfferRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context

    var offersList = listOffers
    var _userName : MutableLiveData<String> = MutableLiveData()

    val db = Firebase.firestore

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer: Offer = offersList.get(position)

        holder.name_post.text = offer.name
        holder.price_post.text = context.getString(R.string.txt_devise, offer.price.toString())
        holder.time_post.text = offer.dateDebut.toString().replace("T", " ")
        holder.owner_post.text = offer.ownerId

        // Affichage des chip en fonction de l'enchère disponible ou non
        if (isOfferActive(LocalDateTime.parse(offer.dateDebut.toString(), DateTimeFormatter.ISO_DATE_TIME))) {
            holder.chip_active.setChipBackgroundColorResource(R.color.teal_200)
            holder.chip_active.text = context.getString(R.string.chip_available)
        } else {
            holder.chip_active.setChipBackgroundColorResource(R.color.red)
            holder.chip_active.text = context.getString(R.string.chip_not_available)
        }

        Glide.with(context).load(offer.image).into(holder.image_post)

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val context = v?.context as AppCompatActivity

                Toast.makeText(context, v.txt_time.toString(), Toast.LENGTH_SHORT).show()

            }
        })
    }

    /**
     * Vérifie si l'enchère actuelle est disponible ou non
     * @param date : Date de début de l'enchère
     */
    private fun isOfferActive(date: LocalDateTime) : Boolean {

        var response = false
        val now = LocalDateTime.now()

        // Vérifie si l'enchère est démarrée et si les 5 min sont écoulées
        if (date.toEpochSecond(ZoneOffset.UTC) <= now.toEpochSecond(ZoneOffset.UTC)
            && now.toEpochSecond(ZoneOffset.UTC) < date.toEpochSecond(ZoneOffset.UTC) + 300) {
            response = true
        }

        return response
    }

    override fun getItemCount(): Int {
        return offersList.size
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