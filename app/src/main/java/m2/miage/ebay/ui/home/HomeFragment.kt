package m2.miage.ebay.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import m2.miage.ebay.R
import m2.miage.ebay.data.Offer
import java.util.*
import com.google.firebase.firestore.ktx.firestore
import m2.miage.ebay.data.Bid
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var offerAdapter: OfferRecyclerViewAdapter

    var offerBid: MutableLiveData<Bid> = MutableLiveData()
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getPosts()
        v_swipe.setOnRefreshListener(this)
    }

    override fun onResume() {
        super.onResume()
        getPosts()
    }

    private fun initializeAdapter(offers: List<Offer>) {
        offerAdapter = OfferRecyclerViewAdapter(offers)
        rv_post.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_post.itemAnimator = DefaultItemAnimator()
        offerAdapter.context = this.requireContext()
        rv_post.adapter = offerAdapter
    }

    private fun getPosts() {

        val offerList = ArrayList<Offer>()

        db.collection("Offers")
            .addSnapshotListener { offers, e ->

            if (e != null) {
                return@addSnapshotListener
            }

                offers?.let {

                    for (offer in offers) {

                        getBid(offer.reference.id)

                        offerBid.observe(viewLifecycleOwner, {
                            offerList.add(
                                Offer(
                                    id = offer.id,
                                    name = offer.getString("nom").toString(),
                                    description = offer.getString("desc"),
                                    price = offer.getString("prixInitial"),
                                    dateDebut = offer.getString("dateDebut"),
                                    image = offer.getString("photo"),
                                    active = offer.getBoolean("active"),
                                    ownerId = offer.getString("proprietaire"),
                                    enchere = it
                                )
                            )
                        })
                    }

                }
                    initializeAdapter(offerList)
             }
    }

    private fun getBid(docRef: String) {

        db.collection("Offers").document(docRef)
            .collection("bid")
            .orderBy("prix", Query.Direction.DESCENDING).limit(1)
            .get()
            .addOnCompleteListener{ bids ->

                bids.result?.let {

                    it.documents.let {

                        if (!it.isEmpty()) {
                            offerBid.value = Bid(it.get(0).getString("acheteur").toString(),
                                            it.get(0).getDate("date"),
                                            it.get(0).getString("prix"))
                        } else {
                            offerBid.value = null
                        }
                    }

                }
            }

    }

    override fun onRefresh() {

        getPosts()

        Handler(Looper.getMainLooper()).run {
            v_swipe.isRefreshing = false
        }
    }
}