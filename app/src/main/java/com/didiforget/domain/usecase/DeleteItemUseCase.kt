package com.didiforget.domain.usecase

import com.didiforget.data.model.Item
import com.didiforget.data.repository.ItemRepository

class DeleteItemUseCase(private val itemRepository: ItemRepository) {
    suspend operator fun invoke(item: Item) = itemRepository.deleteItem(item)
}
