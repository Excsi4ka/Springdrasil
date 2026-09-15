package dev.excsi.springdrasil.component

import dev.excsi.springdrasil.model.TextureType
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class TextureTypeEnumConverter : Converter<String, TextureType> {

    override fun convert(source: String): TextureType {
        return when (source.lowercase()) {
            "skin" -> TextureType.SKIN
            "cape" -> TextureType.CAPE
            else -> throw IllegalArgumentException("Unknown texture type: $source")
        }
    }
}