package com.samuel.lab_week_10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.samuel.lab_week_10.database.Total
import com.samuel.lab_week_10.database.TotalDatabase
import com.samuel.lab_week_10.viewmodels.TotalViewModel

class MainActivity : AppCompatActivity() {
    private val db by lazy { prepareDatabase() }
    private val viewModel by lazy { ViewModelProvider(this)[TotalViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeValueFromDatabase()
        prepareViewModel()
    }

    // Create and build the TotalDatabase with the name 'total-database'
    // allowMainThreadQueries() is used to allow queries to be run on the main
    // thread
    // This is not recommended, but for simplicity it's used here
    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java, "total-database"
        ).allowMainThreadQueries().build()
    }

    // Initialize the value of the total from the database
    // If the database is empty, insert a new Total object with the value of 0
    // If the database is not empty, get the value of the total from the
    // database
    private fun initializeValueFromDatabase() {
        val total = db.totalDao().getTotal(ID)
        if (total.isEmpty()) {
            db.totalDao().insert(Total(id = 1, total = 0))
        } else {
            viewModel.setTotal(total.first().total)
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text = getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) { total ->
            updateText(total)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    // Update the value of the total in the database
    // whenever the activity is paused
    // This is done to ensure that the value of the total is always up to date
    // even if the app is closed
    override fun onPause() {
        super.onPause()
        db.totalDao().update(Total(ID, viewModel.total.value!!))
    }

    companion object {
        // The ID of the Total object in the database
        // For simplicity, we only have one Total object in the database
        // So the ID is always 1
        const val ID: Long = 1
    }
}