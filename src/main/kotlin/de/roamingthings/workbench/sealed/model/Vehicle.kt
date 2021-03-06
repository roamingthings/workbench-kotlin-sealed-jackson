package de.roamingthings.workbench.sealed.model

sealed class Vehicle

data class Car(val color: String, val maxSpeedKph: Int) : Vehicle()

data class Plane(val seatCount: Int, val brand: String) : Vehicle()
