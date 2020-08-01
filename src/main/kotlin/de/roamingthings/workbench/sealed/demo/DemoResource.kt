package de.roamingthings.workbench.sealed.demo

import de.roamingthings.workbench.sealed.model.Animal
import de.roamingthings.workbench.sealed.model.Cat
import de.roamingthings.workbench.sealed.model.Dog
import de.roamingthings.workbench.sealed.model.Fish
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DemoResource {
    @GetMapping("/animals")
    fun listAnimals(): List<Animal> {
        return listOf(
            Cat(color = "Black", purringVolume = 10),
            Dog(color = "Blue", barkingPitch = 100),
            Fish(finCount = 11, size = 13.5)
        )
    }
}
