package com.example.compose.jetchat

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

@OptIn(BetaOpenAI::class)
class OpenAIWrapper {
    private val openAIToken: String = "{OPENAI-TOKEN}"
    private var conversation: MutableList<ChatMessage>
    private var openAI: OpenAI = OpenAI(openAIToken)

    init {
        conversation = mutableListOf(
            ChatMessage(
                role = ChatRole.System,
                content = """You are a personal assistant called JetchatGPT.
                            Your answers will be short and concise, since they will be required to fit on 
                            a mobile device display.""".trimMargin()
            )
        )
    }

    suspend fun chat(message: String): String {
        // add the user's message to the chat history
        conversation.add(
            ChatMessage(
                role = ChatRole.User,
                content = message
            )
        )

        // build the OpenAI network request
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = conversation
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)

        // extract the response to show in the app
        val chatResponse = completion.choices[0].message?.content ?: ""

        // add the response to the conversation history
        conversation.add(
            ChatMessage(
                role = ChatRole.Assistant,
                content = chatResponse
            )
        )

        return chatResponse
    }

    suspend fun imageURL(prompt: String): String {
        val imageRequest = ImageCreation(prompt)

        // OpenAI network request
        val images: List<ImageURL> = openAI.imageURL(imageRequest)

        return if (images.isEmpty()) "" else images[0].url
    }
}