package m2.miage.ebay.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_profile.*
import m2.miage.ebay.FirebaseConnectActivity
import m2.miage.ebay.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_disconnect.setOnClickListener {
            FirebaseConnectActivity.signOut(requireContext())
            activity?.finish()
        }
    }
}