# Kotlin Sealed Classes and Jackson SerDes Workbench

This project demonstrates a way how to serialize and deserialize (SerDes) Kotlin Sealed classes using
Jackson.

The aim is to serialize and deserialize a children of a sealed class into a JSON structure and add the type:

'''json
{
  "@type": "DOG",
  "color": "Black",
  "barkingPitch": 23
}
'''

The `@type` field contains the simple class name of the sealed class child.
