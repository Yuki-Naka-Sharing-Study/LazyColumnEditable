package com.example.lazycolumneditable

class ItemRepository(private val itemDao: ItemDao) {
    suspend fun getAllItems() = itemDao.getAllItems()
    suspend fun insertItem(item: Item) = itemDao.insert(item)
    suspend fun updateItem(item: Item) = itemDao.update(item)
    suspend fun deleteItem(item: Item) = itemDao.delete(item)
}
