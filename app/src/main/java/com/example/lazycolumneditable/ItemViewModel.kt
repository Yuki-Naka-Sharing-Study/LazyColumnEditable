import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lazycolumneditable.Item
import com.example.lazycolumneditable.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> get() = _items.asStateFlow()

    private val _editingItem = MutableStateFlow<Item?>(null)
    val editingItem: StateFlow<Item?> get() = _editingItem.asStateFlow()

    fun loadItems() {
        viewModelScope.launch {
            _items.value = repository.getAllItems()
        }
    }

    fun addItem(name: String, quantity: Int) {
        viewModelScope.launch {
            repository.insertItem(Item(name = name, quantity = quantity))
            loadItems()
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
            loadItems()
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item)
            loadItems()
        }
    }

    fun setEditingItem(item: Item?) {
        _editingItem.value = item
    }

    fun resetEditingItem() {
        _editingItem.value = null
    }
}
