package com.github.factotum_sdp.factotum.models
import kotlinx.serialization.Serializable

/**
 * The Bag data model
 */
@Serializable
class Bag(private val packs: List<Pack>): List<Pack> by packs