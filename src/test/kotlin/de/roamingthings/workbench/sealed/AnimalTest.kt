package de.roamingthings.workbench.sealed

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.roamingthings.workbench.sealed.serdes.createObjectMapper
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


const val dogJson = """
        {
            "@type": "DOG",
            "details": {
                "color": "Black",
                "barkingPitch": 23
            }
        }
        """

class AnimalTest {
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = createObjectMapper()
    }

    @Test
    fun `should serialize Dog into JSON`() {
        val animal: Animal = aDog()

        val json = objectMapper.writeValueAsString(animal)

        assertThatJson(json).isEqualTo(dogJson)
    }

    @Test
    fun `should deserialize into Kotlin`() {
        val animal = objectMapper.readValue<Animal>(dogJson)

        assertThat(animal).isEqualTo(aDog())
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
    fun `should throw exception on bissing detailsin JSON`() {
        val jsonOfUnknownType =
            """
          {
             "@type": "DOG"
          }
        """.trimIndent()

        Assertions.assertThatThrownBy { objectMapper.readValue<Animal>(jsonOfUnknownType) }
            .isInstanceOf(JsonMappingException::class.java)
            .hasMessageStartingWith("Missing details")
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
