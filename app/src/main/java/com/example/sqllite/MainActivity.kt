package com.example.sqllite
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.sqllite.ui.theme.SqlliteTheme
import android.content.ContentValues


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SqlliteTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    UserRegistrationScreen(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegistrationScreen(context: Context) {
    var userName by remember { mutableStateOf("") }
    var userList by remember { mutableStateOf(emptyList<String>()) }

    val dbHelper = DatabaseHelper(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("User Name") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            dbHelper.insertUser(userName)
            userName = "" // Clear the text field
            userList = dbHelper.getAllUsers()
        }) {
            Text("Register User")
        }

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn {
            items(userList) { user ->
                Text(user)
            }
        }
    }

}



class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "users.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users (name TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades here
    }


    fun insertUser(name: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
        }
        db.insert("users", null, values)
        db.close()
    }


    fun getAllUsers(): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name FROM users", null)
        val userList = mutableListOf<String>()
        while (cursor.moveToNext()) {
            userList.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return userList
    }
}