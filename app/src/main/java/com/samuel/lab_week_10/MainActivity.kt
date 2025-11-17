package com.samuel.lab_week_10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.samuel.lab_week_10.database.Total
import com.samuel.lab_week_10.database.TotalDatabase
import com.samuel.lab_week_10.database.TotalObject
import com.samuel.lab_week_10.viewmodels.TotalViewModel
import java.util.Date

class MainActivity : AppCompatActivity() {
    private val db by lazy { prepareDatabase() }
    private val viewModel by lazy { ViewModelProvider(this)[TotalViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeValueFromDatabase()
        prepareViewModel()
    }

    override fun onStart() {
        super.onStart()
        val total = db.totalDao().getTotal(ID)
        if (total.isNotEmpty()) {
            Toast.makeText(this, total.first().total.date, Toast.LENGTH_LONG).show()
        }
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java, "total-database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }

    private fun initializeValueFromDatabase() {
        val totalFromDb = db.totalDao().getTotal(ID)
        if (totalFromDb.isEmpty()) {
            val initialTotal = TotalObject(0, Date().toString())
            db.totalDao().insert(Total(id = ID, total = initialTotal))
            viewModel.setTotal(initialTotal.value)
        } else {
            viewModel.setTotal(totalFromDb.first().total.value)
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text = getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) { total ->
            if (total != null) {
                updateText(total)
            }
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.total.value?.let {
            db.totalDao().update(Total(ID, TotalObject(it, Date().toString())))
        }
    }

    companion object {
        const val ID: Long = 1
    }
}