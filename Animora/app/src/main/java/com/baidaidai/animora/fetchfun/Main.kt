package com.baidaidai.fetch

import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody

val clinit = OkHttpClient()

/**
 * HTTP request methods supported by the fetch function.
 *
 * @param names The string representation of the HTTP method.
 * @author Creater. Bai
 */
enum class HttpMethods(val names: String){
    post("POST"),
    get("GET"),
    put("PUT"),
    delete("DELETE"),
}

/**
 * To Build A HttpOptions Class
 */
class HTTPOptions(){
    var methods:String = HttpMethods.get.names
    var body:RequestBody? = null
    var headers:Headers = Headers.Builder().build()

    fun methods(param_methods:HttpMethods?){
        methods = param_methods?.names ?: HttpMethods.get.names
    }
    fun head(header:Headers.Builder.()->Unit){
        val _headers = Headers.Builder()
        _headers.header()
        headers = _headers.build()
    }
    fun body(bodies:String?){
        body = bodies?.trimIndent()?.toRequestBody()
    }

}

suspend fun fetch(url: String,options:HTTPOptions.() -> Unit = {}):String{
    var responseBody:String
    val httpOptions = HTTPOptions()
    httpOptions.options()

    println(httpOptions.headers)

    val request = Request.Builder()
        .url(url)
        .headers(httpOptions.headers)
        .method(
            method = httpOptions.methods,
            body = httpOptions.body
        )
        .build()

    clinit.newCall(request)
        .execute()
        .use {
            responseBody = it.body?.string() ?: "Body is Void"
        }

    return responseBody
}