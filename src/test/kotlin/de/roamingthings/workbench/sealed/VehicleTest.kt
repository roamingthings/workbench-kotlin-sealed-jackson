package de.roamingthings.workbench.sealed

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.roamingthings.workbench.sealed.model.Car
import de.roamingthings.workbench.sealed.model.Vehicle
import net.javacrumbs.jsonunit.assertj.JsonAssertions
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

const val carJson = """
        {
            "@type": "CAR",
            "color": "Black",
            "maxSpeedKph": 180
        }
        """

@SpringBootTest
class VehicleTest {
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should serialize Car into JSON`() {
        val vehicle: Vehicle = aCar()

        val json = objectMapper.writeValueAsString(vehicle)

        JsonAssertions.assertThatJson(json).isEqualTo(carJson)
    }

    @Test
    fun `should deserialize into Kotlin`() {
        val vehicle = objectMapper.readValue<Vehicle>(carJson)

        Assertions.assertThat(vehicle).isEqualTo(aCar())
    }

    @Test
    fun `should throw exception on unknown type in JSON`() {
        val jsonOfUnknownType =
            """
          {
             "@type": "VEHICLE_UNKNOWN"
          }
        """.trimIndent()

        Assertions.assertThatThrownBy { objectMapper.readValue<Vehicle>(jsonOfUnknownType) }
            .isInstanceOf(JsonMappingException::class.java)
            .hasMessageStartingWith("Unknown @type VEHICLE_UNKNOWN")
    }

    @Test
    fun `should throw exception on missing properties JSON`() {
        val jsonOfUnknownType =
            """
          {
             "@type": "CAR"
          }
        """.trimIndent()

        Assertions.assertThatThrownBy { objectMapper.readValue<Vehicle>(jsonOfUnknownType) }
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

        Assertions.assertThatThrownBy { objectMapper.readValue<Vehicle>(jsonOfUnknownType) }
            .isInstanceOf(JsonMappingException::class.java)
            .hasMessageStartingWith("Missing @type")
    }
}

fun aCar() = Car(
    color = "Black",
    maxSpeedKph = 180
)
