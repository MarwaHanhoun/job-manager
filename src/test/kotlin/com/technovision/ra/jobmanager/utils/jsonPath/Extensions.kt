package com.technovision.ra.jobmanager.utils.jsonPath

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath

fun String.json(): DocumentContext = JsonPath.parse(this)

inline fun <reified T> DocumentContext.jsonPath(path: String): T = read(path)