package de.roamingthings.workbench.sealed

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.roamingthings.workbench.sealed.serdes.createObjectMapper
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


const val dogJson = """
        {
            "@type": "DOG",
            "color": "Black",
            "barkingPitch": 23
        }
        """

class UnknownAnimalTest {
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = createObjectMapper()
    }

    @Test
    fun `should serialize Dog into JSON`() {
        val animal = aDog()

        val json = objectMapper.writeValueAsString(animal)

        assertThatJson(json).isEqualTo(dogJson)
    }

    @Test
    fun `should deserialize JSON into POKO`() {
        val animal = objectMapper.readValue<Animal>(dogJson)

        assertThat(animal).isEqualTo(aDog())
    }

    @Test
    fun `should serialize UnknownAnimal into JSON`() {
        val animal = UnknownAnimal

        val json = objectMapper.writeValueAsString(animal)

        assertThatJson(json).isEqualTo(
        """
          {
             "@type": "UNKNOWN_ANIMAL"
          }
        """.trimIndent()
        )
    }
}

fun aDog() = Dog(
    color = "Black",
    barkingPitch = 23
)
