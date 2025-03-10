package com.example.pmp_domashno2

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var macedonianEditText: EditText
    private lateinit var translationEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var searchButton: Button
    private lateinit var clearButton: Button
    private lateinit var containerLayout: LinearLayout

    private val DICTIONARY_FILE = "dictionary.txt"
    private val dictionaryMap = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText = findViewById(R.id.search)
        macedonianEditText = findViewById(R.id.tag)
        translationEditText = findViewById(R.id.translation)
        saveButton = findViewById(R.id.save)
        searchButton = findViewById(R.id.searchBtn)
        clearButton = findViewById(R.id.clear)
        containerLayout = findViewById(R.id.container)

        loadDictionary()

        saveButton.setOnClickListener {
            saveWordToDictionary()
        }

        searchButton.setOnClickListener {
            searchDictionary()
        }

        clearButton.setOnClickListener {
            clearSearchResults()
        }
    }

    private fun loadDictionary() {
        try {
            val file = File(filesDir, DICTIONARY_FILE)

            if (!file.exists()) {
                file.createNewFile()
            }

            file.forEachLine { line ->
                if (line.isNotBlank() && line.contains(":")) {
                    val parts = line.split(":", limit = 2)
                    if (parts.size == 2) {
                        val macedonian = parts[0].trim()
                        val english = parts[1].trim()

                        dictionaryMap[macedonian.lowercase()] = english
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading dictionary: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveWordToDictionary() {
        val macedonian = macedonianEditText.text.toString().trim()
        val english = translationEditText.text.toString().trim()

        if (macedonian.isEmpty() || english.isEmpty()) {
            Toast.makeText(this, "Please enter both words", Toast.LENGTH_SHORT).show()
            return
        }

        dictionaryMap[macedonian.lowercase()] = english

        try {
            val file = File(filesDir, DICTIONARY_FILE)
            file.appendText("$macedonian:$english\n")

            macedonianEditText.text.clear()
            translationEditText.text.clear()

            Toast.makeText(this, "Word saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving word: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchDictionary() {
        val searchTerm = searchEditText.text.toString().trim().lowercase()

        if (searchTerm.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            return
        }

        containerLayout.removeAllViews()

        val results = mutableListOf<Pair<String, String>>()

        dictionaryMap.forEach { (macedonian, english) ->
            if (macedonian.lowercase().contains(searchTerm) ||
                english.lowercase().contains(searchTerm)) {
                results.add(Pair(macedonian, english))
            }
        }

        if (results.isEmpty()) {
            val noResultsView = TextView(this)
            noResultsView.text = "No results found for '$searchTerm'"
            noResultsView.setPadding(10, 10, 10, 10)
            containerLayout.addView(noResultsView)
        } else {
            for (result in results) {
                val resultView = createResultView(result.first, result.second)
                containerLayout.addView(resultView)

                val divider = View(this)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                )
                params.setMargins(0, 8, 0, 8)
                divider.layoutParams = params
                divider.setBackgroundColor(Color.LTGRAY)
                containerLayout.addView(divider)
            }
        }
    }

    private fun createResultView(macedonian: String, english: String): View {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)

        val macedonianView = TextView(this)
        macedonianView.text = macedonian
        macedonianView.textSize = 18f
        macedonianView.setTextColor(Color.BLACK)

        val englishView = TextView(this)
        englishView.text = english
        englishView.textSize = 16f
        englishView.setTextColor(Color.DKGRAY)

        layout.addView(macedonianView)
        layout.addView(englishView)

        return layout
    }

    private fun clearSearchResults() {
        containerLayout.removeAllViews()
        searchEditText.text.clear()
    }
}