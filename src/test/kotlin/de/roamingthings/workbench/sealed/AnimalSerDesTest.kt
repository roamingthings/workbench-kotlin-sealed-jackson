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

const val catJson = """
        {
            "@type": "CAT",
            "color": "Black",
            "purringVolume": 42
        }
        """

const val fishJson = """
        {
            "@type": "FISH",
            "finCount": 12,
            "size": 71.5
        }
        """

class AnimalSerDesTest {
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
    fun `should deserialize Dog JSON into POKO`() {
        val animal = objectMapper.readValue<Animal>(dogJson)

        assertThat(animal).isEqualTo(aDog())
    }

    @Test
    fun `should serialize Cat into JSON`() {
        val animal = aCat()

        val json = objectMapper.writeValueAsString(animal)

        assertThatJson(json).isEqualTo(catJson)
    }

    @Test
    fun `should deserialize Cat JSON into POKO`() {
        val animal = objectMapper.readValue<Animal>(catJson)

        assertThat(animal).isEqualTo(aCat())
    }

    @Test
    fun `should serialize Fish into JSON`() {
        val animal = aFish()

        val json = objectMapper.writeValueAsString(animal)

        assertThatJson(json).isEqualTo(fishJson)
    }

    @Test
    fun `should deserialize Fish JSON into POKO`() {
        val animal = objectMapper.readValue<Animal>(fishJson)

        assertThat(animal).isEqualTo(aFish())
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

fun aCat() = Cat(
    color = "Black",
    purringVolume = 42
)

fun aFish() = Fish(
    size = 71.5,
    finCount = 12
)
