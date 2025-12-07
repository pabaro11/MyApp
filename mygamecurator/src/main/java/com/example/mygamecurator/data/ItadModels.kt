package com.example.mygamecurator.data

data class ItadPriceResponse(val data: Map<String, ItadPriceData>?)
data class ItadPriceData(val prices: List<ItadShopPrice>?)
data class ItadShopPrice(
    val shop: ItadShop?,
    val price_old: Double?,
    val price_new: Double?,
    val price_cut: Int?
)
data class ItadShop(val name: String)
