package com.example.skitgubbe

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_player_input, null)
            val alertDialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Enter Player Details")
                .setPositiveButton("OK") { dialog, which ->
                    val playerNameEditText = dialogView.findViewById<EditText>(R.id.playerNameEditText)
                    var playerName = playerNameEditText.text.toString().trim()

                    // Check if the user has entered a name. If not, use "Player" as the default name
                    if (playerName.isEmpty()) {
                        playerName = "Player"
                    }
                    val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
                    val opponentType = when (radioGroup.checkedRadioButtonId) {
                        R.id.playAgainstFriendButton -> "Friend"
                        R.id.playAgainstCpuButton -> "CPU"
                        else -> "CPU"
                    }

                    val intent = Intent(this@MainActivity, GameActivity::class.java)
                    intent.putExtra("playerName", playerName)
                    intent.putExtra("opponentType", opponentType)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                .create()

            alertDialog.show()
        }

        }
    }


