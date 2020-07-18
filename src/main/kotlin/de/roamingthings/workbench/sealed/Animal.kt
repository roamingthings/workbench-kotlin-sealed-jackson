package de.roamingthings.workbench.sealed

sealed class Animal

data class Dog(
    val color: String,
    val barkingPitch: Int
) : Animal()

data class Cat(
    val color: String,
    val purringVolume: Int
) : Animal()

data class Fish(
    val finCount: Int,
    val size: Double
) : Animal()
