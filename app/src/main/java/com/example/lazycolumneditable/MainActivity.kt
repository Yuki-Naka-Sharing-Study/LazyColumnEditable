package com.example.lazycolumneditable

import ItemViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.lazycolumneditable.ui.theme.LazyColumnEditableTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "db").build()
        val repository = ItemRepository(database.itemDao())
        val viewModel = ItemViewModel(repository)

        setContent {
            LazyColumnEditableTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ItemListScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun ItemListScreen(viewModel: ItemViewModel) {
    val items by viewModel.items.collectAsState() // 修正: Flow の状態を収集
    val editingItem by viewModel.editingItem.collectAsState() // 編集中のアイテム

    val nameState = remember { mutableStateOf("") }
    val quantityState = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 入力フィールド
        OutlinedTextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = quantityState.value,
            onValueChange = { quantityState.value = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        // 追加ボタン
        Button(
            onClick = {
                val name = nameState.value
                val quantity = quantityState.value.toIntOrNull() ?: 0
                scope.launch {
                    if (editingItem != null) {
                        // 編集
                        viewModel.updateItem(editingItem!!.copy(name = name, quantity = quantity))
                        viewModel.resetEditingItem()
                    } else {
                        // 新規作成
                        viewModel.addItem(name, quantity)
                    }
                }
                nameState.value = ""
                quantityState.value = ""
            },
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ) {
            Text(if (editingItem != null) "Update Item" else "Add Item")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // リスト表示
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            viewModel.setEditingItem(item)
                            nameState.value = item.name
                            quantityState.value = item.quantity.toString()
                        }
                        .background(Color.LightGray)
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${item.name} - ${item.quantity}")
                    }
                    // 削除ボタン
                    IconButton(
                        onClick = {
                            scope.launch {
                                viewModel.deleteItem(item)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Item")
                    }
                }
            }
        }
    }
}