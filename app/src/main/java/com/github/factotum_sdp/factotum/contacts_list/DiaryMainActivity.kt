package com.github.factotum_sdp.factotum.contacts_list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.factotum_sdp.factotum.R

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