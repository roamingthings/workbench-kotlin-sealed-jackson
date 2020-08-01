package de.roamingthings.workbench.sealed

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import de.roamingthings.workbench.sealed.model.Animal
import de.roamingthings.workbench.sealed.serdes.DETAILS_FIELD_NAME
import de.roamingthings.workbench.sealed.serdes.SealedClassSerDesModule
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


const val dogWithDetailsJson = """
        {
            "@type": "DOG",
            "details": {
                "color": "Black",
                "barkingPitch": 23
            }
        }
        """

const val catWithDetailsJson = """
        {
            "@type": "CAT",
            "details": {
                "color": "Black",
                "purringVolume": 42
            }
        }
        """

const val fishWithDetailsJson = """
        {
            "@type": "FISH",
            "details": {
                "finCount": 12,
                "size": 71.5
            }
        }
        """

class AnimalDetailsSerDesTest {
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
        objectMapper.registerModule(KotlinModule())
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false)
        objectMapper.registerModule(SealedClassSerDesModule(detailsFieldName = DETAILS_FIELD_NAME))
    }

    @Test
    fun `should serialize Dog into JSON`() {
        val animal = aDog()

        val json = objectMapper.writeValueAsString(animal)

        assertThatJson(json).isEqualTo(dogWithDetailsJson)
    }

    @Test
    fun `should deserialize Dog JSON into POKO`() {
        val animal = objectMapper.readValue<Animal>(dogWithDetailsJson)

        assertThat(animal).isEqualTo(aDog())
    }

    @Test
    fun `should serialize Cat into JSON`() {
        val animal = aCat()

        val json = objectMapper.writeValueAsString(animal)

        assertThatJson(json).isEqualTo(catWithDetailsJson)
    }

    @Test
    fun `should deserialize Cat JSON into POKO`() {
        val animal = objectMapper.readValue<Animal>(catWithDetailsJson)

        assertThat(animal).isEqualTo(aCat())
    }

    @Test
    fun `should serialize Fish into JSON`() {
        val animal = aFish()

        val json = objectMapper.writeValueAsString(animal)

        assertThatJson(json).isEqualTo(fishWithDetailsJson)
    }

    @Test
    fun `should deserialize Fish JSON into POKO`() {
        val animal = objectMapper.readValue<Animal>(fishWithDetailsJson)

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
