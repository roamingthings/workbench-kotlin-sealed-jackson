package de.roamingthings.workbench.sealed

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.roamingthings.workbench.sealed.model.Animal
import de.roamingthings.workbench.sealed.model.Cat
import de.roamingthings.workbench.sealed.model.Dog
import de.roamingthings.workbench.sealed.model.Fish
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


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

@SpringBootTest
class AnimalSerDesTest {
    @Autowired
    lateinit var objectMapper: ObjectMapper

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
    fun `should throw exception on unknown type in JSON`() {
        val jsonOfUnknownType =
            """
          {
             "@type": "ANIMAL_UNKNOWN"
          }
        """.trimIndent()

        Assertions.assertThatThrownBy { objectMapper.readValue<Animal>(jsonOfUnknownType) }
            .isInstanceOf(JsonMappingException::class.java)
            .hasMessageStartingWith("Unknown @type ANIMAL_UNKNOWN")
    }

    @Test
    fun `should throw exception on missing properties JSON`() {
        val jsonOfUnknownType =
            """
          {
             "@type": "DOG"
          }
        """.trimIndent()

        Assertions.assertThatThrownBy { objectMapper.readValue<Animal>(jsonOfUnknownType) }
            .isInstanceOf(JsonMappingException::class.java)
            .hasMessageContaining("missing (therefore NULL) value for creator parameter color which is a non-nullable type")
    }

    @Test
    fun `should throw exception on null type in JSON`() {
        val jsonOfUnknownType =
            """
          {
             "@type": null
          }
        """.trimIndent()

        Assertions.assertThatThrownBy { objectMapper.readValue<Animal>(jsonOfUnknownType) }
            .isInstanceOf(JsonMappingException::class.java)
            .hasMessageStartingWith("Missing @type")
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
