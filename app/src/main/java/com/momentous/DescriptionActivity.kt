package com.momentous

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.momentous.model.DataModels
import kotlinx.android.synthetic.main.activity_description.*


class DescriptionActivity : AppCompatActivity() {
    private var dataModel: DataModels? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        dataModel = intent?.extras?.getSerializable("dataModel") as DataModels
        loadWithGlide(dataModel?.imageUrl, appCompatImageView)
        textView?.text = dataModel?.name
        desTV?.text = dataModel?.description

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
