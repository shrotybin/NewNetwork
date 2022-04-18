package com.xiangxue.network.error

import com.xiangxue.network.apiresponse.NetworkResponseAdapterFactory

class GlobalErrorHandler : NetworkResponseAdapterFactory.ErrorHandler{
    override fun onFailure(throwable: BusinessException) {
        when (throwable.code) {
            2096 -> {
            }
            3099 -> {
            }
        }
    }
}