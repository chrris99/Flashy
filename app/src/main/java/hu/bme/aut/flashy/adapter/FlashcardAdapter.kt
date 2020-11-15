package hu.bme.aut.flashy.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.flashy.FlashcardActivity
import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.flashcard.Flashcard

class FlashcardAdapter(private val listener: FlashcardClickListener) :
    RecyclerView.Adapter<FlashcardAdapter.FlashcardHolder>() {

    private val flashcards = mutableListOf<Flashcard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardHolder {
        val flashcardView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.flashcard_item, parent, false)
        return FlashcardHolder(flashcardView)
    }

    override fun onBindViewHolder(holder: FlashcardHolder, position: Int) {
        val flashcard = flashcards[position]
        holder.termTextView.text = flashcard.term
        holder.definitionTextView.text = flashcard.definition

        holder.flashcard = flashcard
    }

    override fun getItemCount(): Int {
        return flashcards.size
    }

    fun addFlashcard(flashcard: Flashcard) {
        flashcards.add(flashcard)
        notifyItemInserted(flashcards.size - 1)
    }

    fun updateFlashcard(flashcards: List<Flashcard>) {
        this.flashcards.clear()
        this.flashcards.addAll(flashcards)
        notifyDataSetChanged()
    }

    fun removeFlashcard(position: Int) {
        val flashcard = flashcards[position]
        flashcards.removeAt(position);
        notifyItemRemoved(position);
        listener.onFlashcardRemoved(flashcard)
    }

    interface FlashcardClickListener {
        fun onFlashcardChanged(flashcard: Flashcard)
        fun onFlashcardRemoved(flashcard: Flashcard)
    }

    inner class FlashcardHolder(flashcardView: View) : RecyclerView.ViewHolder(flashcardView){
        val termTextView: TextView = flashcardView.findViewById(R.id.FlashcardTerm)
        val definitionTextView: TextView = flashcardView.findViewById(R.id.FlashcardDefinition)

        var flashcard: Flashcard? = null

        init {
            itemView.setOnClickListener{
                if (termTextView.visibility == TextView.VISIBLE)
                {
                    termTextView.visibility = TextView.GONE
                    definitionTextView.visibility = TextView.VISIBLE
                }

                else {
                    termTextView.visibility = TextView.VISIBLE
                    definitionTextView.visibility = TextView.GONE
                }

            }
        }
    }
}