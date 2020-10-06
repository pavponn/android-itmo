package com.example.networking.config


data class VkConfig(val accessToken: String, val urlConfig: UrlConfig, val version: Version)

data class Version(val major: Int, val minor: Int)

data class UrlConfig(val schema: String, val host:String)


const val vkVersionMajor = "Vk.Version.major"
const val vkVersionMinor = "Vk.Version.minor"
const val vkUrlSchema = "Vk.Url.schema"
const val vkUrlHost = "Vk.Url.host"
const val vkServiceKey = "Vk.service-key"