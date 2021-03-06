package com.xiangxue.network.apiresponse

import com.xiangxue.network.error.BusinessException
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NetworkResponseAdapterFactory( private val errorHandler: ErrorHandler? = null) : CallAdapter.Factory() {
    /**
     * [onFailure] will be called when [Result.isFailure]
     */
    fun interface ErrorHandler {
        fun onFailure(throwable: BusinessException)
    }

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {

        // suspend functions wrap the response type in `Call`
        if (Call::class.java != getRawType(returnType)) {
            return null
        }

        // check first that the return type is `ParameterizedType`
        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<NetworkResponse<<Foo>> or Call<NetworkResponse<out Foo>>"
        }

        // get the response type inside the `Call` type
        val responseType = getParameterUpperBound(0, returnType)
        // if the response type is not ApiResponse then we can't handle this type, so we return null
        if (getRawType(responseType) != NetworkResponse::class.java) {
            return null
        }

        // the response type is ApiResponse and should be parameterized
        check(responseType is ParameterizedType) { "Response must be parameterized as NetworkResponse<Foo> or NetworkResponse<out Foo>" }
        return object : CallAdapter<Any, Call<*>?> {
            override fun responseType(): Type {
                return responseType
            }

            override fun adapt(call: Call<Any>): Call<*> {
                return NetworkResponseCall(call, errorHandler)
            }
        }
    }
}
