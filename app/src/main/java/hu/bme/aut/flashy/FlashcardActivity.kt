package hu.bme.aut.flashy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import hu.bme.aut.flashy.adapter.CollectionAdapter
import hu.bme.aut.flashy.adapter.FlashcardAdapter
import hu.bme.aut.flashy.data.collection.CollectionDatabase
import hu.bme.aut.flashy.data.flashcard.Flashcard
import hu.bme.aut.flashy.data.flashcard.FlashcardDatabase
import hu.bme.aut.flashy.fragments.NewCollectionDialogFragment
import hu.bme.aut.flashy.fragments.NewFlashcardDialogFragment
import kotlin.concurrent.thread

class FlashcardActivity : AppCompatActivity(), FlashcardAdapter.FlashcardClickListener,
    NewFlashcardDialogFragment.NewFlashcardDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlashcardAdapter
    private lateinit var database: FlashcardDatabase

    private var collectionId: Long = 0
    private var collectionName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)
        setSupportActionBar(findViewById(R.id.FlashcardActionBar))

        findViewById<View>(R.id.addFlashcardFab).setOnClickListener{
            NewFlashcardDialogFragment().show(
                supportFragmentManager,
                NewFlashcardDialogFragment.TAG
            )
        }

        collectionId = intent.getLongExtra("collectionId", 0)
        collectionName = intent.getStringExtra("collectionName").toString()

        database = Room.databaseBuilder(
            applicationContext,
            FlashcardDatabase::class.java,
            "flashcard-list"
        ).build()

        this.supportActionBar?.title = collectionName

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.FlashcardRecyclerView)
        adapter = FlashcardAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() {
        thread {
            val flashcards = database.flashcardDao().getAll(collectionId)
            runOnUiThread {
                adapter.updateFlashcard(flashcards)
            }
        }
    }

    override fun onFlashcardChanged(flashcard: Flashcard) {
        thread {
            database.flashcardDao().update(flashcard)
            Log.d("MainActivity", "Flashcard update was successful")
        }
    }

    override fun onFlashcardCreated(newFlashcard: Flashcard) {
        thread {
            var newFlashcard = newFlashcard.copy(
                collectionId = this.collectionId
            )
            val newId = database.flashcardDao().insert(newFlashcard)
            newFlashcard = newFlashcard.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addFlashcard(newFlashcard)
            }
        }
    }

    override fun onFlashcardRemoved(flashcard: Flashcard) {
        thread {
            database.flashcardDao().deleteItem(flashcard)
            Log.d("MainActivity", "Flashcard was removed succesfully")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menuSettings -> {
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menuMore -> {
                Toast.makeText(this, "More selected", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}