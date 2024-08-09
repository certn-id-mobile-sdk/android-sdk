package com.certn.mobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.certn.mobile.databinding.ActivityMainBinding
import com.certn.mobile.storage.CertnIDEncryptedSharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var encryptedStorage: CertnIDEncryptedSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        encryptedStorage.clear()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val navigationHost =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navigationHost.navController
        setContentView(binding.root)
    }

}