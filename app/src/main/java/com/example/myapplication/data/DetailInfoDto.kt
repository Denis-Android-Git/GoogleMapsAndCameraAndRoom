package com.example.myapplication.data

import com.example.myapplication.entity.*

class DetailInfoDto(
    override val address: Address,
    override val image: String,
    override val kinds: String,
    override val name: String,
    override val otm: String,
    override val point: Point,
    override val preview: Preview?,
    override val rate: String,
    override val sources: Sources,
    override val wikidata: String,
    override val wikipedia: String,
    override val wikipedia_extracts: WikipediaExtracts?,
    override val xid: String
) : DetailInfo