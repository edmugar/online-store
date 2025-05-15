package com.robote.onlinestore.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.robote.onlinestore.Constants
import com.robote.onlinestore.OpcionesLogins
import com.robote.onlinestore.R
import com.robote.onlinestore.databinding.FragmentAccountBinding

class FragmentAccount : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        readInfo()

        binding.btnEditProfile.setOnClickListener() {
            startActivity(Intent(mContext, EditProfile::class.java))
        }

        binding.btnCloseSession.setOnClickListener() {
            firebaseAuth.signOut()
            startActivity(Intent(mContext, OpcionesLogins::class.java))
            activity?.finishAffinity()
        }
    }

    private fun readInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    val dateBorn = "${snapshot.child("fecha_nac").value}"
                    val phoneNumber = "${snapshot.child("Telefono").value}"
                    val codePhoneNumber = "${snapshot.child("codigoTelefono").value}"
                    val image = "${snapshot.child("urlImagenPerfil").value}"
                    var time: String = "${snapshot.child("tiempo").value}"
                    val provider = "${snapshot.child("proveedor").value}"

                    val codePhone = codePhoneNumber + phoneNumber;

                    if (time == "null") {
                        time = "0"
                    }

                    val formatTime = Constants.getDate(time.toLong())
                    //setter information
                    binding.tvEmailProfile.text = email
                    binding.tvNames.text = email
                    binding.tvBorn.text = dateBorn
                    binding.tvPhoneNumberProfile.text = phoneNumber
                    binding.tvMemberProfile.text = formatTime
                    binding.txtProfile.text = "Hola $name"

                    //setter image
                    try {
                        Glide.with(mContext)
                            .load(image)
                            .placeholder(R.drawable.ic_profile_user)
                            .into(binding.ivProfile)
                    } catch (e: Exception) {
                        Toast.makeText(
                            mContext,
                            "${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (provider == "Email") {
                        val isVerified = firebaseAuth.currentUser!!.isEmailVerified
                        if (isVerified) {
                            binding.tvAccountStateProfile.text = "Verificado"
                        } else {
                            binding.tvAccountStateProfile.text = "No verificado"
                        }
                    } else {
                        binding.tvAccountStateProfile.text = "Verificado"
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}