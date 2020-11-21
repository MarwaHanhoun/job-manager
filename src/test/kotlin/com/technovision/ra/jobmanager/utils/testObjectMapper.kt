package com.technovision.ra.jobmanager.utils

import com.fasterxml.jackson.databind.ObjectMapper

val testObjectMapper = ObjectMapper().findAndRegisterModules()