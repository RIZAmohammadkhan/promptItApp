package com.example.promptit
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.graphics.Color
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var promptsContainer: LinearLayout
    private lateinit var copyButton: MaterialButton
    private lateinit var saveButton: MaterialButton
    private lateinit var toggleFullScreenButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var doneButton: MaterialButton
    private var promptCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        promptsContainer = findViewById(R.id.promptsContainer)
        copyButton = findViewById(R.id.copyButton)
        saveButton = findViewById(R.id.saveButton)
        toggleFullScreenButton = findViewById(R.id.toggleFullScreenButton)
        deleteButton = findViewById(R.id.deleteButton)
        doneButton = findViewById(R.id.doneButton)

        setupListeners()

        // Create the initial prompt
        createNewPrompt()
    }

    private fun setupListeners() {
        doneButton.setOnClickListener {
            createNewPrompt()
        }

        copyButton.setOnClickListener {
            copyPromptsToClipboard()
        }

        saveButton.setOnClickListener {
            savePrompts()
        }

        toggleFullScreenButton.setOnClickListener {
            toggleFullScreen()
        }

        deleteButton.setOnClickListener {
            deleteCurrentPrompt()
        }
    }

    private fun createNewPrompt() {
        // Horizontal LinearLayout for prompt number and EditText
        val promptLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // TextView for the prompt number
        val promptNumberTextView = TextView(this).apply {
            text = "$promptCount)"
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            // Add padding or styling as needed
        }

        // EditText for the prompt
        val promptEditText = EditText(this).apply {
            hint = "Write your prompt..."
            setHintTextColor(Color.GRAY)
            setTextColor(Color.GRAY) // Default text color set to gray
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            // Add padding or styling as needed

            // Set focus change listener
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                // Change text color based on focus
                setTextColor(if (hasFocus) Color.WHITE else Color.GRAY)
            }

        }


        // Add the TextView and EditText to the promptLayout
        promptLayout.addView(promptNumberTextView)
        promptLayout.addView(promptEditText)

        // Add the promptLayout to the container
        promptsContainer.addView(promptLayout)

        // Increment the prompt count for the next prompt
        promptCount++

        // Request focus on the new prompt
        promptEditText.requestFocus()
    }


    private fun copyPromptsToClipboard() {
        val stringBuilder = StringBuilder()
        for (i in 0 until promptsContainer.childCount) {
            val view = promptsContainer.getChildAt(i)
            if (view is EditText) {
                stringBuilder.append(view.text.toString()).append("\n")
            }
        }
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("prompt_texts", stringBuilder.toString())
        clipboard.setPrimaryClip(clip)
    }

    private fun savePrompts() {
        val filenameInput = EditText(this)
        filenameInput.hint = "Enter filename"

        AlertDialog.Builder(this)
            .setTitle("Save Prompts")
            .setView(filenameInput)
            .setPositiveButton("Save") { dialog, _ ->
                val filename = filenameInput.text.toString()
                if (filename.isNotEmpty()) {
                    saveToFile(filename)
                } else {
                    Toast.makeText(this, "Filename cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun saveToFile(filename: String) {
        val stringBuilder = StringBuilder()
        for (i in 0 until promptsContainer.childCount) {
            val view = promptsContainer.getChildAt(i)
            if (view is EditText) {
                stringBuilder.append(view.text.toString()).append("\n")
            }
        }
        openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(stringBuilder.toString().toByteArray())
        }
        Toast.makeText(this, "Prompts saved to $filename", Toast.LENGTH_SHORT).show()
    }

    private fun toggleFullScreen() {
        // Get the current system UI visibility
        val currentUiVisibility = window.decorView.systemUiVisibility
        val newUiVisibility: Int

        // Check if the app is currently in full-screen mode
        val isFullScreen = currentUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN == View.SYSTEM_UI_FLAG_FULLSCREEN
        newUiVisibility = if (isFullScreen) {
            // If in full-screen mode, exit full-screen by clearing the flags
            currentUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN.inv() and
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv() and
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
        } else {
            // If not in full-screen mode, enter full-screen by setting the flags
            currentUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        // Apply the new UI visibility
        window.decorView.systemUiVisibility = newUiVisibility
    }


    private fun deleteCurrentPrompt() {
        // Find the currently focused EditText and its parent LinearLayout
        val currentFocusView = currentFocus
        if (currentFocusView is EditText) {
            val parentLayout = currentFocusView.parent as? LinearLayout
            parentLayout?.let {
                promptsContainer.removeView(it)
                promptCount-- // Decrement the prompt count
                // After deletion, set focus to the last prompt's EditText
                if (promptsContainer.childCount > 0) {
                    val lastPromptLayout = promptsContainer.getChildAt(promptsContainer.childCount - 1) as LinearLayout
                    val lastPromptEditText = lastPromptLayout.getChildAt(1) as EditText
                    lastPromptEditText.requestFocus()
                }
            }
        }
    }
}