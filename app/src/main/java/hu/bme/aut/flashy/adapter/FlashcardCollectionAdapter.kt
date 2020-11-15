package hu.bme.aut.flashy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.FlashcardCollection

class FlashcardCollectionAdapter(private val listener: FlashcardCollectionClickListener) :
    RecyclerView.Adapter<FlashcardCollectionAdapter.FlashcardCollectionHolder>() {

    private val items = mutableListOf<FlashcardCollection>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardCollectionHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.collection_item, parent, false)
        return FlashcardCollectionHolder(itemView)
    }

    override fun onBindViewHolder(holder: FlashcardCollectionHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.descriptionTextView.text = item.description

        holder.item = item
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(item: FlashcardCollection) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun update(shoppingItems: List<FlashcardCollection>) {
        items.clear()
        items.addAll(shoppingItems)
        notifyDataSetChanged()
    }

    interface FlashcardCollectionClickListener {
        fun onFlashcardCollectionChanged(flashcardCollection: FlashcardCollection)
        fun onFlashcardCollectionRemoved(flashcardCollection: FlashcardCollection)
    }

    inner class FlashcardCollectionHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView = itemView.findViewById(R.id.FlashcardCollectionName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.FlashcardCollectionDescription)

        var item: FlashcardCollection? = null
    }
}