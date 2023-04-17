package com.github.factotum_sdp.factotum.ui.auth

abstract class BaseAuthResult<T>(
    open val success: T? = null,
    open val error: Int? = null
)