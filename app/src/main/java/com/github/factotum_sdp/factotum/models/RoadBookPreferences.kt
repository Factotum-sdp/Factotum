package com.github.factotum_sdp.factotum.models

/**
 * The RoadBookPreferences data model
 */
data class RoadBookPreferences(
    val enableReordering: Boolean,
    val enableArchivingAndDeletion: Boolean,
    val enableEdition: Boolean,
    val enableDetailsAccess: Boolean,
    val showArchived: Boolean
)