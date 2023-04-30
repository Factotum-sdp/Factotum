package com.github.factotum_sdp.factotum.ui.auth

abstract class BaseAuthResult(
    open val success: String? = null,
    open val error: Int? = null
)