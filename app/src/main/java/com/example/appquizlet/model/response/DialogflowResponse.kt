package com.example.appquizlet.model.response

data class DialogflowResponse(val queryResult: QueryResult)
data class QueryResult(val fulfillmentText: String)

data class ChatBotResponse(val response: String)
