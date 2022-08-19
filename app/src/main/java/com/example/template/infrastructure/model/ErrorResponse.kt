/*
 * Copyright (c) 2022 by Hukman Thayib Amri.
 */

package com.example.template.infrastructure.model

import com.google.gson.annotations.SerializedName

/**
 * This class is default error response from API Server
 * @param statusCode
 * @param statusMessage
 * @return [ErrorResponse]
 * */

//TODO: convert with Error response message base on Server
data class ErrorResponse(
    @SerializedName("status_message")
    val statusMessage: String?, // The resource you requested could not be found.
    @SerializedName("status_code")
    val statusCode: Int? // 34
)