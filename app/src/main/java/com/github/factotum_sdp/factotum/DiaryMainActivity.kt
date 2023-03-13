package com.github.factotum_sdp.factotum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DiaryMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_diary)
        /*if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ContactsFragment>(R.id.fragment_container_view)
            }
        } */
    }
}