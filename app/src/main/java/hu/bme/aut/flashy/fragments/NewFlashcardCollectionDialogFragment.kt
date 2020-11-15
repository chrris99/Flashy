package hu.bme.aut.flashy.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.FlashcardCollection

class NewFlashcardCollectionDialogFragment : DialogFragment() {

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var categorySpinner: Spinner

    interface NewFlashcardCollectionDialogListener {
        fun onFlashcardCollectionCreated(newItem: FlashcardCollection)
    }

    private lateinit var listener: NewFlashcardCollectionDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewFlashcardCollectionDialogListener
            ?: throw RuntimeException("Activity must implement the NewShoppingItemDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_shopping_item)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onFlashcardCollectionCreated(getFlashcardCollection())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun isValid() = nameEditText.text.isNotEmpty()

    private fun getFlashcardCollection() = FlashcardCollection(
        id = null,
        name = nameEditText.text.toString(),
        description = descriptionEditText.text.toString()
    )

    private fun getContentView(): View {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_new_flashcard_collection, null)
        nameEditText = contentView.findViewById(R.id.ShoppingItemNameEditText)
        descriptionEditText = contentView.findViewById(R.id.ShoppingItemDescriptionEditText)
        categorySpinner = contentView.findViewById(R.id.ShoppingItemCategorySpinner)
        categorySpinner.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.category_items)
            )
        )

        return contentView
    }

    companion object {
        const val TAG = "NewFlashcardCollectionDialogFragment"
    }
}