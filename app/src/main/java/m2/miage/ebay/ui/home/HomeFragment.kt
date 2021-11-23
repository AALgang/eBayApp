package m2.miage.ebay.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import m2.miage.ebay.R
import m2.miage.ebay.data.Offer
import m2.miage.ebay.util.Resource
import m2.miage.ebay.util.Status
import java.util.*
import com.google.firebase.firestore.ktx.firestore
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var _posts : MutableLiveData<Resource<List<Offer>>> = MutableLiveData()
    lateinit var offerAdapter: OfferRecyclerViewAdapter
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        getPosts()
        initObserver()
    }

    private fun initObserver() {

        _posts.observe(viewLifecycleOwner, { offers ->

            when(offers.status) {

                Status.SUCCESS -> {
                    offers.data?.let {
                        initAdapter(rv_post, it)
                    }

                }
                Status.ERROR -> {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getPosts() {

        val offers = ArrayList<Offer>()

        //TODO: get data from database
        db.collection("Offer").addSnapshotListener { value, e ->

            if (e != null) {
                return@addSnapshotListener
            }
             value?.let {
                    for (doc in value) {

                        offers.add(Offer(doc.getString("nom").toString(),
                            doc.getString("desc").toString(),
                            doc.getDouble("prixInitial"),
                            doc.getDate("date"),
                            doc.getString("photo"),
                            doc.getBoolean("active"),
                            doc.getString("proprietaire").toString()))

                    }

                 _posts.value = Resource.success(offers)

             }
        }
    }

    private fun initAdapter(recyclerView: RecyclerView, list: List<Offer>) {

        val listOffer = list
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()

        offerAdapter = OfferRecyclerViewAdapter(listOffer)
        recyclerView.adapter = offerAdapter

        offerAdapter.context = this.requireContext()
    }
}