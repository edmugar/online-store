package com.robote.onlinestore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.robote.onlinestore.Adds.NewAdd
import com.robote.onlinestore.Fragments.FragmentAccount
import com.robote.onlinestore.Fragments.FragmentChat
import com.robote.onlinestore.Fragments.FragmentInicio
import com.robote.onlinestore.Fragments.FragmentMyAdds
import com.robote.onlinestore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        firebaseAuth = FirebaseAuth.getInstance()
        sessionCheck()
        seeFragmentHome();
        binding.buttonNV.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_home -> {
                    seeFragmentHome();
                    true
                }

                R.id.item_chat -> {
                    seeFragmentChat();
                    true
                }

                R.id.item_my_adds -> {
                    seeFragmentMyAdds();
                    true
                }

                R.id.item_account -> {
                    seeFragmentAccount();
                    true
                }

                else -> {
                    false
                }
            }
        }

        binding.floatingButtonAdd.setOnClickListener {
            startActivity(Intent(this, NewAdd::class.java))
        }
    }

    private fun seeFragmentHome() {
        binding.titleR1Home.text = "Home";
        val fragment = FragmentInicio();
        var fragmentTransition = supportFragmentManager.beginTransaction();
        fragmentTransition.replace(binding.Fragment1.id, fragment, "FragmentInicio");
        fragmentTransition.commit();
    }

    private fun seeFragmentChat() {
        binding.titleR1Home.text = "Chat";
        var fragment = FragmentChat();
        var fragmentTransition = supportFragmentManager.beginTransaction();
        fragmentTransition.replace(binding.Fragment1.id, fragment, "Fragment_Chat")
        fragmentTransition.commit();

    }

    private fun seeFragmentMyAdds() {
        binding.titleR1Home.text = "My adds";
        var fragment = FragmentMyAdds();
        var fragmentTransition = supportFragmentManager.beginTransaction();
        fragmentTransition.replace(binding.Fragment1.id, fragment, "Fragment_My_Adds")
        fragmentTransition.commit();
    }

    private fun seeFragmentAccount() {
        binding.titleR1Home.text = "My account"
        var fragment = FragmentAccount();
        var fragmentTransition = supportFragmentManager.beginTransaction();
        fragmentTransition.replace(binding.Fragment1.id, fragment, "Fragment:_Account")
        fragmentTransition.commit()
    }


    private fun sessionCheck() {
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, OpcionesLogins::class.java))
            finishAffinity()
        }
    }
}