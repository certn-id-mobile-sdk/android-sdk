package com.certn.mobile.util.extensions

import retrofit2.Retrofit
import kotlin.reflect.KClass

fun <T : Any> Retrofit.create(service: KClass<T>): T = create(service.java)
