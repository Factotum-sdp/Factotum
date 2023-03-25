package com.github.factotum_sdp.factotum.data

/**
 * A Client data object which correspond to our main way of representing someone in our Application.
 * Someone is represented in our Application in a customer oriented way, and not a personification.
 *
 *
 * @param clientID
 * @param clientOf: String The clientID of the parent client,
 * when this Client is not directly the client of the courier company using the app
 * otherwise emptyString
 * @param address: String String for the moment but later can be an object containing the cached coordinates, we will see how we manage this
 * @param fullName: The Official full name of the client/company
 * @param entryCode: Int  Code to enter in the delivering place
 * @param floor: Int floor of the apartment, maybe an Enum is better to represent it
 * @param notes: String Additional notes about delivery details or information for the courier to deliver, etc. May put a limitations of charachters
 *
 * Others useful data may be added : .....
 */
data class Client(val clientID: String, val clientOf: String, val address: String, val fullName: String, val entryCode: Int, val floor: Int, val notes: String) {

    fun isSubClient(): Boolean {
        return clientOf.isEmpty()
    }
}