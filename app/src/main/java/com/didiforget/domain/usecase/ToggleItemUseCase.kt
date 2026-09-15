package com.didiforget.domain.usecase

import com.didiforget.data.repository.ItemRepository

class ToggleItemUseCase(private val itemRepository: ItemRepository) {
    suspend operator fun invoke(itemId: Long, checked: Boolean) = itemRepository.setChecked(itemId, checked)
}
