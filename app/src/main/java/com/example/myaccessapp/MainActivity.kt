package com.example.myaccessapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!AccessService.isStart()) {
            try {
                startActivity( Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } catch (e:Exception) {
                startActivity( Intent(Settings.ACTION_SETTINGS))
                e.printStackTrace()
            }
        }
        findViewById<TextView>(R.id.tv).setOnClickListener {
            val intent = Intent()
            intent.setPackage("com.kuaishou.nebula")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}