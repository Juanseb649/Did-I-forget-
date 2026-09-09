package com.didiforget.ai

import com.didiforget.data.model.Item

interface AIService {
    suspend fun suggestItems(description: String): List<Item>
}
