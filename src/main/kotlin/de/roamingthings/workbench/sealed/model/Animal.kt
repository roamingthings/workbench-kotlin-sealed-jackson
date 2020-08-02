package de.roamingthings.workbench.sealed.model

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    oneOf = [
        Dog::class,
        Cat::class,
        Fish::class
    ],
    discriminatorProperty = "@type",
    discriminatorMapping = [
        DiscriminatorMapping(value = "DOG", schema = Dog::class),
        DiscriminatorMapping(value = "CAT", schema = Cat::class),
        DiscriminatorMapping(value = "FISH", schema = Fish::class)
    ]
)
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
