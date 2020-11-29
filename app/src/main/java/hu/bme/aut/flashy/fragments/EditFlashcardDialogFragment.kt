package hu.bme.aut.flashy.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.flashy.R
import hu.bme.aut.flashy.data.flashcard.Flashcard

class EditFlashcardDialogFragment : DialogFragment() {

    private lateinit var termEditText: EditText
    private lateinit var definitionEditText: EditText

    interface EditFlashcardDialogListener {
        fun onFlashcardChanged(changedFlashcard : Flashcard)
    }

    private lateinit var listener: EditFlashcardDialogListener
    private lateinit var flashcard: Flashcard

    fun setFlashcard(flashcard: Flashcard?) {
        if (flashcard != null) {
            this.flashcard = flashcard
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? EditFlashcardDialogListener
            ?: throw RuntimeException("Activity must implement the EditFlashcardDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_collection)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onFlashcardChanged(getFlashcard())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun isValid() = termEditText.text.isNotEmpty() && definitionEditText.text.isNotEmpty()

    private fun getFlashcard() =
        Flashcard(
            id = flashcard.id,
            term = termEditText.text.toString(),
            definition = definitionEditText.text.toString(),
            learned = flashcard.learned,
            collectionId = flashcard.collectionId
        )

    private fun getContentView(): View {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_new_flashcard, null)
        termEditText = contentView.findViewById(R.id.FlashcardTermEditText)
        definitionEditText = contentView.findViewById(R.id.FlashcardDefinitionEditText)

        termEditText.setText(flashcard.term, TextView.BufferType.EDITABLE)
        definitionEditText.setText(flashcard.definition, TextView.BufferType.EDITABLE)

        return contentView
    }

    companion object {
        const val TAG = "EditFlashcardDialogFragment"
    }
}