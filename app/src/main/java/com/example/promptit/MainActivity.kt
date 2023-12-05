package com.example.promptit
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.graphics.Color
import android.widget.TextView
import android.view.KeyEvent
import android.net.Uri
import java.io.IOException
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher






class MainActivity : AppCompatActivity() {


    private lateinit var promptsContainer: LinearLayout
    private lateinit var copyButton: MaterialButton
    private lateinit var saveButton: MaterialButton
    private lateinit var toggleFullScreenButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var doneButton: MaterialButton
    private var promptCount = 0
    private var lastEnterPressTime = 0L


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
            onSaveButtonClicked()
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

            text = (promptCount+1).toString()
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
            setOnKeyListener { v, keyCode, event ->
                // Check for double press of Enter key
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    val currentPressTime = System.currentTimeMillis()
                    if (currentPressTime - lastEnterPressTime < 500) { // 500 milliseconds threshold for double press
                        createNewPrompt() // Creating new prompt on double Enter press
                        true
                    } else {
                        lastEnterPressTime = currentPressTime
                        false
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DEL && text.toString().isEmpty()) {
                    deleteCurrentPrompt()
                    true // Event consumed
                } else {
                    false // Event not consumed
                }
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
        // Update the prompt count based on the current number of prompts
        promptCount = promptsContainer.childCount
    }


    private fun copyPromptsToClipboard() {
        val currentFocusView = currentFocus
        if (currentFocusView is EditText) {
            val textToCopy = currentFocusView.text.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("prompt_text", textToCopy)
            clipboard.setPrimaryClip(clip)
        }
    }


    private fun saveContentToFile(uri: Uri) {
        val content = getPromptsContent() // Get the content to save from your prompts

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPromptsContent(): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until promptsContainer.childCount) {
            // Get the prompt layout
            val promptLayout = promptsContainer.getChildAt(i) as? LinearLayout
            promptLayout?.let {
                // Get the prompt number and EditText from the prompt layout
                val promptNumberTextView = it.getChildAt(0) as? TextView
                val promptEditText = it.getChildAt(1) as? EditText

                // Append the prompt number and text to the StringBuilder
                promptNumberTextView?.text?.let { promptNumber ->
                    promptEditText?.text?.let { promptText ->
                        if (promptText.isNotEmpty()) {
                            stringBuilder.append(promptNumber).append(" ").append(promptText.trim()).append("\n")
                        }
                    }
                }
            }
        }
        return stringBuilder.toString()
    }


    private val createDocument: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri: Uri? ->
        uri?.let {
            saveContentToFile(it)
        }
    }
    private fun onSaveButtonClicked() {
        // Start the process of creating a new document
        createDocument.launch("newfile.txt")
    }



    private var isFullScreen = false

    private fun toggleFullScreen() {
        // Toggle the full-screen state
        isFullScreen = !isFullScreen

        // Get the current system UI visibility
        val currentUiVisibility = window.decorView.systemUiVisibility
        val newUiVisibility: Int = if (isFullScreen) {
            // If not in full-screen mode, enter full-screen by setting the flags
            currentUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        } else {
            // If in full-screen mode, exit full-screen by clearing the flags
            currentUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN.inv() and
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv() and
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
        }

        // Apply the new UI visibility
        window.decorView.systemUiVisibility = newUiVisibility

        // Update the button icon based on the new state
        updateFullScreenButtonIcon()
    }

    private fun updateFullScreenButtonIcon() {
        toggleFullScreenButton.text = if (isFullScreen) "↙️" else "↗️"
    }



    private fun deleteCurrentPrompt() {
        // Find the currently focused EditText and its parent LinearLayout
        val currentFocusView = currentFocus
        if (currentFocusView is EditText) {
            val parentLayout = currentFocusView.parent as? LinearLayout
            parentLayout?.let {
                promptsContainer.removeView(it)
                promptCount-- // Decrement the prompt count

                // Renumber the remaining prompts
                renumberPrompts()

                // After deletion, set focus to the last prompt's EditText
                if (promptsContainer.childCount > 0) {
                    val lastPromptLayout = promptsContainer.getChildAt(promptsContainer.childCount - 1) as LinearLayout
                    val lastPromptEditText = lastPromptLayout.getChildAt(1) as EditText
                    lastPromptEditText.requestFocus()
                }
            }
        }
    }

    private fun renumberPrompts() {
        for (i in 0 until promptsContainer.childCount) {
            val promptLayout = promptsContainer.getChildAt(i) as LinearLayout
            val promptNumberTextView = promptLayout.getChildAt(0) as TextView
            promptNumberTextView.text = "${i + 1})"
        }
    }

}